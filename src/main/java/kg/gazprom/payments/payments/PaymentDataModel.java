package kg.gazprom.payments.payments;


public class PaymentDataModel {
	public long id;
	public String transactionId;
	public String account;
	public String date;
	public float amount;
	public int serviceId;
	public String serviceName;
	public String sender;




	 public PaymentDataModel(long id,
		String transactionId,
		String account, String date,
		float amount, int serviceId,
		String serviceName, String sender) {

		this.id = id;
		this.transactionId = transactionId;
		this.account = account;
		this.date = date;
		this.amount = amount;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.sender = sender;
	}
}

