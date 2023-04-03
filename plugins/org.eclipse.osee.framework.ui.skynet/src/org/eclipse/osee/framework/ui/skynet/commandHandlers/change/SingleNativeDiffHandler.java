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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;

/**
 * @author Jeff C. Phillips
 */
public class SingleNativeDiffHandler extends CommandHandler {
   private ArrayList<Change> changes;

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      changes = new ArrayList<>(Handlers.getArtifactChangesFromStructuredSelection(structuredSelection));
      if (changes.size() == 1) {
         Artifact sampleArtifact = changes.iterator().next().getChangeArtifact();
         return ServiceUtil.accessControlService().hasArtifactPermission(sampleArtifact, PermissionEnum.READ,
            null).isSuccess();
      }
      return false;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
      String pathPrefix = RenderingUtil.getFileNameSegmentFromFirstTransactionDeltaSupplierAssociatedArtifactName(changes).orElse(Strings.emptyString());
      Map<RendererOption, Object> rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.VIEW, Handlers.getViewId());

      RendererManager.diffInJob(artifactDeltas, pathPrefix, rendererOptions);
      return null;
   }
}