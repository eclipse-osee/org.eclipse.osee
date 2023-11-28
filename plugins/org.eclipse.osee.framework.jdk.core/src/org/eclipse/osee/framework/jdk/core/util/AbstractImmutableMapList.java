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

import java.util.List;
import java.util.Map;

/**
 * An extension of the {@link AbstractImmutableMapCollection} for a {@link Map} with {@link List} collections.
 *
 * @author Loren K Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 */

public class AbstractImmutableMapList<K, V> extends AbstractImmutableMapCollection<K, V, List<V>> implements MapList<K, V> {

   /**
    * Creates a new {@link AbstractImmutableMapList} as a wrapper on the immutable <code>mapCollection</code>.
    *
    * @param mapCollection an immutable {@link Map} of immutable {@link List}s.
    */

   AbstractImmutableMapList(Map<K, List<V>> mapCollection) {
      super(mapCollection);
   }

}

/* EOF */
