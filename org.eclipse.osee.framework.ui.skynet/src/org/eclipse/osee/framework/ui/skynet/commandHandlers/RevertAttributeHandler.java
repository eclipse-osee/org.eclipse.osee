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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class RevertAttributeHandler extends AbstractHandler {
   private List<Attribute> attributes;

   public RevertAttributeHandler() {
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      // This is serious stuff, make sure the user understands the impact.
      if (MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(),
            "Confirm Revert of " + attributes.size() + " attributes.",
            "All attribute changes selected will be reverted." + "\n\nTHIS IS IRREVERSIBLE" + "\n\nOSEE must be restarted after all reverting is finished to see the results")) {

         Jobs.startJob(new RevertJob());
      }
      return null;
   }
   private class RevertJob extends Job {

      public RevertJob() {
         super("Reverting " + attributes.size() + " attributes.");
      }

      @Override
      protected IStatus run(final IProgressMonitor monitor) {
         IStatus toReturn;
         try {
            monitor.beginTask("Reverting ...", attributes.size());

            DbTransaction dbTransaction = new DbTransaction() {
               @Override
               protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
                  for (Attribute<?> attribute : attributes) {
                     monitor.setTaskName(attribute.getArtifact().getName() + " : " + attribute.getDisplayableString());
                     ArtifactPersistenceManager.revertAttribute(connection, attribute);
                     monitor.worked(1);
                  }
               }
            };
            dbTransaction.execute();

            toReturn = Status.OK_STATUS;
         } catch (Exception ex) {
            toReturn = new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         } finally {
            monitor.done();
         }
         return toReturn;
      }
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            List<Attribute> changes = Handlers.processSelectionObjects(Attribute.class, structuredSelection);

            if (changes.isEmpty()) {
               return false;
            }

            this.attributes = changes;

            for (Attribute<?> attribute : attributes) {
               if (attributes == null) {

               }
               isEnabled = AccessControlManager.hasPermission(attribute.getArtifact(), PermissionEnum.WRITE);
               if (!isEnabled) {
                  break;
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
      return isEnabled;
   }
}
