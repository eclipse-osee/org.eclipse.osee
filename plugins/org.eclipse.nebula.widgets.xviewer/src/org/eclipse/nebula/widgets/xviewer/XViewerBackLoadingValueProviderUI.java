/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer;

import java.util.Collection;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public interface XViewerBackLoadingValueProviderUI extends IXViewerValueColumn {

   /**
    * Allows for retrieving values in bulk; only use this OR getValue
    */
   public default void getValues(Collection<?> objects, Map<Long, String> idToValueMap) {
      // for sub-class implementation
   }

   /**
    * Allows for retrieving value one object at a time; consider getValues for performance. only use this OR getValues
    */
   public default String getValue(Object obj, Map<Long, String> idToValueMap) {
      // for sub-class implementation
      return null;
   }

   /**
    * Allows for pre-loading tasks (like bulk loading artifacts) before loading happens.
    */
   public default void handlePreLoadingTasks(Collection<?> objects) {
      // for sub-class implementation
   }

   public String getId();

   public Long getKey(Object obj);

}
