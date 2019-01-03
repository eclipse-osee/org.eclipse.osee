/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.ev;

import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageData {

   private String colorTeam = "";
   private String workPackageProgram = "";
   private String workPackageIdStr = "";
   private String activityId = "";
   private String workPackageName;
   private String insertionActivityName = "";
   private String insertionName = "";
   private String programName = "";
   private String countryName = "";
   private String teamNames = "";
   private long workPackageId;
   private boolean workPackageActive = true;
   private int workPackagePercentComplete = 0;
   private double workPackagePointsNumeric = 0.0;
   private String workPackageType = "";
   private double workPackageBac = 0.0;
   private String workPackageIpt = "";
   private Date workPackageStartDate = null;
   private Date workPackageEndDate = null;
   private String workPackageNotes = "";
   private String workPackageAnnotation = "";

   public WorkPackageData(String workPackageName, long workPackageId) {
      this.workPackageName = workPackageName;
      this.workPackageId = workPackageId;
   }

   public String getColorTeam() {
      return colorTeam;
   }

   public void setColorTeam(String colorTeam) {
      this.colorTeam = colorTeam;
   }

   public String getWorkPackageProgram() {
      return workPackageProgram;
   }

   public void setWorkPackageProgram(String workPackageProgram) {
      this.workPackageProgram = workPackageProgram;
   }

   public String getWorkPackageIdStr() {
      return workPackageIdStr;
   }

   public void setWorkPackageIdStr(String workPackageIdStr) {
      this.workPackageIdStr = workPackageIdStr;
   }

   public String getActivityId() {
      return activityId;
   }

   public void setActivityId(String activityId) {
      this.activityId = activityId;
   }

   public String getWorkPackageName() {
      return workPackageName;
   }

   public void setWorkPackageName(String workPackageName) {
      this.workPackageName = workPackageName;
   }

   public String getInsertionActivityName() {
      return insertionActivityName;
   }

   public void setInsertionActivityName(String insertionActivityName) {
      this.insertionActivityName = insertionActivityName;
   }

   public String getInsertionName() {
      return insertionName;
   }

   public void setInsertionName(String insertionName) {
      this.insertionName = insertionName;
   }

   public String getProgramName() {
      return programName;
   }

   public void setProgramName(String programName) {
      this.programName = programName;
   }

   public String getCountryName() {
      return countryName;
   }

   public void setCountryName(String countryName) {
      this.countryName = countryName;
   }

   public String getTeamNames() {
      return teamNames;
   }

   public void setTeamNames(String teamNames) {
      this.teamNames = teamNames;
   }

   public long getWorkPackageId() {
      return workPackageId;
   }

   public void setWorkPackageId(long workPackageId) {
      this.workPackageId = workPackageId;
   }

   public boolean isWorkPackageActive() {
      return workPackageActive;
   }

   public void setWorkPackageActive(boolean workPackageActive) {
      this.workPackageActive = workPackageActive;
   }

   public int getWorkPackagePercentComplete() {
      return workPackagePercentComplete;
   }

   public void setWorkPackagePercentComplete(int workPackagePercentComplete) {
      this.workPackagePercentComplete = workPackagePercentComplete;
   }

   public double getWorkPackagePointsNumeric() {
      return workPackagePointsNumeric;
   }

   public void setWorkPackagePointsNumeric(double workPackagePointsNumeric) {
      this.workPackagePointsNumeric = workPackagePointsNumeric;
   }

   public String getWorkPackageType() {
      return workPackageType;
   }

   public void setWorkPackageType(String workPackageType) {
      this.workPackageType = workPackageType;
   }

   public double getWorkPackageBac() {
      return workPackageBac;
   }

   public void setWorkPackageBac(double workPackageBac) {
      this.workPackageBac = workPackageBac;
   }

   public String getWorkPackageIpt() {
      return workPackageIpt;
   }

   public void setWorkPackageIpt(String workPackageIpt) {
      this.workPackageIpt = workPackageIpt;
   }

   public Date getWorkPackageStartDate() {
      return workPackageStartDate;
   }

   public void setWorkPackageStartDate(Date workPackageStartDate) {
      this.workPackageStartDate = workPackageStartDate;
   }

   public Date getWorkPackageEndDate() {
      return workPackageEndDate;
   }

   public void setWorkPackageEndDate(Date workPackageEndDate) {
      this.workPackageEndDate = workPackageEndDate;
   }

   public String getWorkPackageNotes() {
      return workPackageNotes;
   }

   public void setWorkPackageNotes(String workPackageNotes) {
      this.workPackageNotes = workPackageNotes;
   }

   public String getWorkPackageAnnotation() {
      return workPackageAnnotation;
   }

   public void setWorkPackageAnnotation(String workPackageAnnotation) {
      this.workPackageAnnotation = workPackageAnnotation;
   }

}
