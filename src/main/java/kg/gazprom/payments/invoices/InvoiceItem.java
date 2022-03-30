package kg.gazprom.payments.invoices;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import java.util.Date;


public class InvoiceItem implements IssueAction {
    public InvoiceDataModel invoiceData;

    InvoiceItem(InvoiceDataModel data) {
        this.invoiceData = data;
    }

    public String getHtml() {
        String historySection;
        float sumAmount = 0;
        StringBuilder preHistorySection = new StringBuilder();

        // Генерируем блок истории платежей для тек. счета
        if(this.invoiceData.history != null) {
            preHistorySection.append("<section>" +
                    "<div style=\"padding:.5em 0;\">" +
                    "<span style=\"padding-bottom:.3em;font:700 1.1em sans-serif;\">История платежей:</span>" +
                    "</div>");

            int count = 0;
            while (this.invoiceData.history.size() > count) {

                preHistorySection.append("<div style=\"display: flex;\">" +
                        "<span style=\"flex: auto 1 0;\">"+ this.invoiceData.history.get(count).transactionId +"</span>" +
                        "<span style=\"flex: auto 1 0;\">"+ this.invoiceData.history.get(count).date +"</span>" +
                        "<span style=\"flex: auto 1 0;\">" +
                        "<span style=\" color: darkgreen; font-weight: 700;\">"+ this.invoiceData.history.get(count).amount +" сом</span>" +
                        "</span>" +
                        "<span style=\"flex: auto 1 0;\">" +
                        "<span>"+ this.invoiceData.history.get(count).sender +"</span>" +
                        "</span></div>");
                sumAmount += this.invoiceData.history.get(count).amount;
                count++;
            }

            preHistorySection.append("</section>");
            historySection = preHistorySection.toString();
        }
        else {
            historySection = "";
        }


        // TODO: удалить это и смотреть по статусу из БД
        // Определим статус оплаты
        String stamp;
        if (sumAmount >= this.invoiceData.amount) {
            stamp = "<span style=\"" +
                    "background: #7bdb7f;" +
                    "color: #1b603d;" +
                    "padding: .2em .6em;" +
                    "border-radius: 3px;" +
                    "font-weight: 700;" +
                    "float: right;" +
                    "\">Оплачен</span>";
        }
        else {
            stamp = "<span style=\"" +
                    "background: #ffdd77;" +
                    "color: #877631;" +
                    "padding: .2em .6em;" +
                    "border-radius: 3px;" +
                    "font-weight: 700;" +
                    "float: right;" +
                    "\">Не оплачен</span>";
        }

        return "<article style=\"font: normal 13px sans-serif;\">" +

                "<main style=\"padding: .5em .5em .5em 1em; margin-bottom: 0.8em; background: #f8f8f8; border-left: 4px solid #4a84ff;\">" +
                "<section style=\"display: flex;\">" +
                "<div style=\"flex: auto;\">" +
                "<div style=\"padding-bottom:.3em;font:700 1.1em sans-serif;\">Счет на оплату № "+ this.invoiceData.id +"</div>" +
                "<div>" +
                "<span>Услуга ["+ this.invoiceData.serviceId +"]: <span style=\"font-weight: 700;\">" + this.invoiceData.serviceName +"</span></span>" +
                "</div>" +
                "<div>" +
                "<div>" +
                "<span>К оплате: <span style=\"" +
                "color: darkgreen;" +
                "font-weight: 700;\">" +
                this.invoiceData.amount + " сом</span>" +
                "</span>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div style=\"flex: auto;\">" +
                stamp +
                "</div>" +
                "</section>" +
                historySection +
                "</main>" +
                "</article>";
    }

    public Date getTimePerformed() {
        // Вернуть дату платежа
        return new Date();
    }

    public boolean isDisplayActionAllTab() {
        return true;
    }

}
