/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.Import;

import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class ImportGeneralDocumentsBlamTest extends AbstractBlamTest {

   public ImportGeneralDocumentsBlamTest() {
      super(new ImportGeneralDocumentsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      // Remove when demo operation test is implemented in Blam
      if (DemoUtil.isDemoDatabase()) {
         return;
      }

      setSelected(ImportGeneralDocumentsBlam.FILE_SELECTION, "C:/MyFile.xml");
      setSelected(ImportGeneralDocumentsBlam.PARENT_ARTIFACT, DemoUtil.getSawCodeCommittedWf());

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(ImportGeneralDocumentsBlam.FILE_SELECTION));
      rd.assertNotNull(varMap.getValue(ImportGeneralDocumentsBlam.PARENT_ARTIFACT));

      // Operation and vars tested in another test
   }

}
