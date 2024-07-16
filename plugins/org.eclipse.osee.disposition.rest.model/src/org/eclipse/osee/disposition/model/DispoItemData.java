/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoItemData")
public class DispoItemData implements DispoItem {

   private String guid;
   private String name;
   private String assignee;
   private Date creationDate;
   private Date lastUpdate;
   private String status;
   private String version;
   private Map<String, Discrepancy> discrepanciesList;
   private List<DispoAnnotationData> annotationsList;
   private String totalPoints;
   private Boolean needsRerun;
   private String machine;
   private String category;
   private String elapsedTime;
   private Boolean aborted;
   private Boolean needsReview;
   private String itemNotes;
   private String discrepanciesAsRanges;
   private int failureCount;
   private String fileNumber;
   private String methodNumber;
   private Boolean isIncludeDetails;
   private String team;

   public DispoItemData() {
      this.assignee = "UnAssigned";
      this.creationDate = new Date();
      this.lastUpdate = new Date();
      this.status = "Unspecified";
      this.version = "0.0";
      this.totalPoints = "0.0";
      this.needsRerun = false;
      this.machine = "n/a";
      this.category = "";
      this.elapsedTime = "0.0";
      this.aborted = false;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getAssignee() {
      return assignee;
   }

   @Override
   public Date getCreationDate() {
      return creationDate;
   }

   @Override
   public Date getLastUpdate() {
      return lastUpdate;
   }

   @Override
   public String getStatus() {
      return status;
   }

   @Override
   public String getVersion() {
      return version;
   }

   @Override
   public Map<String, Discrepancy> getDiscrepanciesList() {
      return discrepanciesList;
   }

   @Override
   public List<DispoAnnotationData> getAnnotationsList() {
      return annotationsList;
   }

   @Override
   public String getTotalPoints() {
      return totalPoints;
   }

   @Override
   public Boolean getNeedsRerun() {
      return needsRerun;
   }

   @Override
   public String getMachine() {
      return machine;
   }

   @Override
   public String getCategory() {
      return category;
   }

   @Override
   public String getElapsedTime() {
      return elapsedTime;
   }

   @Override
   public Boolean getAborted() {
      return aborted;
   }

   @Override
   public Boolean getNeedsReview() {
      return needsReview;
   }

   @Override
   public String getItemNotes() {
      return itemNotes;
   }

   @Override
   public String getFileNumber() {
      return fileNumber;
   }

   @Override
   public String getMethodNumber() {
      return methodNumber;
   }

   public String getDiscrepanciesAsRanges() {
      return discrepanciesAsRanges;
   }

   public int getFailureCount() {
      return failureCount;
   }

   @Override
   public Boolean getIsIncludeDetails() {
      return isIncludeDetails;
   }

   @Override
   public String getTeam() {
      return team;
   }

   public void setName(String name) {
      if (name != null) {
         name = name.replaceAll("\\.(\\d)+\\.2\\.ada", ".2.ada");
      }
      this.name = name;
   }

   public void setDiscrepanciesList(Map<String, Discrepancy> discrepanciesList) {
      this.discrepanciesList = discrepanciesList;
   }

   public void setAnnotationsList(List<DispoAnnotationData> annotationsList) {
      this.annotationsList = annotationsList;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public void setLastUpdate(Date lastUpdate) {
      this.lastUpdate = lastUpdate;
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setAssignee(String assignee) {
      this.assignee = assignee;
   }

   public void setTotalPoints(String totalPoints) {
      this.totalPoints = totalPoints;
   }

   public void setNeedsRerun(boolean needsRerun) {
      this.needsRerun = needsRerun;
   }

   public void setMachine(String machine) {
      this.machine = machine;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public void setElapsedTime(String elapsedTime) {
      this.elapsedTime = elapsedTime;
   }

   public void setAborted(Boolean aborted) {
      this.aborted = aborted;
   }

   public void setItemNotes(String itemNotes) {
      this.itemNotes = itemNotes;
   }

   public void setFailureCount(int failureCount) {
      this.failureCount = failureCount;
   }

   public void setFileNumber(String fileNumber) {
      this.fileNumber = fileNumber;
   }

   public void setMethodNumber(String methodNumber) {
      this.methodNumber = methodNumber;
   }

   public void setDiscrepanciesAsRanges(String discrepanciesAsRanges) {
      this.discrepanciesAsRanges = discrepanciesAsRanges;
   }

   public void setIsIncludeDetails(Boolean isIncludeDetails) {
      this.isIncludeDetails = isIncludeDetails;
   }

   public void setNeedsReview(Boolean needsReview) {
      this.needsReview = needsReview;
   }

   public void setTeam(String team) {
      this.team = team;
   }
}
