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

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.actions.AbstractWfeSubWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bhawana Mishra
 */
public class WfeHoldWorkflowHeader extends AbstractWfeSubWorkflow {

   WfeHoldWorkflowHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style, workItem, editor, "Hold", "UnHold", AtsAttributeTypes.HoldReason, SWT.COLOR_DARK_YELLOW);
   }
}
