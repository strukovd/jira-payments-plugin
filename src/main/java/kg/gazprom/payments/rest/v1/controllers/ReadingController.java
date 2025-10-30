package kg.gazprom.payments.rest.v1.controllers;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.rest.v1.services.ReadingService;
import kg.gazprom.payments.utils.log;
import kg.gazprom.payments.utils.pg;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


@Path("/reading")
public class ReadingController {
	@GET
	@Path("/")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public Response find(@QueryParam("account") String account) {
		try {
			if (account == null) {
				log.error("Отсутствует обязательный параметр account");
				return Response.status(400)
					.entity(new JSONObject().put("success", false).toString())
					.build();
			}

			List<ReadingDTO> rows = ReadingService.getList(account);
			JSONObject resObject = new JSONObject();

			if (rows == null || rows.isEmpty()) {
				return Response.status(204)
					.entity(resObject.put("success", true).toString())
					.build();
			}

			JSONArray data = new JSONArray();
			for (ReadingDTO row : rows) {
				data.put(new JSONObject()
					.put("id", row.id)
					.put("account", row.account)
					.put("consumption", row.consumption)
					.put("date", row.date)
					.put("reading", row.reading)
					.put("source", row.source)
					.put("client", row.client));
			}

			resObject.put("success", true).put("data", data);
			return Response.ok(resObject.toString()).build();

		} catch (JSONException e) {
			log.error("Ошибка JSON парсера: {}");
			log.trace(e.getMessage());
			return Response.status(500).build();
		} catch (Exception e) {
			log.error("Неизвестная ошибка!");
			log.trace(e.getMessage());
			return Response.status(500).build();
		}
	}

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON}) // Тип исх. данных
    @Consumes({MediaType.APPLICATION_JSON}) // Тип вх. данных
    public Response delete(@PathParam("id") Integer id) throws JSONException {
		ReadingService.delete(id);
		return Response.status(200)
			.entity(new JSONObject().put("success", true).toString())
			.build();
    }
}
