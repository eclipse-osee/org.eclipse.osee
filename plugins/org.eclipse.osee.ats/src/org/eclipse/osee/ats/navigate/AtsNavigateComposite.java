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
package org.eclipse.osee.ats.navigate;

import java.util.logging.Level;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateComposite extends XNavigateComposite {

   public AtsNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style, String filterText) {
      super(navigateViewItems, parent, style, filterText);
      Result result = DbConnectionUtility.areOSEEServicesAvailable();
      if (result.isFalse()) {
         new Label(parent, SWT.NONE).setText(result.getText());
         return;
      }
   }

   @Override
   protected void handleDoubleClick() throws OseeCoreException {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) {
         return;
      }
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      handleDoubleClick(item);
   }

   @Override
   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      super.disposeTooltip();
      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      }
      ActivityLogEndpoint activityEp = AtsClientService.get().getOseeClient().getActivityLogEndpoint();
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
         throw new OseeWrappedException(ex);
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
   public void refresh() {
      super.refresh();
      if (AtsClientService.get().getUserService().isAtsAdmin()) {
         for (XNavigateItem item : getInput()) {
            if (item.getName().equals("Admin")) {
               filteredTree.getViewer().expandToLevel(item, 1);
            }
         }
      }
      layout(true);
   }
}
