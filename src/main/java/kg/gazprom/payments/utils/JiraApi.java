package kg.gazprom.payments.utils;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ImportUtils;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

import java.util.Collection;
import java.util.List;


public class JiraApi {
    static public ApplicationUser getCurrentuser() {
        return ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
    }


//    static public List<Issue> searchIssue(String jql, int offset, int limit) throws JqlParseException, SearchException {
//
//        IssueManager issueManager = ComponentAccessor.getIssueManager();
//        SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
//        JqlQueryParser jqlQueryParser = ComponentAccessor.getComponentOfType(JqlQueryParser.class);
//
//        Query query = jqlQueryParser.parseQuery(jql);
//        ApplicationUser appUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
//        SearchResults<Issue> res = searchService.search(appUser, query, new PagerFilter<>(offset, limit) ); //PagerFilter.getUnlimitedFilter()
//
//        return res.getResults();
//    }


//    static public boolean doTransition(String issueKey, int actionId) {
//        log.debug("doTransition: issueKey: " + issueKey + "; actionId: " + actionId);
//
//        IssueManager issueManager = ComponentAccessor.getIssueManager();
//        MutableIssue issue = issueManager.getIssueByCurrentKey(issueKey);
//
//        IssueService issueService = ComponentAccessor.getIssueService();
//        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
//
//        ApplicationUser currentuser = ComponentAccessor.getUserManager().getUserByName(Constants.JIRA_USER_TRIPS_TRANSITIONER);
//        IssueService.TransitionValidationResult transitionValidationResult = issueService.validateTransition(currentuser, issue.getId(), actionId, issueInputParameters);
//        if ( transitionValidationResult.isValid() ) {
//            issueService.transition(currentuser, transitionValidationResult);
//            return true;
//        } else {
//            log.error(String.format("Переход %s не сработал. Условие не было соблюдено", actionId));
//            return false;
//        }
//    }


//    static public void setAssignee(String issueKey, String username) {
//        log.debug("Вызван метод setAssignee");
//        log.trace("issueKey: "+ issueKey +" username: "+ username);
//
//        IssueManager issueManager = ComponentAccessor.getIssueManager();
//        MutableIssue issueByCurrentKey = issueManager.getIssueByCurrentKey(issueKey);
//
//        ApplicationUser appUser = ComponentAccessor.getUserManager().getUserByName(username);
//        issueByCurrentKey.setAssignee(appUser);
//
//        ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName(Constants.JIRA_USER_TRIPS_TRANSITIONER);
//        issueManager.updateIssue(currentUser, issueByCurrentKey, EventDispatchOption.ISSUE_ASSIGNED, true); // Обновление задачи
//    }


    static public Boolean setAssigneeToIssues(String newAssigneeUsername, List<String> issueKeyList){
        try {
            for (String issueKey: issueKeyList) {
                MutableIssue issueObject = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
                issueObject.setAssignee(ComponentAccessor.getUserManager().getUserByName(newAssigneeUsername.toString()));
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(" JIRA API: Возникла ошибка при переназначении исполнителя");
            log.trace("Сообщение: "+ e.getMessage() );
            return Boolean.FALSE;
        }
    }


    static public Collection<ApplicationUser> getUsersOfGroup(String groupName) {
        groupName = "Инженер СОВГО";

        GroupManager groupManager = ComponentAccessor.getGroupManager();
        Collection<ApplicationUser> users = groupManager.getDirectUsersInGroup( groupManager.getGroup(groupName) );
        return users;
    }

    static public Boolean isCurrentUserInGroup(String groupName) {
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        ApplicationUser curUser = JiraApi.getCurrentuser();
        boolean userInGroup = groupManager.isUserInGroup(curUser, groupName);
        return userInGroup;
    }
}
