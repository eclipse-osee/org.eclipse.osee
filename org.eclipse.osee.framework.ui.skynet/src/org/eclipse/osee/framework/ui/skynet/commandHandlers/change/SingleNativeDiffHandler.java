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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.preferences.DiffPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class SingleNativeDiffHandler extends CommandHandler {
   private ArrayList<ArtifactChange> artifactChanges;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.util.CommandHandler#isEnabledWithException()
    */
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

            artifactChanges =
                  new ArrayList<ArtifactChange>(Handlers.getArtifactChangesFromStructuredSelection(structuredSelection));

            enabled =
                  artifactChanges.size() == 1 && AccessControlManager.checkObjectPermission(
                        artifactChanges.get(0).getArtifact(), PermissionEnum.READ);
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return enabled;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ArtifactChange artifactChange = artifactChanges.iterator().next();
      Set<Artifact> artifacts = new HashSet<Artifact>();
      try {
         Artifact baseArtifact =
               (artifactChange.getModType() == NEW || artifactChange.getModType() == ModificationType.INTRODUCED) ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                     artifactChange.getArtifact().getArtId(), artifactChange.getBaselineTransactionId());
         artifacts.addAll(checkForTrackedChangesOn(baseArtifact));
         Artifact newerArtifact =
               artifactChange.getModType() == DELETED ? null : (artifactChange.isHistorical() ? ArtifactPersistenceManager.getInstance().getArtifactFromId(
                     artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId()) : artifactChange.getArtifact());
         artifacts.addAll(checkForTrackedChangesOn(newerArtifact));
         if (artifacts.isEmpty()) {
            VariableMap variableMap = new VariableMap();
            String fileName = baseArtifact != null ? baseArtifact.getSafeName() : newerArtifact.getSafeName();
            variableMap.setValue("fileName", fileName + "_" + (new Date()).toString().replaceAll(":", ";") + ".xml");

            RendererManager.diff(baseArtifact, newerArtifact, true);
         } else {
            displayWarningMessageDialog();
            displayTrackedChangesOnArtifacts(artifacts);
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   private Set<Artifact> checkForTrackedChangesOn(Artifact artifact) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      if (!StaticIdManager.hasValue(UserManager.getUser(), DiffPreferencePage.REMOVE_TRACKED_CHANGES)) {
         Attribute attribute;
         if (artifact != null) {
            attribute = artifact.getSoleAttribute(WordAttribute.WORD_TEMPLATE_CONTENT);
            if (attribute != null) {
               String value = attribute.getValue().toString();
               // check for track changes
               if (WordAnnotationHandler.containsWordAnnotations(value)) {
                  // capture those artifacts that have tracked changes on 
                  artifacts.add(artifact);
               }
            }
         }
      }
      return artifacts;
   }

   protected void displayWarningMessageDialog() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Diff Artifacts Warning",
                  "Detected tracked changes for this artifact. It is listed in the Artifact Explorer.");
         }
      }, true);
   }

   private void displayTrackedChangesOnArtifacts(final Collection<Artifact> artifacts) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               try {
                  String page = AHTML.simplePageNoPageEncoding(getStatusReport(artifacts));
                  ResultsEditor.open(new XResultPage("Artifacts with Tracked Changes On", page));
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }
   }

   public String getStatusReport(Collection<Artifact> artifacts) {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(2, "This table lists the Artifacts that were detected to have tracked changes on.")); //
      sb.append(AHTML.heading(3, "Please make sure to accept/reject all tracked changes and comment references.")); //      
      sb.append(AHTML.beginMultiColumnTable(60, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Artifact Name", "HRID"}));
      for (Artifact artifact : artifacts) {
         sb.append(AHTML.addRowMultiColumnTable(new String[] {artifact.toString(), artifact.getHumanReadableId()}));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

}
