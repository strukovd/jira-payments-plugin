package kg.gazprom.payments.invoices;

import kg.gazprom.payments.utils.log;
import kg.gazprom.payments.utils.pg;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel3;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsRequest;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.ShowPanelRequest;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// Этот класс указывается в XML
public class IssueTab extends AbstractIssueTabPanel3 {

    @Override
    public boolean showPanel(ShowPanelRequest showPanelRequest) { return true; }



    @Override
    public List<IssueAction> getActions(GetActionsRequest getActionsRequest) {
        try {
            // Получаем ключ задачи
            String issueKey = getActionsRequest.issue().getKey();
            List<IssueAction> list = new ArrayList<>();

            // Получаем список счетов к оплате из БД
            ArrayList<InvoiceDataModel> invoiceListFromDB = pg.getInvoicesByIssueKey(issueKey);

            int count = 0;
            if(invoiceListFromDB != null) {
                while (invoiceListFromDB.size() > count) {
                    list.add( new InvoiceItem( invoiceListFromDB.get(count) ) );
                    count++;
                }
            }
            else { // Если счетов к оплате в БД нету
                String label = "<div style=\"text-align:center; margin-bottom:.8em;\">Для текущей задачи счета не найдены!</div>";
                String emptyInvoiceItem;
                Issue issue = getActionsRequest.issue();
                CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

                // Если у тек. задачи, есть подзадачи, гененрируем кнопку со списком подзадач
                Collection<Issue> subtasks = issue.getSubTaskObjects();
                if( subtasks.size() > 0 ) {
                    StringBuilder subtaskDivs = new StringBuilder();
                    for( Issue i : issue.getSubTaskObjects() ) {
                        String subTaskKey = i.getKey();
                        String subTaskSummary = i.getSummary();

                        subtaskDivs.append(
                            "<div id=\""+ subTaskKey +"\" onclick=\"" +
                                "event.currentTarget.classList.toggle(`ask-subtask-item-sel`);" +
                                "\" class=\"ask-subtask-item\">" +
                                "<span>["+ subTaskKey +"]</span>" +
                                "<span>"+ subTaskSummary +"</span>" +
                            "</div>"
                        );
                    }

                    emptyInvoiceItem = "<style>\n" +
                        ".ask-btn {\n" +
                            "display: inline-block;\n" +
                            "padding: 0 1em;\n" +
                            "line-height: 2em;\n" +
                            "background: #e9e9e9;\n" +
                            "border-radius: 3px;\n" +
                            "cursor: pointer;\n" +
                            "margin: .8em 0;\n" +
                        "}" +
                        ".ask-btn:hover {\n" +
                            "background: #dfdfdf;\n" +
                        "}" +
                        "#ask-create-invoice-wr-btn {\n" +
                            "text-align:center;\n" +
                        "}" +
                        "#create-invoice-block {\n" +
                            "display: none;\n" +
                            "position:relative;" +
                        "}" +
                        "#ask-err-msg-block {\n" +
                            "position: absolute;\n" +
                            "top: 0;\n" +
                            "bottom: 0;\n" +
                            "left: 0;\n" +
                            "right: 0;\n" +
                            "background-color:#ffffff7d;\n" +
                            "text-align: center;\n" +
                            "padding-top:2em;\n" +
                            "color: #d52222;\n" +
                            "display: none;\n" +
                        "}" +
                        ".ask-h-subtasks {\n" +
                            "margin-bottom:.6em;\n" +
                        "}" +
                        ".ask-subtask-item {\n" +
                            "display: flex;\n" +
                            "line-height: 1.6em;\n" +
                            "padding: 0 .6em;\n" +
                            "border-left: 3px solid #ababab;\n" +
                            "background: #f3f3f3;\n" +
                            "margin: 0 1.2em;\n" +
                            "color: #9f9f9f;\n" +
                            "border-bottom: 1px solid #dbdbdb;\n" +
                            "cursor: pointer;\n" +
                        "}" +
                        ".ask-subtask-item-sel {\n" +
                            "border-left: 3px solid #3284ff;\n" +
                            "background: #e9e9e9;\n" +
                            "color: #333;\n" +
                        "}" +
                        ".ask-subtask-item > *:nth-child(1) {\n" +
                            "flex:10em 1 0;\n" +
                        "}" +
                        ".ask-subtask-item > *:nth-child(2) {\n" +
                            "flex:80% 1 1;\n" +
                        "}" +
                    "</style>" +

                    label +

                    // Дополнительная кнопка, дающая возмжность сформировать счета
                    "<div>" +
                        "<div id=\"ask-create-invoice-wr-btn\">\n" +
                            "<span class=\"ask-btn\" onclick=\"\n" +
                                "document.querySelector(`#ask-create-invoice-wr-btn`).style.display = `none`;\n" +
                                "document.querySelector(`#create-invoice-block`).style.display = `block`;\n" +
                                "\">Сформировать счет на оплату на основе данных подзадачи</span>\n" +
                        "</div>\n" +

                        // Скрытые данные
                        "<div id=\"create-invoice-block\">\n" +
                            "<h4 class=\"ask-h-subtasks\">Выберите подзадачу с данными счетов</h4>\n" +
                            "<div id=\"ask-subtask-list\">\n" +
                            subtaskDivs +
                        "</div>\n" +

                        // Кнопка формирования счтов
                        "<div class=\"ask-btn\" onclick='\n" +
                            "const subtasks = document.querySelectorAll(`.ask-subtask-item-sel`);\n" +
                            "const subTaskKeys = [];\n" +
                            "for (const subtask of subtasks) {\n" +
                            "subTaskKeys.push(subtask.id);\n" +
                            "}\n" +

                            "const url = `${location.origin}/rest/api/1/invoice/create`;\n" +
                            "const body = {issueKey:`"+ issueKey +"`, subTaskKeys: subTaskKeys};\n" +
                            "const res = fetch(url, {method:`POST`, headers: {\n" +
                                "\"Content-Type\": `application/json`\n" +
                            "}, body: JSON.stringify(body)});\n" +
                            "res.then((response)=>{\n" +
                                "if(response.ok) {\n" +
                                    "return response.json();\n" +
                                "}\n" +
                                "else {\n" +
                                    "const errBlock = document.querySelector(`#ask-err-msg-block`);" +
                                    "errBlock.innerText = `Ответ от сервера не получен!`;\n" +
                                    "errBlock.style.display = `block`;" +
                                    "throw new Error(`Не удалось получить ответ от сервера!`);\n" +
                                "}\n" +
                                "})\n" +
                                ".catch(()=>{\n" +
                                    "const errBlock = document.querySelector(`#ask-err-msg-block`);" +
                                    "errBlock.innerText = `Ошибка при попытке получить ответ от сервера!`;\n" +
                                    "errBlock.style.display = `block`;" +
                                    "throw new Error(`Ошибка при попытке получить ответ от сервера!`);\n" +
                                "})\n" +
                                ".then((data) => {\n" +
                                    "location.reload();\n" +
                                "})\n" +
                                ".catch(() => {\n" +
                                    "const errBlock = document.querySelector(`#ask-err-msg-block`);" +
                                    "errBlock.innerText = `Не удалось создать счета к оплате!`;\n" +
                                    "errBlock.style.display = `block`;" +
                                "});" +

                            "\n" +
                            "'>Сформировать счёт к оплате</div>\n" +
                            "<div id=\"ask-err-msg-block\"></div>" +
                        "</div>" +
                    "</div>";

                    return Lists.newArrayList(
                        new GenericMessageAction(emptyInvoiceItem)
                    );
                }
                else { // Если подзадач нет, просто пишем что счетов нет
                    emptyInvoiceItem = label;

                    return Lists.newArrayList(
                        new GenericMessageAction(emptyInvoiceItem)
                    );
                }
            }

            return list;
        }
        catch (Exception e) {
            log.error("В методе getActions вознакла ошибка!");
            log.trace("Сообщение: " + e.getMessage());
            return null;
        }
    }
}
