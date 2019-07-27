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
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
public class WfeEstimatedHoursHeader extends Composite implements IWfeEventHandle {

   private final static String LABEL = "Estimated Hours:";
   Label valueLabel;
   private final IAtsWorkItem workItem;

   public WfeEstimatedHoursHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         if (!workItem.isCancelled() && !workItem.isCompleted()) {
            Hyperlink link = editor.getToolkit().createHyperlink(this, LABEL, SWT.NONE);
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
                     PromptChangeUtil.promptChangeAttribute(workItem, AtsAttributeTypes.EstimatedHours, true, false);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
         } else {
            Label origLabel = editor.getToolkit().createLabel(this, LABEL);
            origLabel.setLayoutData(new GridData());
         }

         valueLabel = editor.getToolkit().createLabel(this, "0.0");
         valueLabel.setToolTipText(getToolTip());
         valueLabel.setLayoutData(new GridData());
         refresh();
         editor.registerEvent(this, AtsAttributeTypes.EstimatedHours);

      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   private String getEstHoursStr() {
      double totalEst = 0;
      double awaEst = AtsClientService.get().getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.EstimatedHours, 0.0);
      if (awaEst < 0) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP,
            "Negative estimated hours not allowed.  Please set to the expected estimated hours.");
         PromptChangeUtil.promptChangeAttribute(workItem, AtsAttributeTypes.EstimatedHours, true, false);
      } else {
         totalEst = AtsClientService.get().getEarnedValueService().getEstimatedHoursTotal(workItem);
      }
      if (awaEst != totalEst) {
         return String.format("%s | %s", AtsUtil.doubleToI18nString(awaEst), AtsUtil.doubleToI18nString(totalEst));
      } else {
         return AtsUtil.doubleToI18nString(awaEst);
      }
   }

   @Override
   public void refresh() {
      valueLabel.setText(getEstHoursStr());
      valueLabel.getParent().getParent().layout();
   }

   private String getToolTip() {
      return "[Workflow Estimate] | [Calculation: Sum estimated hours for workflow and all tasks and reviews]";
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }

}
