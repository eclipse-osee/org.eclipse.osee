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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.ArtifactGuis;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class ViewWordChangeReportHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection)  {
      List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(selection);
      if (!localChanges.isEmpty()) {
         Collection<Change> changes = new ArrayList<>(localChanges.size());
         Set<Artifact> artifacts = new HashSet<>();
         for (Change change : localChanges) {
            Artifact artifact = change.getChangeArtifact();
            if (!artifacts.contains(artifact)) {
               artifacts.add(artifact);
               changes.add(change);
            }
         }
         Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);

         if (ArtifactGuis.checkDeletedOnParent(artifacts)) {
            String pathPrefix = RenderingUtil.getAssociatedArtifactName(localChanges);
            WordTemplateRenderer preferredRenderer = new WordTemplateRenderer();
            preferredRenderer.updateOption(RendererOption.VIEW, Handlers.getViewId());
            RendererManager.diffInJobWithPreferedRenderer(artifactDeltas, pathPrefix, preferredRenderer);
         }
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
      return !localChanges.isEmpty();
   }

}