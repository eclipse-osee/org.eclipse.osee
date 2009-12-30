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

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XPercent;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class SMAStatusDialog extends MessageDialog {

   protected Label statusLabel;
   protected XPercent percent = new XPercent("Percent Complete", "");
   protected XFloat hours = new XFloat("Additional Hours Spent", "");
   protected XRadioButton splitRadio = new XRadioButton("Split Hours Spent between Tasks");
   protected XRadioButton eachRadio = new XRadioButton("Apply Hours Spent to each Task");
   private Button okButton;
   private final boolean showPercent;
   protected final Collection<? extends StateMachineArtifact> smas;

   public SMAStatusDialog(Shell parentShell, String dialogTitle, String dialogMessage, Collection<? extends StateMachineArtifact> smas) {
      this(parentShell, dialogTitle, dialogMessage, true, smas);
   }

   public SMAStatusDialog(Shell parentShell, String dialogTitle, String dialogMessage, boolean showPercent, Collection<? extends StateMachineArtifact> smas) {
      super(parentShell, dialogTitle, null, dialogMessage, MessageDialog.NONE, new String[] {"OK", "Cancel"}, 0);
      this.showPercent = showPercent;
      this.smas = smas;
   }

   protected void createPreCustomArea(Composite parent) {
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      boolean hasTask = false;
      for (StateMachineArtifact sma : smas)
         if (sma instanceof TaskArtifact) hasTask = true;

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      updateStatusLabel();

      if (smas.size() > 1) {
         Label label = new Label(parent, SWT.NONE);
         label.setText("Mulitple objects being statused.  All objects will be " + "set to percent\ncomplete and hours spent will be split or added into each task.");
      }

      createPreCustomArea(parent);

      if (hasTask) (new Label(parent, SWT.NONE)).setText("Task will auto-transition to complete when statused 100%.\n" + "Make all other changes to Task prior to statusing 100%.");

      if (showPercent) {
         percent.setRequiredEntry(true);
         percent.setToolTip("Enter total percent complete.");
         percent.createWidgets(parent, 2);
         try {
            if (smas.size() == 1) percent.set(smas.iterator().next().getStateMgr().getPercentComplete());
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
         percent.addModifyListener(new ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
               updateButtons();
               updateStatusLabel();
            };
         });
         percent.getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  percent.set("100");
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
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            updateButtons();
            updateStatusLabel();
         };
      });

      if (smas.size() > 1) {
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(2, false));
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         eachRadio.createWidgets(comp, 2);
         eachRadio.setSelected(false);
         eachRadio.setToolTip("Hours Spent will be added to to time spent for each object.");

         splitRadio.createWidgets(comp, 2);
         splitRadio.setSelected(true);
         splitRadio.setToolTip("Hours Spent will be divided equaly by the number of objects " + "and added to the existing hours spent for the object.");
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

   public boolean isSplitHours() {
      return (splitRadio.isSelected());
   }

   protected IStatus isComplete() {
      IStatus status = percent.isValid();
      if (!status.isOK()) {
         return status;
      }
      status = hours.isValid();
      if (!status.isOK()) {
         return status;
      }
      if (smas.size() > 1) {
         if (!splitRadio.isSelected() && !eachRadio.isSelected()) {
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Either split or each must be selected");
         }
         if (splitRadio.isSelected() && eachRadio.isSelected()) {
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Select only split or each");
         }
      }
      return Status.OK_STATUS;
   }

   private void updateButtons() {
      okButton.setEnabled(isComplete().isOK());
   }

   public XFloat getHours() {
      return hours;
   }

   public XRadioButton getEachRadio() {
      return eachRadio;
   }

   public XRadioButton getSplitRadio() {
      return splitRadio;
   }

   public XPercent getPercent() {
      return percent;
   }

}
