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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.List;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

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
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      if (!artifacts.isEmpty()) {
         return ServiceUtil.accessControlService().hasArtifactPermission(artifacts, getPermissionLevel(),
            null).isSuccess();
      }
      return false;
   }
}