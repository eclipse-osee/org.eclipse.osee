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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getGuid()
    */
   @Override
   public String getGuid() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperArtifact()
    */
   @Override
   public Artifact getHyperArtifact() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssignee()
    */
   @Override
   public String getHyperAssignee() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssigneeImage()
    */
   @Override
   public Image getHyperAssigneeImage() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperImage()
    */
   @Override
   public Image getHyperImage() {
      return taskArts.iterator().next().getImage();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperName()
    */
   @Override
   public String getHyperName() {
      return taskArts.size() + " Tasks";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperState()
    */
   @Override
   public String getHyperState() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperType()
    */
   @Override
   public String getHyperType() {
      return TaskArtifact.ARTIFACT_NAME;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperTargetVersion()
    */
   @Override
   public String getHyperTargetVersion() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#isDeleted()
    */
   @Override
   public boolean isDeleted() {
      return false;
   }

}
