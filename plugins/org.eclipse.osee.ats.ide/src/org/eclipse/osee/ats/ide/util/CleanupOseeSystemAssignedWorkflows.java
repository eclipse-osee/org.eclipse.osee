/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class CleanupOseeSystemAssignedWorkflows extends XNavigateItemAction {

   public CleanupOseeSystemAssignedWorkflows() {
      super("Cleanup OSEE System assigned workflows.", PluginUiImage.ADMIN, AtsNavigateViewItems.ATS_ADMIN,
         XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      XResultData results = new XResultData();
      Collection<IAtsWorkItem> workItems = AtsApiService.get().getQueryService().getWorkItemsFromQuery(
         "select art_id from osee_attribute where attr_type_id in (1152921504606847192) and value like '%99999999%'");
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("OSEE System Assignee Cleanup");
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
            changes.removeAssignee(workItem, AtsCoreUsers.SYSTEM_USER);
            results.log("Removed System User from " + workItem.toStringWithId());
            if (workItem.getAssignees().isEmpty()) {
               changes.addAssignee(workItem, AtsCoreUsers.UNASSIGNED_USER);
               results.log("Added UnAssigned to " + workItem.toStringWithId());
            }
         }
      }
      XResultDataUI.report(results, getName());
      changes.execute();
   }

}
