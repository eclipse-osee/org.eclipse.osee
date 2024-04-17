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
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class CoverageItemToken extends ArtifactAccessorResult {

   public static final CoverageItemToken SENTINEL = new CoverageItemToken();

   private String coverageAnnotationsJson = "";
   private String coverageDiscrepanciesJson = "";
   private Date coverageCreatedDate = new Date();
   private String coverageAssignee = "";
   private String coverageFileNumber = "";
   private Date coverageLastUpdated = new Date();
   private String coverageMethodNumber = "";
   private boolean coverageNeedsRerun = false;
   private String notes = "";
   private String coverageStatus = "";
   private String coverageTotalPoints = "";
   private String coverageTeam = "";
   private int passedCount = 0;
   private int failedCount = 0;
   private int coveredCount = 0;
   private int modifyCount = 0;
   private String result = "";
   private Date executionDate = new Date();
   private String executedBy = "";
   private String errors = "";

   public CoverageItemToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public CoverageItemToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setCoverageAnnotationsJson(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageAnnotationsJson, ""));
      this.setCoverageDiscrepanciesJson(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageDiscrepanciesJson, ""));
      this.setCoverageCreatedDate(art.getSoleAttributeValue(CoreAttributeTypes.CoverageCreatedDate, new Date()));
      this.setCoverageAssignee(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageAssignee, ""));
      this.setCoverageFileNumber(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageFileNumber, ""));
      this.setCoverageLastUpdated(art.getSoleAttributeValue(CoreAttributeTypes.CoverageLastUpdated, new Date()));
      this.setCoverageMethodNumber(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageMethodNumber, ""));
      this.setCoverageNeedsRerun(art.getSoleAttributeValue(CoreAttributeTypes.CoverageNeedsRerun, false));
      this.setNotes(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageNotes, ""));
      this.setCoverageStatus(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageStatus, ""));
      this.setCoverageTotalPoints(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageTotalPoints, ""));
      this.setCoverageTeam(art.getSoleAttributeAsString(CoreAttributeTypes.CoverageTeam, ""));
      this.setPassedCount(art.getSoleAttributeValue(CoreAttributeTypes.PassedCount, 0));
      this.setFailedCount(art.getSoleAttributeValue(CoreAttributeTypes.FailedCount, 0));
      this.setCoveredCount(art.getSoleAttributeValue(CoreAttributeTypes.CoveredCount, 0));
      this.setModifyCount(art.getSoleAttributeValue(CoreAttributeTypes.ModifyCount, 0));
      this.setResult(art.getSoleAttributeAsString(CoreAttributeTypes.Result, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setExecutedBy(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutedBy, ""));
      this.setErrors("");
   }

   public CoverageItemToken(Long id, String name) {
      super(id, name);
      this.setCoverageAnnotationsJson("");
      this.setCoverageDiscrepanciesJson("");
      this.setCoverageCreatedDate(new Date());
      this.setCoverageAssignee("");
      this.setCoverageFileNumber("");
      this.setCoverageLastUpdated(new Date());
      this.setCoverageMethodNumber("");
      this.setCoverageNeedsRerun(false);
      this.setNotes("");
      this.setCoverageStatus("");
      this.setCoverageTotalPoints("");
      this.setCoverageTeam("");
      this.setPassedCount(0);
      this.setFailedCount(0);
      this.setCoveredCount(0);
      this.setModifyCount(0);
      this.setResult("");
      this.setExecutionDate(new Date());
      this.setExecutedBy("");
      this.setErrors("");
   }

   public CoverageItemToken() {
      super();
   }

   public CoverageItemToken(Exception ex) {
      super();
      this.setErrors(ex.toString());
   }

   /**
    * @return get the coverageAnnotationsJson
    */
   public String getCoverageAnnotationsJson() {
      return coverageAnnotationsJson;
   }

   /**
    * @param set the coverageAnnotationsJson to coverageAnnotationsJson
    */
   public void setCoverageAnnotationsJson(String coverageAnnotationsJson) {
      this.coverageAnnotationsJson = coverageAnnotationsJson;
   }

   /**
    * @return get the coverageDiscrepanciesJson
    */
   public String getCoverageDiscrepanciesJson() {
      return coverageDiscrepanciesJson;
   }

   /**
    * @param set the coverageDiscrepanciesJson to coverageDiscrepanciesJson
    */
   public void setCoverageDiscrepanciesJson(String coverageDiscrepanciesJson) {
      this.coverageDiscrepanciesJson = coverageDiscrepanciesJson;
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
    * @return get the coverageAssignee
    */
   public String getCoverageAssignee() {
      return coverageAssignee;
   }

   /**
    * @param set the coverageAssignee to coverageAssignee
    */
   public void setCoverageAssignee(String coverageAssignee) {
      this.coverageAssignee = coverageAssignee;
   }

   /**
    * @return get the coverageFileNumber
    */
   public String getCoverageFileNumber() {
      return coverageFileNumber;
   }

   /**
    * @param set the coverageFileNumber to coverageFileNumber
    */
   public void setCoverageFileNumber(String coverageFileNumber) {
      this.coverageFileNumber = coverageFileNumber;
   }

   /**
    * @return get the coverageLastUpdated
    */
   public Date getCoverageLastUpdated() {
      return coverageLastUpdated;
   }

   /**
    * @param set the coverageLastUpdated to coverageLastUpdated
    */
   public void setCoverageLastUpdated(Date coverageLastUpdated) {
      this.coverageLastUpdated = coverageLastUpdated;
   }

   /**
    * @return get the coverageMethodNumber
    */
   public String getCoverageMethodNumber() {
      return coverageMethodNumber;
   }

   /**
    * @param set the coverageMethodNumber to coverageMethodNumber
    */
   public void setCoverageMethodNumber(String coverageMethodNumber) {
      this.coverageMethodNumber = coverageMethodNumber;
   }

   /**
    * @return get the coverageNeedsRerun
    */
   public boolean getCoverageNeedsRerun() {
      return coverageNeedsRerun;
   }

   /**
    * @param set the coverageNeedsRerun to coverageNeedsRerun
    */
   public void setCoverageNeedsRerun(boolean coverageNeedsRerun) {
      this.coverageNeedsRerun = coverageNeedsRerun;
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
    * @return get the coverageStatus
    */
   public String getCoverageStatus() {
      return coverageStatus;
   }

   /**
    * @param set the coverageStatus to coverageStatus
    */
   public void setCoverageStatus(String coverageStatus) {
      this.coverageStatus = coverageStatus;
   }

   /**
    * @return get the coverageTotalPoints
    */
   public String getCoverageTotalPoints() {
      return coverageTotalPoints;
   }

   /**
    * @param set the coverageTotalPoints to coverageTotalPoints
    */
   public void setCoverageTotalPoints(String coverageTotalPoints) {
      this.coverageTotalPoints = coverageTotalPoints;
   }

   /**
    * @return get the coverageTeam
    */
   public String getCoverageTeam() {
      return coverageTeam;
   }

   /**
    * @param set the coverageTeam to coverageTeam
    */
   public void setCoverageTeam(String coverageTeam) {
      this.coverageTeam = coverageTeam;
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