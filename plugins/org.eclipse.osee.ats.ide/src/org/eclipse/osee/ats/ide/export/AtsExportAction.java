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
package org.eclipse.osee.ats.ide.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.WfePrint;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AtsExportAction extends Action {

   private static final String AS_PDF_NOT_IMPLEMENTED_YET = "AS_PDF Not Implemented Yet";
   public static final String ATS_EXPORT_HTML_FILE = "ATS_Export.html";
   private final ISelectedAtsArtifacts selected;
   private boolean popup = true;

   public AtsExportAction() {
      this(null);
   }

   public AtsExportAction(ISelectedAtsArtifacts selected) {
      this.selected = selected;
      setText("Export Selected ATS Artifacts");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA));
   }

   public enum ExportOption {
      AS_HTML_TO_RESULT_EDITOR,
      AS_HTML_TO_FILE,
      AS_PDF,
      MERGE_INTO_SINGLE_FILE,
      SAVE_INTO_SEPARATE_FILES,
      INCLUDE_TASKLIST;
   };

   public Result export(Collection<? extends Artifact> artifacts, Collection<ExportOption> exportOption, String fileLocation) {
      Result result = Result.TrueResult;
      boolean singleFile = exportOption.contains(ExportOption.MERGE_INTO_SINGLE_FILE);
      boolean asHtmlToFile = exportOption.contains(ExportOption.AS_HTML_TO_FILE);
      boolean asHtmlToResultEditor = exportOption.contains(ExportOption.AS_HTML_TO_RESULT_EDITOR);
      boolean multipleFile = exportOption.contains(ExportOption.SAVE_INTO_SEPARATE_FILES);
      boolean includeTaskList = exportOption.contains(ExportOption.INCLUDE_TASKLIST);
      if (asHtmlToFile || asHtmlToResultEditor) {
         StringBuffer singleSb = new StringBuffer();
         for (Artifact artifact : artifacts) {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               WfePrint smaPrint = new WfePrint(awa);
               smaPrint.setIncludeTaskList(includeTaskList);
               String html = XResultDataUI.getReport(smaPrint.getResultData(), "").getManipulatedHtml();
               if (multipleFile) {
                  try {
                     if (asHtmlToFile) {
                        File file = new File(getFileLocation(fileLocation, awa.getAtsId() + ".html"));
                        Lib.writeStringToFile(html, file);
                     }
                     if (asHtmlToResultEditor) {
                        ResultsEditor.open("Output", "Export " + awa.getAtsId(), html);
                     }
                  } catch (IOException ex) {
                     throw new OseeCoreException("Error writing to html file", ex);
                  }
               }
               if (singleFile) {
                  singleSb.append(html + AHTML.newline(3));
               }
            }
         }
         if (singleFile) {
            try {
               if (asHtmlToFile) {
                  File file = new File(getFileLocation(fileLocation, ATS_EXPORT_HTML_FILE));
                  Lib.writeStringToFile(singleSb.toString(), file);
               }
               if (asHtmlToResultEditor) {
                  ResultsEditor.open("Output", "Export ATS Artifacts", singleSb.toString());
               }
            } catch (IOException ex) {
               throw new OseeCoreException("Error writing to html file", ex);
            }
         }
         if (popup) {
            AWorkbench.popup("Export Completed", "Export Completed");
         }
      }
      if (exportOption.contains(ExportOption.AS_PDF)) {
         if (popup) {
            AWorkbench.popup("ERROR", AS_PDF_NOT_IMPLEMENTED_YET);
         } else {
            result = new Result(false, AS_PDF_NOT_IMPLEMENTED_YET);
         }
      }
      return result;
   }

   private String getFileLocation(String fileLocation, String filename) {
      if (fileLocation == null) {
         fileLocation = System.getProperty("user.home");
      }
      return fileLocation + System.getProperty("file.separator") + filename;
   }

   @Override
   public void run() {
      if (selected == null) {
         AtsExportBlam.openAtsExportBlam(new ArrayList<AbstractWorkflowArtifact>());
      } else {
         AtsExportBlam.openAtsExportBlam(
            Collections.castAll(AbstractWorkflowArtifact.class, selected.getSelectedWorkflowArtifacts()));
      }
   }

   public void setPopup(boolean popup) {
      this.popup = popup;
   }

}
