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
package org.eclipse.osee.ats.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsFor096Database extends XNavigateItemAction {

   public ConvertAtsFor096Database(XNavigateItem parent) {
      super(parent, "Convert ATS for 0.9.6 Database", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         getName() + "\n\nThis will remove ATS User<->Workflow relations that are no longer used by ATS.\n" + //
         "This can be run mulitple times without error.")) {
         return;
      }
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            XResultData rd = new XResultData(false);
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "remove related assignees");
            for (User user : UserManager.getUsers()) {
               Set<Artifact> smasToRemove = new HashSet<Artifact>();
               for (Artifact art : user.getRelatedArtifacts(CoreRelationTypes.Users_Artifact)) {
                  if (art instanceof AbstractWorkflowArtifact) {
                     if (!AccessControlManager.hasPermission(art, PermissionEnum.FULLACCESS)) {
                        rd.logError(String.format("No permission to remove relations for [%s]", art));
                     } else {
                        user.deleteRelation(CoreRelationTypes.Users_Artifact, art);
                        smasToRemove.add(art);
                     }
                  }
               }
               if (smasToRemove.size() > 0) {
                  rd.log(String.format("Removed [%d] sma relations from [%s]", smasToRemove.size(), user));
                  user.persist(transaction);
               }
            }
            transaction.execute();
            rd.report(getName());
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
