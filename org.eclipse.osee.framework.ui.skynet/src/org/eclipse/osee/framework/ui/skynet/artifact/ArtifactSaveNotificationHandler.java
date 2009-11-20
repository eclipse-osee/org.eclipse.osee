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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

/**
 * This handler will find any artifacts in the artifact cache that were dirty and allow user to persist before shutdown.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactSaveNotificationHandler implements IWorkbenchListener {

   public static boolean noPopUp = false;

   @Override
   public void postShutdown(IWorkbench arg0) {
   }

   @Override
   public boolean preShutdown(IWorkbench arg0, boolean arg1) {
      if (noPopUp) {
         return true;
      }
      OseeLog.log(SkynetGuiPlugin.class, OseeLevel.INFO, "Verifying Artifact Persistence");
      try {
         Collection<Artifact> dirtyArts = ArtifactCache.getDirtyArtifacts();
         if (dirtyArts.size() == 0) {
            return true;
         }
         ArtifactDecorator artDecorator = new ArtifactDecorator("");
         artDecorator.addActions(null, null);
         artDecorator.setShowArtBranch(true);
         artDecorator.setShowArtType(true);
         SimpleCheckFilteredTreeDialog dialog =
               new SimpleCheckFilteredTreeDialog(
                     "Unsaved Artifacts Detected",
                     "Some artifacts have not been saved.\n\nSelect any artifact to save (if any) and select Ok or Cancel to stop shutdown.",
                     new ArrayTreeContentProvider(), new ArtifactLabelProvider(artDecorator),
                     new ArtifactViewerSorter(), 0, Integer.MAX_VALUE);
         dialog.setInput(dirtyArts);
         if (dialog.open() == 0) {
            if (dialog.getResult().length == 0) {
               return true;
            }
            HashCollection<Branch, Artifact> branchMap = new HashCollection<Branch, Artifact>();
            for (Artifact artifact : dirtyArts) {
               branchMap.put(artifact.getBranch(), artifact);
            }
            for (Branch branch : branchMap.keySet()) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.INFO, String.format(
                     "Persisting [%d] unsaved artifacts for branch [%s]", branchMap.getValues().size(), branch));
               Artifacts.persistInTransaction(branchMap.getValues(branch));
            }
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static void setNoPopUp(boolean noPopUp) {
      // mainly for testing purposes
      ArtifactSaveNotificationHandler.noPopUp = noPopUp;
   }
}
