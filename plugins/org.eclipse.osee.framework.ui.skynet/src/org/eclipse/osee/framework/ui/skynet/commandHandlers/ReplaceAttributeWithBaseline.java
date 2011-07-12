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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class ReplaceAttributeWithBaseline extends AbstractHandler {
   @SuppressWarnings("rawtypes")
   private List<Attribute> attributes;

   @Override
   public Object execute(ExecutionEvent event) {
      if (MessageDialog.openConfirm(Displays.getActiveShell(),
         "Confirm Replace with baseline version of " + attributes.size() + " attributes.",
         "All attribute changes selected will be replaced with thier baseline version.")) {

         Set<Artifact> artifacts = new HashSet<Artifact>();
         for (Attribute<?> attribute : attributes) {
            try {
               TransactionRecord baselineTransactionRecord = attribute.getArtifact().getBranch().getBaseTransaction();
               for (Change change : ChangeManager.getChangesPerArtifact(attribute.getArtifact(),
                  new NullProgressMonitor())) {
                  if (change.getTxDelta().getEndTx().getId() == baselineTransactionRecord.getId()) {
                     if (change.getItemKind().equals("Attribute") && change.getItemId() == attribute.getId()) {
                        attribute.replaceWithVersion((int) change.getGamma());
                        artifacts.add(attribute.getArtifact());
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
            }
         }
         persistAndReloadArtifacts(artifacts);
      }
      return null;
   }

   private void persistAndReloadArtifacts(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            artifact.persist("Replace attribute with baseline version");
            artifact.reloadAttributesAndRelations();
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
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
            this.attributes = Handlers.processSelectionObjects(Attribute.class, structuredSelection);

            if (attributes.isEmpty()) {
               return false;
            }

            for (Attribute<?> attribute : attributes) {
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
