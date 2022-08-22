/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public abstract class BackgroundLoadingPreComputedColumn extends XViewerAtsColumn implements IXViewerPreComputedColumn, BackgroundLoadingValueProvider {

   public AtomicBoolean loading = new AtomicBoolean(false);
   public AtomicBoolean loaded = new AtomicBoolean(false);
   protected Map<Long, String> preComputedValueMap = new HashMap<>();

   public BackgroundLoadingPreComputedColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   private void startLoadingThread(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      startLoadingThread(getName(), objects, loading, loaded, (WorldXViewer) getXViewer(), preComputedValueMap, this);
   }

   /**
    * Available for columns that can't extend BackgroundLoadingColumn
    */
   public static void startLoadingThread(String name, Collection<?> objects, AtomicBoolean loading, AtomicBoolean loaded, //
      final WorldXViewer xViewer, Map<Long, String> preComputedValueMap, final BackgroundLoadingValueProvider valueProvider) {
      if (loading.get() || loaded.get()) {
         return;
      }

      Thread loadingThread = new Thread("Loading " + name) {

         @Override
         public void run() {
            if (loading.getAndSet(true)) {
               return;
            }

            // Handle any special pre-loading tasks (eg: Bulk loading)
            valueProvider.handlePreLoadingTasks(objects);

            // Calculate fields; by all items
            valueProvider.getValues(objects, preComputedValueMap);

            // OR Calculate fields; by item
            for (Object element : objects) {
               Long key = valueProvider.getObjKey(element);
               try {
                  if (element instanceof IAtsWorkItem) {
                     IAtsWorkItem workItem = (IAtsWorkItem) element;
                     String value = valueProvider.getValue(workItem, preComputedValueMap);
                     if (value != null) {
                        preComputedValueMap.put(key, value);
                     }
                  }
               } catch (OseeCoreException ex) {
                  String cellExceptionString = LogUtil.getCellExceptionString(ex);
                  preComputedValueMap.put(key, cellExceptionString);
               }
            }

            // Turn off loading
            loaded.set(true);
            loading.set(false);

            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (xViewer != null) {
                     xViewer.refreshColumn(valueProvider.getId());
                  }
               }
            });
         }

      };
      loadingThread.start();
   }

   @Override
   public Long getKey(Object obj) {
      if (obj instanceof Id) {
         return ((Id) obj).getId();
      }
      return Id.SENTINEL;
   }

   @Override
   public String getColumnText(Object obj, XViewerColumn column, int columnIndex) {
      return getColumnText(obj, loading, loaded, preComputedValueMap, this);
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return getColumnText(obj, loading, loaded, preComputedValueMap, this);
   }

   /**
    * Available for columns that can't extend BackgroundLoadingColumn but also used by this class's getColumnText above
    */
   public static String getColumnText(Object obj, AtomicBoolean loading, AtomicBoolean loaded, Map<Long, String> idToValueMap, BackgroundLoadingValueProvider valueProvider) {
      String value = "";
      try {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            value = idToValueMap.get(workItem.getId());
            /**
             * Need null here, cause empty string means calculation has been done and there is no value
             */
            if (value == null) {
               value = valueProvider.getValue(workItem, idToValueMap);
               idToValueMap.put(workItem.getId(), value);
            }
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return value;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      this.preComputedValueMap = preComputedValueMap;
      this.loaded.set(false);
      this.loading.set(false);
      for (Object obj : objects) {
         this.preComputedValueMap.put(getKey(obj), "loading...");
      }
      startLoadingThread(objects, preComputedValueMap);
   }

}
