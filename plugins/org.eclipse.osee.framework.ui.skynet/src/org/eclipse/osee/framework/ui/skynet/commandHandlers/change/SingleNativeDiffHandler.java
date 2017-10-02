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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;

/**
 * @author Jeff C. Phillips
 */
public class SingleNativeDiffHandler extends CommandHandler {
   private ArrayList<Change> changes;

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection)  {
      changes = new ArrayList<>(Handlers.getArtifactChangesFromStructuredSelection(structuredSelection));
      if (changes.size() == 1) {
         Artifact sampleArtifact = changes.iterator().next().getChangeArtifact();
         return AccessControlManager.hasPermission(sampleArtifact, PermissionEnum.READ);
      }
      return false;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection)  {
      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
      String pathPrefix = RenderingUtil.getAssociatedArtifactName(changes);
      Map<RendererOption, Object> rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.VIEW, Handlers.getViewId());

      RendererManager.diffInJob(artifactDeltas, pathPrefix, rendererOptions);
      return null;
   }
}