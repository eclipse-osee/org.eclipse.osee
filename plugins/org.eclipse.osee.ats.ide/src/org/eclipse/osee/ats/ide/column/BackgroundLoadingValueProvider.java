/*******************************************************************************
 * Copyright (c) 2022 Boeing.
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
package org.eclipse.osee.ats.ide.column;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author Donald G. Dunne
 */
public interface BackgroundLoadingValueProvider {

   /**
    * Allows for retrieving value one workitem in bulk; only use this OR getValue
    */
   public default void getValues(Collection<?> objects, Map<Long, String> idToValueMap) {
      // for sub-class implementation
   }

   /**
    * Allows for retriving value one workitem at a time; consider getValues for performance. only use this OR getValues
    */
   public default String getValue(IAtsWorkItem teamWf, Map<Long, String> idToValueMap) {
      // for sub-class implementation
      return null;
   }

   public Long getKey(Object obj);

}
