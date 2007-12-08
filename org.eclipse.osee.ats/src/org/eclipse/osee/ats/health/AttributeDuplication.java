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
package org.eclipse.osee.ats.health;

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class AttributeDuplication extends XNavigateItemAction {

   /**
    * @param parent
    */
   public AttributeDuplication(XNavigateItem parent) {
      super(parent, "Report Duplication Attribute");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new LoadArtifactsJob(getName()), true);
   }

   public class LoadArtifactsJob extends Job {

      private final String name;

      public LoadArtifactsJob(String name) {
         super(name);
         this.name = name;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         final XResultData rd = new XResultData(AtsPlugin.getLogger());
         for (String type : StateMachineArtifact.getAllSMATypeNames()) {
            monitor.subTask("Loading " + type + "...");
            try {
               // just need to load the aritfacts for them to exception out
               ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName(type,
                     BranchPersistenceManager.getInstance().getAtsBranch());
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, false);
               rd.logError(ex.getLocalizedMessage());
            }
         }
         rd.report(name);
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
