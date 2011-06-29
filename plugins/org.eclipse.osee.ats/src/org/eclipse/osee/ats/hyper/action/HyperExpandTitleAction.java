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
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.hyper.HyperView;

public class HyperExpandTitleAction extends Action {

   private final HyperView hyperView;

   public HyperExpandTitleAction(final HyperView hyperView) {
      super("Expand Titles", IAction.AS_CHECK_BOX);
      this.hyperView = hyperView;
      setToolTipText("Expand Titles");
   }

   @Override
   public void run() {
      hyperView.refresh();
   }

}
