/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.WorkflowMetrics;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WorkflowMetricsUI {

   protected Label showReleaseMetricsLabel;
   private Action selectionMetricsAction;
   private final WorldComposite worldComposite;
   protected final XFormToolkit toolkit;

   public WorkflowMetricsUI(WorldComposite worldComposite, XFormToolkit toolkit) {
      this.worldComposite = worldComposite;
      this.toolkit = toolkit;
   }

   private void updateExtraInfoLine() {
      if (selectionMetricsAction != null && selectionMetricsAction.isChecked()) {
         if (showReleaseMetricsLabel == null || showReleaseMetricsLabel.isDisposed()) {
            showReleaseMetricsLabel = toolkit.createLabel(worldComposite.getParent(), "");
            showReleaseMetricsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            showReleaseMetricsLabel.setFont(FontManager.getCourierNew12Bold());
         }
         if (worldComposite.getXViewer() != null && worldComposite.getXViewer().getSelectedWorkflowArtifacts() != null && !worldComposite.getXViewer().getSelectedWorkflowArtifacts().isEmpty()) {
            showReleaseMetricsLabel.setText(
               WorkflowMetrics.getEstRemainMetrics(worldComposite.getXViewer().getSelectedWorkflowArtifacts(), null,
                  ((TeamWorkFlowArtifact) worldComposite.getXViewer().getSelectedWorkflowArtifacts().iterator().next()).getManHrsPerDayPreference(),
                  null));
         } else {
            showReleaseMetricsLabel.setText("");
         }
      } else {
         showReleaseMetricsLabel.dispose();
      }
      worldComposite.getParent().layout();
   }

   public Action getOrCreateAction() {
      if (selectionMetricsAction == null) {
         selectionMetricsAction = new Action("Show Release Metrics by Selection - Ctrl-X", IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
               try {
                  updateExtraInfoLine();
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         };
         selectionMetricsAction.setToolTipText("Show Release Metrics by Selection - Ctrl-X");
         selectionMetricsAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.PAGE));
         addSelectionListener();
      }
      return selectionMetricsAction;
   }

   private void addSelectionListener() {
      worldComposite.getXViewer().getTree().addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (selectionMetricsAction != null) {
               if (selectionMetricsAction.isChecked()) {
                  selectionMetricsAction.run();
               }
            }
         }
      });
   }

}
