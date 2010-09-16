/*
 * Created on Sep 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.IWorkProductTaskProvider;
import org.eclipse.osee.coverage.model.WorkProductAction;
import org.eclipse.osee.coverage.model.WorkProductTask;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
            for (Artifact pcrArt : packageArt.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo)) {
               if (cm.isPcrArtifact(pcrArt)) {
                  WorkProductAction action =
                     new WorkProductAction(pcrArt.getGuid(), pcrArt.getName(), cm.isCompleted(pcrArt));
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

   @Override
   public void removeWorkProductAction(WorkProductAction action) {
      try {
         OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, branch);
         for (Artifact artifact : store.getArtifact(false).getRelatedArtifacts(
            CoreRelationTypes.SupportingInfo_SupportingInfo)) {
            if (artifact.getGuid().equals(action.getGuid())) {
               store.getArtifact(false).deleteRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, artifact);
            }
         }
         store.getArtifact(false).persist("Un-Relate Coverage work product Actions");
         reload();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void addWorkProductAction(WorkProductAction action) {
      reload();
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
