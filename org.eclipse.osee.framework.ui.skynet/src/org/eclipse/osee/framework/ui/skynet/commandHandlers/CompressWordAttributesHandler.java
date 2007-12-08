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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.util.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class CompressWordAttributesHandler extends AbstractSelectionHandler {
   List<Integer> mySelectedArtifactIDList;
   List<Branch> mySelectedBranchList;
   TreeViewer myChangeTableTreeViewer;
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();

   public CompressWordAttributesHandler() {
      super(new String[] {"ChangeTableTreeViewer"});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      mySelectedArtifactIDList = super.getArtifactIDList();
      mySelectedBranchList = super.getBranchList();
      myChangeTableTreeViewer = super.getChangeTableTreeViewer();
      Jobs.startJob(new Job("Compress Word Attributes") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final int total = mySelectedArtifactIDList.size();
               int count = 0;

               monitor.beginTask("Analyzing attributes", total);

               for (Integer mySelectedArtifactID : mySelectedArtifactIDList) {
                  if (WordUtil.revertNonusefulWordChanges(mySelectedArtifactID, mySelectedBranchList.get(0),
                        "osee_compression_gammas")) count++;
                  monitor.worked(1);
                  if (monitor.isCanceled()) {
                     monitor.done();
                     return Status.CANCEL_STATUS;
                  }
               }

               final int finalCount = count;
               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     MessageDialog.openInformation(myChangeTableTreeViewer.getControl().getShell(), "Compression Data",
                           finalCount + " of the " + total + " artifacts need compression");
                  }
               });

               monitor.done();
               return Status.OK_STATUS;
            } catch (SQLException ex) {
               return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
            }
         }

      });
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractArtifactSelectionHandler#permissionLevel()
    */
   @Override
   protected PermissionEnum permissionLevel() {
      return PermissionEnum.READ;
   }

   @Override
   public boolean isEnabled() {
      try {
         List<Artifact> mySelectedArtifactList = super.getArtifactList();
         Artifact mySelectedArtifact = mySelectedArtifactList.get(0);
         boolean writePermission = myAccessControlManager.checkObjectPermission(mySelectedArtifact,
               PermissionEnum.WRITE);
         return writePermission && OseeProperties.getInstance().isDeveloper();
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
         return false;
      }
   }

}
