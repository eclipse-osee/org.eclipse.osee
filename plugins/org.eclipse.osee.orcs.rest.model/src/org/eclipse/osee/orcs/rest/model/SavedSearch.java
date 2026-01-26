/*
 * Created on Jan 26, 2026

 *
 * Task 147 - Construct a saved search object to include the title, query, columns, and timestamp
 * Daria Berezianska (dvydybor)
 */
package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;

public class SavedSearch {
   private Long id;
   private String title;
   private SearchRequest query;
   private List<String> columns;
   private Long timestamp;

   @JsonCreator
   public SavedSearch(@JsonProperty("title") String title,
                      @JsonProperty("query") SearchRequest query,
                      @JsonProperty("columns") List<String> columns,
                      @JsonProperty("timestamp") Long timestamp) {
      this.title = title;
      this.query = query;
      this.columns = columns;
      this.timestamp = timestamp;
   }

   // getters and setters
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   public String getTitle() { return title; }
   public void setTitle(String title) { this.title = title; }
   public SearchRequest getQuery() { return query; }
   public void setQuery(SearchRequest query) { this.query = query; }
   public List<String> getColumns() { return columns; }
   public void setColumns(List<String> columns) { this.columns = columns; }
   public Long getTimestamp() { return timestamp; }
   public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
