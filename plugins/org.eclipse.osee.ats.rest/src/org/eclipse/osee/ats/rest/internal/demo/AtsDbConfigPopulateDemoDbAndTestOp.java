/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Initialization class that populate the demo database with requirements, workflows and etc. This data can be used for
 * development or for demoing OSEE features without having to re-create manuall every time. It's also used for
 * integration testing.
 *
 * @author Donald G. Dunne
 */
public class AtsDbConfigPopulateDemoDbAndTestOp {

   private final AtsApi atsApi;
   private final XResultData rd;
   private final OrcsApi orcsApi;

   public AtsDbConfigPopulateDemoDbAndTestOp(XResultData rd, AtsApi atsApi, OrcsApi orcsApi) {
      this.rd = rd;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData run() {

      System.setProperty("osee.db", "orgdemo");
      atsApi.getWorkDefinitionService().internalClearCaches();

      (new Pdd15CreateNotesAndAnnotations(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd20CreateCommittedAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd20CreateCommittedActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd21CreateUnCommittedAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd21CreateUnCommittedActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd22CreateUnCommittedConflictedAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd22CreateUnCommittedConflictedActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd23CreateNoBranchAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd23CreateNoBranchActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd51CreateWorkaroundForGraphViewActions(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd51CreateWorkaroundForGraphViewActionsTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd52CreateWorkingWithDiagramTreeActions(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd52CreateWorkingWithDiagramTreeActionsTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd80CreateButtonSDoesntWorkAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd80CreateButtonSDoesntWorkActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd81CreateButtonWDoesntWorkAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd81CreateButtonWDoesntWorkActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd82CreateCantLoadDiagramTreeAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd82CreateCantLoadDiagramTreeActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd83CreateCantSeeTheGraphViewAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd83CreateCantSeeTheGraphViewActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd84CreateProblemInTreeAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd84CreateProblemInTreeActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd85CreateProblemWithTheGraphViewAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd85CreateProblemWithTheGraphViewActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd86CreateProblemWithTheUserWindowAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd86CreateProblemWithTheUserWindowActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd90CreateDemoTasks(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd90CreateDemoTasksTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd91CreateDemoGroups(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd92CreateDemoReviews(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd92CreateDemoReviewsTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd93CreateDemoAgile(rd, atsApi, orcsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd93CreateDemoAgileTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd94CreateDemoFavoritesAndSubscribed(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd94CreateDemoFavoritesAndSubscribedTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd95CreateDemoPrograms(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd96CreateDemoWebExportGoal(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }

      return new XResultData();
   }

}
