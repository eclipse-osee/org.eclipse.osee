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
package org.eclipse.osee.ats.test.cases;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Before;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, then adds an Action, Workflow and 30 Tasks, deletes these objects and compares the row count. If
 * purge works properly, all rows should be equal.
 * 
 * @author Donald G. Dunne
 */
public class AtsPurgeTest {

   private final Map<String, Integer> preCreateActionCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postCreateActionCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postPurgeCount = new HashMap<String, Integer>();
   List<String> tables =
         Arrays.asList("osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs",
               "osee_artifact_version");

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(AtsUtil.isProductionDb());
   }

   @org.junit.Test
   public void testPurgeArtifacts() throws Exception {
      // Count rows in tables prior to purge
      DbUtil.getTableRowCounts(preCreateActionCount, tables);

      Set<Artifact> artsToPurge = new HashSet<Artifact>();

      // Create Action, Workflow and Tasks
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      ActionArtifact actionArt =
            ActionManager.createAction(null, getClass().getSimpleName(), "description", ChangeType.Improvement,
                  PriorityType.Priority_2, Arrays.asList("Other"), false, null,
                  org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ActionableItemArtifact.class,
                        ArtifactQuery.getArtifactListFromTypeAndName(ActionableItemArtifact.ARTIFACT_NAME, "SAW Test",
                              AtsUtil.getAtsBranch())), transaction);
      actionArt.persist(transaction);
      transaction.execute();

      artsToPurge.add(actionArt);
      artsToPurge.addAll(actionArt.getTeamWorkFlowArtifacts());

      for (int x = 0; x < 30; x++) {
         TaskArtifact taskArt =
               actionArt.getTeamWorkFlowArtifacts().iterator().next().getSmaMgr().getTaskMgr().createNewTask(
                     getClass().getSimpleName() + x);
         taskArt.persist();
         artsToPurge.add(taskArt);
      }

      // Count rows and check that increased
      DbUtil.getTableRowCounts(postCreateActionCount, tables);
      TestUtil.checkThatIncreased(preCreateActionCount, postCreateActionCount);

      // Purge Action, Workflow and Tasks
      for (Artifact art : artsToPurge) {
         art.purgeFromBranch();
      }

      // Count rows and check that same as when began
      DbUtil.getTableRowCounts(postPurgeCount, tables);
      TestUtil.checkThatEqual(preCreateActionCount, postPurgeCount);
   }

}
