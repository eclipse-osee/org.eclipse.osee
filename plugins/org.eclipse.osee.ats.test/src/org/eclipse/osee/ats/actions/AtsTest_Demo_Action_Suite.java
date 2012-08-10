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
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AccessControlActionTest.class,
   AddNoteActionTest.class,
   CopyActionDetailsActionTest.class,
   DeletePurgeAtsArtifactsActionTest.class,
   DirtyReportActionTest.class,
   DirtyReportActionTest2.class,
   DuplicateWorkflowActionTest.class,
   DuplicateWorkflowViaWorldEditorActionTest.class,
   FavoriteActionTest.class,
   ImportTasksViaSimpleListTest.class,
   ImportTasksViaSpreadsheetTest.class,
   MyFavoritesActionTest.class,
   MyWorldActionTest.class,
   NewGoalTest.class,
   OpenAtsPerspectiveActionTest.class,
   OpenChangeReportByIdActionTest.class,
   OpenInArtifactEditorActionTest.class,
   OpenInAtsWorkflowEditorActionTest.class,
   OpenInSkyWalkerActionTest.class,
   OpenInAtsWorldActionTest.class,
   OpenInAtsWorldActionTest2.class,
   OpenInAtsWorldActionTest3.class,
   OpenNewAtsTaskEditorActionTest.class,
   OpenNewAtsWorldEditorActionTest.class,
   OpenNewAtsTaskEditorSelectedTest.class,
   OpenNewAtsWorldEditorSelectedActionTest.class,
   OpenParentActionTest.class,
   OpenReviewPerspectiveActionTest.class,
   OpenWorkflowByIdActionTest.class,
   OpenWorldByIdActionTest.class,
   RefreshDirtyActionTest.class,
   ReloadActionTest.class,
   ResourceHistoryActionTest.class,
   ShowBranchChangeDataActionTest.class,
   ShowChangeReportActionTest.class,
   ShowMergeManagerActionTest.class,
   ShowWorkDefinitionActionTest.class,
   SubscribedActionTest.class,
   TaskAddActionTest.class,})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_Action_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsTest_Demo_Action_Suite.class.getSimpleName());
      DemoTestUtil.setUpTest();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Demo_Action_Suite.class.getSimpleName());
   }
}
