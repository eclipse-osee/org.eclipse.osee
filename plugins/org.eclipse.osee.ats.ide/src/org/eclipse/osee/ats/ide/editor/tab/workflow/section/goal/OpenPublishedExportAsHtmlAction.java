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
import java.rmi.activation.Activator;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OpenPublishedExportAsHtmlAction extends AbstractWebExportAction {

   public OpenPublishedExportAsHtmlAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Published Export as HTML", goalArt, editor, AtsImage.RIGHT_ARROW_SM);
   }

   @Override
   public void runWithException() {
      String html =
         goalArt.getAtsApi().getServerEndpoints().getWorldEndpoint().getCollectionExportAsHtml(goalArt.getArtifactId());
      File outFile = new File("exportSaved.html");
      try {
         Lib.writeStringToFile(html, outFile);
         Program.launch(outFile.getAbsolutePath());
      } catch (IOException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }
   }

}
