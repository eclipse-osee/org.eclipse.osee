/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.bit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeBitTab extends WfeAbstractTab implements IArtifactEventListener {
   private Composite bodyComp;
   private ScrolledForm scrolledForm;
   public final static String ID = "ats.bit.tab";
   protected final IAtsTeamWorkflow teamWf;
   protected XBitViewer xViewer;
   protected WfeBitToolbar toolBar;
   protected Label messageLabel;
   protected BuildImpactDatas bids;
   protected AtsApi atsApi;

   public WfeBitTab(WorkflowEditor editor, IAtsTeamWorkflow teamWf) {
      super(editor, ID, teamWf, "Build Impact Table");
      this.teamWf = teamWf;
      atsApi = AtsApiService.get();
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      try {
         bodyComp = scrolledForm.getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
         bodyComp.setLayoutData(gd);

         final Composite mainComp = new Composite(bodyComp, SWT.BORDER);
         GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd2.widthHint = 100;
         gd2.heightHint = 100;
         mainComp.setLayoutData(gd2);
         mainComp.setLayout(ALayout.getZeroMarginLayout());
         managedForm.getToolkit().paintBordersFor(mainComp);

         messageLabel = new Label(mainComp, SWT.NONE);
         messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (atsApi.getUserService().isAtsAdmin()) {
            messageLabel.setText("Debug Here");
         }
         messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         managedForm.getToolkit().adapt(messageLabel, true, true);

         xViewer = new XBitViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, new XBitXViewerFactory(),
            editor, teamWf);

         xViewer.setContentProvider(new XBitContentProvider(xViewer));
         xViewer.setLabelProvider(new XBitLabelProvider(xViewer));
         xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
         getSite().setSelectionProvider(xViewer);

         final WfeBitTab fWfeBitTab = this;
         xViewer.getTree().addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               OseeEventManager.removeListener(fWfeBitTab);
            }
         });

         refresh();

         updateTitleBar(managedForm);
         createToolbar(managedForm);
         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

         managedForm.reflow(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      if (xViewer == null || xViewer.getTree() == null || xViewer.getTree().isDisposed()) {
         return;
      }
      Job loadJob = new Job("Loading Build Impacts") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            bids = atsApi.getServerEndpoints().getActionEndpoint().getBids(teamWf.getAtsId());

            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (!Widgets.isAccessible(bodyComp)) {
                     return;
                  }
                  storeExpandState();
                  xViewer.setBids(bids);
                  xViewer.loadTable();
                  restoreExpandState();
               }
            });

            return Status.OK_STATUS;
         }
      };
      Operations.scheduleJob(loadJob, true, Job.SHORT, null);

   }

   private final Set<ArtifactToken> expanded = new HashSet<>();
   private void storeExpandState() {
      if (!Widgets.isAccessible(bodyComp)) {
         return;
      }
      expanded.clear();
      for (TreeItem item : xViewer.getVisibleItems()) {
         if (item.getExpanded()) {
            BuildImpactData bid = (BuildImpactData) item.getData();
            expanded.add(bid.getBidArt());
         }
      }
   }

   private void restoreExpandState() {
      if (!Widgets.isAccessible(bodyComp)) {
         return;
      }
      for (TreeItem item : xViewer.getVisibleItems()) {
         BuildImpactData bid = (BuildImpactData) item.getData();
         if (expanded.contains(bid.getBidArt())) {
            xViewer.expandToLevel(bid, 2);
         }
      }
   }

   @Override
   public IToolBarManager createToolbar(IManagedForm managedForm) {

      toolBar = new WfeBitToolbar(scrolledForm, xViewer, editor, teamWf);
      toolBar.build();

      return super.createToolbar(managedForm);
   }

   public XBitViewer getxViewer() {
      return xViewer;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtilClient.getAtsObjectEventFilters();
   }

   /**
    * XBitViewer listens to it's own events because BID artifacts and sibling workflows can change independent of main
    * team wf and refresh of table is needed for those.
    */
   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (editor.getBitTab() == null) {
         return;
      }
      boolean refresh = false;
      for (Artifact art : artifactEvent.getCacheArtifacts(EventModType.values())) {
         if (art.isOfType(AtsArtifactTypes.BuildImpactData)) {
            refresh = true;
            break;
         } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            if (atsApi.getRelationResolver().getRelatedOrSentinel(art,
               AtsRelationTypes.BuildImpactDataToTeamWf_Bid).isValid()) {
               refresh = true;
               break;
            }
         }
      }

      if (refresh) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               refresh();
            }
         });
      }

   }

   public ArtifactTypeToken getBuildImpactDataType() {
      return AtsArtifactTypes.BuildImpactData;
   }
}
