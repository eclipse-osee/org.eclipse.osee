/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.hyper.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.hyper.HyperView;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class HyperBackAction extends Action {

   private final HyperView hyperView;

   public HyperBackAction(final HyperView hyperView) {
      this.hyperView = hyperView;
      setText("Back");
      setToolTipText("Back");
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
   }

   @Override
   public void run() {
      hyperView.printBackList("pre backSelected");
      if (hyperView.getBackList().isEmpty()) {
         return;
      }
      if (hyperView.backListIndex > 0) {
         hyperView.backListIndex--;
      }
      hyperView.jumpTo(hyperView.getBackList().get(hyperView.backListIndex));
      hyperView.printBackList("post backSelected");
   }

}
