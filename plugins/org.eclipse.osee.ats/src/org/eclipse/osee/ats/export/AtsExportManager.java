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
package org.eclipse.osee.ats.export;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.SMAPrint;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AtsExportManager extends Action {

   private final TreeViewer treeViewer;

   public AtsExportManager(WorldEditor worldEditor) {
      this(worldEditor.getWorldComposite().getXViewer());
   }

   public AtsExportManager(TreeViewer treeViewer) {
      setText("Export Selected ATS Artifacts");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA));
      this.treeViewer = treeViewer;
   }

   public enum ExportOption {
      NONE,
      POPUP_DIALOG,
      AS_HTML_TO_RESULT_EDITOR,
      AS_HTML_TO_FILE,
      AS_PDF,
      MERGE_INTO_SINGLE_FILE,
      SAVE_INTO_SEPARATE_FILES,
      INCLUDE_TASKLIST;

   };

   public static Collection<AbstractWorkflowArtifact> getSmaArts(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      Set<AbstractWorkflowArtifact> smaArts = new HashSet<AbstractWorkflowArtifact>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof AbstractWorkflowArtifact) {
            smaArts.add((AbstractWorkflowArtifact) artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.Action)) {
            smaArts.addAll(ActionManager.getTeams(artifact));
         }
      }
      return smaArts;
   }

   public static Collection<AbstractWorkflowArtifact> getSmaArts(ISelection selection) throws OseeCoreException {
      Set<AbstractWorkflowArtifact> smaArts = new HashSet<AbstractWorkflowArtifact>();
      if (selection != null) {
         Iterator<?> selectionIterator = ((IStructuredSelection) selection).iterator();
         while (selectionIterator.hasNext()) {
            Object selectedObject = selectionIterator.next();

            if (selectedObject instanceof Match) {
               selectedObject = ((Match) selectedObject).getElement();
            } else if (selectedObject instanceof IAdaptable) {
               selectedObject = ((IAdaptable) selectedObject).getAdapter(Artifact.class);
            }

            if (selectedObject instanceof AbstractWorkflowArtifact) {
               smaArts.add((AbstractWorkflowArtifact) selectedObject);
            } else if (Artifacts.isOfType(selectedObject, AtsArtifactTypes.Action)) {
               smaArts.addAll(ActionManager.getTeams(selectedObject));
            } else {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Expected selection to be of type Artifact");
            }
         }
      }
      return smaArts;
   }

   public static Result export(ISelection selection, ExportOption... exportOption) throws OseeCoreException {
      return export(getSmaArts(selection), exportOption);
   }

   public static Result export(Collection<? extends Artifact> artifacts, ExportOption... exportOption) throws OseeCoreException {
      Collection<ExportOption> exportOptions = Collections.getAggregate(exportOption);
      if (exportOptions.contains(ExportOption.POPUP_DIALOG)) {
         AtsExportWizard exportWizard = new AtsExportWizard(getSmaArts(artifacts));
         WizardDialog dialog =
            new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), exportWizard);
         if (dialog.open() == Window.OK) {
            Collection<ExportOption> selectedExportOptions = exportWizard.getSelectedExportOptions();
            boolean singleFile = selectedExportOptions.contains(ExportOption.MERGE_INTO_SINGLE_FILE);
            boolean asHtmlToFile = selectedExportOptions.contains(ExportOption.AS_HTML_TO_FILE);
            boolean asHtmlToResultEditor = selectedExportOptions.contains(ExportOption.AS_HTML_TO_RESULT_EDITOR);
            boolean multipleFile = selectedExportOptions.contains(ExportOption.SAVE_INTO_SEPARATE_FILES);
            boolean includeTaskList = selectedExportOptions.contains(ExportOption.INCLUDE_TASKLIST);
            if (asHtmlToFile || asHtmlToResultEditor) {
               StringBuffer singleSb = new StringBuffer();
               for (Artifact artifact : artifacts) {
                  if (artifact instanceof AbstractWorkflowArtifact) {
                     AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
                     SMAPrint smaPrint = new SMAPrint(awa);
                     smaPrint.setIncludeTaskList(includeTaskList);
                     String html = XResultDataUI.getReport(smaPrint.getResultData(), "").getManipulatedHtml();
                     if (multipleFile) {
                        try {
                           if (asHtmlToFile) {
                              File file =
                                 new File(exportWizard.getFileLocation() + "\\" + awa.getAtsId() + ".html");
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
                        File file = new File(exportWizard.getFileLocation() + "\\ATS_Export.html");
                        Lib.writeStringToFile(singleSb.toString(), file);
                     }
                     if (asHtmlToResultEditor) {
                        ResultsEditor.open("Output", "Export ATS Artifacts", singleSb.toString());
                     }
                  } catch (IOException ex) {
                     throw new OseeCoreException("Error writing to html file", ex);
                  }
               }
               AWorkbench.popup("Export Completed", "Export Completed");
            }
            if (selectedExportOptions.contains(ExportOption.AS_PDF)) {
               AWorkbench.popup("ERROR", "AS_PDF Not Implemented Yet");
            }
         }
      }
      return Result.TrueResult;
   }

   @Override
   public void run() {
      if (treeViewer != null) {
         try {
            AtsExportManager.export(treeViewer.getSelection(), ExportOption.POPUP_DIALOG);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}
