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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Paul K. Waldfogel
 */
public class ShowResourceHistoryHandler extends AbstractSelectionChangedHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection structuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      for (ArtifactChange mySelectedArtifactChange : Handlers.getArtifactChangesFromStructuredSelection(structuredSelection)) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         try {
            Artifact selectedArtifact = mySelectedArtifactChange.getArtifact();

            RevisionHistoryView revisionHistoryView =
                  (RevisionHistoryView) page.showView(
                        RevisionHistoryView.VIEW_ID,
                        selectedArtifact != null ? selectedArtifact.getGuid() : Integer.toString(mySelectedArtifactChange.getArtId()),
                        IWorkbenchPage.VIEW_ACTIVATE);
            revisionHistoryView.explore(selectedArtifact);
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
         }
      }
      return null;
   }
}
