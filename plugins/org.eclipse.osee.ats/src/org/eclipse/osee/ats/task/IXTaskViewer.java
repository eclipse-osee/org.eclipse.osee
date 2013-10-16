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
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;

/**
 * @author Donald G. Dunne
 */
public interface IXTaskViewer {

   public String getTabName() throws OseeCoreException;

   public Collection<TaskArtifact> getTaskArtifacts(IStateToken state) throws OseeCoreException;

   public Collection<TaskArtifact> getTaskArtifacts() throws OseeCoreException;

   public IDirtiableEditor getEditor() throws OseeCoreException;

   /**
    * Denotes whether tasks are available to this object. Doesn't imply whether editable, use isTaskEditable().<br>
    * Example: This will show the task tab or not.
    * 
    * @return true if view of tasks is allowed
    */
   public boolean isTaskable() throws OseeCoreException;

   public String getCurrentStateName() throws OseeCoreException;

   public AbstractWorkflowArtifact getAwa() throws OseeCoreException;

   /**
    * Overriding flag to denote if tasks are allowed to be edited. If false, task viewer will disable all right-click
    * and alt-left-click editing functionality.
    * 
    * @return false if tasks can be created, deleted, edited
    */
   public boolean isTasksEditable() throws OseeCoreException;

   @Override
   public String toString();

   /**
    * Returning true will allow implementer class to handle the refresh button press which whill result in
    * handlRefreshAction() being called
    * 
    * @return if implementer will handle refresh calls
    */
   public boolean isRefreshActionHandled() throws OseeCoreException;

   /**
    * Called if isRefreshActionHandled() returns true
    */
   public void handleRefreshAction() throws OseeCoreException;

}
