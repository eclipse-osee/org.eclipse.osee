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
import org.eclipse.osee.orcs.rest.model.search.artifact.SavedSearchRequest;
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
   public Response createSavedSearch(SavedSearchRequest savedSearchRequest) {
      validateSavedSearch(savedSearchRequest);
      UserId currentUser = getCurrentUserId();

      String payload = toPayload(savedSearchRequest);

      try {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Create Saved Search");
         AttributeId attributeId = tx.createAttribute(currentUser, CoreAttributeTypes.SavedSearch, payload);
         tx.commit();
         SavedSearch savedSearch = toSavedSearch(savedSearchRequest, attributeId.getId());
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
   public Response updateSavedSearch(@PathParam("id") Long id, SavedSearchRequest savedSearchRequest) {
      if (id == null || id <= 0) {
         throw new WebApplicationException("id is required", Status.BAD_REQUEST);
      }

      validateSavedSearch(savedSearchRequest);
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

         String payload = toPayload(savedSearchRequest);
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Update Saved Search");
         tx.setAttributeById(currentUser, AttributeId.valueOf(id), payload);
         tx.commit();

         SavedSearch savedSearch = toSavedSearch(savedSearchRequest, id);
         return Response.ok(savedSearch).build();
      } catch (WebApplicationException wae) {
         throw wae;
      } catch (Exception ex) {
         throw new WebApplicationException("Error updating SavedSearch", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   private void validateSavedSearch(SavedSearchRequest savedSearchRequest) {
      if (savedSearchRequest == null || savedSearchRequest.getTitle() == null || savedSearchRequest.getTitle().trim().isEmpty()) {
         throw new WebApplicationException("title is required", Status.BAD_REQUEST);
      }
      if (savedSearchRequest.getQuery() == null || savedSearchRequest.getQuery().trim().isEmpty()) {
         throw new WebApplicationException("query is required", Status.BAD_REQUEST);
      }
      savedSearchRequest.setTitle(savedSearchRequest.getTitle().trim());
      savedSearchRequest.setQuery(savedSearchRequest.getQuery().trim());
      if (savedSearchRequest.getTimestamp() == null) {
         savedSearchRequest.setTimestamp(System.currentTimeMillis());
      }
      if (savedSearchRequest.getArtifactTypes() == null) {
         savedSearchRequest.setArtifactTypes(new ArrayList<>());
      }
      if (savedSearchRequest.getAttributeTypes() == null) {
         savedSearchRequest.setAttributeTypes(new ArrayList<>());
      }
      if (savedSearchRequest.getExactMatch() == null) {
         savedSearchRequest.setExactMatch(Boolean.FALSE);
      }
      if (savedSearchRequest.getSearchById() == null) {
         savedSearchRequest.setSearchById(Boolean.FALSE);
      }
   }

   private UserId getCurrentUserId() {
      UserId currentUser = orcsApi.userService().getUser();
      if (currentUser.isInvalid()) {
         throw new WebApplicationException("No authenticated user found", Status.UNAUTHORIZED);
      }
      return currentUser;
   }

   private String toPayload(SavedSearchRequest savedSearchRequest) {
      try {
         return mapper.writeValueAsString(new SavedSearchPayload(savedSearchRequest));
      } catch (Exception e) {
         throw new WebApplicationException("savedSearch serialization failed", e, Status.BAD_REQUEST);
      }
   }

   private SavedSearch toSavedSearch(SavedSearchRequest savedSearchRequest, Long id) {
      SavedSearch savedSearch =
         new SavedSearch(savedSearchRequest.getTitle(), savedSearchRequest.getQuery(), savedSearchRequest.getTimestamp());
      savedSearch.setId(id);
      return savedSearch;
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
      public final List<SavedSearchRequest.SavedSearchSelection> artifactTypes;
      public final List<SavedSearchRequest.SavedSearchSelection> attributeTypes;
      public final Boolean exactMatch;
      public final Boolean searchById;

      private SavedSearchPayload(SavedSearchRequest savedSearchRequest) {
         this.title = savedSearchRequest.getTitle();
         this.query = savedSearchRequest.getQuery();
         this.timestamp = savedSearchRequest.getTimestamp();
         this.artifactTypes = savedSearchRequest.getArtifactTypes();
         this.attributeTypes = savedSearchRequest.getAttributeTypes();
         this.exactMatch = savedSearchRequest.getExactMatch();
         this.searchById = savedSearchRequest.getSearchById();
      }
   }
}
