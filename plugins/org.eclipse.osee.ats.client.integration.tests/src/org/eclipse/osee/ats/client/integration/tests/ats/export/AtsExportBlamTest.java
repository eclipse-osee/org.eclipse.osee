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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.export.AtsExportAction.ExportOption;
import org.eclipse.osee.ats.export.AtsExportBlam;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsExportBlamTest {

   @Before
   @After
   public void setupCleanup() {
      BlamEditor.closeAll();
   }

   @Test
   public void testGetXWidgetsXml() {
      AtsExportBlam blam = new AtsExportBlam(new ArrayList<AbstractWorkflowArtifact>());
      String xml = blam.getXWidgetsXml();
      Assert.assertNotNull(xml);
      SwtXWidgetRenderer renderer = new SwtXWidgetRenderer();
      List<XWidgetRendererItem> layoutDatas = XWidgetParser.extractWorkAttributes(renderer, xml);
      Assert.assertEquals(8, layoutDatas.size());
   }

   @Test
   public void testOpenAtsExportBlam() {
      BlamEditor.closeAll();
      AtsExportBlam.openAtsExportBlam(null);
      Collection<BlamEditor> editors = BlamEditor.getEditors();
      Assert.assertEquals(1, editors.size());
   }

   @Test
   public void testIsEntryValid() {
      AtsExportBlam blam = new AtsExportBlam(new ArrayList<AbstractWorkflowArtifact>());
      VariableMap variableMap = new VariableMap();

      // test nothing selected
      variableMap.setValue(AtsExportBlam.ARTIFACTS, new ArrayList<Artifact>());
      Result result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isFalse());
      Assert.assertEquals(AtsExportBlam.NO_ARTIFACTS_SELECTED, result.getText());

      // test artifacts
      variableMap.setValue(AtsExportBlam.ARTIFACTS, Arrays.asList(DemoUtil.getSawCodeCommittedWf()));
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isFalse());
      Assert.assertEquals(AtsExportBlam.MUST_SELECT_AT_LEAST_ONE_EXPORT_AS_OPTION, result.getText());

      // detfault ExportOptions to false
      for (ExportOption option : ExportOption.values()) {
         variableMap.setValue(option.name(), Boolean.FALSE);
      }

      // test AS_HTML_TO_FILE or AS_HTML_TO_RESULT_EDITOR
      variableMap.setValue(ExportOption.AS_HTML_TO_FILE.name(), Boolean.TRUE);
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isFalse());
      Assert.assertEquals(AtsExportBlam.MUST_SELECT_MERGE_INTO_SINGLE_FILE_OR_SAVE_INTO_SEPARATE_FILES,
         result.getText());

      variableMap.setValue(ExportOption.AS_HTML_TO_FILE.name(), Boolean.FALSE);
      variableMap.setValue(ExportOption.AS_HTML_TO_RESULT_EDITOR.name(), Boolean.TRUE);
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isFalse());
      Assert.assertEquals(AtsExportBlam.MUST_SELECT_MERGE_INTO_SINGLE_FILE_OR_SAVE_INTO_SEPARATE_FILES,
         result.getText());

      // test MERGE_INTO_SINGLE_FILE or SAVE_INTO_SEPARATE_FILES
      variableMap.setValue(ExportOption.MERGE_INTO_SINGLE_FILE.name(), Boolean.TRUE);
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isTrue());

      variableMap.setValue(ExportOption.MERGE_INTO_SINGLE_FILE.name(), Boolean.FALSE);
      variableMap.setValue(ExportOption.SAVE_INTO_SEPARATE_FILES.name(), Boolean.TRUE);
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isTrue());

      // test export location
      variableMap.setValue(ExportOption.AS_HTML_TO_FILE.name(), Boolean.TRUE);
      variableMap.setValue(ExportOption.AS_HTML_TO_RESULT_EDITOR.name(), Boolean.FALSE);
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isFalse());
      Assert.assertEquals(AtsExportBlam.MUST_SELECT_EXPORT_LOCATION, result.getText());

      variableMap.setValue(AtsExportBlam.EXPORT_LOCATION, "asdf");
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isFalse());
      Assert.assertEquals(AtsExportBlam.INVALID_DIRECTORY, result.getText());

      variableMap.setValue(AtsExportBlam.EXPORT_LOCATION, System.getProperty("user.home"));
      result = blam.isEntryValid(variableMap);
      Assert.assertTrue(result.isTrue());
   }

   @Test
   public void testGetExportOptions() {
      AtsExportBlam blam = new AtsExportBlam(new ArrayList<AbstractWorkflowArtifact>());
      VariableMap variableMap = new VariableMap();
      Collection<ExportOption> exportOptions = blam.getExportOptions(variableMap);
      Assert.assertEquals(0, exportOptions.size());

      variableMap.setValue(ExportOption.AS_HTML_TO_FILE.name(), Boolean.TRUE);
      exportOptions = blam.getExportOptions(variableMap);
      Assert.assertEquals(1, exportOptions.size());
      Assert.assertEquals(ExportOption.AS_HTML_TO_FILE, exportOptions.iterator().next());
   }
}
