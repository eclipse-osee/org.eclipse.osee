/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.task.create;

/**
 * @author Donald G. Dunne
 */
public enum ChangeReportTaskMatchType {

   Manual("User Manually added task.- No Change Needed."),
   Match("Computed task needed matches existing task. - No Change Needed."),
   //
   ChangedReportTaskComputedAsNeeded("Change Report task computed as needed. - Awaiting determination if task exists or Create Task"),
   StaticTaskComputedAsNeeded("Statically defined task from StaticTaskDefinition computed as needed. - Awaiting determination if task exists or Create Task"),
   //
   TaskRefAttrMissing("Task Referenced Attr was not found. - Delete Task."),
   TaskRefAttrValidButRefChgArtMissing("Task Referenced Attr found but no matching changed art. - Delete Task");

   private final String description;

   private ChangeReportTaskMatchType(String description) {
      this.description = description;
   }

   public String getDesc() {
      return description;
   }
}
