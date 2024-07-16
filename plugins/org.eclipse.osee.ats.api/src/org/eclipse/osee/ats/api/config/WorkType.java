/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * WorkTypes used for ATS Config objects like Team Def an AI
 *
 * @author Donald G. Dunne
 */
public class WorkType extends OseeEnum {

   private static final Long ENUM_ID = 233285922899L;

   public static final WorkType All = new WorkType("All");
   public static final WorkType Applicability = new WorkType("Applicability");
   public static final WorkType ARB = new WorkType("ARB", "Architecture Review Board");
   public static final WorkType ChangeRequest =
      new WorkType("Change Requst", "Top level Change Request to rule them all");
   public static final WorkType Code = new WorkType("Code");
   public static final WorkType Custom = new WorkType("Custom", "Custom Work Type that doesn't match a WorkType enum");
   public static final WorkType Hardware = new WorkType("Hardware");
   public static final WorkType ICDs = new WorkType("ICDs");
   public static final WorkType ImplDetails = new WorkType("ImplDetails", "Implementation Details", "Impl Details");
   public static final WorkType Integration = new WorkType("Integration");
   public static final WorkType IntegrationTest = new WorkType("IntegrationTest", "Integration Test", "");
   public static final WorkType Issues = new WorkType("Issues");
   public static final WorkType Maintenance = new WorkType("Maintenance", true);
   public static final WorkType MIM = new WorkType("MIM", "Message Interface Modeling");
   public static final WorkType MissionCode = new WorkType("MissionCode", "Mission Code", "");
   public static final WorkType None = new WorkType("None");
   public static final WorkType PIDS = new WorkType("PIDS");
   public static final WorkType ProblemReport = new WorkType("Problem Report");
   public static final WorkType Program = new WorkType("Program", "Top Level item of given type for a Program");
   public static final WorkType Requirements = new WorkType("Requirements", true);
   public static final WorkType Software = new WorkType("Software");
   public static final WorkType SoftwareTest = new WorkType("SoftwareTest", "Software Test", "");
   public static final WorkType SSDD = new WorkType("SSDD");
   public static final WorkType SubSystems = new WorkType("SubSystems");
   public static final WorkType Support = new WorkType("Support");
   public static final WorkType SW_Design = new WorkType("SW_Design", "Software Design", "", true);
   public static final WorkType SW_TechAppr = new WorkType("SW_TechAppr", "Software Tech Approach", "", true);
   public static final WorkType Systems = new WorkType("Systems", true);
   public static final WorkType Test = new WorkType("Test");
   public static final WorkType Test_Librarian = new WorkType("Test_Librarian", "Test Librarian", "");
   public static final WorkType Test_Procedures = new WorkType("Test_Procedures", "Test Procedures", "", true);

   private String humanReadableName;
   private String description;

   public WorkType() {
      super(ENUM_ID, "");
   }

   private WorkType(String name) {
      this(name, "");
   }

   private WorkType(String name, boolean createBranch) {
      this(name, name, "", createBranch);
   }

   private WorkType(String name, String description) {
      this(name, name, description);
   }

   private WorkType(String name, String humanReadableName, String description) {
      this(name, humanReadableName, description, false);
   }

   private WorkType(String name, String humanReadableName, String description, boolean createBranch) {
      super(ENUM_ID, name);
      this.humanReadableName = humanReadableName;
      this.description = description;
   }

   public String getHumanReadableName() {
      return humanReadableName;
   }

   public String getDescription() {
      return description;
   }

   public static WorkType valueOfOrNone(String workTypeStr) {
      WorkType workType = (WorkType) None.get(workTypeStr);
      if (workType == null) {
         workType = WorkType.None;
      }
      return workType;
   }

   @JsonIgnore
   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return None;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @JsonIgnore
   public boolean isNotNone() {
      return !this.equals(WorkType.None);
   }

}
