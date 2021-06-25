/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.estimates;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstViewer extends TaskXViewer {

   public XTaskEstViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor, IAtsTeamWorkflow teamWf) {
      super(parent, style, xViewerFactory, editor, teamWf);
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      List<TaskEstDefinition> selected = getSelected();
      if (selected.isEmpty()) {
         return false;
      }
      TaskEstDefinition ted = selected.iterator().next();
      ted.setChecked(!ted.isChecked());
      refresh(ted);
      return true;
   }

   @Override
   protected boolean isAddTaskEnabled() {
      return false;
   }

   public List<TaskEstDefinition> getSelected() {
      List<TaskEstDefinition> teds = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof TaskEstDefinition) {
               teds.add((TaskEstDefinition) item.getData());
            }
         }
      }
      return teds;
   }

}
