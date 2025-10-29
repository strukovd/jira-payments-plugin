package kg.gazprom.payments.readings;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel3;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsRequest;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.ShowPanelRequest;
import com.google.common.collect.Lists;
import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.log;
import kg.gazprom.payments.utils.pg;

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

            // Получаем список из БД
            List<ReadingDTO> rows = pg.getReadings("100002647");

            int count = 0;
            if(rows != null) {
                while (rows.size() > count) {
                    boolean isFirstItem = false;
                    if(count == 0) {
                        isFirstItem = true;
                    }

                    list.add( new ReadingItem( rows.get(count), isFirstItem ) );
                    count++;
                }
            }
            else { // Если записей в БД нету
                String label = "<div style=\"text-align:center; margin-bottom:.8em;\">Записи не найдены!</div>";
                Issue issue = getActionsRequest.issue();
                CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

				return Lists.newArrayList(
					new GenericMessageAction(label)
				);
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
