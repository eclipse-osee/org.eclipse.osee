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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
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
      ArtifactId targetArtifact = getSavedSearchContainer(savedSearch.getGlobal());

      savedSearch.setId(null);
      String payload = toPayload(savedSearch);

      try {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Create Saved Search");
         AttributeId attributeId = tx.createAttribute(targetArtifact, CoreAttributeTypes.SavedSearch, payload);
         tx.commit();
         savedSearch.setId(attributeId.getId());
         return Response.ok(savedSearch).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error creating SavedSearch", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSavedSearches(@QueryParam("pageNum") @DefaultValue("0") Long pageNum,
      @QueryParam("count") @DefaultValue("0") Long pageSize) {
      try {
         List<SavedSearch> savedSearches = new ArrayList<>(getPrivateSavedSearchesInternal());
         savedSearches.addAll(getGlobalSavedSearchesInternal());
         sortSavedSearches(savedSearches);
         return Response.ok(applyPagination(savedSearches, pageNum, pageSize)).build();
      } catch (WebApplicationException wae) {
         throw wae;
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting SavedSearches", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @GET
   @Path("/count")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSavedSearchesCount() {
      try {
         List<SavedSearch> savedSearches = new ArrayList<>(getPrivateSavedSearchesInternal());
         savedSearches.addAll(getGlobalSavedSearchesInternal());
         sortSavedSearches(savedSearches);
         return Response.ok(savedSearches.size()).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting SavedSearches count", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @GET
   @Path("/private")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getPrivateSavedSearches(@QueryParam("pageNum") @DefaultValue("0") Long pageNum,
      @QueryParam("count") @DefaultValue("0") Long pageSize) {
      try {
         return Response.ok(applyPagination(getPrivateSavedSearchesInternal(), pageNum, pageSize)).build();
      } catch (WebApplicationException wae) {
         throw wae;
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting private SavedSearches", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @GET
   @Path("/private/count")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getPrivateSavedSearchesCount() {
      try {
         return Response.ok(getPrivateSavedSearchesInternal().size()).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting private SavedSearches count", ex,
            Status.INTERNAL_SERVER_ERROR);
      }
   }

   @GET
   @Path("/global")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getGlobalSavedSearches(@QueryParam("pageNum") @DefaultValue("0") Long pageNum,
      @QueryParam("count") @DefaultValue("0") Long pageSize) {
      try {
         return Response.ok(applyPagination(getGlobalSavedSearchesInternal(), pageNum, pageSize)).build();
      } catch (WebApplicationException wae) {
         throw wae;
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting global SavedSearches", ex, Status.INTERNAL_SERVER_ERROR);
      }
   }

   @GET
   @Path("/global/count")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getGlobalSavedSearchesCount() {
      try {
         return Response.ok(getGlobalSavedSearchesInternal().size()).build();
      } catch (Exception ex) {
         throw new WebApplicationException("Error getting global SavedSearches count", ex,
            Status.INTERNAL_SERVER_ERROR);
      }
   }

   @DELETE
   @Path("/{id}")
   public Response deleteSavedSearch(@PathParam("id") Long id) {
      if (id == null || id <= 0) {
         throw new WebApplicationException("id is required", Status.BAD_REQUEST);
      }

      try {
         SavedSearchLocation location = findSavedSearchLocation(id);

         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Delete Saved Search");

         tx.deleteByAttributeId(location.getContainer(), AttributeId.valueOf(id));
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

      try {
         SavedSearchLocation existingLocation = findSavedSearchLocation(id);
         ArtifactId targetContainer = getSavedSearchContainer(savedSearch.getGlobal());
         String payload = toPayload(savedSearch);
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Update Saved Search");

         if (existingLocation.getContainer().equals(targetContainer)) {
            tx.setAttributeById(targetContainer, AttributeId.valueOf(id), payload);
            tx.commit();
            savedSearch.setId(id);
         } else {
            tx.deleteByAttributeId(existingLocation.getContainer(), AttributeId.valueOf(id));
            AttributeId newAttributeId = tx.createAttribute(targetContainer, CoreAttributeTypes.SavedSearch, payload);
            tx.commit();
            savedSearch.setId(newAttributeId.getId());
         }
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

   private ArtifactId getSavedSearchContainer(Boolean global) {
      if (Boolean.TRUE.equals(global)) {
         return CoreArtifactTokens.GlobalPreferences;
      }
      return getCurrentUserId();
   }

   private List<SavedSearch> getPrivateSavedSearchesInternal() {
      List<SavedSearch> savedSearches = getSavedSearchesFromContainer(getCurrentUserId(), false);
      sortSavedSearches(savedSearches);
      return savedSearches;
   }

   private List<SavedSearch> getGlobalSavedSearchesInternal() {
      List<SavedSearch> savedSearches = getSavedSearchesFromContainer(CoreArtifactTokens.GlobalPreferences, true);
      sortSavedSearches(savedSearches);
      return savedSearches;
   }

   private void sortSavedSearches(List<SavedSearch> savedSearches) {
      savedSearches.sort(Comparator.comparing(SavedSearch::getId, Comparator.nullsLast(Long::compareTo)).reversed());
   }

   private List<SavedSearch> applyPagination(List<SavedSearch> savedSearches, Long pageNum, Long pageSize) {
      long safePageNum = pageNum == null ? 0L : pageNum;
      long safePageSize = pageSize == null ? 0L : pageSize;

      if (safePageNum == 0L && safePageSize == 0L) {
         return savedSearches;
      }
      if (safePageNum <= 0L || safePageSize <= 0L) {
         throw new WebApplicationException("pageNum and count must both be greater than 0", Status.BAD_REQUEST);
      }

      int fromIndex = Math.toIntExact((safePageNum - 1L) * safePageSize);
      if (fromIndex >= savedSearches.size()) {
         return new ArrayList<>();
      }

      int toIndex = Math.min(savedSearches.size(), Math.toIntExact(fromIndex + safePageSize));
      return new ArrayList<>(savedSearches.subList(fromIndex, toIndex));
   }

   private List<SavedSearch> getSavedSearchesFromContainer(ArtifactId artifactId, boolean isGlobalContainer) {
      ArtifactReadable container =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(artifactId).asArtifactOrSentinel();

      List<SavedSearch> savedSearches = new ArrayList<>();
      if (container.isInvalid()) {
         return savedSearches;
      }

      for (IAttribute<String> attribute : container.getAttributeList(CoreAttributeTypes.SavedSearch)) {
         SavedSearch savedSearch = fromPayload(attribute.getValue(), attribute.getId());
         if (isGlobalContainer) {
            savedSearch.setGlobal(Boolean.TRUE);
         }
         savedSearches.add(savedSearch);
      }
      return savedSearches;
   }

   private SavedSearchLocation findSavedSearchLocation(Long id) {
      if (id == null || id <= 0) {
         throw new WebApplicationException("id is required", Status.BAD_REQUEST);
      }

      SavedSearchLocation personalLocation = findSavedSearchLocation(getCurrentUserId(), id, false);
      if (personalLocation != null) {
         return personalLocation;
      }

      SavedSearchLocation globalLocation = findSavedSearchLocation(CoreArtifactTokens.GlobalPreferences, id, true);
      if (globalLocation != null) {
         return globalLocation;
      }

      throw new WebApplicationException("SavedSearch not found", Status.NOT_FOUND);
   }

   private SavedSearchLocation findSavedSearchLocation(ArtifactId containerId, Long attributeId, boolean global) {
      ArtifactReadable container =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(containerId).asArtifactOrSentinel();

      if (container.isInvalid()) {
         return null;
      }

      for (IAttribute<String> attr : container.getAttributeList(CoreAttributeTypes.SavedSearch)) {
         if (attr.getId() != null && attr.getId().equals(attributeId)) {
            return new SavedSearchLocation(containerId, global);
         }
      }
      return null;
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

   private static class SavedSearchLocation {
      private final ArtifactId container;
      private final boolean global;

      private SavedSearchLocation(ArtifactId container, boolean global) {
         this.container = container;
         this.global = global;
      }

      public ArtifactId getContainer() {
         return container;
      }

      public boolean isGlobal() {
         return global;
      }
   }
}
