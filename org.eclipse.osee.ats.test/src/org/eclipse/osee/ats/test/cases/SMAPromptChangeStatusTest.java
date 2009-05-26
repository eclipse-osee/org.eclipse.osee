/*
 * Created on May 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.cases;

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class SMAPromptChangeStatusTest extends TestCase {

   public static TeamWorkFlowArtifact teamArt;
   public static Collection<TaskArtifact> tasks;

   /**
    * @throws java.lang.Exception
    */
   @Override
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsPlugin.isProductionDb());
   }

   public void testCleanupPre() throws Exception {
      DemoTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   public void testInitialize() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      teamArt = DemoTestUtil.createSimpleAction(getClass().getSimpleName(), transaction);
      tasks = DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName(), 5, transaction);
      transaction.execute();

      assertNotNull(teamArt);
   }

   public void testChangeTaskStatusNoResolution() throws Exception {
      fail("Not implemented yet");
      // Change two to 100, 1 hr split
      // SMAPromptChangeStatus.promptChangeStatus(tasks, persist);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state

      // Change two to 55, 0
      // no transition, no hours spent

      // Test the cancelled state, what do there?
   }

   public void testChangeTaskStatusWithResolutionOptions() throws Exception {
      fail("Not implemented yet");
      // Change two to 100, 1 hr split
      // SMAPromptChangeStatus.promptChangeStatus(tasks, true);

      // Change two to 100, 1 hr split
      // either exception out or add hours to inwork state

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state

      // Change two to 55, 0
      // no transition, no hours spent
   }

   public void testPromptChangeWorkflowStatus() throws Exception {
      fail("Not implemented yet");
   }

   public void testCleanupPost() throws Exception {
      DemoTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

}
