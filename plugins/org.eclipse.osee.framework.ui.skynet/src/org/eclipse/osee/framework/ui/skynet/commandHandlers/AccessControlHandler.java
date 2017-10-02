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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class AccessControlHandler extends CommandHandler {
   private Object object;

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      PolicyDialog pd = new PolicyDialog(Displays.getActiveShell(), object);
      pd.open();
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

      boolean hasArtifacts = artifacts.size() == 1;
      boolean enabled = hasArtifacts || branches.size() == 1;

      if (enabled) {
         object = hasArtifacts ? artifacts.iterator().next() : branches.iterator().next();
         enabled &=
            AccessControlManager.isOseeAdmin() || AccessControlManager.hasPermission(object, PermissionEnum.FULLACCESS);
      }

      return enabled;
   }
}