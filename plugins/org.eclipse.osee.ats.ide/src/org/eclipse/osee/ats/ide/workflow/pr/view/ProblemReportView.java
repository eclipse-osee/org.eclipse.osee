/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.workflow.pr.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.pr.PrViewData;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.framework.core.data.ArtifactResultRow;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab.IResultsEditorLabelProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerContentProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ProblemReportView extends AbstractAtsAction {

   private static final String TITLE = "Problem Report View";
   private final WorldComposite worldComposite;
   private IAtsWorkItem workItem;

   public ProblemReportView(WorldComposite worldComposite) {
      super();
      this.worldComposite = worldComposite;
      setText(TITLE);
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.PROBLEM_REPORT));
   }

   public ProblemReportView(IAtsWorkItem workItem) {
      this((WorldComposite) null);
      this.workItem = workItem;
   }

   @Override
   public void runWithException() {

      Job serverJob = new Job(TITLE) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            boolean debugOn = false;
            PrViewData prData = new PrViewData();

            ElapsedTime time = new ElapsedTime(TITLE + " - Server Report", debugOn);
            List<IAtsTeamWorkflow> prWorkflows = getPrWorkflows();
            for (IAtsTeamWorkflow prWf : prWorkflows) {
               prData.addPrWf(prWf.getArtifactToken());
            }

            ElapsedTime epTime = new ElapsedTime(TITLE + " - Server Report - endpoint call", debugOn);
            prData = AtsApiService.get().getServerEndpoints().getPrEp().generatePrView(prData);
            epTime.end(Units.SEC);

            // Turn PrView ResultRows (AR) into XViewerRows (XR) that ResultEditor/XViewer can display
            List<IResultsXViewerRow> prXrRows = new ArrayList<>();
            for (ArtifactResultRow prArRow : prData.getArtRows()) {
               ResultsXViewerRow prXrRow = new ResultsXViewerRow(prArRow.getValues());
               prXrRow.setData(prArRow);
               prXrRows.add(prXrRow);
               processChildren(prArRow.getChildren(), prXrRow);
            }

            if (prData.getRd().isErrors()) {
               XResultDataUI.report(prData.getRd(), TITLE);
            }
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  ElapsedTime time = new ElapsedTime(TITLE + " - openResultsEditor", debugOn);
                  openResultsEditor(prXrRows, TITLE + " - Server");
                  time.end(Units.MSEC);
               }
            });

            time.end(Units.SEC);

            return Status.OK_STATUS;
         }

         private void processChildren(List<ArtifactResultRow> childrenARow, ResultsXViewerRow parentXRow) {
            for (ArtifactResultRow childARow : childrenARow) {
               ResultsXViewerRow childXRow = new ResultsXViewerRow(childARow.getValues());
               childXRow.setData(childARow);
               parentXRow.getChildren().add(childXRow);
               processChildren(childARow.getChildren(), childXRow);
            }
         }
      };
      serverJob.schedule();
   }

   private void openResultsEditor(List<IResultsXViewerRow> artRows, String title) {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return title;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createPRTab(artRows));
            }
            return tabs;
         }

         @Override
         public boolean expandAll() {
            return artRows.size() == 1;
         }

      });
   }

   private IResultsEditorTab createPRTab(List<IResultsXViewerRow> artRows) {
      List<XViewerColumn> artColumns = Arrays.asList( //
      // @formatter:off
         ProblemrReportViewColumns.ArtifactTypeCol,
         ProblemrReportViewColumns.AtsIdCol,
         ProblemrReportViewColumns.ProgramCol,
         ProblemrReportViewColumns.NameCol,
         ProblemrReportViewColumns.StateCol,
         ProblemrReportViewColumns.BIStatusCol,
         ProblemrReportViewColumns.IdCol
      // @formatter:on
      );

      IResultsEditorLabelProvider provider = new IResultsEditorLabelProvider() {

         @Override
         public XViewerLabelProvider getLabelProvider(ResultsXViewer xViewer) {
            return new ProblemReportLabelProvider(xViewer);
         }

      };

      return new ResultsEditorTableTab("Problem Report(s)", artColumns, artRows, new ResultsXViewerContentProvider(),
         provider);
   }

   @SuppressWarnings("unchecked")
   private List<IAtsTeamWorkflow> getPrWorkflows() {
      Collection<Object> objects = null;
      if (worldComposite != null) {
         objects = (Collection<Object>) worldComposite.getXViewer().getInput();
      } else {
         objects = Arrays.asList(workItem);
      }
      List<IAtsTeamWorkflow> prWorkflows = new ArrayList<>();
      for (Object obj : objects) {
         if (obj instanceof IAtsWorkItem && workItem.isOfType(AtsArtifactTypes.ProblemReportTeamWorkflow)) {
            prWorkflows.add((IAtsTeamWorkflow) workItem);
         } else if (obj instanceof IAtsAction) {
            for (IAtsTeamWorkflow teamWf : ((IAtsAction) obj).getTeamWorkflows()) {
               if (teamWf.isOfType(AtsArtifactTypes.ProblemReportTeamWorkflow)) {
                  prWorkflows.add(teamWf);
               }
            }
         }
      }
      return prWorkflows;
   }

}
