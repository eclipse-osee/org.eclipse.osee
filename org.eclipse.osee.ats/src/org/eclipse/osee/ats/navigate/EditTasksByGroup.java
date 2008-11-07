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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.ITaskEditorProvider;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      GroupListDialog dialog = new GroupListDialog(Display.getCurrent().getActiveShell());
      if (dialog.open() == 0) {
         Artifact selectedGroup = (Artifact) dialog.getResult()[0];
         TaskEditor.open(new TasksByGroupProvider(selectedGroup));
      }
   }

   public class TasksByGroupProvider implements ITaskEditorProvider {

      private final Artifact selectedGroup;

      public TasksByGroupProvider(Artifact selectedGroup) {
         this.selectedGroup = selectedGroup;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTableLoadOptions()
       */
      @Override
      public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
         return Collections.emptyList();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
       */
      @Override
      public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
         return "Tasks from group \"" + selectedGroup + "\"";
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
       */
      @Override
      public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
         Set<TaskArtifact> taskArts = new HashSet<TaskArtifact>();
         for (Artifact art : selectedGroup.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
            if (art instanceof TaskArtifact) {
               taskArts.add((TaskArtifact) art);
            } else if (art instanceof StateMachineArtifact) {
               taskArts.addAll(((StateMachineArtifact) art).getSmaMgr().getTaskMgr().getTaskArtifacts());
            }
         }
         if (taskArts.size() == 0) {
            AWorkbench.popup("ERROR", "No tasks associated with selected groups.");
         }
         return taskArts;
      }

   }
}
