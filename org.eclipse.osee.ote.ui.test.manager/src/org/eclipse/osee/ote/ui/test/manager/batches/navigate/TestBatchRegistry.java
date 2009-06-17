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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;

/**
 * @author Roberto E. Escobar
 */
public class TestBatchRegistry {

   private final Map<String, TestBatchData> itemsMap;
   private final Set<ITestBatchDataListener> listeners;
   private final XNavigateItem parent;

   public TestBatchRegistry(XNavigateItem parent) {
      this.itemsMap = new HashMap<String, TestBatchData>();
      this.listeners = new HashSet<ITestBatchDataListener>();
      this.parent = parent;
   }

   public List<XNavigateItem> getXNavigateItems() {
      List<XNavigateItem> toReturn = new ArrayList<XNavigateItem>();
      for (TestBatchData data : itemsMap.values()) {
         toReturn.add(data.getXNavigateItem());
      }
      return toReturn;
   }

   public void deregisterTestBatch(String id) {
      TestBatchData data = this.itemsMap.get(id);
      if (data != null) {
         data.dispose();
         this.itemsMap.remove(id);
         notifyAddEvent(data);
      }
   }

   public void registerTestBatch(String id, URI projectSetFile, URI testBatchFile) {
      if (itemsMap.containsKey(id) != true) {
         TestBatchData data = createNewTestBatchItem(id, projectSetFile, testBatchFile);
         this.itemsMap.put(data.getId(), data);
         notifyRemoveEvent(data);
      }
   }

   public void addListener(ITestBatchDataListener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(ITestBatchDataListener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   private void notifyAddEvent(final TestBatchData data) {
      synchronized (listeners) {
         for (ITestBatchDataListener listener : listeners) {
            listener.dataAddedEvent(data);
         }
      }
   }

   private void notifyRemoveEvent(final TestBatchData data) {
      synchronized (listeners) {
         for (ITestBatchDataListener listener : listeners) {
            listener.dataRemovedEvent(data);
         }
      }
   }

   private TestBatchData createNewTestBatchItem(String id, URI projectSetFile, URI testBatchFile) {
      // Chain Items
      XNavigateItem parentFolder = new XNavigateItem(parent, id, FrameworkImage.FOLDER);
      new ProjectSetupItem(parentFolder, id + " Checkout", OteTestManagerImage.PROJECT_SET_IMAGE, projectSetFile);
      new TestBatchSetupItem(parentFolder, id + " Test Manager Batch Config", OteTestManagerImage.TEST_BATCH_IMAGE, testBatchFile);

      // Store into dataObject
      return new TestBatchData(parentFolder, id, projectSetFile, testBatchFile);
   }
}
