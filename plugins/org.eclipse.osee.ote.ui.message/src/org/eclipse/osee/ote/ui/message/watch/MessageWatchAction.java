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
package org.eclipse.osee.ote.ui.message.watch;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;


/**
 * @author Donald G. Dunne
 */
public class MessageWatchAction extends Action {

   public MessageWatchAction() {
      super("Open Message Watch");
   }

   @Override
   public void run() {
      ViewPartUtil.openOrShowView(WatchView.VIEW_ID);
   }
}
