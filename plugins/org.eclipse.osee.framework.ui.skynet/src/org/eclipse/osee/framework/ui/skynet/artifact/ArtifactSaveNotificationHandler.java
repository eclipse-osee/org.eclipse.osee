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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecoratorPreferences;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

/**
 * This handler will find any artifacts in the artifact cache that were dirty and allow user to persist before shutdown.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactSaveNotificationHandler implements IWorkbenchListener {

   @Override
   public void postShutdown(IWorkbench arg0) {
      // do nothing
   }

   @Override
   public boolean preShutdown(IWorkbench arg0, boolean force) {
      boolean isShutdownAllowed = true;
      OseeLog.log(Activator.class, Level.INFO, "Verifying Artifact Persistence");
      Collection<Artifact> dirtyArts = ArtifactCache.getDirtyArtifacts();

      if (!dirtyArts.isEmpty()) {

         if (RenderingUtil.arePopupsAllowed()) {
            FilteredCheckboxTreeDialog dialog = createDialog(dirtyArts);
            int result = dialog.open();
            if (result == Window.OK) {
               isShutdownAllowed = true;
               Object[] selected = dialog.getResult();
               if (selected.length > 0) {
                  Collection<Artifact> itemsToSave = new HashSet<Artifact>();
                  for (Object object : selected) {
                     itemsToSave.add((Artifact) object);
                  }

                  try {
                     HashCollection<IOseeBranch, Artifact> branchMap = Artifacts.getBranchArtifactMap(itemsToSave);
                     for (Entry<IOseeBranch, Collection<Artifact>> entry : branchMap.entrySet()) {
                        IOseeBranch branch = entry.getKey();
                        Collection<Artifact> arts = entry.getValue();
                        OseeLog.logf(Activator.class, Level.INFO, "Persisting [%d] unsaved artifacts for branch [%s]",
                           arts.size(), branch);
                        Artifacts.persistInTransaction("Artifact Save Notification", arts);
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            } else {
               isShutdownAllowed = false;
               HashCollection<IOseeBranch, Artifact> branchMap = Artifacts.getBranchArtifactMap(dirtyArts);

               for (IOseeBranch branch : branchMap.keySet()) {
                  MassArtifactEditor.editArtifacts(String.format("Unsaved Artifacts for Branch [%s]", branch),
                     branchMap.getValues(branch));
               }
            }
         } else {
            // For Test Purposes
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Found dirty artifacts after tests: " + dirtyArts);
         }
      }

      return force || isShutdownAllowed;
   }

   public FilteredCheckboxTreeDialog createDialog(Collection<Artifact> dirtyArts) {
      ArtifactDecoratorPreferences preferences = new ArtifactDecoratorPreferences();
      preferences.setShowArtBranch(true);
      preferences.setShowArtType(true);

      FilteredCheckboxTreeDialog dialog =
         new FilteredCheckboxTreeDialog(
            "Unsaved Artifacts Detected",
            "Some artifacts have not been saved.\n\nCheck artifacts to save (if any) and select Ok to continue shutdown. Select Cancel to stop shutdown.",
            new ArrayTreeContentProvider(), new ArtifactLabelProvider(preferences), new ArtifactViewerSorter());
      dialog.setInput(dirtyArts);

      return dialog;
   }

}
