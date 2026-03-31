/*
 * Created on Feb 2, 2026
 *
 * Daria Berezianska - Task 146 Implement the Save Search button behavior to save a search and prevent a save if required data is missing
 * Task 164 - Implement the POST endpoint saveSearch to be associated with a user
 * Task 166 - Implement GET endpoint for saveSearch to get savedSearches only for the author of the item
 */
package org.eclipse.osee.orcs.rest.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.IAttribute;
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

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSavedSearches() {
      UserId currentUser = getCurrentUserId();

      try {
         ArtifactReadable userArtifact =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(currentUser).asArtifactOrSentinel();

         List<SavedSearch> savedSearches = new ArrayList<>();
         if (userArtifact.isInvalid()) {
            return Response.ok(savedSearches).build();
         }

         for (IAttribute<String> attribute : userArtifact.getAttributeList(CoreAttributeTypes.SavedSearch)) {
            savedSearches.add(fromPayload(attribute.getValue(), attribute.getId()));
         }
         savedSearches.sort(Comparator.comparing(SavedSearch::getId, Comparator.nullsLast(Long::compareTo)).reversed());
         return Response.ok(savedSearches).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting SavedSearches", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @DELETE
   @Path("/{id}")
   public Response deleteSavedSearch(@PathParam("id") Long id) {
      if (id == null || id <= 0) {
         throw new WebApplicationException("id is required", Status.BAD_REQUEST);
      }

      UserId currentUser = getCurrentUserId();

      try {
         ArtifactReadable userArtifact =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(currentUser).asArtifactOrSentinel();

         if (userArtifact.isInvalid()) {
            throw new WebApplicationException("SavedSearch not found", Status.NOT_FOUND);
         }

         IAttribute<String> toDelete = null;
         for (IAttribute<String> attr : userArtifact.getAttributeList(CoreAttributeTypes.SavedSearch)) {
            if (attr.getId() != null && attr.getId().equals(id)) {
               toDelete = attr;
               break;
            }
         }

         if (toDelete == null) {
            throw new WebApplicationException("SavedSearch not found", Status.NOT_FOUND);
         }

         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Delete Saved Search");

         // Remove the attribute from the user's artifact
         tx.deleteByAttributeId(currentUser, AttributeId.valueOf(id));
         tx.commit();

         return Response.noContent().build();
      } catch (WebApplicationException wae) {
         throw wae;
      } catch (Exception ex) {
         throw new WebApplicationException("Error deleting SavedSearch", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @PUT
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateSavedSearch(@PathParam("id") Long id, SavedSearch savedSearch) {
      if (id == null || id <= 0) {
         throw new WebApplicationException("id is required", Status.BAD_REQUEST);
      }

      validateSavedSearch(savedSearch);
      UserId currentUser = getCurrentUserId();

      try {
         ArtifactReadable userArtifact =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(currentUser).asArtifactOrSentinel();

         if (userArtifact.isInvalid()) {
            throw new WebApplicationException("SavedSearch not found", Status.NOT_FOUND);
         }

         IAttribute<String> existing = null;
         for (IAttribute<String> attr : userArtifact.getAttributeList(CoreAttributeTypes.SavedSearch)) {
            if (attr.getId() != null && attr.getId().equals(id)) {
               existing = attr;
               break;
            }
         }

         if (existing == null) {
            throw new WebApplicationException("SavedSearch not found", Status.NOT_FOUND);
         }

         String payload = toPayload(savedSearch);
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Update Saved Search");
         tx.setAttributeById(currentUser, AttributeId.valueOf(id), payload);
         tx.commit();

         savedSearch.setId(id);
         return Response.ok(savedSearch).build();
      } catch (WebApplicationException wae) {
         throw wae;
      } catch (Exception ex) {
         throw new WebApplicationException("Error updating SavedSearch", ex, Status.INTERNAL_SERVER_ERROR);
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
      if (savedSearch.getArtifactTypes() == null) {
         savedSearch.setArtifactTypes(new ArrayList<>());
      }
      if (savedSearch.getAttributeTypes() == null) {
         savedSearch.setAttributeTypes(new ArrayList<>());
      }
      if (savedSearch.getExactMatch() == null) {
         savedSearch.setExactMatch(Boolean.FALSE);
      }
      if (savedSearch.getSearchById() == null) {
         savedSearch.setSearchById(Boolean.FALSE);
      }
      if (savedSearch.getGlobal() == null) {
         savedSearch.setGlobal(Boolean.FALSE);
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

   private SavedSearch fromPayload(String payload, Long attributeId) {
      try {
         SavedSearch savedSearch = mapper.readValue(payload, SavedSearch.class);
         savedSearch.setId(attributeId);
         return savedSearch;
      } catch (Exception e) {
         throw new WebApplicationException("savedSearch deserialization failed", e, Status.INTERNAL_SERVER_ERROR);
      }
   }

   private static class SavedSearchPayload {
      public final String title;
      public final String query;
      public final Long timestamp;
      public final List<SavedSearch.SavedSearchSelection> artifactTypes;
      public final List<SavedSearch.SavedSearchSelection> attributeTypes;
      public final Boolean exactMatch;
      public final Boolean searchById;
      public final Boolean global;

      private SavedSearchPayload(SavedSearch savedSearch) {
         this.title = savedSearch.getTitle();
         this.query = savedSearch.getQuery();
         this.timestamp = savedSearch.getTimestamp();
         this.artifactTypes = savedSearch.getArtifactTypes();
         this.attributeTypes = savedSearch.getAttributeTypes();
         this.exactMatch = savedSearch.getExactMatch();
         this.searchById = savedSearch.getSearchById();
         this.global = savedSearch.getGlobal();
      }
   }
}
