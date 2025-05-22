/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.nebula.widgets.xviewer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerBackLoadingPreComputedColumnUI extends XViewerColumn implements IXViewerPreComputedColumn, XViewerBackLoadingValueProviderUI {

   public AtomicBoolean loading = new AtomicBoolean(false);
   public AtomicBoolean loaded = new AtomicBoolean(false);

   public XViewerBackLoadingPreComputedColumnUI(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   private void startLoadingThread(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      startLoadingThread(getName(), objects, loading, loaded, (XViewer) getXViewer(), preComputedValueMap, this);
   }

   /**
    * Available for columns that can't extend BackgroundLoadingColumn
    */
   public static void startLoadingThread(String name, Collection<?> objects, AtomicBoolean loading,
      AtomicBoolean loaded, //
      final XViewer xViewer, Map<Long, String> preComputedValueMap,
      final XViewerBackLoadingValueProviderUI valueProvider) {
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
               Long key = valueProvider.getKey(element);
               try {
                  String value = valueProvider.getValue(element, preComputedValueMap);
                  if (value != null) {
                     preComputedValueMap.put(key, value);
                  }
               } catch (Exception ex) {
                  String cellExceptionString = XViewerCells.getCellExceptionString(ex);
                  preComputedValueMap.put(key, cellExceptionString);
               }
            }

            // Turn off loading
            loaded.set(true);
            loading.set(false);

            XViewerLib.ensureInDisplayThread(new Runnable() {

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
   public static String getColumnText(Object obj, AtomicBoolean loading, AtomicBoolean loaded,
      Map<Long, String> idToValueMap, XViewerBackLoadingValueProviderUI valueProvider) {
      String value = "";
      try {
         if (idToValueMap != null) {
            value = idToValueMap.get(valueProvider.getKey(obj));
         }
         /**
          * Need null here, cause empty string means calculation has been done and there is no value
          */
         if (value == null) {
            value = valueProvider.getValue(obj, idToValueMap);
            if (idToValueMap != null) {
               idToValueMap.put(valueProvider.getKey(obj), value);
            }
         }
      } catch (Exception ex) {
         XViewerCells.getCellExceptionString(ex);
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

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) {
      return null;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   @Override
   public StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex) {
      return null;
   }

   @Override
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) {
      return null;
   }

}
