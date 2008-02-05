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
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorInput extends ArtifactEditorInput {

   private final Collection<TaskArtifact> taskArts;
   private final String name;
   private List<TaskResOptionDefinition> resOptions;

   /**
    * @param artifact
    */
   public TaskEditorInput(String name, Collection<TaskArtifact> taskArts, List<TaskResOptionDefinition> resOptions) {
      super(null);
      this.name = name;
      this.taskArts = taskArts;
      this.resOptions = resOptions;
   }

   public TaskEditorInput(String name, Collection<TaskArtifact> taskArts) {
      super(null);
      this.name = name;
      this.taskArts = taskArts;
      this.resOptions = null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput#equals(java.lang.Object)
    */
   @Override
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
   public List<TaskResOptionDefinition> getResOptions() throws SQLException {
      if (resOptions == null) {
         resOptions =
               taskArts.iterator().next().getParentTeamWorkflow().getSmaMgr().getWorkPage(
                     DefaultTeamState.Implement.name()).getTaskResDef() != null ? taskArts.iterator().next().getParentTeamWorkflow().getSmaMgr().getWorkPage(
                     DefaultTeamState.Implement.name()).getTaskResDef().getOptions() : null;
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

}
