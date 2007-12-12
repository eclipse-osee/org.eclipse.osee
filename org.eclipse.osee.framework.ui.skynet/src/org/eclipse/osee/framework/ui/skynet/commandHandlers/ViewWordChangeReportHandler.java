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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class ViewWordChangeReportHandler extends AbstractSelectionHandler {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   // TreeViewer myChangeTableTreeViewer;
   List<ArtifactChange> mySelectedArtifactChangeList = null;

   public ViewWordChangeReportHandler() {
      super(new String[] {"Branch", "ChangeTableTreeViewer", "ArtifactID"});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      mySelectedArtifactChangeList = super.getArtifactChangeList();
      // mySelectedBranchList = super.getBranchList();
      // IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();
      // Iterator<?> iterator = selection.iterator();
      // int listSize = selection.size();
      ArtifactChange selectedItem = null;

      ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>(mySelectedArtifactChangeList.size());
      ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>(mySelectedArtifactChangeList.size());
      // while (iterator.hasNext()) {
      for (int i = 0; i < mySelectedArtifactChangeList.size(); i++) {
         selectedItem = mySelectedArtifactChangeList.get(i);
         // }
         // for (ArtifactChange selectedItem:mySelectedArtifactChangeList) {
         // selectedItem = (ArtifactChange) ((ITreeNode) iterator.next()).getBackingData();

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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractArtifactSelectionHandler#permissionLevel()
    */
   @Override
   protected PermissionEnum permissionLevel() {
      return PermissionEnum.READ;
   }

}
