/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXDoubleSTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXPercentSTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class TransitionStatusDialog extends MessageDialog {

   protected Label statusLabel;
   protected XXPercentSTextWidget percentWidget = new XXPercentSTextWidget("Percent Complete");
   protected XXDoubleSTextWidget hoursWidget = new XXDoubleSTextWidget("Additional Hours Spent");
   protected XXCheckBoxWidget splitRadioWidget = new XXCheckBoxWidget("Split Hours Spent between Items");
   protected XXCheckBoxWidget eachRadioWidget = new XXCheckBoxWidget("Apply Hours Spent to each Item");
   private Button okButton;
   private final TransitionStatusData data;

   public TransitionStatusDialog(String dialogTitle, String dialogMessage, TransitionStatusData data) {
      super(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE,
         new String[] {"OK", "Cancel"}, 0);
      this.data = data;
   }

   protected void createPreCustomArea(Composite parent) {
      // do nothing
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      updateStatusLabel();

      if (data.getWorkItems().size() > 1) {
         Label label = new Label(parent, SWT.NONE);
         label.setText(
            "Mulitple objects being statused.  All objects will be set to percent\ncomplete and hours spent will be split or added into each item.");
      }

      createPreCustomArea(parent);

      boolean percentSet = false;
      if (data.isPercentRequired()) {
         percentWidget.setRequiredEntry(true);
         percentWidget.setToolTip("Enter total percent complete.");
         percentWidget.createWidgets(parent, 2);
         try {
            Integer defaultPercent = data.getDefaultPercent();
            if (defaultPercent != null) {
               data.setPercent(defaultPercent);
               percentWidget.setSelected(defaultPercent.toString());
               percentSet = true;
            } else if (data.getWorkItems().size() == 1) {
               Integer currentPercent = 0;
               AbstractWorkflowArtifact awa =
                  (AbstractWorkflowArtifact) AtsApiService.get().getQueryService().getArtifact(
                     data.getWorkItems().iterator().next());
               currentPercent = AtsApiService.get().getWorkItemMetricsService().getPercentComplete(awa);
               data.setPercent(currentPercent);
               percentWidget.setSelected(currentPercent.toString());
               percentSet = true;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         percentWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               XResultData status = percentWidget.isValid();
               if (status.isNotOK()) {
                  data.setPercent(null);
               } else {
                  String str = percentWidget.getSelectedFirst();
                  if (Strings.isNumeric(str)) {
                     data.setPercent(Integer.valueOf(str));
                  }
               }
               updateButtons();
               updateStatusLabel();
            }

         });
         percentWidget.addLabelWidgetListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
               if (e.button == 3) {
                  data.setPercent(99);
                  percentWidget.setSelected("99");
                  data.setAdditionalHours(1.0);
                  hoursWidget.setSelected("1");
                  updateStatusLabel();
               }
            }
         });
      }

      hoursWidget.setRequiredEntry(true);
      hoursWidget.setToolTip("Enter hours spent since last status entry.");
      hoursWidget.createWidgets(parent, 2);
      hoursWidget.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            XResultData status = hoursWidget.isValid();
            if (status.isNotOK()) {
               data.setAdditionalHours(null);
            } else {
               data.setAdditionalHours(Double.valueOf(hoursWidget.getSelectedFirst()));
            }
            updateButtons();
            updateStatusLabel();
         }
      });

      if (data.getWorkItems().size() > 1) {
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(2, false));
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         eachRadioWidget.createWidgets(comp, 2);
         eachRadioWidget.setSelected(data.isApplyHoursToEachItem());
         eachRadioWidget.setToolTip("Hours Spent will be added to to time spent for each object.");
         eachRadioWidget.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               data.setApplyHoursToEachItem(eachRadioWidget.isSelected());
               updateButtons();
               updateStatusLabel();
            }

         });

         splitRadioWidget.createWidgets(comp, 2);
         splitRadioWidget.setSelected(data.isSplitHoursBetweenItems());
         splitRadioWidget.setToolTip(
            "Hours Spent will be divided equaly by the number of objects " + "and added to the existing hours spent for the object.");
         splitRadioWidget.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               data.setSplitHoursBetweenItems(splitRadioWidget.isSelected());
               updateButtons();
               updateStatusLabel();
            }

         });
      }
      updateStatusLabel();
      if (!data.isPercentRequired() || percentSet) {
         hoursWidget.setFocus();
      }
      return parent;
   }

   protected void updateStatusLabel() {
      IStatus result = isComplete();
      statusLabel.setText(result.isOK() ? "" : result.getMessage());
      statusLabel.getParent().layout();
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.setEnabled(false);
      return c;
   }

   protected IStatus isComplete() {
      Result result = data.isValid();
      if (result.isFalse()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, result.getText());
      }
      return Status.OK_STATUS;
   }

   private void updateButtons() {
      okButton.setEnabled(isComplete().isOK());
   }

   public TransitionStatusData getData() {
      return data;
   }

}
