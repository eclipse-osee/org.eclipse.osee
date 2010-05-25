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
package org.eclipse.osee.ote.ui.message.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.view.MessageViewAction;
import org.eclipse.osee.ote.ui.message.watch.MessageWatchAction;

/**
 * @author Donald G. Dunne
 */
public class MessageNavigateViewItems implements IXNavigateContainer {

   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      items.add(new XNavigateItemAction(null, new MessageViewAction(), OteMessageImage.GLASSES, false));
      items.add(new XNavigateItemAction(null, new MessageWatchAction(), OteMessageImage.BINOCULARS, false));
      return items;
   }

}
