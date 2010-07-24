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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;

/**
 * @author Jeff C. Phillips
 */
public class OpenMassArtifactEditorHandler extends CommandHandler {
   private List<Artifact> artifacts;

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      MassArtifactEditor.editArtifacts("", artifacts);
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException {
      artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      return AccessControlManager.checkObjectListPermission(artifacts, PermissionEnum.WRITE);
   }
}