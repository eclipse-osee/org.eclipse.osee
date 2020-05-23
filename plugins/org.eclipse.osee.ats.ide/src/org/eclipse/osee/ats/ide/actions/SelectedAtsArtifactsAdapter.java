/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
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
