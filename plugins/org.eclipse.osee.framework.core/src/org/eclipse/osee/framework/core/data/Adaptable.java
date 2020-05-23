/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Ryan D. Brooks
 */
public interface Adaptable extends IAdaptable {
   @SuppressWarnings("unchecked")
   @Override
   default <T> T getAdapter(Class<T> type) {
      if (type != null && type.isAssignableFrom(getClass())) {
         return (T) this;
      }
      return null;
   }
}
