package kg.gazprom.payments.rest.v1.controllers;

import kg.gazprom.payments.rest.v1.services.InvoiceService;
import kg.gazprom.payments.utils.log;
import kg.gazprom.payments.utils.pg;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;


@Path("/invoice")
public class InvoiceController {


    // TODO: вынести обработку в service и dao пакеты
    @POST
    @Path("/create")
    @Produces({MediaType.APPLICATION_JSON}) // Тип исх. данных
    @Consumes({MediaType.APPLICATION_JSON}) // Тип вх. данных
    public Response createInvoicesInDB(Map<String, Object> params) {
        log.debug("Вызван REST метод createInvoicesInDB [api/1/invoice/create]");
        log.trace("Параметры вызова: " + params.toString() );

        try {
            if( params.get("issueKey") == null )
                log.error("Отсутствует обязательный параметр issueKey");
            if( params.get("subTaskKeys") == null )
                log.error("Отсутствует обязательный параметр (JSON массив) subTaskKeys");

            String issueKey = (String) params.get("issueKey");
            String subTaskKeys = params.get("subTaskKeys").toString();

            JSONArray jsonArraySubTasks = new JSONArray(subTaskKeys);
            for (int i=0; i < jsonArraySubTasks.length(); i++) {
                String subTaskKey = jsonArraySubTasks.getString(i);

                // Получаем данные подзадачи
                log.debug("Получаем данные подзадачи: " + subTaskKey);
                IssueManager issueManager = ComponentAccessor.getIssueManager();
                MutableIssue issue = issueManager.getIssueByCurrentKey(subTaskKey);
                CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

                // Ищем лицевой счет
                CustomField cfAccount = customFieldManager.getCustomFieldObject("customfield_11916");
                if(cfAccount != null) {
                    Object account = issue.getCustomFieldValue(cfAccount);
                    String strAccount = (String)account;

                    // Ищем поля с данными счетов в подзадаче
                    Object serviceType1 = issue.getCustomFieldValue( customFieldManager.getCustomFieldObject("customfield_10139") );
                    Object amount1 = issue.getCustomFieldValue( customFieldManager.getCustomFieldObject("customfield_10144") );

                    if (serviceType1 != null && amount1 != null) {
                        String[] splitted = serviceType1.toString().split(":");
                        int serviceId1 = Integer.parseInt( splitted[0].trim() );
                        String serviceName1 = splitted[1].trim();

                        // На основе найденных данных, создаем счета к оплате в таблице счетов
                        pg.createInvoiceInDB(strAccount, serviceId1, (double)amount1, issueKey);
                        log.info("Добавлен счет на оплату в БД");
                        log.trace("Данные счета: лс: " + strAccount +
                                " номер услуги: " + serviceId1 +
                                " требуемая сумма: " + amount1 +
                                " задача: " + issueKey);
                    }

                    Object serviceType2 = issue.getCustomFieldValue( customFieldManager.getCustomFieldObject("customfield_14705") );
                    Object amount2 = issue.getCustomFieldValue( customFieldManager.getCustomFieldObject("customfield_14707") );
                    if (serviceType2 != null && amount2 != null) {
                        String[] splitted = serviceType2.toString().split(":");
                        int serviceId2 = Integer.parseInt( splitted[0].trim() );
                        String serviceName2 = splitted[1].trim();

                        pg.createInvoiceInDB(strAccount, serviceId2, (double)amount2, issueKey);
                        log.info("Добавлен счет на оплату в БД");
                        log.trace("Данные счета: лс: " + strAccount +
                                " номер услуги: " + serviceId2 +
                                " требуемая сумма: " + amount2 +
                                " задача: " + issueKey);
                    }

                    Object serviceType3 = issue.getCustomFieldValue( customFieldManager.getCustomFieldObject("customfield_14706") );
                    Object amount3 = issue.getCustomFieldValue( customFieldManager.getCustomFieldObject("customfield_14708") );
                    if (serviceType3 != null && amount3 != null) {
                        String[] splitted = serviceType3.toString().split(":");
                        int serviceId3 = Integer.parseInt( splitted[0].trim() );
                        String serviceName3 = splitted[1].trim();

                        pg.createInvoiceInDB(strAccount, serviceId3, (double)amount3, issueKey);
                        log.info("Добавлен счет на оплату в БД");
                        log.trace("Данные счета: лс: " + strAccount +
                                " номер услуги: " + serviceId3 +
                                " требуемая сумма: " + amount3 +
                                " задача: " + issueKey);
                    }
                }
            }

            // Возвращаем успешный ответ
            return Response.ok("{\"success\": true}").build();
        } catch (JSONException e) {
            log.error("Возникла ошибка JSON парсера!");
            log.trace("Сообщение: "+ e.getMessage() );
            return Response.status(500).build();
        }
        catch (Exception e) {
            log.error("Возникла не определенная ошибка!");
            log.trace("Сообщение: "+ e.getMessage() );
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/status")
    @Produces({MediaType.APPLICATION_JSON}) // Тип исх. данных
    @Consumes({MediaType.APPLICATION_JSON}) // Тип вх. данных
    public Response changePaymentStatus(@QueryParam("id") int id, @QueryParam("isPaid") boolean isPaid) {
        // `${location.origin}/rest/api/1/invoice/status?id=123&isPaid=false`
        InvoiceService.changePaymentStatus(id, isPaid);
        return Response.ok("{\"success\": true}").build();
    }

    @PUT
    @Path("/amount")
    @Produces({MediaType.APPLICATION_JSON}) // Тип исх. данных
    @Consumes({MediaType.APPLICATION_JSON}) // Тип вх. данных
    public Response changeInvoiceAmount(@QueryParam("id") int id, @QueryParam("reqAmount") double reqAmount) {
        // `${location.origin}/rest/api/1/invoice/amount?id=123&reqAmount=200.22`
        InvoiceService.changeInvoiceReqAmount(id, reqAmount);
        return Response.ok("{\"success\": true}").build();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON}) // Тип исх. данных
    @Consumes({MediaType.APPLICATION_JSON}) // Тип вх. данных
    public Response deleteInvoiceInDB(@PathParam("id") String id) {
        // `${location.origin}/rest/api/1/invoice/12345`
        return Response.ok("{\"success\": true}").build();
    }
}
