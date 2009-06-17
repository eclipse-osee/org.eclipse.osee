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
package org.eclipse.osee.ote.ui.test.manager.batches.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.ote.ui.navigate.IOteNavigateItem;
import org.eclipse.osee.ote.ui.test.manager.batches.TestBatchProjectNature;

/**
 * @author Roberto E. Escobar
 */
public class TestBatchSetupViewItems implements IOteNavigateItem {
   private static final String PARENT_FOLDER_NAME = "Test Batch Setup";

   private static TestBatchSetupViewItems instance = null;
   private TestBatchRegistry registry;
   private XNavigateItem parentFolder;

   public TestBatchSetupViewItems() {
      this.parentFolder = new XNavigateItem(null, PARENT_FOLDER_NAME, FrameworkImage.FOLDER);
      this.registry = new TestBatchRegistry(parentFolder);
      this.registry.addListener(new RefreshOteNavigator());

      TestBatchProjectNature.initializeProjectSet();
   }

   public static TestBatchSetupViewItems getInstance() {
      if (instance == null) {
         instance = new TestBatchSetupViewItems();
      }
      return instance;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.navigate.IOteNavigateItem#getNavigateItems()
    */
   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      items.add(parentFolder);
      return items;
   }

   public TestBatchRegistry getRegistry() {
      return registry;
   }
}
