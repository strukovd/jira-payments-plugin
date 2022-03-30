package kg.gazprom.payments.payments;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import java.util.Date;

public class PaymentItem implements IssueAction {
    public PaymentDataModel paymentData;

    PaymentItem(PaymentDataModel data) {this.paymentData = data;}



    public String getHtml() {
        String itemBackground;
        if(this.paymentData.serviceId != 0)
            itemBackground = "#f5bf02";
        else
            itemBackground = "#4a84ff";

        return "<div style=\"display:flex;background: #f8f8f8;flex-wrap: wrap;border-left: 4px solid "+ itemBackground +";margin-bottom: .5em;padding: .5em .5em;\">\n" +
                    "<div style=\"flex:15% 1 0\">"+ this.paymentData.transactionId +"</div>\n" +
                    "<div style=\"flex:20% 1 0\">"+ this.paymentData.date +"</div>\n" +
                    "<div style=\"flex:10% 1 0\";color: darkgreen;font-weight: 700;>"+ this.paymentData.amount +"</div>\n" +
                    "<div style=\"flex:25% 1 0\">"+ this.paymentData.sender +"</div>\n" +
                    "<div style=\"flex:35% 1 0\">["+ this.paymentData.serviceId +"] "+ this.paymentData.serviceName +"</div>\n" +
                "</div>";
    }

    public Date getTimePerformed() {
        return new Date();
    }

    public boolean isDisplayActionAllTab() {
        return true;
    }
}
