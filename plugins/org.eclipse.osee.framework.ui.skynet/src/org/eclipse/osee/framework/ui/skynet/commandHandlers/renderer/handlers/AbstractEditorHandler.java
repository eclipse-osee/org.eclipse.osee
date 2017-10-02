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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.List;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * This abstract class provides the basic functionality for opening renderer editors.
 *
 * @author Jeff C. Phillips
 */
public abstract class AbstractEditorHandler extends CommandHandler {
   protected List<Artifact> artifacts;
   ISelectionProvider selectionProvider;

   protected PermissionEnum getPermissionLevel() {
      return PermissionEnum.READ;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection)  {
      artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      if (!artifacts.isEmpty()) {
         return AccessControlManager.hasPermission(artifacts, getPermissionLevel());
      }
      return false;
   }
}