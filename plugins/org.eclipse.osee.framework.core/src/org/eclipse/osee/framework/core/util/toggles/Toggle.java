/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.util.toggles;

/**
 * Implementations of this interface return the value of a specific toggle.
 *
 * @author Loren K. Ashley
 * @param <T> the type of the {@link Toggle} implementation's return value.
 */

public interface Toggle<T> {

   /**
    * Returns the value of the toggle.
    *
    * @return the toggle value.
    */

   T get();

}

/* EOF */
