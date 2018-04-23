/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.actions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class SelectedAtsArtifactsAdapter implements ISelectedAtsArtifacts {

   public SelectedAtsArtifactsAdapter() {
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      return Collections.emptySet();
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      return Collections.emptyList();
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      return Collections.emptyList();
   }

}
