/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class CleanupOseeSystemAssignedWorkflows extends XNavigateItemAction {

   public CleanupOseeSystemAssignedWorkflows(XNavigateItem parent) {
      super(parent, "Cleanup OSEE System assigned workflows.", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      XResultData results = new XResultData();
      Collection<IAtsWorkItem> workItems = AtsClientService.get().getQueryService().getWorkItemsFromQuery(
         "select art_id from osee_attribute where attr_type_id in (1152921504606847192) and value like '%99999999%'");
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("OSEE System Assignee Cleanup");
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
            workItem.getStateMgr().removeAssignee(AtsCoreUsers.SYSTEM_USER);
            results.log("Removed System User from " + workItem.toStringWithId());
            changes.add(workItem);
            if (workItem.getStateMgr().getAssignees().isEmpty()) {
               workItem.getStateMgr().addAssignee(AtsCoreUsers.UNASSIGNED_USER);
               results.log("Added UnAssigned to " + workItem.toStringWithId());
            }
         }
      }
      XResultDataUI.report(results, getName());
      changes.execute();
   }

}
