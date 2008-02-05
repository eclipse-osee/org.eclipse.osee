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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 */
public class ViewWordChangeReportHandler extends AbstractSelectionChangedHandler {
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private List<ArtifactChange> mySelectedArtifactChangeList;

   public ViewWordChangeReportHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>(mySelectedArtifactChangeList.size());
      ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>(mySelectedArtifactChangeList.size());
      ArtifactChange selectedItem = null;

      for (int i = 0; i < mySelectedArtifactChangeList.size(); i++) {
         selectedItem = mySelectedArtifactChangeList.get(i);

         try {
            Artifact baseArtifact =
                  selectedItem.getModType() == NEW ? null : artifactManager.getArtifactFromId(
                        selectedItem.getArtifact().getArtId(), selectedItem.getBaselineTransactionId());
            Artifact newerArtifact =
                  selectedItem.getModType() == DELETE ? null : artifactManager.getArtifactFromId(
                        selectedItem.getArtifact().getArtId(), selectedItem.getToTransactionId());

            baseArtifacts.add(baseArtifact);
            newerArtifacts.add(newerArtifact);
         } catch (Exception e1) {
            OSEELog.logException(getClass(), e1, true);
         }
      }

      // This is a HACK ... I needed a way to ask the renderManager for the wordRender. There
      // should exist such a method on the manager
      WordRenderer renderer =
            (WordRenderer) RendererManager.getInstance().getRendererById("org.eclipse.osee.framework.ui.skynet.word");

      try {
         renderer.compareArtifacts(baseArtifacts, newerArtifacts, DIFF_ARTIFACT, null,
               selectedItem.getBaselineTransactionId().getBranch());
      } catch (CoreException ex) {
         OSEELog.logException(getClass(), ex, true);
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      List<Artifact> artifacts = new LinkedList<Artifact>();
      boolean isEnabled = false;

      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            mySelectedArtifactChangeList = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            for (ArtifactChange artifactChange : mySelectedArtifactChangeList) {
               artifacts.add(artifactChange.getArtifact());
            }
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
         }
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
      }

      return isEnabled;
   }
}
