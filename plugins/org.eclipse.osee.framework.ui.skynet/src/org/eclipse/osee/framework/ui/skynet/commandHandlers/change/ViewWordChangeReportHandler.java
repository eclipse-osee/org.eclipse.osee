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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class ViewWordChangeReportHandler extends AbstractHandler {

   private Collection<Change> changes;

   @Override
   public Object execute(ExecutionEvent event) {
      viewWordChangeReport(changes, false, null);
      return null;
   }

   public void viewWordChangeReport(Collection<Change> changes, boolean suppressWord, String diffReportFolderName) {
      VariableMap variableMap = new VariableMap();
      variableMap.setValue("suppressWord", suppressWord);
      variableMap.setValue("diffReportFolderName", diffReportFolderName);

      Collection<Pair<Artifact, Artifact>> compareArtifacts = ChangeManager.getCompareArtifacts(changes);

      //All other artifacts types can be rendered by the wordRenderer so the are displayed in the word change report.
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      try {
         renderer.setOptions(variableMap);
         renderer.getComparator().compareArtifacts(new NullProgressMonitor(), PresentationType.DIFF, compareArtifacts);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
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

            List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            changes = new ArrayList<Change>(localChanges.size());

            Set<Artifact> artifacts = new HashSet<Artifact>();
            for (Change change : localChanges) {
               if (!artifacts.contains(change.getArtifact())) {
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