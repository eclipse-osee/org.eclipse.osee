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
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class TasksActionHyperItem extends HyperViewItem {

   public TasksActionHyperItem(Collection<TaskArtifact> taskArts) {
      super(taskArts.size() + " Tasks");
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.TASK);
   }

}
