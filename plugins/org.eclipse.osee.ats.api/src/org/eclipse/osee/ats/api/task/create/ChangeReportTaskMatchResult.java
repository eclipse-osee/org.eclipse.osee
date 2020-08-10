/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskMatchResult extends OseeEnum {

   private static final Long ENUM_ID = 38283465L;

   public static final ChangeReportTaskMatchResult None = new ChangeReportTaskMatchResult("None");
   public static final ChangeReportTaskMatchResult TaskExistsNeedsDereference =
      new ChangeReportTaskMatchResult("Task %s Exists - Needs DeReference\\n");
   public static final ChangeReportTaskMatchResult TaskExistsNoChangeNeeded =
      new ChangeReportTaskMatchResult("Task %s Exists - No Change Needed\n");
   public static final ChangeReportTaskMatchResult CreateNewChgRptTask =
      new ChangeReportTaskMatchResult("Create New Chg Rpt Task [%s]\n");
   public static final ChangeReportTaskMatchResult CreateNewStaticTask =
      new ChangeReportTaskMatchResult("Create New Static Task [%s]\n");
   public static final ChangeReportTaskMatchResult NoMatchArtTaskCanBeDeleted =
      new ChangeReportTaskMatchResult("No Matching Artifact for Task %s; De-referenced task can be deleted.\n");
   public static final ChangeReportTaskMatchResult UnhandledMatchType =
      new ChangeReportTaskMatchResult("Unhandled Match Type [%s]\n");

   public ChangeReportTaskMatchResult() {
      super(ENUM_ID, -1L, "");
   }

   public ChangeReportTaskMatchResult(String name) {
      super(ENUM_ID, name);
   }

   public ChangeReportTaskMatchResult(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public ChangeReportTaskMatchResult getDefault() {
      return None;
   }

   public String getDisplayName() {
      String name = getName();
      name = name.replaceAll("\\[%s\\]", "");
      name = name.replaceAll("%s", "");
      return name;
   }

}
