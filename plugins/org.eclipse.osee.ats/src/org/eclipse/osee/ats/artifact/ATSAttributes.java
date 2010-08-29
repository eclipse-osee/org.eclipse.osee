/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.artifact;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class ATSAttributes {
   private static final Map<String, ATSAttributes> WORK_ITEM_ID_TO_ATS_ATTRIBUTE_MAP =
      new HashMap<String, ATSAttributes>();

   // @formatter:off
   public static final ATSAttributes WORKING_BRANCH_WIDGET = new ATSAttributes("Working Branch");
   public static final ATSAttributes VALIDATE_REQ_CHANGES_WIDGET = new ATSAttributes("Validate Requirement Changes");
   public static final ATSAttributes CREATE_CODE_TEST_TASKS_OFF_REQUIREMENTS = new ATSAttributes("Create Code/Test Tasks");
   public static final ATSAttributes CHECK_SIGNALS_VIA_CDB_WIDGET = new ATSAttributes("Check Signals Via CDB");
   public static final ATSAttributes SHOW_CDB_DIFF_REPORT_WIDGET = new ATSAttributes("Show CDB Differences Report");
   public static final ATSAttributes ASSIGNEE_ATTRIBUTE = new ATSAttributes("Assignees", "Users currently assigned to do work.");
   public static final ATSAttributes COMMIT_MANAGER_WIDGET = new ATSAttributes("Commit Manager", "Commit branches to parent and parallel branches.");
   // @formatter:on

   private final String displayName;
   private final String description;
   private final String workItemId;

   protected ATSAttributes(String displayName, String workItemId, String description) {
      this.displayName = displayName;
      this.workItemId = workItemId;
      this.description = description;
      WORK_ITEM_ID_TO_ATS_ATTRIBUTE_MAP.put(workItemId, this);
   }

   private ATSAttributes(String displayName) {
      this(displayName, "");
   }

   private ATSAttributes(String displayName, String description) {
      this(displayName, "ats." + displayName, description);
   }

   public static ATSAttributes getAtsAttributeByStoreName(String workItemId) {
      return WORK_ITEM_ID_TO_ATS_ATTRIBUTE_MAP.get(workItemId);
   }

   @Override
   public final boolean equals(Object obj) {
      return super.equals(obj);
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getWorkItemId() {
      return workItemId;
   }

   public String getDescription() {
      return description;
   }
}