/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.IWorkProductTaskProvider;
import org.eclipse.osee.coverage.model.WorkProductAction;
import org.eclipse.osee.coverage.model.WorkProductTask;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;

public class DbWorkProductTaskProvider implements IWorkProductTaskProvider {

   private CoveragePackage coveragePackage;
   private final Branch branch;
   List<WorkProductAction> actions = new ArrayList<WorkProductAction>();
   Map<String, WorkProductTask> guidToTasks = new HashMap<String, WorkProductTask>();

   public DbWorkProductTaskProvider(Branch branch) {
      this.branch = branch;
   }

   @Override
   public List<WorkProductAction> getWorkProductRelatedActions() {
      return actions;
   }

   @Override
   public void reload() {
      actions.clear();
      guidToTasks.clear();
      try {
         OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, branch);
         IOseeCmService cm = SkynetGuiPlugin.getInstance().getOseeCmService();
         Artifact packageArt = store.getArtifact(false);
         if (packageArt != null) {
            List<String> relatedActionGuids =
               packageArt.getAttributesToStringList(CoverageAttributeTypes.WorkProductPcrGuid);
            List<Artifact> relatedArtifacts =
               ArtifactQuery.getArtifactListFromIds(relatedActionGuids, BranchManager.getCommonBranch());
            checkForErrorCase(relatedActionGuids, relatedArtifacts, store.getBranch());
            for (Artifact pcrArt : relatedArtifacts) {
               if (cm.isPcrArtifact(pcrArt)) {
                  WorkProductAction action = new WorkProductAction(pcrArt, cm.isCompleted(pcrArt));
                  actions.add(action);
                  for (Artifact taskArt : cm.getTaskArtifacts(pcrArt)) {
                     action.getTasks().add(
                        new WorkProductTask(taskArt.getGuid(), taskArt.getName(), cm.isCompleted(taskArt), action));
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void checkForErrorCase(List<String> relatedActionGuids, List<Artifact> relatedArtifacts, Branch branch) {
      boolean found = false;
      for (String guid : relatedActionGuids) {
         for (Artifact art : relatedArtifacts) {
            if (art.getGuid().equals(guid)) {
               found = true;
               break;
            }
         }
         if (!found) {
            OseeLog.format(Activator.class, Level.SEVERE,
               "Invalid related WorkProductPcrGuid [%s] for Coverage Package [%s] on branch [%s]; ignoring.", guid,
               coveragePackage.getName(), branch.getName());
         }
      }
   }

   @Override
   public void removeWorkProductAction(WorkProductAction action) {
      try {
         OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, branch);
         Artifact artifact = store.getArtifact(false);
         List<String> relatedActionGuids =
            artifact.getAttributesToStringList(CoverageAttributeTypes.WorkProductPcrGuid);
         if (relatedActionGuids.contains(action.getGuid())) {
            store.getArtifact(false).deleteAttribute(CoverageAttributeTypes.WorkProductPcrGuid, action.getGuid());
         }
         store.getArtifact(false).persist("Un-Relate Coverage work product Actions");
         reload();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void addWorkProductAction(Collection<WorkProductAction> actions) {
      try {
         OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, branch);
         Artifact artifact = store.getArtifact(false);
         List<String> relatedActionGuids =
            artifact.getAttributesToStringList(CoverageAttributeTypes.WorkProductPcrGuid);
         for (WorkProductAction action : actions) {
            if (!relatedActionGuids.contains(action.getGuid())) {
               artifact.addAttribute(CoverageAttributeTypes.WorkProductPcrGuid, action.getGuid());
               relatedActionGuids.add(action.getGuid());
            }
         }
         artifact.persist("Relate Coverage work product Actions");
         reload();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public List<WorkProductTask> getWorkProductTasks() {
      List<WorkProductTask> tasks = new ArrayList<WorkProductTask>();
      for (WorkProductAction action : actions) {
         tasks.addAll(action.getTasks());
      }
      return tasks;

   }

   @Override
   public WorkProductTask getWorkProductTask(String guid) {
      for (WorkProductAction action : actions) {
         for (WorkProductTask task : action.getTasks()) {
            if (task.getGuid().equals(guid)) {
               return task;
            }
         }
      }
      return null;
   }

   @Override
   public void setCoveragePackage(CoveragePackage coveragePackage) {
      this.coveragePackage = coveragePackage;
      reload();
   }

   @Override
   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

}
