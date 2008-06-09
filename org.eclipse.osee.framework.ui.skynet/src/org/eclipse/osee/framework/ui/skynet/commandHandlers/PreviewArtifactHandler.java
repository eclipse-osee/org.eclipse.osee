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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public abstract class PreviewArtifactHandler extends AbstractHandler {
   private static final RendererManager rendererManager = RendererManager.getInstance();
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private List<Artifact> artifacts;

   /**
    * 
    */
   public PreviewArtifactHandler() {
      super();
      artifacts = new LinkedList<Artifact>();
   }

   /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
       */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         rendererManager.previewInJob(artifacts, getPreviewType());
      }
      return null;
   }

   protected abstract String getPreviewType();

   @Override
   public boolean isEnabled() {

      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      IWorkbenchPartSite partSite = AWorkbench.getActivePage().getActivePart().getSite();
      ISelectionProvider selectionProvider = partSite.getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
         isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);

         //whole word artifacts can only be viewed as a single document
         for (Artifact artifact : artifacts) {
            if (artifact instanceof WordArtifact && !((WordArtifact) artifact).isWholeWordArtifact()) {
               isEnabled &= artifacts.size() == 1;
               break;
            }
         }
      }

      return isEnabled;
   }
}
