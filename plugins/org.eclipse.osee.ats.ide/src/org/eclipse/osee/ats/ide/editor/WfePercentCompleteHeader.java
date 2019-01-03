/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor;

import java.text.NumberFormat;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
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
   Label valueLabel;
   private final AbstractWorkflowArtifact awa;

   public WfePercentCompleteHeader(Composite parent, int style, final AbstractWorkflowArtifact sma, final WorkflowEditor editor) {
      super(parent, style);
      this.awa = sma;
      setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         if (!sma.isCancelled() && !sma.isCompleted()) {
            Hyperlink link = editor.getToolkit().createHyperlink(this, PERCENT_COMPLETE, SWT.NONE);
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
                     if (sma.getStateMgr().getPercentCompleteValue() != null) {
                        percent = sma.getStateMgr().getPercentCompleteValue();
                     }
                     dialog.setEntry(String.valueOf(percent));
                     if (dialog.open() == 0) {
                        Integer intValue = dialog.getInt();
                        sma.getStateMgr().setPercentCompleteValue(intValue);
                        AtsClientService.get().getStoreService().executeChangeSet(
                           "ATS Workflow Editor - set Percent Complete", sma);
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
         valueLabel.setToolTipText(getToolTip());
         valueLabel.setLayoutData(new GridData());
         updateLabel(sma);

      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   public String getPercentCompleteStr() {
      int awaPercent = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
      int totalPecent = PercentCompleteTotalUtil.getPercentCompleteTotal(awa, AtsClientService.get().getServices());
      if (awaPercent != totalPecent) {
         return String.format("%d | %d", awaPercent, totalPecent);
      } else {
         return String.valueOf(awaPercent);
      }
   }

   public void refresh() {
      updateLabel(awa);
   }

   private void updateLabel(AbstractWorkflowArtifact sma) {
      valueLabel.setText(getPercentCompleteStr());
      valueLabel.getParent().layout();
   }

   private String getToolTip() {
      return " [Workflow Percent] | [Calculation: Sum of percent for workflow, reviews and tasks / # workflows, reviews and tasks] ";
   }
}
