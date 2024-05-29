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

import java.rmi.activation.Activator;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.mdeditor.OseeMarkdownEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class OpenInstructionsAction extends AbstractWebExportAction {

   public OpenInstructionsAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Instructions", goalArt, editor, AtsImage.REPORT);
   }

   @Override
   public void runWithException() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               OseeMarkdownEditor.openOseeInfInOseeData("misc/AtsWebExportDesign.md", "AtsWebExportDesign.md",
                  getClass(), OseeMarkdownEditor.EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

}
