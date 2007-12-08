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

import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.world.WorldArtifactItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifactItem extends WorldArtifactItem {

   /**
    * @param xViewer
    * @param artifact
    */
   public TaskArtifactItem(TaskXViewer xViewer, Artifact artifact, WorldArtifactItem parentItem) {
      super(xViewer, artifact, parentItem);
   }

   public TaskArtifact getTaskArtifact() {
      return (TaskArtifact) getArtifact();
   }
}
