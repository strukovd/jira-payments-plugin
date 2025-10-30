package kg.gazprom.payments.models.db;

public class ReadingDTO {
    public Integer id;
    public String account;
	public Integer consumption;
    public String date;
    public Integer reading;
    public Integer source;
    public String client;

	public String sourceName;
	public String sourceKey;

    public String equipment;
    public int tariff;
	public Boolean sended_to_oktell;
	public Boolean removed;
	public String portal_user;

	public ReadingDTO() {}
    public ReadingDTO(
			Integer id,
            String account,
            Integer consumption,
            String date,
            Integer reading,
            Integer source,
            String client
	) {
        this.id = id;
        this.account = account;
		this.consumption = consumption;
        this.date = date;
        this.reading = reading;
        this.source = source;
        this.client = client;
    }

	@Override
	public String toString() {
		return "{ " +
			"\"id\":" + this.id +
			", \"account\":\"" + this.account + "\"" +
			", \"consumption\":" + this.consumption +
			", \"date\":\"" + this.date + "\"" +
			", \"reading\":" + this.reading +
			", \"source\":" + this.source +
			", \"client\":\"" + this.client + "\"" +
			"}";
	}
}
