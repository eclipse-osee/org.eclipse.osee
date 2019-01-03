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
package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class TaskAddAction extends Action {

   private final ITaskAddActionHandler taskAddActionHandler;

   public TaskAddAction(ITaskAddActionHandler taskAddActionHandler) {
      super("Create New Task");
      this.taskAddActionHandler = taskAddActionHandler;
      setToolTipText(getText());
   }

   public static interface ITaskAddActionHandler {
      void taskAddActionHandler();
   }

   @Override
   public void run() {
      super.run();
      taskAddActionHandler.taskAddActionHandler();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.NEW_TASK);
   }
}