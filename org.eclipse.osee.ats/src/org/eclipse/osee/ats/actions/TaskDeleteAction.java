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
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class TaskDeleteAction extends Action {

   private final ITaskDeleteActionHandler taskDeleteActionHandler;

   public TaskDeleteAction(ITaskDeleteActionHandler taskDeleteActionHandler) {
      super("Delete Task");
      this.taskDeleteActionHandler = taskDeleteActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.X_RED));
      setToolTipText(getText());
   }

   public static interface ITaskDeleteActionHandler {
      public void taskDeleteActionHandler();
   }

   @Override
   public void run() {
      super.run();
      taskDeleteActionHandler.taskDeleteActionHandler();
   }

}