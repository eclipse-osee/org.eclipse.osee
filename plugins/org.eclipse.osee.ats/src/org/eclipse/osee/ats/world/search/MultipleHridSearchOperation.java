/*
 * Created on May 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.SmaWorkflowLabelProvider;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.LegacyPCRActions;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecoratorPreferences;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class MultipleHridSearchOperation extends AbstractOperation implements IOperationFactory {
   private String enteredIds = "";
   private boolean includeArtIds = false;
   private Branch branch;
   List<String> ids = new ArrayList<String>();
   Set<Artifact> resultAtsArts = new HashSet<Artifact>();
   Set<Artifact> resultNonAtsArts = new HashSet<Artifact>();
   Set<Artifact> artifacts = new HashSet<Artifact>();
   private final AtsEditor atsEditor;

   public MultipleHridSearchOperation(String operationName, AtsEditor atsEditor) {
      super(operationName, AtsPlugin.PLUGIN_ID);
      this.atsEditor = atsEditor;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (getUserEntry()) {
         extractIds();
         if (ids.size() == 0) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Must Enter Valid Id");
            return;
         }
         searchAndSplitResults();
         if (resultAtsArts.size() == 0 && resultNonAtsArts.size() == 0) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
                  "Invalid HRID/Guid/Legacy PCR Id(s): " + Collections.toString(ids, ", "));
            return;
         }
         if (resultNonAtsArts.size() > 0) {
            ArtifactEditor.editArtifacts(resultNonAtsArts);
         }
         if (resultAtsArts.size() > 0) {
            if (atsEditor == AtsEditor.WorkflowEditor) {
               openWorkflowEditor(resultAtsArts);
            } else if (atsEditor == AtsEditor.ChangeReport) {
               openChangeReport(resultAtsArts, enteredIds);
            } else {
               WorldEditor.open(new WorldEditorSimpleProvider(getWorldViewName(), resultAtsArts));
            }
         }
      }
   }

   private void openChangeReport(Set<Artifact> artifacts, final String enteredIds) {
      try {
         final Set<Artifact> addedArts = new HashSet<Artifact>();
         for (Artifact artifact : artifacts) {
            if (artifact instanceof ActionArtifact) {
               for (TeamWorkFlowArtifact team : ((ActionArtifact) artifact).getTeamWorkFlowArtifacts()) {
                  if (team.getBranchMgr().isCommittedBranchExists() || team.getBranchMgr().isWorkingBranchInWork()) {
                     addedArts.add(team);
                  }
               }
            }
            if (artifact instanceof TeamWorkFlowArtifact) {
               if (((TeamWorkFlowArtifact) artifact).getBranchMgr().isCommittedBranchExists() || ((TeamWorkFlowArtifact) artifact).getBranchMgr().isWorkingBranchInWork()) {
                  addedArts.add(artifact);
               }
            }
         }
         if (addedArts.size() == 1) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  for (Artifact art : addedArts) {
                     if (art instanceof TeamWorkFlowArtifact) {
                        ((TeamWorkFlowArtifact) art).getBranchMgr().showChangeReport();
                     }
                  }
               }
            });
         } else if (addedArts.size() > 0) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  ArtifactDecoratorPreferences artDecorator = new ArtifactDecoratorPreferences();
                  artDecorator.setShowArtBranch(true);
                  artDecorator.setShowArtType(true);
                  SimpleCheckFilteredTreeDialog dialog =
                        new SimpleCheckFilteredTreeDialog("Select Available Change Reports",
                              "Select available Change Reports to run.", new ArrayTreeContentProvider(),
                              new ArtifactLabelProvider(artDecorator), new ArtifactViewerSorter(), 0, Integer.MAX_VALUE);
                  dialog.setInput(addedArts);
                  if (dialog.open() == 0) {
                     if (dialog.getResult().length == 0) {
                        return;
                     }
                     for (Object obj : dialog.getResult()) {
                        ((TeamWorkFlowArtifact) obj).getBranchMgr().showChangeReport();
                     }
                  }
               }
            });
         } else {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Open Change Reports",
                        "No change report exists for " + enteredIds);
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void openWorkflowEditor(final Set<Artifact> resultAtsArts) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            Artifact artifact = null;
            if (resultAtsArts.size() == 1) {
               artifact = resultAtsArts.iterator().next();
            } else {
               ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
               ld.setContentProvider(new ArrayContentProvider());
               ld.setLabelProvider(new SmaWorkflowLabelProvider());
               ld.setTitle("Select Workflow");
               ld.setMessage("Select Workflow");
               ld.setInput(resultAtsArts);
               if (ld.open() == 0) {
                  artifact = (Artifact) ld.getResult()[0];
               }
            }
            if (artifact instanceof ActionArtifact) {
               AtsUtil.openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               SMAEditor.editArtifact(artifact);
            }
         }
      });
   }

   private void searchAndSplitResults() throws OseeCoreException {
      resultAtsArts.addAll(LegacyPCRActions.getTeamsTeamWorkflowArtifacts(ids,
            (Collection<TeamDefinitionArtifact>) null));

      // This does artId search
      if (includeArtIds && branch != null) {
         for (Artifact art : ArtifactQuery.getArtifactListFromIds(Lib.stringToIntegerList(enteredIds), branch)) {
            artifacts.add(art);
         }
      }
      // This does hrid/guid search
      for (Artifact art : ArtifactQuery.getArtifactListFromIds(ids, AtsUtil.getAtsBranch())) {
         artifacts.add(art);
      }

      for (Artifact art : artifacts) {
         if (art instanceof IATSArtifact) {
            resultAtsArts.add(art);
         } else {
            resultNonAtsArts.add(art);
         }
      }
   }

   private void extractIds() {
      for (String str : enteredIds.split(",")) {
         str = str.replaceAll("^\\s+", "");
         str = str.replaceAll("\\s+$", "");
         if (!str.equals("")) {
            ids.add(str);
         }
         // allow for lower case hrids
         if (str.length() == 5) {
            if (!ids.contains(str.toUpperCase())) {
               ids.add(str.toUpperCase());
            }
         }
      }
   }

   private String getWorldViewName() throws OseeCoreException {
      return String.format(getName() + " - %s", enteredIds);
   }

   private boolean getUserEntry() throws OseeCoreException {
      EntryJob job = new EntryJob();
      Displays.ensureInDisplayThread(job, true);
      return job.isValid();
   }

   public class EntryJob implements Runnable {
      boolean valid;

      @Override
      public void run() {
         EntryDialog ed = null;
         if (AtsUtil.isAtsAdmin()) {
            ed = new EntryCheckDialog(getName(), "Enter Legacy ID, Guid or HRID (comma separated)", "Include ArtIds");
         } else {
            ed =
                  new EntryDialog(Display.getCurrent().getActiveShell(), getName(), null,
                        "Enter Legacy ID, Guid or HRID (comma separated)", MessageDialog.QUESTION, new String[] {"OK",
                              "Cancel"}, 0);
         }
         int response = ed.open();
         if (response == 0) {
            enteredIds = ed.getEntry();
            if (ed instanceof EntryCheckDialog) {
               includeArtIds = ((EntryCheckDialog) ed).isChecked();
               if (includeArtIds) {
                  branch = BranchSelectionDialog.getBranchFromUser();
               }
               valid = true;
            }
            if (!Strings.isValid(enteredIds)) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Must Enter Valid Id");
               valid = false;
            }
            if (enteredIds.equals("oseerocks") || enteredIds.equals("osee rocks")) {
               AWorkbench.popup("Confirmation", "Confirmed!  Osee Rocks!");
               valid = false;
            }
            if (enteredIds.equals("purple icons")) {
               AWorkbench.popup("Confirmation", "Yeehaw, Purple Icons Rule!!");
               ArtifactImageManager.setOverrideImageEnum(FrameworkImage.PURPLE);
               valid = false;
            }
         }
      }

      public boolean isValid() {
         return valid;
      }
   }

   @Override
   public IOperation createOperation() {
      return this;
   }

}
