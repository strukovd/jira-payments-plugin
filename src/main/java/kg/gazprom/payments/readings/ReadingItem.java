package kg.gazprom.payments.readings;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.JiraApi;

import java.util.Date;


public class ReadingItem implements IssueAction {
    public ReadingDTO payload;
    public boolean isFirst;

    private String scriptBlock = "<script></script>";
    private String styleBlock = "<style></style>";

    ReadingItem(ReadingDTO payload, boolean isFirst) {
        this.payload = payload;
        this.isFirst = isFirst;
    }

    public String getHtml() {
        // Если пользователь состоит в группе admin2, то ему добавим вып. список функций
        String actionList;
        if( JiraApi.isCurrentUserInGroup("admin2") ) {
            actionList = "<span class='menu-button'>⋮</span>";
        }
        else {
            actionList = "";
        }

        String itemCode =
            "<section>" +
				this.payload.toString() +
            "</section>";


        if (isFirst) itemCode = styleBlock + scriptBlock + itemCode;
        return itemCode;
    }

    public Date getTimePerformed() {
        // Вернуть дату платежа
        return new Date();
    }

    public boolean isDisplayActionAllTab() {
        return true;
    }

}
