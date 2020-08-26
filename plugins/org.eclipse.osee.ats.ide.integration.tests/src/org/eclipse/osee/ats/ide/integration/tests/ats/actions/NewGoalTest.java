/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import org.eclipse.osee.ats.ide.actions.NewGoal;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class NewGoalTest extends AbstractAtsActionRunTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), NewGoalTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromName(NewGoalTest.class.getSimpleName(),
         AtsApiService.get().getAtsBranch())) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @Override
   public NewGoal createAction() {
      NewGoal action = new NewGoal();
      action.setTitleOverride(getClass().getSimpleName());
      return action;
   }

}
