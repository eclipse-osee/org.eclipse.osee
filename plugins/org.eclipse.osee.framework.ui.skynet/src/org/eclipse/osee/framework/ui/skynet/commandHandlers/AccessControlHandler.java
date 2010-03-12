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
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.IAccessControllable;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class AccessControlHandler extends CommandHandler {
   private IAccessControllable object;

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      try {
         PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), object);
         pd.open();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      boolean enabled = false;

      if (AWorkbench.getActivePage() == null) return false;
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(selection);

      boolean hasArtifacts = artifacts.size() == 1;
      enabled = hasArtifacts || (branches.size() == 1);

      if (enabled) {
         object = hasArtifacts ? artifacts.iterator().next() : branches.iterator().next();
         enabled &=
               (AccessControlManager.isOseeAdmin() || AccessControlManager.hasPermission(object,
                     PermissionEnum.FULLACCESS));
      }

      return enabled;
   }
}