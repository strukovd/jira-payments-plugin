package kg.gazprom.payments.invoices;
import kg.gazprom.payments.payments.PaymentDataModel;

import java.util.ArrayList;

public class InvoiceDataModel {
    public String id;
    public int serviceId;
    public String serviceName;
    public float reqAmount;
    public boolean payed;
    public ArrayList<PaymentDataModel> history;

    public InvoiceDataModel(String id, int serviceId, String serviceName, float amount, boolean payed, ArrayList<PaymentDataModel> history) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.reqAmount = amount;
        this.payed = payed;
        this.history = history;
    }
}
