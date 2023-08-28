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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Map;
import java.util.Set;

/**
 * Interface for an object that maps keys to {@link Set} collections of values. The interface extends the interface for
 * {@link Map}<code>&lt;K,{@link Set}&lt;V&gt;&gt;</code> with additional methods for directly accessing or adding
 * values to the collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the {@link Set} collections associated with the map keys.
 */

public interface MapSet<K, V> extends MapCollection<K, V, Set<V>> {
   /*
    * Specialization of a generic interface.
    */
}

/* EOF */
