/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.editor.TaskEditorInput;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.GroupListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class EditTasksByGroup extends XNavigateItemAction {

   /**
    * @param parent
    */
   public EditTasksByGroup(XNavigateItem parent) {
      super(parent, "Edit Tasks by Group");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException, SQLException {
      GroupListDialog dialog = new GroupListDialog(Display.getCurrent().getActiveShell());
      if (dialog.open() == 0) {
         Set<TaskArtifact> taskArts = new HashSet<TaskArtifact>();
         Artifact selectedGroup = (Artifact) dialog.getResult()[0];
         for (Artifact art : selectedGroup.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
            if (art instanceof TaskArtifact) {
               taskArts.add((TaskArtifact) art);
            } else if (art instanceof StateMachineArtifact) {
               taskArts.addAll(((StateMachineArtifact) art).getSmaMgr().getTaskMgr().getTaskArtifacts());
            }
         }
         if (taskArts.size() == 0) {
            AWorkbench.popup("ERROR", "No tasks associated with selected workflows.");
            return;
         }
         TaskEditor.editArtifacts(new TaskEditorInput("Tasks from selected workflows.", taskArts));
      }
   }
}
