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
package org.eclipse.osee.ats;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.actions.AtsTest_Demo_Action_Suite;
import org.eclipse.osee.ats.artifact.log.AtsLogTest;
import org.eclipse.osee.ats.artifact.log.LogItemTest;
import org.eclipse.osee.ats.artifact.note.AtsNoteTest;
import org.eclipse.osee.ats.artifact.note.NoteItemTest;
import org.eclipse.osee.ats.config.AtsBranchConfigurationTest;
import org.eclipse.osee.ats.editor.SMAPrintTest;
import org.eclipse.osee.ats.health.AtsValidateAtsDatabaseTest;
import org.eclipse.osee.ats.navigate.AtsNavigateItemsToMassEditorTest;
import org.eclipse.osee.ats.navigate.AtsNavigateItemsToTaskEditorTest;
import org.eclipse.osee.ats.navigate.AtsNavigateItemsToWorldViewTest;
import org.eclipse.osee.ats.render.RendererManagerTest;
import org.eclipse.osee.ats.util.AtsDeleteManagerTest;
import org.eclipse.osee.ats.util.AtsImageTest;
import org.eclipse.osee.ats.util.AtsPurgeTest;
import org.eclipse.osee.ats.util.AtsXWidgetsExampleBlamTest;
import org.eclipse.osee.ats.util.ImportActionsViaSpreadsheetTest;
import org.eclipse.osee.ats.workflow.SMAPromptChangeStatusTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsTest_Demo_Action_Suite.class,
   AtsTest_Demo_Access_Suite.class,
   AtsNavigateItemsToMassEditorTest.class,
   AtsNavigateItemsToTaskEditorTest.class,
   AtsNavigateItemsToWorldViewTest.class,
   ImportActionsViaSpreadsheetTest.class,
   AtsTest_Demo_Column_Suite.class,
   AtsTest_Demo_Util_Suite.class,
   LogItemTest.class,
   AtsLogTest.class,
   NoteItemTest.class,
   AtsNoteTest.class,
   RendererManagerTest.class,
   SMAPrintTest.class,
   AtsImageTest.class,
   SMAPromptChangeStatusTest.class,
   AtsDeleteManagerTest.class,
   AtsPurgeTest.class,
   AtsBranchConfigurationTest.class,
   AtsValidateAtsDatabaseTest.class,
   AtsXWidgetsExampleBlamTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      System.out.println("\n\nBegin " + AtsTest_Demo_Suite.class.getSimpleName());
      if (!OseeData.isProjectOpen()) {
         System.err.println("osee.data project should be open");
         OseeData.ensureProjectOpen();
      }
   }

   @AfterClass
   public static void tearDown() throws Exception {
      if (!OseeData.isProjectOpen()) {
         System.err.println("osee.data project should be open");
         OseeData.ensureProjectOpen();
      }
      System.out.println("End " + AtsTest_Demo_Suite.class.getSimpleName());
   }
}
