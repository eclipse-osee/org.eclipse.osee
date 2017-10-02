/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.column.ev.WorkPackageColumnUI;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class XWorkPackageWidget extends GenericXWidget implements IArtifactWidget {
   public static final String WIDGET_ID = XWorkPackageWidget.class.getSimpleName();
   protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

   private Composite composite;
   private AbstractWorkflowArtifact workflow;
   private Text textWidget;
   private Button button;

   public XWorkPackageWidget() {
      super("Work Package");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      composite = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout(3, false);
      gL.marginWidth = 0;
      gL.marginHeight = 0;
      composite.setLayout(gL);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      // Create List Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText("Work Package:");
      }

      createButton(composite);

      textWidget = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      data.widthHint = SIZING_TEXT_FIELD_WIDTH;
      textWidget.setLayoutData(data);
      textWidget.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      textWidget.setDoubleClickEnabled(false);
      textWidget.addListener(SWT.MouseDoubleClick, new Listener() {
         @Override
         public void handleEvent(Event event) {
            handlePromptChange();
         }

      });

      refreshText();
   }

   private void createButton(Composite parent) {
      button = new Button(parent, SWT.PUSH);
      button.setText("Select");
      button.setLayoutData(new GridData());
      button.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(Event event) {
            handlePromptChange();
         }
      });
   }

   private void handlePromptChange() {
      if (workflow.isInWork()) {
         WorkPackageColumnUI.promptChangeActivityId(workflow, true);
      } else {
         AWorkbench.popup("Work Package can not be set on completed/cancelled workflow");
      }

      refreshText();
   }

   @Override
   public void dispose() {
      if (composite != null) {
         composite.dispose();
      }
   }

   @Override
   public Control getControl() {
      return textWidget;
   }

   public Control getButtonControl() {
      return button;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (getControl() != null && !getControl().isDisposed()) {
         getControl().setEnabled(editable);
      }
      if (getButtonControl() != null && !getButtonControl().isDisposed()) {
         getButtonControl().setEnabled(editable);
      }
   }

   @Override
   public String getReportData() {
      String result = "";
      try {
         result = AtsClientService.get().getColumnService().getColumn(AtsColumnId.ActivityId).getColumnText(workflow);
      } catch (Exception ex) {
         result = "Error resolving work package (see log for details)";
         OseeLog.log(Activator.class, Level.SEVERE, result, ex);
      }
      return result;
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, org.eclipse.osee.ats.internal.Activator.PLUGIN_ID,
            "Must select a Work Package");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return AtsClientService.get().getEarnedValueService().getWorkPackage((IAtsWorkItem) workflow) == null;
   }

   @Override
   public void setFocus() {
      textWidget.setFocus();
   }

   @Override
   public void setDisplayLabel(final String displayLabel) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            XWorkPackageWidget.super.setDisplayLabel(displayLabel);
            getLabelWidget().setText(displayLabel);
         }
      });
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         this.workflow = (AbstractWorkflowArtifact) artifact;
      }
   }

   @Override
   public Artifact getArtifact()  {
      return workflow;
   }

   @Override
   public void saveToArtifact()  {
      // do nothing
   }

   @Override
   public void revert()  {
      // do nothing
   }

   @Override
   public Result isDirty()  {
      return Result.FalseResult;
   }

   private void refreshText() {
      String workPackageStr = getReportData();
      if (Strings.isValid(workPackageStr)) {
         textWidget.setText(workPackageStr);
      } else {
         textWidget.setText(" -- Select A Work Package -- ");
      }
   }

   @Override
   public void refresh() {
      super.refresh();
      refreshText();
   }

}
