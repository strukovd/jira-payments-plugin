package kg.gazprom.payments.payments;


public class PaymentDataModel {
    public String transactionId;
    public String account;
    public String date;
    public float amount;
    public int serviceId;
    public String serviceName;
    public String sender;




     public PaymentDataModel(String transactionId,
                             String account, String date,
                             float amount, int serviceId,
                             String serviceName, String sender) {

        this.transactionId = transactionId;
        this.account = account;
        this.date = date;
        this.amount = amount;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.sender = sender;

    }
}

