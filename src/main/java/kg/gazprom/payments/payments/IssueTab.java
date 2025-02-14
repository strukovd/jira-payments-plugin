package kg.gazprom.payments.payments;


import kg.gazprom.payments.utils.log;
import kg.gazprom.payments.utils.pg;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel3;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsRequest;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.ShowPanelRequest;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

//import org.postgresql.jdbc.;


// Этот класс указывается в XML
public class IssueTab extends AbstractIssueTabPanel3 {
    @Override
    public boolean showPanel(ShowPanelRequest showPanelRequest) {


        // Эта функция вызывается при открытии задачи, тут нужно проверить проект,
        // и если это CL, вернуть true для включения вкладки на страницу
        Issue curIssue = showPanelRequest.issue();
        String curProjectKey = curIssue.getProjectObject().getKey();

        return curProjectKey.matches("CL|NGN|GAZ|UL");
    }




    @Override
    public List<IssueAction> getActions(GetActionsRequest getActionsRequest) {
        try {
            List<IssueAction> res = new ArrayList<>();

            // Получаем лицевой счет абонента
            String accountCustomFieldName = "customfield_11916";
            CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
            CustomField customFieldObject = customFieldManager.getCustomFieldObject(accountCustomFieldName);
            Object accountObject = getActionsRequest.issue().getCustomFieldValue(customFieldObject);
            String account;
            if(accountObject != null) {
                account = (String)accountObject;

                // Получаем список счетов к оплате из БД
                ArrayList<PaymentDataModel> paymentsListFromDB = pg.getPaymentsByAccount(account);

                int count = 0;
                // Если список счетов с БД не получен
                if (paymentsListFromDB != null) {
                    while (paymentsListFromDB.size() > count) {
                        res.add(new PaymentItem(paymentsListFromDB.get(count++), true));
                    }
                    return res;
                } else {
                    return Lists.newArrayList(
                        new GenericMessageAction("<div style=\"text-align:center;\">Платежи абонента не найдены!</div>")
                    );
                }
            }
            else { // Лицевой счет не указан
                return Lists.newArrayList(
                        new GenericMessageAction("<div style=\"text-align:center;\">Лицевой счет не указан!</div>")
                );
            }
        } catch (Exception e) {
            log.error("В методе getActions вознакла ошибка!");
            log.trace("Сообщение: " + e.getMessage());
            return null;
        }
    }
}
