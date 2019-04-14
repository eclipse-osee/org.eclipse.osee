/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WfeStateCreatedOrigHeader extends Composite implements IWfeEventHandle {

   private final IAtsWorkItem workItem;
   Label stateValueLabel, createdValueLabel;

   public WfeStateCreatedOrigHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(3, false));
      editor.getToolkit().adapt(this);

      try {
         stateValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Current State: ", "");
         createdValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Created: ", "");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      new WfeOriginatorHeader(this, SWT.NONE, workItem, editor);

      refresh();
      editor.registerEvent(this, AtsAttributeTypes.CurrentState, AtsAttributeTypes.CreatedDate);

   }

   @Override
   public void refresh() {
      stateValueLabel.setText(workItem.getStateMgr().getCurrentStateName());
      createdValueLabel.setText(DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()));
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }
}
