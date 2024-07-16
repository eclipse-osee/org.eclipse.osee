/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;

/**
 * @author Shawn F. Cook
 */
public class GenericDiffHandler extends CommandHandler {

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
      return !localChanges.isEmpty();
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
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

         //@formatter:off
         RenderingUtil
            .getFileNameSegmentFromFirstTransactionDeltaSupplierAssociatedArtifactName( localChanges )
            .ifPresentOrElse
               (
                  ( pathPrefix ) -> RendererManager.diffInJob( artifactDeltas, pathPrefix           ),
                  ( )            -> RendererManager.diffInJob( artifactDeltas, Strings.emptyString() )
               );
         //@formatter:on
      }
      return null;
   }

}
