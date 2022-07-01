/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WfeStateCreatedOrigHeader extends Composite {

   private final IAtsWorkItem workItem;
   Label stateValueLabel, createdValueLabel;
   private final WfeOriginatorHeader originatorHeader;

   public WfeStateCreatedOrigHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(3, true));
      editor.getToolkit().adapt(this);

      try {
         stateValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Current State: ", "");
         createdValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Created: ", "");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      originatorHeader = new WfeOriginatorHeader(this, SWT.NONE, workItem, editor);
      originatorHeader.setBackground(parent.getParent().getParent().getBackground());
      refresh();
   }

   public void refresh() {
      if (Widgets.isAccessible(stateValueLabel)) {
         String isBlocked = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.BlockedReason, "");
         String isHold = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.HoldReason, "");
         if (Strings.isValid(isBlocked)) {
            stateValueLabel.setText(workItem.getStateMgr().getCurrentStateName() + " (Blocked)");
            stateValueLabel.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_RED));
         } else if (Strings.isValid(isHold)) {
            stateValueLabel.setText(workItem.getStateMgr().getCurrentStateName() + " (Hold)");
            stateValueLabel.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_YELLOW));
         } else {
            stateValueLabel.setText(workItem.getStateMgr().getCurrentStateName());
            stateValueLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         }
         createdValueLabel.setText(DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()));
      }
      if (Widgets.isAccessible(originatorHeader)) {
         originatorHeader.refresh();
      }
   }

}
