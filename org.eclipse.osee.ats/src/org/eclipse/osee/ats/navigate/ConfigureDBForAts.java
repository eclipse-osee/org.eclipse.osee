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

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.ImportWorkflowAction;
import org.eclipse.osee.ats.config.LoadAIsAndTeamsAction;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ConfigureDBForAts extends XNavigateItemAction {

   private final String pluginId;

   public ConfigureDBForAts(XNavigateItem parent, String pluginId) {
      super(parent, "Admin - Configure DB For ATS");
      this.pluginId = pluginId;
   }

   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Configure DB for ATS",
            "Configure DB for ATS " + pluginId)) return;
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Configure DB for ATS",
            "This will break things really bad if ATS is alread configured for this item.  Are you sure?")) return;

      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            monitor.subTask("Loading TeamWorkflows for " + pluginId);
            (new ImportWorkflowAction(false, pluginId)).run();
            monitor.subTask("Loading Actionable Items and Teams for " + pluginId);
            (new LoadAIsAndTeamsAction(false, pluginId)).run();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
