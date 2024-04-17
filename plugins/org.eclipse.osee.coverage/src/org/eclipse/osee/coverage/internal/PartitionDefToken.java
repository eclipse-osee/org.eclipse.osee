/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.coverage.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Stephen J. Molaro
 */
public class PartitionDefToken extends ArtifactAccessorResult {

   public static final PartitionDefToken SENTINEL = new PartitionDefToken();

   private final Comparator<Date> dateComparator = new DateComparator();

   private boolean active = true;
   private String team = "";
   private String lastAuthor = "";
   private Date lastModified = new Date();
   private String modifiedFlag = "";
   private String coverageImportPath = "";
   private String serverImportPath = "";
   private Date latestExecutionDate = new Date();
   private int latestPassedCount = 0;
   private int latestFailedCount = 0;
   private int latestCoveredCount = 0;
   private int latestModifyCount = 0;
   private String latestResult = "";
   private String description = "";
   private String notes = "";
   private String errors = "";

   private List<PartitionResultToken> partitionResults;

   public PartitionDefToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PartitionDefToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setActive(art.getSoleAttributeValue(CoreAttributeTypes.Active, true));
      this.setTeam(art.getSoleAttributeAsString(CoreAttributeTypes.TeamName, ""));
      this.setLastAuthor(art.getSoleAttributeAsString(CoreAttributeTypes.LastAuthor, ""));
      this.setLastModified(art.getSoleAttributeValue(CoreAttributeTypes.LastModifiedDate, new Date()));
      this.setModifiedFlag(art.getSoleAttributeAsString(CoreAttributeTypes.ModifiedFlag, ""));
      this.setCoverageImportPath(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageImportPath, ""));
      this.setServerImportPath(art.getSoleAttributeAsString(CoreAttributeTypes.ServerImportPath, ""));
      this.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
      this.setNotes(art.getSoleAttributeAsString(CoreAttributeTypes.Notes, ""));
      this.setErrors("");

      this.setPartitionResults(
         art.getRelated(CoreRelationTypes.PartitionDefToPartitionResult_PartitionResult).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).sorted(
               Comparator.comparing(this::getExecutionDateByAttr, dateComparator)).filter(
                  a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new PartitionResultToken(a)).collect(
                     Collectors.toList()));

      if (!getPartitionResults().isEmpty()) {
         PartitionResultToken resultToken = getPartitionResults().get(0);
         this.setLatestExecutionDate(resultToken.getExecutionDate());
         this.setLatestPassedCount(resultToken.getPassedCount());
         this.setLatestFailedCount(resultToken.getFailedCount());
         this.setLatestCoveredCount(resultToken.getCoveredCount());
         this.setLatestModifyCount(resultToken.getModifyCount());
         this.setLatestResult(resultToken.getResult());
      }
   }

   public PartitionDefToken(Long id, String name) {
      super(id, name);
      this.setActive(true);
      this.setTeam("");
      this.setLastAuthor("");
      this.setLastModified(new Date());
      this.setModifiedFlag("");
      this.setCoverageImportPath("");
      this.setServerImportPath("");
      this.setLatestExecutionDate(new Date());
      this.setLatestPassedCount(0);
      this.setLatestFailedCount(0);
      this.setLatestCoveredCount(0);
      this.setLatestModifyCount(0);
      this.setLatestResult("");
      this.setDescription("");
      this.setNotes("");
      this.setErrors("");
      this.setPartitionResults(new ArrayList<PartitionResultToken>());
   }

   public PartitionDefToken(Exception ex) {
      super();
      this.setErrors(ex.toString());
   }

   public PartitionDefToken() {
      super();
   }

   /**
    * @return get the active
    */
   public boolean getActive() {
      return active;
   }

   /**
    * @param set the active to active
    */
   public void setActive(boolean active) {
      this.active = active;
   }

   /**
    * @return get the team
    */
   public String getTeam() {
      return team;
   }

   /**
    * @param set the team to team
    */
   public void setTeam(String team) {
      this.team = team;
   }

   /**
    * @return get the lastAuthor
    */
   public String getLastAuthor() {
      return lastAuthor;
   }

   /**
    * @param set the lastAuthor to lastAuthor
    */
   public void setLastAuthor(String lastAuthor) {
      this.lastAuthor = lastAuthor;
   }

   /**
    * @return get the lastModified
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * @param set the lastModified to lastModified
    */
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   /**
    * @return get the modifiedFlag
    */
   public String getModifiedFlag() {
      return modifiedFlag;
   }

   /**
    * @param set the modifiedFlag to modifiedFlag
    */
   public void setModifiedFlag(String modifiedFlag) {
      this.modifiedFlag = modifiedFlag;
   }

   /**
    * @return get the coverageImportPath
    */
   public String getCoverageImportPath() {
      return coverageImportPath;
   }

   /**
    * @param set the coverageImportPath to coverageImportPath
    */
   public void setCoverageImportPath(String coverageImportPath) {
      this.coverageImportPath = coverageImportPath;
   }

   /**
    * @return get the serverImportPath
    */
   public String getServerImportPath() {
      return serverImportPath;
   }

   /**
    * @param set the serverImportPath to serverImportPath
    */
   public void setServerImportPath(String team) {
      this.serverImportPath = serverImportPath;
   }

   /**
    * @return get the latestExecutionDate
    */
   public Date getLatestExecutionDate() {
      return latestExecutionDate;
   }

   /**
    * @param set the latestExecutionDate to latestExecutionDate
    */
   public void setLatestExecutionDate(Date latestExecutionDate) {
      this.latestExecutionDate = latestExecutionDate;
   }

   /**
    * @return get the latestPassedCount
    */
   public int getLatestPassedCount() {
      return latestPassedCount;
   }

   /**
    * @param set the latestPassedCount to latestPassedCount
    */
   public void setLatestPassedCount(int latestPassedCount) {
      this.latestPassedCount = latestPassedCount;
   }

   /**
    * @return get the latestFailedCount
    */
   public int getLatestFailedCount() {
      return latestFailedCount;
   }

   /**
    * @param set the latestFailedCount to latestFailedCount
    */
   public void setLatestFailedCount(int latestFailedCount) {
      this.latestFailedCount = latestFailedCount;
   }

   /**
    * @return get the latestCoveredCount
    */
   public int getLatestCoveredCount() {
      return latestCoveredCount;
   }

   /**
    * @param set the latestCoveredCount to latestCoveredCount
    */
   public void setLatestCoveredCount(int latestCoveredCount) {
      this.latestCoveredCount = latestCoveredCount;
   }

   /**
    * @return get the latestModifyCount
    */
   public int getLatestModifyCount() {
      return latestModifyCount;
   }

   /**
    * @param set the latestModifyCount to latestModifyCount
    */
   public void setLatestModifyCount(int latestModifyCount) {
      this.latestModifyCount = latestModifyCount;
   }

   /**
    * @return get the latestResult
    */
   public String getLatestResult() {
      return latestResult;
   }

   /**
    * @param set the latestResult to latestResult
    */
   public void setLatestResult(String latestResult) {
      this.latestResult = latestResult;
   }

   /**
    * @return get the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param set the description to description
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return get the notes
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @param set the notes to notes
    */
   public void setNotes(String notes) {
      this.notes = notes;
   }

   /**
    * @return get the partitionResults
    */
   public List<PartitionResultToken> getPartitionResults() {
      return partitionResults;
   }

   /**
    * @param set the partitionResults to partitionResults
    */
   public void setPartitionResults(List<PartitionResultToken> partitionResults) {
      this.partitionResults = partitionResults;
   }

   /**
    * @return get the stack trace errors
    */
   public String getErrors() {
      return errors;
   }

   /**
    * @param set the errors to errors
    */
   public void setErrors(String errors) {
      this.errors = errors;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Active, Boolean.toString(this.getActive()));
      values.put(CoreAttributeTypes.TeamName, this.getTeam());
      values.put(CoreAttributeTypes.LastAuthor, this.getLastAuthor());
      values.put(CoreAttributeTypes.LastModifiedDate, Long.toString(this.getLastModified().getTime()));
      values.put(CoreAttributeTypes.ModifiedFlag, getModifiedFlag()); // TODO should this be a boolean?
      values.put(CoreAttributeTypes.CoverageImportPath, this.getCoverageImportPath());
      values.put(CoreAttributeTypes.ServerImportPath, this.getServerImportPath());
      values.put(CoreAttributeTypes.Description, this.getDescription());
      values.put(CoreAttributeTypes.Notes, this.getNotes());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.CoveragePartitionDef.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.CoveragePartitionDef.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

   public Date getExecutionDateByAttr(ArtifactReadable art) {
      return art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate);
   }

   public class DateComparator implements Comparator<Date> {

      @Override
      public int compare(Date date1, Date date2) {

         if (date1 == null && date2 == null) {
            return 0;
         } else if (date1 == null) {
            return 1; // obj1 comes after obj2
         } else if (date2 == null) {
            return -1; // obj1 comes before obj2
         }
         return date1.compareTo(date2);
      }
   }

}