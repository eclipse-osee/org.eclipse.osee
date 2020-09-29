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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class AccessControlHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      Object object = getSelection(selection);
      if (object != null) {
         PolicyDialog pd = PolicyDialog.createPolicyDialog(Displays.getActiveShell(), object);
         pd.open();
      } else {
         AWorkbench.popup("Selection can not be determined");
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      return getSelection(structuredSelection) != null;
   }

   /**
    * @return selected object or null if invalid selection
    */
   private Object getSelection(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

      boolean hasArtifacts = artifacts.size() == 1;
      boolean hasSelection = hasArtifacts || branches.size() == 1;

      Object object = null;
      if (hasSelection) {
         object = hasArtifacts ? artifacts.iterator().next() : branches.iterator().next();
      }
      return object;
   }
}
