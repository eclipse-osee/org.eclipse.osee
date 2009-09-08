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

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class TaskAddAction extends Action {

   private final ITaskAddActionHandler taskAddActionHandler;

   public TaskAddAction(ITaskAddActionHandler taskAddActionHandler) {
      super("Create New Task");
      this.taskAddActionHandler = taskAddActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.NEW_TASK));
      setToolTipText(getText());
   }

   public static interface ITaskAddActionHandler {
      public void taskAddActionHandler();
   }

   @Override
   public void run() {
      super.run();
      taskAddActionHandler.taskAddActionHandler();
   }

}