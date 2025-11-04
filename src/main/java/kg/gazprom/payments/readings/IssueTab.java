package kg.gazprom.payments.readings;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.*;
import com.google.common.collect.Lists;
import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.fs;
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
			CustomFieldManager cfManager = ComponentAccessor.getCustomFieldManager();
			CustomField cfAccount = cfManager.getCustomFieldObject("customfield_11916");
			if(cfAccount == null) {
				return Lists.newArrayList(
					new GenericMessageAction("<section id='readings-tab'>⚠\uFE0F Компонент поля customfield_11916 не найден</section>")
				);
			}

			Issue issue = getActionsRequest.issue();
			Object account = issue.getCustomFieldValue(cfAccount);
			if(account == null) {
				return Lists.newArrayList(
					new GenericMessageAction("<section id='readings-tab'>\uD83E\uDEAA Лицевой счет не найден!</section>")
				);
			}

			String code = fs.readFileAsString("template/ReadingSection.html");

			String label = "<section id='readings-tab' data-account='"+account+"'></section>" + code;
			return Lists.newArrayList(
				new GenericMessageAction(label)
			);
        }
        catch (Exception e) {
            log.error("В методе getActions вознакла ошибка!");
            log.trace("Сообщение: " + e.getMessage());
            return null;
        }
    }
}
