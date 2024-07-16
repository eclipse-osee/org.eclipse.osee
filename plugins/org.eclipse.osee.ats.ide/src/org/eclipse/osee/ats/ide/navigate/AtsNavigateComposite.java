/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.NavigateItemCollector;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateComposite extends XNavigateComposite {

   public AtsNavigateComposite(NavigateItemCollector navigateItemCollector, Composite parent, int style, String filterText) {
      super(navigateItemCollector, parent, style, filterText);
      Result result = DbConnectionUtility.areOSEEServicesAvailable();
      if (result.isFalse()) {
         new Label(parent, SWT.NONE).setText(result.getText());
         return;
      }
   }

   @Override
   protected void handleDoubleClick() {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) {
         return;
      }
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      handleDoubleClick(item);
   }

   @Override
   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) {
      super.disposeTooltip();
      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      }
      ActivityLogEndpoint activityEp = AtsApiService.get().getOseeClient().getActivityLogEndpoint();
      ActivityEntryId activityId = null;
      try {
         activityId =
            activityEp.createEntry(CoreActivityTypes.XNAVIGATEITEM, 0L, ActivityLog.INITIAL_STATUS, item.getName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Eror creating activity log entry", ex);
      }
      try {
         item.run(tableLoadOptions);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
      try {
         if (activityId != null) {
            activityEp.updateEntry(activityId.getId(), ActivityLog.COMPLETE_STATUS);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Eror updating activity log entry", ex);
      }
   }

   @Override
   public Collection<? extends ArtifactId> getCurrUserUserGroups() {
      return ServiceUtil.getOseeClient().userService().getMyUserGroups();
   }

}
