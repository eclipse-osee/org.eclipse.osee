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

import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeBitTab extends WfeAbstractTab {
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
         messageLabel.setText("Debug Here");
         messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         managedForm.getToolkit().adapt(messageLabel, true, true);

         xViewer = new XBitViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, new XBitXViewerFactory(),
            xViewer, teamWf);

         xViewer.setContentProvider(new XBitContentProvider(xViewer));
         xViewer.setLabelProvider(new XBitLabelProvider(xViewer));
         xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
         getSite().setSelectionProvider(xViewer);

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
      Thread loadBits = new Thread("Load Build Impact Table") {

         @Override
         public void run() {

            bids = atsApi.getServerEndpoints().getActionEndpoint().getBids(teamWf.getAtsId());

            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  xViewer.setBids(bids);
                  xViewer.loadTable();
               }
            });
         }
      };
      loadBits.start();

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

}
