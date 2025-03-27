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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section.goal;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OpenWebViewLegacyHtmlAction extends AbstractWebExportAction {

   public OpenWebViewLegacyHtmlAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Web View Legacy HTML - Live (admin)", goalArt, editor, AtsImage.GLOBE);
   }

   @Override
   public void runWithException() {
      String custGuid = validateAndGetCustomizeDataGuid();
      if (Strings.isInvalid(custGuid)) {
         return;
      }

      String html = goalArt.getAtsApi().getServerEndpoints().getWorldEndpoint().getCollectionUICustomized(
         goalArt.getArtifactId(), custGuid);
      File outFile = new File("exportLegacy.html");
      try {
         Lib.writeStringToFile(html, outFile);
         Program.launch(outFile.getAbsolutePath());
      } catch (IOException ex) {
         OseeLog.log(getClass(), Level.SEVERE, Lib.exceptionToString(ex));
      }
   }

}
