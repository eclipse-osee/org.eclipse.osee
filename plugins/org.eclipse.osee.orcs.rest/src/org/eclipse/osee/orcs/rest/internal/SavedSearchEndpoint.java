/*
 * Created on Feb 2, 2026
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.rest.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.search.artifact.SavedSearch;

@Path("/savedSearch")
public class SavedSearchEndpoint {
   private final OrcsApi orcsApi;
   private final ObjectMapper mapper;

   public SavedSearchEndpoint(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.mapper = new ObjectMapper();
   }

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response saveSavedSearch(SavedSearch savedSearch) {
      if (savedSearch == null || savedSearch.getTitle() == null || savedSearch.getTitle().trim().isEmpty()) {
         throw new WebApplicationException("title is required", Status.BAD_REQUEST);
      }

      if (savedSearch.getTimestamp() == null) {
         savedSearch.setTimestamp(System.currentTimeMillis());
      }

      final String colsJson;
      try {
         colsJson = savedSearch.getColumns() == null ? "[]" : mapper.writeValueAsString(savedSearch.getColumns());
      } catch (Exception e) {
         throw new WebApplicationException("columns serialization failed", e, Status.BAD_REQUEST);
      }

      final String sql =
         "INSERT INTO saved_searches (title, query, columns, timestamp) " + "VALUES (?, ?, ?::jsonb, ?) RETURNING id";

      try {
         var client = orcsApi.getJdbcService().getClient();
         Long id = client.fetch(-1L, stmt -> stmt.getLong("id"), sql, savedSearch.getTitle().trim(),
            savedSearch.getQuery(), colsJson, savedSearch.getTimestamp());
         if (id != null && id > 0) {
            savedSearch.setId(id);
         }
         return Response.ok(savedSearch).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error saving SavedSearch", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }
}
