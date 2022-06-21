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

package org.eclipse.osee.ats.ide.editor.tab.defects;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.core.review.ReviewDefectError;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectData;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectLabelProvider;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectRefreshListener;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectXViewer;
import org.eclipse.osee.ats.ide.workflow.review.defect.ReviewDefectValidator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
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
public class WfeDefectsTab extends WfeAbstractTab implements DefectRefreshListener {
   private Composite bodyComp;
   private ScrolledForm scrolledForm;
   public final static String ID = "ats.review.defects.tab";
   private final IAtsPeerToPeerReview review;
   private DefectXViewer xViewer;
   private WfeDefectsToolbar toolBar;
   private Label messageLabel;

   public WfeDefectsTab(WorkflowEditor editor, IAtsPeerToPeerReview review) {
      super(editor, ID, review, "Defects");
      this.review = review;
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
         messageLabel.setText("");
         messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         managedForm.getToolkit().adapt(messageLabel, true, true);

         xViewer = new DefectXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, null, review);
         xViewer.setContentProvider(new DefectContentProvider(xViewer));
         xViewer.setLabelProvider(new DefectLabelProvider(xViewer));
         xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
         getSite().setSelectionProvider(xViewer);

         xViewer.loadTable(this);

         updateTitleBar(managedForm);
         createToolbar(managedForm);
         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

         managedForm.reflow(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refreshMessageLabel() {
      Jobs.startJob(new Job("Refresh Defect Label") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               DefectData data = new DefectData();
               data.setError(
                  ReviewDefectValidator.isValid(AtsApiService.get().getQueryServiceIde().getArtifact(review)));
               refreshMessageLabel(data);
            } catch (Exception ex) {
               // do nothing
            }
            return Status.OK_STATUS;
         }
      }, false);

   }

   public void refreshMessageLabel(DefectData data) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               if (!Widgets.isAccessible(messageLabel)) {
                  return;
               }
               ReviewDefectError error = data.getError();
               if (error == ReviewDefectError.AllItemsMustBeMarkedAndClosed) {
                  messageLabel.setText(
                     "All items must be marked for severity, disposition and closed.  Select icon in cell or right-click to update field.");
                  messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
               } else {
                  messageLabel.setText(
                     "Select \"New Defect\" to add.  Select icon in cell or right-click to update field.");
                  messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   @Override
   public void refresh() {
      if (xViewer != null) {
         xViewer.loadTable(this);
      }
   }

   @Override
   public void refreshCompleted(DefectData data) {
      xViewer.loadTable(data);
      refreshMessageLabel(data);
   }

   @Override
   public IToolBarManager createToolbar(IManagedForm managedForm) {

      toolBar = new WfeDefectsToolbar(scrolledForm, xViewer, review);
      toolBar.build();

      return super.createToolbar(managedForm);
   }

}
