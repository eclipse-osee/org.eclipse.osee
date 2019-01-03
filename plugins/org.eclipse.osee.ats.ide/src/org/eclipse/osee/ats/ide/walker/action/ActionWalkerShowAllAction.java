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
import org.eclipse.osee.ats.ide.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ActionWalkerShowAllAction extends Action {

   private final ActionWalkerView view;
   private boolean showAll = false;

   public ActionWalkerShowAllAction(ActionWalkerView view) {
      super("Toggle Show All", ImageManager.getImageDescriptor(FrameworkImage.EXPAND_ALL));
      this.view = view;
   }

   @Override
   public void run() {
      showAll = !showAll;
      view.setShowAll(showAll);
   }
}
