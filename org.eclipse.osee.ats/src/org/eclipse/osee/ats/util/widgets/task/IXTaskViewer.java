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
package org.eclipse.osee.ats.util.widgets.task;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;

/**
 * @author Donald G. Dunne
 */
public interface IXTaskViewer {

   public enum RelationChangeAction {
      RemoveTask, AddTask, UpdateTask, ReLoadTable, None
   };

   public String getTabName() throws OseeCoreException;

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException;

   public IDirtiableEditor getEditor() throws OseeCoreException;

   public boolean isUsingTaskResolutionOptions() throws OseeCoreException;

   public List<TaskResOptionDefinition> getResOptions() throws OseeCoreException;

   public boolean isTaskable() throws OseeCoreException;

   public String getCurrentStateName() throws OseeCoreException;

   public SMAManager getParentSmaMgr() throws OseeCoreException;

   /**
    * Overriding flag to denote if tasks are allowed to be edited. If false, task viewer will disable all right-click
    * and alt-left-click editing functionality.
    * 
    * @return false if tasks are readonly from the TaskViewer
    * @throws
    */
   public boolean isTasksEditable() throws OseeCoreException;

   public String toString();

}
