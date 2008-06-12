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
package org.eclipse.osee.ats.editor;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorInput implements IEditorInput {

   private final Collection<TaskArtifact> taskArts;
   private final String name;
   private List<TaskResOptionDefinition> resOptions;

   /**
    * @param artifact
    */
   public TaskEditorInput(String name, Collection<TaskArtifact> taskArts, List<TaskResOptionDefinition> resOptions) {
      this.name = name;
      this.taskArts = taskArts;
      this.resOptions = resOptions;
   }

   public TaskEditorInput(String name, Collection<TaskArtifact> taskArts) {
      this.name = name;
      this.taskArts = taskArts;
      this.resOptions = null;
   }

   public boolean equals(Object obj) {
      return false;
   }

   /**
    * @return the taskArts
    */
   public Collection<TaskArtifact> getTaskArts() {
      return taskArts;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the resOptions
    */
   public List<TaskResOptionDefinition> getResOptions() throws SQLException, OseeCoreException {
      if (resOptions == null) {
         resOptions =
               TaskResolutionOptionRule.getTaskResolutionOptions(taskArts.iterator().next().getParentTeamWorkflow().getSmaMgr().getWorkPageDefinitionByName(
                     DefaultTeamState.Implement.name()));
      }
      return resOptions;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

}
