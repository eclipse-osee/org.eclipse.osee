/*
 * Created on May 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, then adds an Action, Workflow and 30 Tasks, deletes these objects and compares the row count. If
 * purge works properly, all rows should be equal.
 * 
 * @author Donald G. Dunne
 */
public class DemoPurgeTest extends TestCase {

   private final List<String> tables =
         Arrays.asList("osee_attribute", "osee_artifact", "osee_artifact_version", "osee_relation_link",
               "osee_tx_details", "osee_txs");
   private final Map<String, Integer> preCreateActionCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postCreateActionCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postPurgeCount = new HashMap<String, Integer>();

   /**
    * @throws java.lang.Exception
    */
   @Override
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
   }

   public void testDemoPurge() throws Exception {
      System.out.println("Validating OSEE Application Server...");
      if (!OseeLog.isStatusOk()) {
         System.err.println(OseeLog.getStatusReport() + ". \nExiting.");
         return;
      }
      System.out.println("Begin Demo Purge Test...");
      System.out.println("Pre Purge Table Counts.");
      // Count rows in tables prior to purge
      getTableCounts(preCreateActionCount);

      Set<Artifact> artsToPurge = new HashSet<Artifact>();

      // Create Action, Workflow and Tasks
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      ActionArtifact actionArt =
            NewActionJob.createAction(null, "Action to Purge", "description", ChangeType.Improvement,
                  PriorityType.Priority_2, Arrays.asList("Other"), false, null,
                  org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ActionableItemArtifact.class,
                        ArtifactQuery.getArtifactsFromTypeAndName(ActionableItemArtifact.ARTIFACT_NAME, "SAW Test",
                              AtsPlugin.getAtsBranch())), transaction);
      actionArt.persistAttributesAndRelations(transaction);
      transaction.execute();

      artsToPurge.add(actionArt);
      artsToPurge.addAll(actionArt.getTeamWorkFlowArtifacts());

      for (int x = 0; x < 30; x++) {
         TaskArtifact taskArt =
               actionArt.getTeamWorkFlowArtifacts().iterator().next().getSmaMgr().getTaskMgr().createNewTask(
                     "New Task " + x, true);
         artsToPurge.add(taskArt);
      }

      // Count rows and check that increased
      System.out.println("Post Create Action Table Counts.");
      getTableCounts(postCreateActionCount);
      checkThatIncreased(preCreateActionCount, postCreateActionCount);

      // Purge Action, Workflow and Tasks
      ArtifactPersistenceManager.purgeArtifacts(artsToPurge);

      // Count rows and check that same as when began
      System.out.println("Post Purge Table Counts.");
      getTableCounts(postPurgeCount);
      checkThatEqual(preCreateActionCount, postPurgeCount);

      System.out.println("End Demo Purge Test.");
   }

   private void checkThatIncreased(Map<String, Integer> prevTableCount, Map<String, Integer> postTableCount) {
      for (String tableName : prevTableCount.keySet()) {
         assertTrue(postTableCount.get(tableName) > prevTableCount.get(tableName));
      }
   }

   private void checkThatEqual(Map<String, Integer> prevTableCount, Map<String, Integer> postTableCount) {
      for (String tableName : prevTableCount.keySet()) {
         String str =
               String.format("%s post[%d] vs pre[%d]", tableName, postTableCount.get(tableName),
                     prevTableCount.get(tableName));
         System.out.println(str);
         assertTrue(str, postTableCount.get(tableName).equals(prevTableCount.get(tableName)));
      }
   }

   private void getTableCounts(Map<String, Integer> tableCount) throws OseeDataStoreException {
      for (String tableName : tables) {
         tableCount.put(tableName, getTableRowCount(tableName));
      }
   }

   private int getTableRowCount(String tableName) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, "SELECT count(1) FROM " + tableName);
   }
}
