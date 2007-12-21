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

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView.RevertDbTx;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class RevertArtifactHandler extends AbstractSelectionHandler {
   private static final RevisionManager myRevisionManager = RevisionManager.getInstance();
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();
   TransactionId baseTransactionId = null;
   TransactionId toTransactionId = null;

   public RevertArtifactHandler() {
      super(new String[] {});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      List<ArtifactChange> mySelectedArtifactChangeList = super.getArtifactChangeList();
      TreeViewer myTreeViewer = super.getChangeTableTreeViewer();
      List<ChangeReportInput> myChangeReportNewInputList = super.getChangeReportInputNewList();
      baseTransactionId = myChangeReportNewInputList.get(0).getBaseTransaction();
      toTransactionId = myChangeReportNewInputList.get(0).getToTransaction();
      System.out.println("baseTransactionId/toTransactionId " + baseTransactionId + "/" + toTransactionId);
      ArtifactChange selectedArtifactChange = mySelectedArtifactChangeList.get(0);
      // This is serious stuff, make sure the user understands the impact.
      if (MessageDialog.openConfirm(
            myTreeViewer.getTree().getShell(),
            "Confirm Revert of " + selectedArtifactChange.getName(),
            "All attribute changes for the artifact and all link changes that involve the artifact on this branch will be reverted." + "\n\nTHIS IS IRREVERSIBLE" + "\n\nOSEE must be restarted after all reverting is finished to see the results")) {

         Jobs.startJob(new RevertJob(selectedArtifactChange.getName(), selectedArtifactChange.getArtId()));
      }
      return null;
   }
   private class RevertJob extends Job {

      private final int artId;

      /**
       * @param name
       * @param artId
       */
      public RevertJob(String name, int artId) {
         super("Reverting Artifact " + name);
         this.artId = artId;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            new RevertDbTx(getName(), artId, monitor, baseTransactionId, toTransactionId).execute();
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, false);
         }
         return Status.OK_STATUS;
      }
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
         boolean writePermission =
               myAccessControlManager.checkObjectPermission(mySelectedArtifact, PermissionEnum.WRITE);
         return mySelectedArtifactList.size() > 0 && writePermission;
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
         return false;
      }
   }
}
