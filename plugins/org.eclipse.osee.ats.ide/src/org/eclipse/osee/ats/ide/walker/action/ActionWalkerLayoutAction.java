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
package org.eclipse.osee.ats.ide.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ActionWalkerLayoutAction extends Action {

   private final ActionWalkerView view;

   public ActionWalkerLayoutAction(ActionWalkerView view) {
      super("Change Layout", ImageManager.getImageDescriptor(AtsImage.LAYOUT));
      this.view = view;
   }

   @Override
   public void run() {
      view.getLayoutMgr().nextLayout();
      setText("Change Layout (" + view.getLayoutMgr().getCurrentLayoutName() + ")");
   }

}
