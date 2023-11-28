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
 * An extension of the {@link AbstractImmutableMapCollection} for a {@link Map} with {@link Set} collections.
 *
 * @author Loren K Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 */

public class AbstractImmutableMapSet<K, V> extends AbstractImmutableMapCollection<K, V, Set<V>> implements MapSet<K, V> {

   /**
    * Creates a new {@link AbstractImmutableMapSet} as a wrapper on the immutable <code>mapCollection</code>.
    *
    * @param mapCollection an immutable {@link Map} of immutable {@link Set}s.
    */

   AbstractImmutableMapSet(Map<K, Set<V>> mapCollection) {
      super(mapCollection);
   }

}

/* EOF */
