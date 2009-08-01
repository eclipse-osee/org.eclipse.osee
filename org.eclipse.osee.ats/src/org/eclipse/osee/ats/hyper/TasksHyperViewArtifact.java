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
package org.eclipse.osee.ats.hyper;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class TasksHyperViewArtifact implements IHyperArtifact {

   private final Collection<? extends TaskArtifact> taskArts;

   public TasksHyperViewArtifact(Collection<? extends TaskArtifact> taskArts) {
      this.taskArts = taskArts;
   }

   @Override
   public String getGuid() {
      return null;
   }

   @Override
   public Artifact getHyperArtifact() {
      return null;
   }

   @Override
   public String getHyperAssignee() {
      return null;
   }

   @Override
   public Image getHyperAssigneeImage() throws OseeCoreException {
      return null;
   }

   @Override
   public String getHyperName() {
      return taskArts.size() + " Tasks";
   }

   @Override
   public String getHyperState() {
      return null;
   }

   @Override
   public String getHyperType() {
      return TaskArtifact.ARTIFACT_NAME;
   }

   @Override
   public String getHyperTargetVersion() {
      return null;
   }

   @Override
   public boolean isDeleted() {
      return false;
   }

}
