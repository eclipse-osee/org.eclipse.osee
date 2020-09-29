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

package org.eclipse.osee.framework.ui.skynet.update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IntroduceArtifactOperation;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public class InterArtifactExplorerDropHandlerOperation extends AbstractOperation {

   private static final String ACCESS_ERROR_MSG_TITLE = "Drag and Drop Error";
   private static final String UPDATE_FROM_PARENT_ERROR_MSG =
      "Attempting to update child branch from parent branch. Use 'Update Branch' instead.";
   private static final String ACCESS_ERROR_MSG =
      "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.";
   private final Artifact destinationParentArtifact;
   private final Collection<Artifact> sourceArtifacts;
   private final boolean prompt;

   public InterArtifactExplorerDropHandlerOperation(Artifact destinationParentArtifact, Artifact[] sourceArtifacts, boolean prompt) {
      super("Introduce Artifact(s)", Activator.PLUGIN_ID);
      this.destinationParentArtifact = destinationParentArtifact;
      this.prompt = prompt;
      this.sourceArtifacts = new ArrayList<>(Arrays.asList(sourceArtifacts));
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      if (destinationParentArtifact == null || sourceArtifacts == null || sourceArtifacts.isEmpty()) {
         throw new OseeArgumentException("Invalid arguments");
      }
      BranchToken sourceBranch = sourceArtifacts.iterator().next().getBranchToken();
      final BranchToken destinationBranch = destinationParentArtifact.getBranchToken();

      if (isUpdateFromParent(sourceBranch, destinationBranch)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE, UPDATE_FROM_PARENT_ERROR_MSG);
            }
         });
      }
      XResultData rd = isAccessAllowed(sourceBranch, destinationBranch);
      if (rd.isSuccess()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  if (prompt) {
                     CheckBoxDialog confirm = new CheckBoxDialog(Displays.getActiveShell(), "Introduce Artifact(s)",
                        null, "Introduce " + sourceArtifacts.size() + " Artifact(s)", "Include Children",
                        MessageDialog.QUESTION, 0);
                     if (confirm.open() == 0) {
                        if (confirm.isChecked()) {
                           sourceArtifacts.addAll(getRecurseChildren());
                        }
                     }
                  }
                  SkynetTransaction transaction = TransactionManager.createTransaction(destinationBranch,
                     String.format("Introduce %d artifact(s)", sourceArtifacts.size()));
                  List<Artifact> destinationArtifacts =
                     new IntroduceArtifactOperation(destinationBranch).introduce(sourceArtifacts);
                  for (Artifact destinationArtifact : destinationArtifacts) {
                     transaction.addArtifact(destinationArtifact);
                  }
                  transaction.execute();
               } catch (OseeCoreException ex) {
                  OseeLog.log(InterArtifactExplorerDropHandlerOperation.class, Level.WARNING, ex.getLocalizedMessage());
               }
            }
         });
      } else {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               XResultDataDialog.open(rd, ACCESS_ERROR_MSG_TITLE, ACCESS_ERROR_MSG);
            }
         });
      }
      monitor.done();
   }

   private XResultData isAccessAllowed(BranchToken sourceBranch, BranchToken destinationBranch) {
      XResultData rd = OseeApiService.get().getAccessControlService().hasBranchPermission(destinationBranch,
         PermissionEnum.WRITE, AccessControlArtifactUtil.getXResultAccessHeader("Drag/Drop Artifact", sourceBranch));
      if (rd.isErrors()) {
         return rd;
      }
      rd = OseeApiService.get().getAccessControlService().hasBranchPermission(sourceBranch, PermissionEnum.READ,
         AccessControlArtifactUtil.getXResultAccessHeader("Drag/Drop Artifact", sourceBranch));
      return rd;
   }

   private boolean isUpdateFromParent(BranchId sourceBranch, BranchId destinationBranch) {
      return sourceBranch.equals(BranchManager.getParentBranch(destinationBranch));
   }

   private Collection<Artifact> getRecurseChildren() {
      Collection<Artifact> allArtifacts = new ArrayList<>();
      for (Artifact art : sourceArtifacts) {
         allArtifacts.addAll(art.getDescendants(DeletionFlag.INCLUDE_DELETED));
      }
      return allArtifacts;
   }
}
