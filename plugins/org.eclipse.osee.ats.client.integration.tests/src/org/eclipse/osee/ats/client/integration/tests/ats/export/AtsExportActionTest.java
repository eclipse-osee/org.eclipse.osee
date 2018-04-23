/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.export;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.export.AtsExportAction;
import org.eclipse.osee.ats.export.AtsExportAction.ExportOption;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.ui.IEditorPart;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for {@link AtsExportAction}
 * 
 * @author Donald G. Dunne
 */
public class AtsExportActionTest {

   @Before
   @After
   public void cleanup() throws CoreException {
      IFolder toDir = getTestFolder();
      if (toDir.exists()) {
         toDir.delete(true, null);
      }
      Assert.assertFalse(toDir.exists());
      ResultsEditor.closeAll();
   }

   private IFolder getTestFolder() {
      return OseeData.getFolder(AtsExportActionTest.class.getSimpleName());
   }

   private IFolder getTestFolderOrCreate() throws CoreException {
      IFolder toDir = getTestFolder();
      if (!toDir.exists()) {
         toDir.create(true, false, null);
      }
      return toDir;
   }

   @Test
   public void testExport_asHtmlMerged() throws CoreException, IOException {
      AtsExportAction exporter = new AtsExportAction();
      exporter.setPopup(false);
      TeamWorkFlowArtifact codeWf1 = DemoUtil.getSawCodeCommittedWf();
      TeamWorkFlowArtifact codeWf2 = DemoUtil.getSawCodeNoBranchWf();
      List<ExportOption> exportOptions =
         Arrays.asList(ExportOption.MERGE_INTO_SINGLE_FILE, ExportOption.AS_HTML_TO_FILE);
      IFolder toDir = getTestFolderOrCreate();
      Result result = exporter.export(Arrays.asList(codeWf1, codeWf2), exportOptions, toDir.getLocation().toOSString());
      Assert.assertTrue(result.getText(), result.isTrue());
      String html =
         Lib.fileToString(new File(toDir.getFile(AtsExportAction.ATS_EXPORT_HTML_FILE).getLocation().toOSString()));
      Assert.assertTrue("Does not contain codeWf1 title", html.contains(codeWf1.getName()));
      Assert.assertTrue("Does not contain codeWf2 title", html.contains(codeWf2.getName()));
   }

   @Test
   public void testExport_asHtmlSingle() throws CoreException, IOException {
      AtsExportAction exporter = new AtsExportAction();
      exporter.setPopup(false);
      TeamWorkFlowArtifact codeWf1 = DemoUtil.getSawCodeCommittedWf();
      TeamWorkFlowArtifact codeWf2 = DemoUtil.getSawCodeNoBranchWf();
      List<ExportOption> exportOptions =
         Arrays.asList(ExportOption.SAVE_INTO_SEPARATE_FILES, ExportOption.AS_HTML_TO_FILE);
      IFolder toDir = getTestFolderOrCreate();
      Result result = exporter.export(Arrays.asList(codeWf1, codeWf2), exportOptions, toDir.getLocation().toOSString());
      Assert.assertTrue(result.getText(), result.isTrue());
      String html1 = Lib.fileToString(new File(toDir.getFile(codeWf1.getAtsId() + ".html").getLocation().toOSString()));
      Assert.assertTrue("Does not contain codeWf1 title", html1.contains(codeWf1.getName()));
      String html2 = Lib.fileToString(new File(toDir.getFile(codeWf2.getAtsId() + ".html").getLocation().toOSString()));
      Assert.assertTrue("Does not contain codeWf2 title", html2.contains(codeWf2.getName()));
   }

   @Test
   public void testExport_asHtmlSingleWithTasks() throws CoreException, IOException {
      AtsExportAction exporter = new AtsExportAction();
      exporter.setPopup(false);
      TeamWorkFlowArtifact codeWf1 = DemoUtil.getSawCodeCommittedWf();
      List<ExportOption> exportOptions = Arrays.asList(ExportOption.SAVE_INTO_SEPARATE_FILES,
         ExportOption.AS_HTML_TO_FILE, ExportOption.INCLUDE_TASKLIST);
      IFolder toDir = getTestFolderOrCreate();
      Result result = exporter.export(Arrays.asList(codeWf1), exportOptions, toDir.getLocation().toOSString());
      Assert.assertTrue(result.getText(), result.isTrue());
      String html1 = Lib.fileToString(new File(toDir.getFile(codeWf1.getAtsId() + ".html").getLocation().toOSString()));
      Assert.assertTrue("Does not contain codeWf1 title", html1.contains(codeWf1.getName()));
      Matcher m = Pattern.compile("<b>Tasks<\\/b>").matcher(html1);
      Assert.assertTrue("Does not contain Tasks table", m.find());
      m = Pattern.compile("<td>Create development plan<\\/td>").matcher(html1);
      Assert.assertTrue("Does not contain task 1 in table", m.find());
   }

   @Test
   public void testExport_asHtmlResultsEditorSingle() {
      AtsExportAction exporter = new AtsExportAction();
      exporter.setPopup(false);
      TeamWorkFlowArtifact codeWf1 = DemoUtil.getSawCodeCommittedWf();
      TeamWorkFlowArtifact codeWf2 = DemoUtil.getSawCodeNoBranchWf();
      List<ExportOption> exportOptions =
         Arrays.asList(ExportOption.SAVE_INTO_SEPARATE_FILES, ExportOption.AS_HTML_TO_RESULT_EDITOR);
      Result result = exporter.export(Arrays.asList(codeWf1, codeWf2), exportOptions, null);
      Assert.assertTrue(result.getText(), result.isTrue());
      Collection<ResultsEditor> editors = ResultsEditor.getEditors();
      Assert.assertEquals(2, editors.size());
      List<String> expectedTitles = Arrays.asList("Export " + codeWf1.getAtsId(), "Export " + codeWf2.getAtsId());
      for (IEditorPart part : editors) {
         Assert.assertTrue(expectedTitles.contains(part.getTitle()));
      }
   }

   @Test
   public void testExport_asHtmlResultsEditorMerge() {
      AtsExportAction exporter = new AtsExportAction();
      exporter.setPopup(false);
      TeamWorkFlowArtifact codeWf1 = DemoUtil.getSawCodeCommittedWf();
      TeamWorkFlowArtifact codeWf2 = DemoUtil.getSawCodeNoBranchWf();
      List<ExportOption> exportOptions =
         Arrays.asList(ExportOption.MERGE_INTO_SINGLE_FILE, ExportOption.AS_HTML_TO_RESULT_EDITOR);
      Result result = exporter.export(Arrays.asList(codeWf1, codeWf2), exportOptions, null);
      Assert.assertTrue(result.getText(), result.isTrue());
      Collection<ResultsEditor> editors = ResultsEditor.getEditors();
      Assert.assertEquals(1, editors.size());
      IEditorPart part = editors.iterator().next();
      Assert.assertEquals("Export ATS Artifacts", part.getTitle());
   }

}
