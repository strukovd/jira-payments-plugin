package kg.gazprom.payments.readings;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.JiraApi;

import java.util.Date;


public class ReadingItem implements IssueAction {
    public ReadingDTO payload;
    public boolean isFirst;

    private String scriptBlock = "<script></script>";
    private String styleBlock = "<style>" +
		".table-readings { width:100%; border-collapse:collapse; }" +
		".table-readings thead tr {  }" +
		".table-readings thead td { border:1px solid #f0f0f0; }" +
		".table-readings tbody tr {  }" +
		".table-readings tbody td { border:1px solid #f0f0f0; }" +
		".table-readings tbody td.options {  }" +
		"</style>";

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
            "<tr>" +
			"    <td>"+ this.payload.id +"</td>\n" +
			"    <td>"+ this.payload.consumption +"</td>\n" +
			"    <td>"+ this.payload.date +"</td>\n" +
			"    <td>"+ this.payload.reading +"</td>\n" +
			"    <td>"+ this.payload.sourceName +"</td>\n" +
				"<td class='options' style=\"\n" +
				"    text-align: center;\n" +
				"    padding: .6em 1em;\n" +
				"\">\n" +
				"<button class=\"aui-button\"><span class='aui-icon aui-icon-small aui-iconfont-trash'></span><span> Удалить</span></button>" +
				"</td>" +
            "</tr>";


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
