package kg.gazprom.payments.readings;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.JiraApi;

import java.util.Date;


public class ReadingItem implements IssueAction {
    public ReadingDTO payload;
    public boolean isFirst;

    private String scriptBlock = "";
    private String styleBlock = "<style>" +
		".table-readings { width:100%; border-collapse:collapse; }" +
		".table-readings thead tr {  }" +
		".table-readings thead td { border:1px solid #f0f0f0; background:#f9f9f9; text-align:center; font-weight:700; }" +
		".table-readings tbody tr {  }" +
		".table-readings tbody td { border:1px solid #f0f0f0; text-align:center; }" +
		".table-readings tbody td.options { padding:.6em 1em; }" +
		".table-readings tbody tr[id^=r] { --bg-line:#ff000012; background-image:linear-gradient(25deg, var(--bg-line) 20%, rgba(255, 255, 255, 0) 20%, rgba(255, 255, 255, 1) 40%, var(--bg-line) 40%, var(--bg-line) 60%, rgba(255, 255, 255, 1) 60%, rgba(255, 255, 255, 1) 80%, var(--bg-line) 80%); background-size: 70px 100%; }" +
		"</style>";

    ReadingItem(ReadingDTO payload, boolean isFirst) {
        this.payload = payload;
        this.isFirst = isFirst;
    }

    public String getHtml() {
        // Если пользователь состоит в группе admin2, то ему добавим вып. список функций
//        String actionList;
//        if( JiraApi.isCurrentUserInGroup("admin2") ) {
//            actionList = "<span class='menu-button'>⋮</span>";
//        }
//        else {
//            actionList = "";
//        }

//		String removeCeil;
//		if(this.payload.removed) removeCeil = "<button onclick='removeReading("+ this.payload.id +")' class=\"aui-button\"><span class='aui-icon aui-icon-small aui-iconfont-trash'></span><span> Удалить</span></button>";
//		else removeCeil = this.payload.removed_by;
        String itemCode =
            "<tr id='r"+ this.payload.id +"'>" +
			"    <td>"+ this.payload.id +"</td>\n" +
			"    <td>"+ this.payload.consumption +"</td>\n" +
			"    <td>"+ this.payload.date +"</td>\n" +
			"    <td>"+ this.payload.reading +"</td>\n" +
			"    <td>"+ this.payload.sourceName +"</td>\n" +
			"    <td class='options'></td>" +
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
