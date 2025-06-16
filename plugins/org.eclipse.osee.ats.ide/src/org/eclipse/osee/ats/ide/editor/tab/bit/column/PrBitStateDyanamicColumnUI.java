/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.editor.tab.bit.column;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ProblemReportTeamWorkflow;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.IXViewerDynamicColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.tab.bit.action.CreateNewBitAction;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ProgramVersion;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ProgramVersionTreeDialog;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class PrBitStateDyanamicColumnUI extends XViewerAtsCoreCodeXColumn implements IXViewerDynamicColumn {

   public ProgramVersion progVer;
   public Boolean changed = false;
   public static PrBitStateDyanamicColumnUI instance = new PrBitStateDyanamicColumnUI();

   public static PrBitStateDyanamicColumnUI getInstance() {
      return instance;
   }

   private PrBitStateDyanamicColumnUI() {
      super(AtsColumnTokensDefault.PrBitStateDynamicColumn, AtsApiService.get());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsCoreCodeXColumn copy() {
      PrBitStateDyanamicColumnUI newXCol = new PrBitStateDyanamicColumnUI();
      super.copy(this, newXCol);
      newXCol.setProgVer(getProgVer());
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (progVer == null) {
         return "Alt-Left-Click to Set";
      }
      if (element instanceof IAtsTeamWorkflow) {
         AtsApi atsApi = AtsApiService.get();
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) element;

         if (Artifacts.isOfType(element, ProblemReportTeamWorkflow)) {
            Collection<ArtifactToken> relatedProgVers =
               atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.ProblemReportToBid_Bid);
            for (ArtifactToken relatedProgVer : relatedProgVers) {
               if (relatedProgVer.getName().equals(progVer.getVersion().getName())) {
                  String state = atsApi.getAttributeResolver().getSoleAttributeValue(relatedProgVer,
                     AtsAttributeTypes.BitState, "");
                  return String.format("%s - [%s]", state, progVer.getVersion().getName());
               }
            }
         }
      }
      return "";
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      changed = false;
      if (treeItem.getData() instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = ((IAtsTeamWorkflow) treeItem.getData());
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               progVer = null;
               List<ProgramVersion> programVersions =
                  CreateNewBitAction.getProgramVersions(teamWf, AtsApiService.get());
               ProgramVersionTreeDialog dialog = new ProgramVersionTreeDialog(programVersions);
               if (dialog.open() == Window.OK) {
                  progVer = dialog.getChecked().iterator().next();
                  changed = true;
               }
            }
         }, true);
      }
      return changed;
   }

   @Override
   public boolean refreshColumnOnChange() {
      return true;
   }

   public ProgramVersion getProgVer() {
      return progVer;
   }

   public void setProgVer(ProgramVersion progVer) {
      this.progVer = progVer;
   }

   @Override
   public void addColumnsOnShow(Object xViewerObj, List<XViewerColumn> newXCols) {
      XViewer xViewer = (XViewer) xViewerObj;
      final PrBitStateDyanamicColumnUI thisCol = this;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            progVer = null;
            IAtsTeamWorkflow teamWf = null;
            for (TreeItem treeItem : ((WorldXViewer) xViewer).getTree().getItems()) {
               if (treeItem.getData() instanceof IAtsTeamWorkflow && (((IAtsTeamWorkflow) treeItem.getData()).getStoreObject().isOfType(
                  AtsArtifactTypes.ProblemReportTeamWorkflow))) {
                  teamWf = (IAtsTeamWorkflow) treeItem.getData();
                  break;
               }
            }
            if (teamWf == null) {
               AWorkbench.popup("No Problem Reports Found Loaded");
               return;
            }
            List<ProgramVersion> programVersions = CreateNewBitAction.getProgramVersions(teamWf, AtsApiService.get());
            ProgramVersionTreeDialog dialog = new ProgramVersionTreeDialog(programVersions);
            if (dialog.open() == Window.OK) {
               for (ProgramVersion progVer : dialog.getChecked()) {
                  PrBitStateDyanamicColumnUI newCol = new PrBitStateDyanamicColumnUI();
                  newCol.setId(newCol.getId() + "." + progVer.getVersion().getName());
                  newCol.setShow(true);
                  newCol.setProgVer(progVer);
                  newXCols.add(newCol);
               }
               // re-add this column with show=false so it can be selected again
               newXCols.add(thisCol);
            }
         }
      }, true);
   }

}
