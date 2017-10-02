/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XPercent;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class TransitionStatusDialog extends MessageDialog {

   protected Label statusLabel;
   protected XPercent percent = new XPercent("Percent Complete");
   protected XFloat hours = new XFloat("Additional Hours Spent");
   protected XRadioButton splitRadio = new XRadioButton("Split Hours Spent between Items");
   protected XRadioButton eachRadio = new XRadioButton("Apply Hours Spent to each Item");
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
         percent.setRequiredEntry(true);
         percent.setToolTip("Enter total percent complete.");
         percent.createWidgets(parent, 2);
         try {
            Integer defaultPercent = data.getDefaultPercent();
            if (defaultPercent != null) {
               data.setPercent(defaultPercent);
               percent.set(defaultPercent);
               percentSet = true;
            } else if (data.getWorkItems().size() == 1) {
               int currentPercent = 0;
               AbstractWorkflowArtifact awa =
                  (AbstractWorkflowArtifact) AtsClientService.get().getArtifact(data.getWorkItems().iterator().next());
               if (!AtsClientService.get().getWorkDefinitionService().isStateWeightingEnabled(
                  awa.getWorkDefinition())) {
                  currentPercent = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
               } else {
                  currentPercent = awa.getStateMgr().getPercentComplete(awa.getCurrentStateName());
               }
               data.setPercent(currentPercent);
               percent.set(currentPercent);
               percentSet = true;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         percent.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
               IStatus status = percent.isValid();
               if (status.getSeverity() != IStatus.OK) {
                  data.setPercent(null);
               } else {
                  data.setPercent(percent.getInt());
               }
               updateButtons();
               updateStatusLabel();
            };
         });
         percent.getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  data.setPercent(99);
                  percent.set("99");
                  data.setAdditionalHours(1.0);
                  hours.set("1");
                  updateStatusLabel();
               }
            }
         });
      }

      hours.setRequiredEntry(true);
      hours.setToolTip("Enter hours spent since last status entry.");
      hours.createWidgets(parent, 2);
      hours.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            IStatus status = hours.isValid();
            if (status.getSeverity() != IStatus.OK) {
               data.setAdditionalHours(null);
            } else {
               data.setAdditionalHours(hours.getFloat());
            }
            updateButtons();
            updateStatusLabel();
         };
      });

      if (data.getWorkItems().size() > 1) {
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(2, false));
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         eachRadio.createWidgets(comp, 2);
         eachRadio.setSelected(data.isApplyHoursToEachItem());
         eachRadio.setToolTip("Hours Spent will be added to to time spent for each object.");
         eachRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               data.setApplyHoursToEachItem(eachRadio.isSelected());
               updateButtons();
               updateStatusLabel();
            }

         });

         splitRadio.createWidgets(comp, 2);
         splitRadio.setSelected(data.isSplitHoursBetweenItems());
         splitRadio.setToolTip(
            "Hours Spent will be divided equaly by the number of objects " + "and added to the existing hours spent for the object.");
         splitRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               data.setSplitHoursBetweenItems(splitRadio.isSelected());
               updateButtons();
               updateStatusLabel();
            }

         });
      }
      updateStatusLabel();
      if (!data.isPercentRequired() || percentSet) {
         hours.setFocus();
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
