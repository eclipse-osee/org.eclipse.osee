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
package org.eclipse.osee.ats.rest.internal.build.report.model;

/**
 * @author Megumi Telles
 */
public class AtsWorkflowData {

   private String workflowName;
   private String workflowId;
   private String workflowProgramName;
   private String workflowProgramId;
   private String workflowBuildName;
   private String workflowBuildId;
   private String State;
   private String workflowType;
   private String workflowAtsId;
   private String workflowPcrId;
   private String workflowDescription;
   private String workflowChangeType;
   private String workflowSubSystem;
   private String workflowEnhancement;
   private String workflowBuildIncrement;
   private String workflowChangeReportPath;

   public AtsWorkflowData() {
   }

   public String getWorkflowName() {
      return workflowName;
   }

   public void setWorkflowName(String workflowName) {
      this.workflowName = workflowName;
   }

   public String getWorkflowId() {
      return workflowId;
   }

   public void setWorkflowId(String workflowId) {
      this.workflowId = workflowId;
   }

   public String getWorkflowProgramName() {
      return workflowProgramName;
   }

   public void setWorkflowProgramName(String workflowProgramName) {
      this.workflowProgramName = workflowProgramName;
   }

   public String getWorkflowProgramId() {
      return workflowProgramId;
   }

   public void setWorkflowProgramId(String workflowProgramId) {
      this.workflowProgramId = workflowProgramId;
   }

   public String getWorkflowBuildName() {
      return workflowBuildName;
   }

   public void setWorkflowBuildName(String workflowBuildName) {
      this.workflowBuildName = workflowBuildName;
   }

   public String getWorkflowBuildId() {
      return workflowBuildId;
   }

   public void setWorkflowBuildId(String workflowBuildId) {
      this.workflowBuildId = workflowBuildId;
   }

   public String getState() {
      return State;
   }

   public void setState(String state) {
      State = state;
   }

   public String getWorkflowType() {
      return workflowType;
   }

   public void setWorkflowType(String workflowType) {
      this.workflowType = workflowType;
   }

   public String getWorkflowAtsId() {
      return workflowAtsId;
   }

   public void setWorkflowAtsId(String workflowAtsId) {
      this.workflowAtsId = workflowAtsId;
   }

   public String getWorkflowPcrId() {
      return workflowPcrId;
   }

   public void setWorkflowPcrId(String workflowPcrId) {
      this.workflowPcrId = workflowPcrId;
   }

   public String getWorkflowDescription() {
      return workflowDescription;
   }

   public void setWorkflowDescription(String workflowDescription) {
      this.workflowDescription = workflowDescription;
   }

   public String getWorkflowChangeType() {
      return workflowChangeType;
   }

   public void setWorkflowChangeType(String workflowChangeType) {
      this.workflowChangeType = workflowChangeType;
   }

   public String getWorkflowSubSystem() {
      return workflowSubSystem;
   }

   public void setWorkflowSubSystem(String workflowSubSystem) {
      this.workflowSubSystem = workflowSubSystem;
   }

   public String getWorkflowEnhancement() {
      return workflowEnhancement;
   }

   public void setWorkflowEnhancement(String workflowEnhancement) {
      this.workflowEnhancement = workflowEnhancement;
   }

   public String getWorkflowBuildIncrement() {
      return workflowBuildIncrement;
   }

   public void setWorkflowBuildIncrement(String workflowBuildIncrement) {
      this.workflowBuildIncrement = workflowBuildIncrement;
   }

   public String getWorkflowChangeReportPath() {
      return workflowChangeReportPath;
   }

   public void setWorkflowChangeReportPath(String workflowChangeReportPath) {
      this.workflowChangeReportPath = workflowChangeReportPath;
   }

}
