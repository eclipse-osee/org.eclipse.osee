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

package org.eclipse.osee.ats.ide.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.api.util.AtsImage;
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
