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

package org.eclipse.osee.ats.ide.editor.tab.bit;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.bit.action.CreateSiblingAction;
import org.eclipse.osee.ats.ide.editor.tab.bit.action.DeleteProgramVersionAction;
import org.eclipse.osee.ats.ide.editor.tab.bit.action.NewProgramVersionAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeBitToolbar {

   private final ScrolledForm scrolledForm;
   private final IAtsTeamWorkflow teamWf;
   private final XBitViewer xViewer;
   private final WorkflowEditor editor;

   public WfeBitToolbar(ScrolledForm scrolledForm, XBitViewer xXViewer, WorkflowEditor editor, IAtsTeamWorkflow teamWf) {
      this.scrolledForm = scrolledForm;
      this.xViewer = xXViewer;
      this.editor = editor;
      this.teamWf = teamWf;
   }

   public void build() {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();
      toolBarMgr.add(new NewProgramVersionAction(teamWf, editor.getBitTab()));
      toolBarMgr.add(new DeleteProgramVersionAction(teamWf));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new CreateSiblingAction(teamWf, editor));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new ExpandAllAction(xViewer));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(xViewer.getCustomizeAction());
      toolBarMgr.add(new Separator());
      scrolledForm.updateToolBar();
   }

}
