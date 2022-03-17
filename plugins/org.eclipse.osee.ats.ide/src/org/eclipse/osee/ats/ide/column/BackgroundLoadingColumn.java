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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public abstract class BackgroundLoadingColumn extends XViewerAtsColumn implements IXViewerPreComputedColumn {

   public AtomicBoolean loading = new AtomicBoolean(false);
   public AtomicBoolean loaded = new AtomicBoolean(false);
   protected Map<Long, String> idToValueMap = new HashMap<>();

   public BackgroundLoadingColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   private void startLoadingThread(Collection<?> objects) {
      Thread loadingThread = new Thread("Loading " + getName()) {

         @Override
         public void run() {

            // Calculate fields; by all items
            getValues(objects, idToValueMap);

            // OR Calculate fields; by item
            for (Object element : objects) {
               Long key = getKey(element);
               try {
                  if (element instanceof IAtsWorkItem) {
                     IAtsWorkItem workItem = (IAtsWorkItem) element;
                     String value = getValue(workItem, idToValueMap);
                     if (value != null) {
                        idToValueMap.put(key, value);
                     }
                  }
               } catch (OseeCoreException ex) {
                  String cellExceptionString = LogUtil.getCellExceptionString(ex);
                  idToValueMap.put(key, cellExceptionString);
               }
            }

            // Turn off loading
            loading.set(false);
            loaded.set(true);

            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  WorldXViewer xViewer = (WorldXViewer) getXViewer();
                  xViewer.refreshColumnsWithPreCompute();
               }
            });
         }

      };
      loadingThread.start();
   }

   /**
    * Allows for retrieving value one workitem in bulk; only use this OR getValue
    */
   protected void getValues(Collection<?> objects, Map<Long, String> idToValueMap) {
      // for sub-class implementation
   }

   /**
    * Allows for retriving value one workitem at a time; consider getValues for performance. only use this OR getValues
    */
   protected String getValue(IAtsWorkItem teamWf, Map<Long, String> idToValueMap) {
      // for sub-class implementation
      return null;
   }

   @Override
   public Long getKey(Object obj) {
      if (obj instanceof IAtsTeamWorkflow) {
         return ((IAtsTeamWorkflow) obj).getId();
      }
      return null;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      if (!loaded.get() && !loading.getAndSet(true)) {
         startLoadingThread(objects);
      }
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      try {
         if (obj instanceof IAtsWorkItem) {
            if (loading.get()) {
               return "loading...";
            } else if (loaded.get()) {
               IAtsWorkItem workItem = (IAtsWorkItem) obj;
               String value = idToValueMap.get(workItem.getId());
               // Need null here, cause empty string means calculation has been done and there is no value
               if (value != null) {
                  return value;
               } else {
                  return "unknown";
               }
            }
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
