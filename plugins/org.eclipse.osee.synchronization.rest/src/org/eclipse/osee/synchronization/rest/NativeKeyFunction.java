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

package org.eclipse.osee.synchronization.rest;

import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * Represents a function to obtain a unique identifier as a {@link Long} from a native OSEE thing that extends the
 * interface {@link Id}.
 *
 * @author Loren K. Ashley
 * @param <NT> the type of native OSEE data object.
 */
@FunctionalInterface
public interface NativeKeyFunction<NT extends Id> {

   /**
    * Get a unique identifier for the native thing.
    *
    * @param nativeThing native thing to get a unique identifier from.
    * @return a {@link Long} unique identifier for the native thing.
    */
   Long getKey(NT nativeThing);
}

/* EOF */