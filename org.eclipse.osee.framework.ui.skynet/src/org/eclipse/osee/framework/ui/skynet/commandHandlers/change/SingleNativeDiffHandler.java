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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class SingleNativeDiffHandler extends CommandHandler {
   private ArrayList<Change> changes;

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      boolean enabled = false;

      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();

            changes =
                  new ArrayList<Change>(Handlers.getArtifactChangesFromStructuredSelection(structuredSelection));

            enabled =
                  changes.size() == 1 && AccessControlManager.hasPermission(
                        changes.get(0).getArtifact(), PermissionEnum.READ);
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return enabled;
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      Change change = changes.iterator().next();
      Set<Artifact> artifacts = new HashSet<Artifact>();
      try {
         Artifact baseArtifact =
               (change.getModificationType() == NEW || change.getModificationType() == ModificationType.INTRODUCED) ? null : ArtifactQuery.getHistoricalArtifactFromId(
                     change.getArtifact().getArtId(), change.getFromTransactionId(), true);
         artifacts.addAll(checkForTrackedChangesOn(baseArtifact));
         Artifact newerArtifact =
               change.getModificationType() == DELETED ? null : (change.isHistorical() ? ArtifactQuery.getHistoricalArtifactFromId(
                     change.getArtifact().getArtId(), change.getToTransactionId(), true) : change.getArtifact());
         artifacts.addAll(checkForTrackedChangesOn(newerArtifact));
         if (artifacts.isEmpty()) {
            VariableMap variableMap = new VariableMap();
            String fileName = baseArtifact != null ? baseArtifact.getSafeName() : newerArtifact.getSafeName();
            variableMap.setValue("fileName", fileName + "_" + (new Date()).toString().replaceAll(":", ";") + ".xml");

            RendererManager.diff(baseArtifact, newerArtifact, true);
         } else {
            WordUiUtil.displayWarningMessageDialog("Diff Artifacts Warning",
                  "Detected tracked changes for some artifacts. Please refer to the results HTML report.");
            WordUiUtil.displayTrackedChangesOnArtifacts(artifacts);
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   private Set<Artifact> checkForTrackedChangesOn(Artifact artifact) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.REMOVE_TRACKED_CHANGES)) {
         if (artifact != null) {
            String value = artifact.getSoleAttributeValueAsString(WordAttribute.WORD_TEMPLATE_CONTENT, "");
            // check for track changes
            if (WordAnnotationHandler.containsWordAnnotations(value)) {
               // capture those artifacts that have tracked changes on 
               artifacts.add(artifact);
            }
         }
      }
      return artifacts;
   }
}