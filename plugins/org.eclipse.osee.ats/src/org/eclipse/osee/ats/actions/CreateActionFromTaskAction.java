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
package org.eclipse.osee.ats.actions;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.operation.CreateActionFromTaskBlam;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CreateActionFromTaskAction extends Action {

   private final Collection<TaskArtifact> tasks;

   public CreateActionFromTaskAction(Collection<TaskArtifact> tasks) {
      super("Create Action from Task");
      this.tasks = tasks;
   }

   @Override
   public void run() {
      CreateActionFromTaskBlam blamOperation = new CreateActionFromTaskBlam();
      blamOperation.setDefaultTeamWorkflows(tasks);
      BlamEditor.edit(blamOperation);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

}
