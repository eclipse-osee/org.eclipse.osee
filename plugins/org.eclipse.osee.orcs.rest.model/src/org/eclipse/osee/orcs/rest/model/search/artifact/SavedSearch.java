/*
 * Created on Jan 26, 2026

 *
 * Task 147 - Construct a saved search object to include the title, query, and timestamp
 * Daria Berezianska (dvydybor)
 */
package org.eclipse.osee.orcs.rest.model.search.artifact;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavedSearch {
   private Long id;
   private String title;
   private String query;
   private Long timestamp;
   private List<SavedSearchSelection> artifactTypes;
   private List<SavedSearchSelection> attributeTypes;
   private Boolean exactMatch;
   private Boolean searchById;
   private Boolean global;

   @JsonCreator
   public SavedSearch(@JsonProperty("title") String title, @JsonProperty("query") String query,
      @JsonProperty("timestamp") Long timestamp,
      @JsonProperty("artifactTypes") List<SavedSearchSelection> artifactTypes,
      @JsonProperty("attributeTypes") List<SavedSearchSelection> attributeTypes,
      @JsonProperty("exactMatch") Boolean exactMatch, @JsonProperty("searchById") Boolean searchById,
      @JsonProperty("global") Boolean global) {
      this.title = title;
      this.query = query;
      this.timestamp = timestamp;
      this.artifactTypes = artifactTypes != null ? artifactTypes : new ArrayList<>();
      this.attributeTypes = attributeTypes != null ? attributeTypes : new ArrayList<>();
      this.exactMatch = exactMatch != null ? exactMatch : Boolean.FALSE;
      this.searchById = searchById != null ? searchById : Boolean.FALSE;
      this.global = global != null ? global : Boolean.FALSE;
   }

   // getters and setters
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getQuery() {
      return query;
   }

   public void setQuery(String query) {
      this.query = query;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }

   public List<SavedSearchSelection> getArtifactTypes() {
      return artifactTypes;
   }

   public void setArtifactTypes(List<SavedSearchSelection> artifactTypes) {
      this.artifactTypes = artifactTypes != null ? artifactTypes : new ArrayList<>();
   }

   public List<SavedSearchSelection> getAttributeTypes() {
      return attributeTypes;
   }

   public void setAttributeTypes(List<SavedSearchSelection> attributeTypes) {
      this.attributeTypes = attributeTypes != null ? attributeTypes : new ArrayList<>();
   }

   public Boolean getExactMatch() {
      return exactMatch;
   }

   public void setExactMatch(Boolean exactMatch) {
      this.exactMatch = exactMatch != null ? exactMatch : Boolean.FALSE;
   }

   public Boolean getSearchById() {
      return searchById;
   }

   public void setSearchById(Boolean searchById) {
      this.searchById = searchById != null ? searchById : Boolean.FALSE;
   }

   public Boolean getGlobal() {
      return global;
   }

   public void setGlobal(Boolean global) {
      this.global = global != null ? global : Boolean.FALSE;
   }

   @JsonIgnoreProperties(ignoreUnknown = true)
   public static class SavedSearchSelection {
      private String id;
      private String name;

      @JsonCreator
      public SavedSearchSelection(@JsonProperty("id") String id, @JsonProperty("name") String name) {
         this.id = id;
         this.name = name;
      }

      public String getId() {
         return id;
      }

      public void setId(String id) {
         this.id = id;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }
   }
}
