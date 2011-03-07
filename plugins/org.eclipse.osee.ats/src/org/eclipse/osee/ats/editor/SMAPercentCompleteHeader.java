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
package org.eclipse.osee.ats.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
public class SMAPercentCompleteHeader extends Composite {

   private final static String PERCENT_COMPLETE = "Percent Complete:";
   Label valueLabel;
   private final AbstractWorkflowArtifact awa;

   public SMAPercentCompleteHeader(Composite parent, int style, final AbstractWorkflowArtifact sma, final SMAEditor editor) {
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
                     PromptChangeUtil.promptChangeAttribute(sma, AtsAttributeTypes.PercentComplete, true, false);
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

   }

   public String getPercentCompleteStr() throws OseeCoreException {
      int awaPercent = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
      int totalPecent = PercentCompleteTotalColumn.getPercentCompleteTotal(awa);
      if (awaPercent != totalPecent) {
         return String.format("%d | %d", awaPercent, totalPecent);
      } else {
         return String.valueOf(awaPercent);
      }
   }

   public void refresh() throws OseeCoreException {
      updateLabel(awa);
   }

   private void updateLabel(AbstractWorkflowArtifact sma) throws OseeCoreException {
      valueLabel.setText(getPercentCompleteStr());
      valueLabel.getParent().layout();
   }

   private String getToolTip() {
      return " [Workflow Percent] | [Calculation: Sum of percent for workflow, reviews and tasks / # workflows, reviews and tasks] ";
   }
}
