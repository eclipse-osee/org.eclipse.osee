/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
