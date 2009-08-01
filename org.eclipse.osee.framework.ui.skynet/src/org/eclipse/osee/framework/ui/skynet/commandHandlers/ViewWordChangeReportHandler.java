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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class ViewWordChangeReportHandler extends AbstractHandler {
   private List<Change> changes;

   @Override
   public Object execute(ExecutionEvent event) {
      ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>(changes.size());
      ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>(changes.size());
      VariableMap variableMap = new VariableMap();
      String fileName = null;

      for (Change artifactChange : changes) {
         try {
            Artifact baseArtifact =
                  (artifactChange.getModificationType() == ModificationType.NEW || artifactChange.getModificationType() == ModificationType.INTRODUCED) ? null : ArtifactQuery.getHistoricalArtifactFromId(
                        artifactChange.getArtifact().getArtId(), artifactChange.getFromTransactionId(), true);

            Artifact newerArtifact =
                  artifactChange.getModificationType() == ModificationType.DELETED ? null : (artifactChange.isHistorical() ? ArtifactQuery.getHistoricalArtifactFromId(
                        artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId(), true) : artifactChange.getArtifact());

            baseArtifacts.add(baseArtifact);
            newerArtifacts.add(newerArtifact);

            if (fileName == null) {
               if (changes.size() == 1) {
                  fileName = baseArtifact != null ? baseArtifact.getSafeName() : newerArtifact.getSafeName();
               } else {
                  fileName =
                        baseArtifact != null ? baseArtifact.getBranch().getShortName() : newerArtifact.getBranch().getShortName();
               }
               variableMap.setValue("fileName", fileName + "_" + (new Date()).toString().replaceAll(":", ";") + ".xml");
            }

         } catch (OseeCoreException ex1) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex1);
         }
      }

      if (newerArtifacts.size() == 0 || (baseArtifacts.size() != newerArtifacts.size())) {
         throw new IllegalArgumentException(
               "base artifacts size: " + baseArtifacts.size() + " must match newer artifacts size: " + newerArtifacts.size() + ".");
      }

      Artifact bArtifact = baseArtifacts.iterator().next();
      Artifact nArtifact = newerArtifacts.iterator().next();

      Artifact instanceOfArtifact = bArtifact != null ? bArtifact : nArtifact;

      //All other artifacts types can be rendered by the wordRenderer so the are displayed in the word change report.
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      try {
         renderer.setOptions(variableMap);
         renderer.compareArtifacts(baseArtifacts, newerArtifacts, new NullProgressMonitor(),
               instanceOfArtifact.getBranch(), PresentationType.DIFF);
      } catch (OseeCoreException e) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, e);
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

            List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
            changes = new ArrayList<Change>(localChanges.size());
            
            for (Change change : localChanges) {
               if(!artifacts.contains(change.getArtifact())){
                  artifacts.add(change.getArtifact());
                  changes.add(change);
               }
            }
            isEnabled = AccessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }

      return isEnabled;
   }
}