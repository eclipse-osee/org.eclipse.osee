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
package org.eclipse.osee.ote.ui.define.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.navigate.IOteNavigateItem;

/**
 * @author Roberto E. Escobar
 */
public class TestRunViewNavigateItem implements IOteNavigateItem {

   public TestRunViewNavigateItem() {
      super();
   }

   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      XNavigateItem msgItem = new XNavigateItem(null, "Test Analysis", FrameworkImage.FOLDER);
      new XNavigateItemAction(msgItem, new OpenTestRunView(), OteDefineImage.TEST_RUN_VIEW, false);
      items.add(msgItem);

      return items;
   }

   private final class OpenTestRunView extends Action {

      public OpenTestRunView() {
         super("Open Test Run View");
      }

      @Override
      public void run() {
         ViewPartUtil.openOrShowView(TestRunView.class.getName());
      }
   }
}
