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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.TagArtifactsJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class TagArtifactsHandler extends AbstractSelectionChangedHandler {
   private List<Artifact> artifacts;

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      Jobs.startJob(new TagArtifactsJob(artifacts));
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

            isEnabled = artifacts.size() > 0;
         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
         return false;
      }
      return isEnabled;
   }
}
