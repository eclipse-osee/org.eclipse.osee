/*
 * Created on Jan 31, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.export;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAPrint;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AtsExportManager {

   public enum ExportOption {
      NONE, POPUP_DIALOG, AS_HTML, AS_PDF, SINGLE_FILE, MULTIPLE_FILES, INCLUDE_TASKLIST
   };

   public static Collection<StateMachineArtifact> getSmaArts(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      Set<StateMachineArtifact> smaArts = new HashSet<StateMachineArtifact>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof StateMachineArtifact) {
            smaArts.add((StateMachineArtifact) artifact);
         } else if (artifact instanceof ActionArtifact) {
            smaArts.addAll(((ActionArtifact) artifact).getTeamWorkFlowArtifacts());
         }
      }
      return smaArts;
   }

   public static Collection<StateMachineArtifact> getSmaArts(ISelection selection) throws OseeCoreException {
      Set<StateMachineArtifact> smaArts = new HashSet<StateMachineArtifact>();
      if (selection != null) {
         Iterator<?> selectionIterator = ((IStructuredSelection) selection).iterator();
         while (selectionIterator.hasNext()) {
            Object selectedObject = selectionIterator.next();

            if (selectedObject instanceof Match) {
               selectedObject = ((Match) selectedObject).getElement();
            } else if (selectedObject instanceof IAdaptable) {
               selectedObject = ((IAdaptable) selectedObject).getAdapter(Artifact.class);
            }

            if (selectedObject instanceof StateMachineArtifact) {
               smaArts.add((StateMachineArtifact) selectedObject);
            } else if (selectedObject instanceof ActionArtifact) {
               smaArts.addAll(((ActionArtifact) selectedObject).getTeamWorkFlowArtifacts());
            } else {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Expected selection to be of type Artifact");
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
         if (dialog.open() == WizardDialog.OK) {
            Collection<ExportOption> selectedExportOptions = exportWizard.getSelectedExportOptions();
            boolean singleFile = selectedExportOptions.contains(ExportOption.SINGLE_FILE);
            boolean multipleFile = selectedExportOptions.contains(ExportOption.MULTIPLE_FILES);
            boolean includeTaskList = selectedExportOptions.contains(ExportOption.INCLUDE_TASKLIST);
            if (selectedExportOptions.contains(ExportOption.AS_HTML)) {
               StringBuffer singleSb = new StringBuffer();
               for (Artifact artifact : artifacts) {
                  if (artifact instanceof StateMachineArtifact) {
                     SMAEditor.editArtifact((StateMachineArtifact) artifact, true);
                     SMAEditor editor = SMAEditor.getSmaEditor((StateMachineArtifact) artifact);
                     SMAPrint smaPrint =
                           new SMAPrint(((StateMachineArtifact) artifact).getSmaMgr(), editor.getWorkFlowTab(),
                                 (includeTaskList ? editor.getTaskComposite() : null));
                     String html = smaPrint.getResultData().getReport("").getManipulatedHtml();
                     if (multipleFile) {
                        try {
                           File file = new File("C:\\UserData\\" + artifact.getHumanReadableId() + ".html");
                           Lib.writeStringToFile(html, file);
                           Program.launch(file.getAbsolutePath());
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
                     File file = new File("C:\\UserData\\ATS_Export.html");
                     Lib.writeStringToFile(singleSb.toString(), file);
                     Program.launch(file.getAbsolutePath());
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
}
