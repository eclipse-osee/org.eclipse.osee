/*
 * Created on Feb 2, 2026
 *
 * Daria Berezianska - Task 146 Implement the Save Search button behavior to save a search and prevent a save if required data is missing
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
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.search.artifact.SavedSearch;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

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
   public Response createSavedSearch(SavedSearch savedSearch) {
      validateSavedSearch(savedSearch);
      UserId currentUser = getCurrentUserId();

      savedSearch.setId(null);
      String payload = toPayload(savedSearch);

      try {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Create Saved Search");
         AttributeId attributeId = tx.createAttribute(currentUser, CoreAttributeTypes.SavedSearch, payload);
         tx.commit();
         savedSearch.setId(attributeId.getId());
         return Response.ok(savedSearch).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error creating SavedSearch", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   private void validateSavedSearch(SavedSearch savedSearch) {
      if (savedSearch == null || savedSearch.getTitle() == null || savedSearch.getTitle().trim().isEmpty()) {
         throw new WebApplicationException("title is required", Status.BAD_REQUEST);
      }
      if (savedSearch.getQuery() == null || savedSearch.getQuery().trim().isEmpty()) {
         throw new WebApplicationException("query is required", Status.BAD_REQUEST);
      }
      savedSearch.setTitle(savedSearch.getTitle().trim());
      savedSearch.setQuery(savedSearch.getQuery().trim());
      if (savedSearch.getTimestamp() == null) {
         savedSearch.setTimestamp(System.currentTimeMillis());
      }
   }

   private UserId getCurrentUserId() {
      UserId currentUser = orcsApi.userService().getUser();
      if (currentUser.isInvalid()) {
         throw new WebApplicationException("No authenticated user found", Status.UNAUTHORIZED);
      }
      return currentUser;
   }

   private String toPayload(SavedSearch savedSearch) {
      try {
         return mapper.writeValueAsString(new SavedSearchPayload(savedSearch));
      } catch (Exception e) {
         throw new WebApplicationException("savedSearch serialization failed", e, Status.BAD_REQUEST);
      }
   }

   private static class SavedSearchPayload {
      public final String title;
      public final String query;
      public final Object columns;
      public final Long timestamp;

      private SavedSearchPayload(SavedSearch savedSearch) {
         this.title = savedSearch.getTitle();
         this.query = savedSearch.getQuery();
         this.columns = savedSearch.getColumns();
         this.timestamp = savedSearch.getTimestamp();
      }
   }
}
