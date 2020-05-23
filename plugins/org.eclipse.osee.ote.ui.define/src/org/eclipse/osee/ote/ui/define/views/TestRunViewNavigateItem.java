/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.define.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.ote.ui.define.OteDefineImage;

/**
 * @author Roberto E. Escobar
 */
public class TestRunViewNavigateItem implements IXNavigateContainer {

   public TestRunViewNavigateItem() {
      super();
   }

   @Override
   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<>();

      XNavigateItem msgItem = new XNavigateItem(null, "Test Analysis", PluginUiImage.FOLDER);
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
