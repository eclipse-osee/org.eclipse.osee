/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.text.NumberFormat;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.IntegerDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfePercentCompleteHeader extends Composite {

   private final static String PERCENT_COMPLETE = "Percent Complete:";
   private final String LABEL_TOOL_TIP =
      "[Workflow Percent] | [Calculation: Sum of percent for workflow, reviews and tasks / # workflows, reviews and tasks]";

   private final String LINK_TOOL_TIP = "Select to set percent of workflow complete.";

   Label valueLabel;
   private final IAtsWorkItem workItem;

   public WfePercentCompleteHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      setLayout(ALayout.getZeroMarginLayout(2, false));

      try {
         if (!workItem.isCancelled() && !workItem.isCompleted()) {
            Hyperlink link = editor.getToolkit().createHyperlink(this, PERCENT_COMPLETE, SWT.NONE);
            link.setToolTipText(LINK_TOOL_TIP);
            link.addHyperlinkListener(new IHyperlinkListener() {

               @Override
               public void linkEntered(HyperlinkEvent e) {
                  // do nothing
               }

               @Override
               public void linkExited(HyperlinkEvent e) {
                  // do nothing
               }

               @Override
               public void linkActivated(HyperlinkEvent e) {
                  try {
                     if (editor.isDirty()) {
                        editor.doSave(null);
                     }
                     IntegerDialog dialog = new IntegerDialog("Enter Percent Complete",
                        "Enter Percent Complete (0 to 99)\n\n(use Transition to mark complete.)", 0, 99);
                     dialog.setNumberFormat(NumberFormat.getIntegerInstance());
                     int percent = 0;
                     if (AtsApiService.get().getWorkItemMetricsService().getPercentComplete(workItem) != null) {
                        percent = AtsApiService.get().getWorkItemMetricsService().getPercentComplete(workItem);
                     }
                     dialog.setEntry(String.valueOf(percent));
                     if (dialog.open() == Window.OK) {
                        Integer intValue = dialog.getInt();
                        IAtsChangeSet changes =
                           AtsApiService.get().createChangeSet("ATS Workflow Editor - set Percent Complete");
                        AtsApiService.get().getWorkItemMetricsService().setPercentComplete(workItem, intValue, changes);
                        changes.executeIfNeeded();
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
         } else {
            Label origLabel = editor.getToolkit().createLabel(this, PERCENT_COMPLETE);
            origLabel.setLayoutData(new GridData());
         }

         valueLabel = editor.getToolkit().createLabel(this, "0");
         valueLabel.setToolTipText(LABEL_TOOL_TIP);
         valueLabel.setLayoutData(new GridData());
         refresh();
      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   public String getPercentCompleteStr() {
      int awaPercent = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.PercentComplete, 0);
      int totalPecent = AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(workItem);
      if (awaPercent != totalPecent) {
         return String.format("%d | %d", awaPercent, totalPecent);
      } else {
         return String.valueOf(awaPercent);
      }
   }

   public void refresh() {
      valueLabel.setText(getPercentCompleteStr());
      valueLabel.getParent().layout(true);
      valueLabel.getParent().getParent().layout(true);
   }

}
