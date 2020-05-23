/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class WfeWorkflowCustomSection extends WfeWorkflowSection {

   public WfeWorkflowCustomSection(Composite parent, int style, StateXWidgetPage page, AbstractWorkflowArtifact sma, WorkflowEditor editor) {
      super(parent, style, page, sma, editor);
   }

}
