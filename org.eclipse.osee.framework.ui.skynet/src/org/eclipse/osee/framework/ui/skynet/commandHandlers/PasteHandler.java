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

import java.util.Iterator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPasteOperation;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactClipboard;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Theron Virgin
 */
public class PasteHandler extends AbstractHandler {
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (HandlerUtil.getActivePartChecked(event) instanceof ViewPart) {
         ViewPart view = (ViewPart) HandlerUtil.getActivePartChecked(event);
         IWorkbenchPartSite myIWorkbenchPartSite = view.getSite();
         ISelectionProvider selectionProvider = myIWorkbenchPartSite.getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();

            ArtifactClipboard clipboard = new ArtifactClipboard(view.getSite().getId());
            Iterator<?> iterator = selection.iterator();
            Object selectionObject = null;

            while (iterator.hasNext()) {
               Object object = iterator.next();

               if (object instanceof IAdaptable) {
                  selectionObject = ((IAdaptable) object).getAdapter(Branch.class);

                  if (selectionObject == null) {
                     selectionObject = ((IAdaptable) object).getAdapter(Artifact.class);
                  }
               } else if (object instanceof Match) {
                  selectionObject = ((Match) object).getElement();
               }

               if (selectionObject instanceof Artifact) {
                  ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();
                  Operations.executeAsJob(new ArtifactPasteOperation(config, (Artifact) selectionObject,
                        clipboard.getCopiedContents()), true);
               }
            }

         }
      }
      return null;
   }

   @Override
   public boolean isHandled() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}
