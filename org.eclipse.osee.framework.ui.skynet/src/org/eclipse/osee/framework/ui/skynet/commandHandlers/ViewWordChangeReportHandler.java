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

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class ViewWordChangeReportHandler extends AbstractHandler {
   private Map<Integer, ArtifactChange> artifactChangeMap = new HashMap<Integer, ArtifactChange>();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) {
      ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>(artifactChangeMap.size());
      ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>(artifactChangeMap.size());
      VariableMap variableMap = new VariableMap();
      String fileName = null;

      for (ArtifactChange artifactChange : artifactChangeMap.values()) {
         try {
            Artifact baseArtifact =
                  artifactChange.getModType() == NEW ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                        artifactChange.getArtifact().getArtId(), artifactChange.getBaselineTransactionId());
            Artifact newerArtifact =
                  artifactChange.getModType() == DELETED ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                        artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId());

            baseArtifacts.add(baseArtifact);
            newerArtifacts.add(newerArtifact);

            if (fileName == null) {
               if (artifactChangeMap.values().size() == 1) {
                  fileName = baseArtifact != null ? baseArtifact.getSafeName() : newerArtifact.getSafeName();
               } else {
                  fileName =
                        baseArtifact != null ? baseArtifact.getBranch().getBranchShortName() : newerArtifact.getBranch().getBranchShortName();
               }
               variableMap.setValue("fileName", fileName + "_" + (new Date()).toString().replaceAll(":", ";") + ".xml");
            }

         } catch (OseeCoreException ex1) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex1);
         }
      }

      if (newerArtifacts.size() == 0 || (baseArtifacts.size() != newerArtifacts.size())) {
         throw new IllegalArgumentException(
               "base artifacts size: " + baseArtifacts.size() + " must match newer artifacts size: " + newerArtifacts.size() + ".");
      }

      RendererManager.diffInJob(baseArtifacts, newerArtifacts, variableMap);
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      artifactChangeMap.clear();
      List<Artifact> artifacts = new LinkedList<Artifact>();
      boolean isEnabled = false;

      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            List<ArtifactChange> artifactChanges =
                  Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            for (ArtifactChange artifactChange : artifactChanges) {
               artifacts.add(artifactChange.getArtifact());
               artifactChangeMap.put(artifactChange.getArtifact().getArtId(), artifactChange);
            }
            isEnabled = AccessControlManager.getInstance().checkObjectListPermission(artifacts, PermissionEnum.READ);
         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
      }

      return isEnabled;
   }
}