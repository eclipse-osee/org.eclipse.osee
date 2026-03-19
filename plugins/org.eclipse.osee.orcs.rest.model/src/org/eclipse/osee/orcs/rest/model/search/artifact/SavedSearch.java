/*
 * Created on Jan 26, 2026

 *
 * Task 147 - Construct a saved search object to include the title, query, and timestamp
 * Daria Berezianska (dvydybor)
 */
package org.eclipse.osee.orcs.rest.model.search.artifact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavedSearch {
   private Long id;
   private String title;
   private String query;
   private Long timestamp;

   @JsonCreator
   public SavedSearch(@JsonProperty("title") String title, @JsonProperty("query") String query, @JsonProperty("timestamp") Long timestamp) {
      this.title = title;
      this.query = query;
      this.timestamp = timestamp;
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
}
