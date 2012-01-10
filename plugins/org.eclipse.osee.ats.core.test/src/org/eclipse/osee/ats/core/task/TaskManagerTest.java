/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.HoursSpentUtil;
import org.eclipse.osee.ats.core.workflow.PercentCompleteTotalUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test unit for {@link TaskManager}
 * 
 * @author Donald G. Dunne
 */
public class TaskManagerTest extends TaskManager {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testTransitionToCompletedThenInWork() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("TaskManagerTest - TransitionToCompleted");

      TaskArtifact taskArt = AtsTestUtil.getOrCreateTask();

      // ensure nothing dirty
      AtsTestUtil.validateArtifactCache();

      // transition to Completed
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtilCore.getAtsBranch(),
            getClass().getSimpleName() + " testTransitionToCompletedThenInWork() 1");
      Result result = TaskManager.transitionToCompleted(taskArt, 0.0, 3, transaction);
      Assert.assertEquals(Result.TrueResult, result);
      transaction.execute();

      Assert.assertEquals(TaskStates.Completed.getPageName(), taskArt.getCurrentStateName());
      Assert.assertEquals(3.0, HoursSpentUtil.getHoursSpentTotal(taskArt));
      Assert.assertEquals("", taskArt.getStateMgr().getAssigneesStr());

      // ensure nothing dirty
      AtsTestUtil.validateArtifactCache();

      // transition back to InWork
      transaction =
         new SkynetTransaction(AtsUtilCore.getAtsBranch(),
            getClass().getSimpleName() + " testTransitionToCompletedThenInWork() 2");
      result = TaskManager.transitionToInWork(taskArt, UserManager.getUser(), 45, .5, transaction);
      Assert.assertEquals(Result.TrueResult, result);
      transaction.execute();

      Assert.assertEquals(TaskStates.InWork.getPageName(), taskArt.getCurrentStateName());
      Assert.assertEquals(3.5, HoursSpentUtil.getHoursSpentTotal(taskArt));
      Assert.assertEquals("Joe Smith", taskArt.getStateMgr().getAssigneesStr());
   }

   @org.junit.Test
   public void testStatusPercentChanged() throws OseeCoreException {

      System.out.println("\n\n...testStatusPercentChanged Start...");

      AtsTestUtil.cleanupAndReset("TaskManagerTest - StatusPercentChanged");

      TaskArtifact taskArt = AtsTestUtil.getOrCreateTask();

      // ensure nothing dirty
      AtsTestUtil.validateArtifactCache();

      // status 34% completed
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName() + " testStatusPercentChanged() 1");
      Result result = TaskManager.statusPercentChanged(taskArt, 3, 34, transaction);
      Assert.assertEquals(Result.TrueResult, result);
      transaction.execute();

      Assert.assertEquals(TaskStates.InWork.getPageName(), taskArt.getCurrentStateName());
      Assert.assertEquals(3.0, HoursSpentUtil.getHoursSpentTotal(taskArt));
      Assert.assertEquals(34, PercentCompleteTotalUtil.getPercentCompleteTotal(taskArt));
      Assert.assertEquals("Joe Smith", taskArt.getStateMgr().getAssigneesStr());

      // ensure nothing dirty
      AtsTestUtil.validateArtifactCache();

      // status 100% completed
      transaction =
         new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName() + " testStatusPercentChanged() 2");
      result = TaskManager.statusPercentChanged(taskArt, 3, 100, transaction);
      Assert.assertEquals(Result.TrueResult, result);
      transaction.execute();

      Assert.assertEquals(TaskStates.Completed.getPageName(), taskArt.getCurrentStateName());
      Assert.assertEquals(6.0, HoursSpentUtil.getHoursSpentTotal(taskArt));
      Assert.assertEquals(100, PercentCompleteTotalUtil.getPercentCompleteTotal(taskArt));
      Assert.assertEquals("", taskArt.getStateMgr().getAssigneesStr());

      // ensure nothing dirty
      AtsTestUtil.validateArtifactCache();

      // status back to 25%
      transaction =
         new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName() + " testStatusPercentChanged() 3");
      result = TaskManager.statusPercentChanged(taskArt, 1, 25, transaction);
      Assert.assertEquals(Result.TrueResult, result);
      transaction.execute();

      Assert.assertEquals(TaskStates.InWork.getPageName(), taskArt.getCurrentStateName());
      Assert.assertEquals(7.0, HoursSpentUtil.getHoursSpentTotal(taskArt));
      Assert.assertEquals(25, PercentCompleteTotalUtil.getPercentCompleteTotal(taskArt));
      Assert.assertEquals("Joe Smith", taskArt.getStateMgr().getAssigneesStr());

      // ensure nothing dirty
      AtsTestUtil.validateArtifactCache();

      System.out.println("...testStatusPercentChanged End...");
   }
}
