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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Stephen J. Molaro
 */
public class PartitionResultToken extends ArtifactAccessorResultWithoutGammas {

   public static final PartitionResultToken SENTINEL = new PartitionResultToken();

   //private String setId; //Use for Program, have to be careful to reuse the name 'set' since its used for what is now PartitionResultToken

   private Date coverageCreatedDate = new Date();
   private String coverageImportPath = "";
   private String serverImportPath = "";
   private String coverageImportState = "";
   private String coverageNotesJson = "";
   private String coverageOperationSummary = "";
   private Date coverageImportDate = new Date();
   private int passedCount = 0;
   private int failedCount = 0;
   private int coveredCount = 0;
   private int modifyCount = 0;
   private String result = "";
   private Date executionDate = new Date();
   private String executedBy = "";
   private String errors = "";

   private List<CoverageItemToken> coverageItems;

   public PartitionResultToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PartitionResultToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setCoverageCreatedDate(art.getSoleAttributeValue(CoreAttributeTypes.CoverageCreatedDate, new Date()));
      this.setCoverageImportPath(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageImportPath, ""));
      this.setServerImportPath(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageImportApi, ""));
      this.setCoverageImportState(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageImportState, ""));
      this.setCoverageNotesJson(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageNotesJson, ""));
      this.setCoverageOperationSummary(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageOperationSummary, ""));
      this.setCoverageImportDate(art.getSoleAttributeValue(CoreAttributeTypes.CoverageImportDate, new Date()));
      this.setPassedCount(art.getSoleAttributeValue(CoreAttributeTypes.PassedCount, 0));
      this.setFailedCount(art.getSoleAttributeValue(CoreAttributeTypes.FailedCount, 0));
      this.setCoveredCount(art.getSoleAttributeValue(CoreAttributeTypes.CoveredCount, 0));
      this.setModifyCount(art.getSoleAttributeValue(CoreAttributeTypes.ModifyCount, 0));
      this.setResult(art.getSoleAttributeAsString(CoreAttributeTypes.Result, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setExecutedBy(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutedBy, ""));
      this.setErrors("");

      this.setCoverageItems(art.getRelated(CoreRelationTypes.DefaultHierarchical_Child).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new CoverageItemToken(a)).collect(
            Collectors.toList()));

   }

   public PartitionResultToken(Long id, String name) {
      super(id, name);
      this.setCoverageCreatedDate(new Date());
      this.setCoverageImportPath("");
      this.setServerImportPath("");
      this.setCoverageImportState("");
      this.setCoverageNotesJson("");
      this.setCoverageOperationSummary("");
      this.setCoverageImportDate(new Date());
      this.setPassedCount(0);
      this.setFailedCount(0);
      this.setCoveredCount(0);
      this.setModifyCount(0);
      this.setResult("");
      this.setExecutionDate(new Date());
      this.setExecutedBy("");
      this.setErrors("");

      this.setCoverageItems(new LinkedList<>());
   }

   public PartitionResultToken(Exception ex) {
      super();
      this.setErrors(ex.toString());
   }

   public PartitionResultToken() {
      super();
   }

   /**
    * @return get the coverageCreatedDate
    */
   public Date getCoverageCreatedDate() {
      return coverageCreatedDate;
   }

   /**
    * @param set the coverageCreatedDate to coverageCreatedDate
    */
   public void setCoverageCreatedDate(Date coverageCreatedDate) {
      this.coverageCreatedDate = coverageCreatedDate;
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
   public void setServerImportPath(String serverImportPath) {
      this.serverImportPath = serverImportPath;
   }

   /**
    * @return get the coverageImportState
    */
   public String getCoverageImportState() {
      return coverageImportState;
   }

   /**
    * @param set the coverageImportState to coverageImportState
    */
   public void setCoverageImportState(String coverageImportState) {
      this.coverageImportState = coverageImportState;
   }

   /**
    * @return get the coverageNotesJson
    */
   public String getCoverageNotesJson() {
      return coverageNotesJson;
   }

   /**
    * @param set the coverageNotesJson to coverageNotesJson
    */
   public void setCoverageNotesJson(String coverageNotesJson) {
      this.coverageNotesJson = coverageNotesJson;
   }

   /**
    * @return get the coverageOperationSummary
    */
   public String getCoverageOperationSummary() {
      return coverageOperationSummary;
   }

   /**
    * @param set the coverageOperationSummary to coverageOperationSummary
    */
   public void setCoverageOperationSummary(String coverageOperationSummary) {
      this.coverageOperationSummary = coverageOperationSummary;
   }

   /**
    * @return get the coverageImportDate
    */
   public Date getCoverageImportDate() {
      return coverageImportDate;
   }

   /**
    * @param set the coverageImportDate to coverageImportDate
    */
   public void setCoverageImportDate(Date coverageImportDate) {
      this.coverageImportDate = coverageImportDate;
   }

   /**
    * @return get the passedCount
    */
   public int getPassedCount() {
      return passedCount;
   }

   /**
    * @param set the passedCount to passedCount
    */
   public void setPassedCount(int passedCount) {
      this.passedCount = passedCount;
   }

   /**
    * @return get the failedCount
    */
   public int getFailedCount() {
      return failedCount;
   }

   /**
    * @param set the failedCount to failedCount
    */
   public void setFailedCount(int failedCount) {
      this.failedCount = failedCount;
   }

   /**
    * @return get the coveredCount
    */
   public int getCoveredCount() {
      return coveredCount;
   }

   /**
    * @param set the coveredCount to coveredCount
    */
   public void setCoveredCount(int coveredCount) {
      this.coveredCount = coveredCount;
   }

   /**
    * @return get the modifyCount
    */
   public int getModifyCount() {
      return modifyCount;
   }

   /**
    * @param set the modifyCount to modifyCount
    */
   public void setModifyCount(int modifyCount) {
      this.modifyCount = modifyCount;
   }

   /**
    * @return get the result
    */
   public String getResult() {
      return result;
   }

   /**
    * @param set the result to result
    */
   public void setResult(String result) {
      this.result = result;
   }

   /**
    * @return get the executionDate
    */
   public Date getExecutionDate() {
      return executionDate;
   }

   /**
    * @param set the executionDate to executionDate
    */
   public void setExecutionDate(Date executionDate) {
      this.executionDate = executionDate;
   }

   /**
    * @return get the executedBy
    */
   public String getExecutedBy() {
      return executedBy;
   }

   /**
    * @param set the executedBy to executedBy
    */
   public void setExecutedBy(String executedBy) {
      this.executedBy = executedBy;
   }

   /**
    * @return get the coverageItems
    */
   public List<CoverageItemToken> getCoverageItems() {
      return coverageItems;
   }

   /**
    * @param set the coverageItems to coverageItems
    */
   public void setCoverageItems(List<CoverageItemToken> coverageItems) {
      this.coverageItems = coverageItems;
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

}