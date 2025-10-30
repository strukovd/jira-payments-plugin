package kg.gazprom.payments.rest.v1.controllers;

import kg.gazprom.payments.models.db.ServicePaymentDTO;
import kg.gazprom.payments.utils.log;
import kg.gazprom.payments.utils.pg;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.util.json.JSONException;

import java.util.ArrayList;



@Path("/payment")
public class PaymentController {


    // http://bs-mfc-srv01/rest/api/1/payment/find?account=050000821
    @GET
    @Path("/find")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    // @Context HttpServeletRequest req,
    public Response findPaymentsByAccount(@QueryParam("account") String account) {
        log.debug("Вызван REST метод findPaymentsByAccount [rest/api/1/payment/find]");
        log.trace("Параметры вызова: account: " + account );


        try {
            ArrayList<ServicePaymentDTO> paymentListFromDB = pg.getServicePaymentsByAccount( account );
            JSONObject resObject = new JSONObject();

            if( account == null ) {
                log.error("Отсутствует обязательный параметр account");
                return Response.ok( resObject.put("success", false).toString() ).status(400).build();
            }

            int count = 0;
            if(paymentListFromDB != null) {
                JSONArray resArr = new JSONArray();
                while (paymentListFromDB.size() > count) {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("transactionId", paymentListFromDB.get(count).transactionId);
                    jsonObject.put("internalId", paymentListFromDB.get(count).internalId);
                    jsonObject.put("date", paymentListFromDB.get(count).date);
                    jsonObject.put("amount", paymentListFromDB.get(count).amount);
                    jsonObject.put("serviceId", paymentListFromDB.get(count).serviceId);
                    jsonObject.put("serviceName", paymentListFromDB.get(count).serviceName);
                    jsonObject.put("sender", paymentListFromDB.get(count).sender);

                    resArr.put(jsonObject);
                    count++;
                }

                resObject.put("data", resArr);
            }
            else {
                return Response.ok( resObject.put("success", true).toString() ).status(204).build();
            }

            // Возвращаем успешный ответ
            resObject.put("success", true);
            return Response.ok( resObject.toString() ).build();
        }
        catch (JSONException e) {
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


    @DELETE
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public void delete(@QueryParam("id") Integer paymentId, @QueryParam("type") String typeOfPayment) {
        try {
            if(paymentId == null) throw new Exception("Параметр id не определен!");
            if(typeOfPayment == null) typeOfPayment = "billing";

            pg.removePayment( paymentId, typeOfPayment );
            Response.ok().status(204).build();
        }
        catch (Exception e) {
            log.error("Возникла не определенная ошибка!");
            log.trace("Сообщение: "+ e.getMessage() );
            e.printStackTrace();
            Response.status(500).build();
        }
    }


}
