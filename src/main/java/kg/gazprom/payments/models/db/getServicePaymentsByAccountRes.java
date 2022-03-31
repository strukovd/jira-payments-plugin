package kg.gazprom.payments.models.db;

public class getServicePaymentsByAccountRes {
    public String transactionId;
    public String date;
    public float amount;
    public int serviceId;
    public String serviceName;
    public String sender;
    public int internalId;

    public getServicePaymentsByAccountRes(
            String transactionId,
            String date,
            float amount,
            int serviceId,
            String serviceName,
            String sender,
            int internalId) {

        this.transactionId = transactionId;
        this.date = date;
        this.amount = amount;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.sender = sender;
        this.internalId = internalId;

    }
}
