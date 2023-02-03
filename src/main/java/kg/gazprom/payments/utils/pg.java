package kg.gazprom.payments.utils;

import kg.gazprom.payments.invoices.InvoiceDataModel;
import kg.gazprom.payments.payments.PaymentDataModel;
import kg.gazprom.payments.models.db.*;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;


public class pg {

    static {
        // TODO: get pool connection
    }


    private static final String url = "jdbc:postgresql://bs-docker-srv02:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "jellyfish";

    public static ArrayList<InvoiceDataModel> getInvoicesByIssueKey(String issueKey) {

        Connection conn = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);


            String query = "SELECT i.id, i.account, i.service_id, i.req_amount, i.issue_key, i.payed, COALESCE(s.description, 'Неизвестная услуга') AS service_name, " +
                    "(" +
                        "SELECT json_agg(payments) " +
                        "FROM (" +
                            "SELECT p.txn_id, to_char(p.txn_date, 'DD.MM.YYYY HH24:MI:SS') AS txn_date, p.pay_amount, COALESCE(p.sender, 'Не указано') AS sender " +
                            "FROM ps_replenishment_payment p " +
                            "WHERE p.for_invoice = i.id" +
                        ") AS payments" +
                    ") AS payments " +
                    "FROM invoices_for_payment i " +
                    "LEFT JOIN rng_public_services s ON s.code = i.service_id " +
                    "WHERE issue_key = ?;";



            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, issueKey);

            log.trace("Отправляем запрос: "+ query);
            ResultSet rs = preparedStatement.executeQuery();
            conn.close();

            // Собираем список счетов к оплате
            ArrayList<InvoiceDataModel> invoiceList = new ArrayList<InvoiceDataModel>();


            // Обрабатываем полученные строки
            while( rs.next() ){
                String id = rs.getString("id");
                String account = rs.getString("account");
                int serviceId = rs.getInt("service_id");
                float required_amount = rs.getFloat("req_amount");
                boolean payed = rs.getBoolean("payed");
//                String issueKey = rs.getString(6);
                String serviceName = rs.getString("service_name");
                String jsonArrPayments = rs.getString("payments");

                // Если есть хроника платежей, парсим ее в список
                ArrayList<PaymentDataModel> paymentHistory;
                if (jsonArrPayments != null) {
                    paymentHistory = new ArrayList<PaymentDataModel>();
                    JSONArray arrPayments = new JSONArray( rs.getString(8) );

                    // JSON массив платежей преобразуем в список
                    for (int i = 0; i < arrPayments.length(); i++) {
                        JSONObject rec = arrPayments.getJSONObject(i);

                        String txn_id = rec.getString("txn_id");
                        String txn_date = rec.getString("txn_date");
                        float pay_amount = (float)rec.getDouble("pay_amount");
                        String sender = rec.getString("sender");

                        paymentHistory.add(new PaymentDataModel(txn_id, account, txn_date, pay_amount, serviceId, serviceName, sender)  );
                    }
                }
                else {
                    paymentHistory = null;
                }

                InvoiceDataModel invoiceItem = new InvoiceDataModel(id, serviceId, serviceName, required_amount, payed, paymentHistory);
                invoiceList.add(invoiceItem);
            }

            if( invoiceList.size() == 0 ) {
                log.debug("Для задачи: "+ issueKey +" счета к оплате не найдены");
                return null;
            }
            else {
                log.debug("Успешно получен список счетов к оплате для задачи "+ issueKey);
                return invoiceList;
            }
        } catch (SQLException e) {
            log.error("Возникла ошибка SQL выражения!");
            log.trace("Сообщение: "+ e.getMessage() );
            return null;
        } catch (JSONException e) {
            log.error("Возникла ошибка JSON парсера!");
            log.trace("Сообщение: "+ e.getMessage() );
            return null;
        }
        catch (Exception e) {
            log.error("Возникла не определенная ошибка!");
            log.trace("Сообщение: "+ e.getMessage() );
            e.printStackTrace();
            return null;
        }
    }





    public static ArrayList<PaymentDataModel> getPaymentsByAccount(String account) {
        Connection conn = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * " +
                "FROM (" +
                    "SELECT rp.txn_id, " +
                        "to_char(rp.txn_date, 'DD.MM.YYYY HH24:MI:SS') AS txn_date, " +
                        "rp.pay_amount AS amount, " +
                        "COALESCE(rp.sender, 'Не указано') AS sender, " +
                        "rp.service_id, " +
                        "COALESCE(s.description, 'Неизвестная услуга') AS service_name " +
                    "FROM ps_replenishment_payment rp " +
                    "LEFT JOIN rng_public_services s ON s.code = rp.service_id " +
                    "WHERE account = ?" +
                    "UNION " +
                    "SELECT bp.txn_id, " +
                        "to_char(bp.txn_date, 'DD.MM.YYYY HH24:MI:SS') AS txn_date, " +
                        "bp.bill_amount AS amount, " +
                        "COALESCE(bp.sender, 'Не указано') AS sender, " +
                        "0 AS service_id, " +
                        "'Оплата за газ' AS service_name " +
                    "FROM ps_billing_payment bp " +
                    "WHERE account = ?" +
                ") AS payments " +
                "ORDER BY txn_date;");

            preparedStatement.setString(1, account);
            preparedStatement.setString(2, account);
            ResultSet rs = preparedStatement.executeQuery();
            conn.close();

            // Собираем в список
            ArrayList<PaymentDataModel> paymentList = new ArrayList<>();

            // Обрабатываем полученные строки
            while( rs.next() ){
                String id = rs.getString(1);
                String date = rs.getString(2);
                float amount = rs.getFloat(3);
                String sender = rs.getString(4);
                int serviceId = rs.getInt(5);
                String serviceName = rs.getString(6);

                PaymentDataModel paymentItem = new PaymentDataModel(id, account, date, amount, serviceId, serviceName, sender);
                paymentList.add(paymentItem);
            }

            if( paymentList.size() == 0 ) {
                return null;
            }
            else {
                return paymentList;
            }
        } catch (SQLException e) {
            log.error("Вознакла ошибка SQL выражения!");
            log.trace("Сообщение: "+ e.getMessage() );
            return null;
        }
        catch (Exception e) {
            log.error("Вознакла не определенная ошибка!");
            log.trace("Сообщение: "+ e.getMessage() );
            e.printStackTrace();
            return null;
        }
    }





    public static void createInvoiceInDB(String account, int serviceId, double req_amount, String issueKey) {
        Connection conn = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);


            String query = "INSERT INTO invoices_for_payment (account, service_id, req_amount, issue_key) VALUES (?, ?, ?, ?) RETURNING id;";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, account);
            preparedStatement.setInt(2, serviceId);
            preparedStatement.setDouble(3, req_amount);
            preparedStatement.setString(4, issueKey);

            log.trace("Отправляем запрос: "+ query);
            ResultSet rs = preparedStatement.executeQuery();
            conn.close();

            if( !rs.next() ) {
                log.error("Не удалось получить ID нового счета к оплате!");
            }
        } catch (SQLException e) {
            log.error("Возникла ошибка SQL выражения!");
            log.trace("Сообщение: "+ e.getMessage() );
        }
        catch (Exception e) {
            log.error("Возникла не определенная ошибка!");
            log.trace("Сообщение: "+ e.getMessage() );
            e.printStackTrace();
        }
    }






    public static ArrayList<getServicePaymentsByAccountRes> getServicePaymentsByAccount(String account) {
        Connection conn = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT txn_id, txn_date, pay_amount, service_id, id, sender, s.description AS service_name\n" +
                    "FROM ps_replenishment_payment p\n" +
                    "INNER JOIN rng_public_services s ON s.code = p.service_id\n" +
                    "WHERE account = ?;");

            preparedStatement.setString(1, account);
            ResultSet rs = preparedStatement.executeQuery();
            conn.close();

            // Собираем в список
            ArrayList<getServicePaymentsByAccountRes> paymentList = new ArrayList<>();

            // Обрабатываем полученные строки
            while( rs.next() ){
                String transactionId = rs.getString("txn_id");
                String date = rs.getString("txn_date");
                float amount = rs.getFloat("pay_amount");
                int serviceId = rs.getInt("service_id");
                String serviceName = rs.getString("service_name");
                String sender = rs.getString("sender");
                int internalId = rs.getInt("id");

                getServicePaymentsByAccountRes paymentItem = new getServicePaymentsByAccountRes(transactionId, date, amount, serviceId, serviceName, sender, internalId);
                paymentList.add(paymentItem);
            }

            if( paymentList.size() == 0 ) {
                return null;
            }
            else {
                return paymentList;
            }
        } catch (SQLException e) {
            log.error("Вознакла ошибка SQL выражения!");
            log.trace("Сообщение: "+ e.getMessage() );
            return null;
        }
        catch (Exception e) {
            log.error("Вознакла не определенная ошибка!");
            log.trace("Сообщение: "+ e.getMessage() );
            e.printStackTrace();
            return null;
        }
    }
}
