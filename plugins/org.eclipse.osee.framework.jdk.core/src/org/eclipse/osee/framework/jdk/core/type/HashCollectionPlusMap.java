/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A HashCollectionPlus which maintains a map as the "plus" object.
 * 
 * @see HashCollectionPlus
 * @author David Diepenbrock
 */
public class HashCollectionPlusMap<K, V, P, S> extends HashCollectionPlus<K, V, Map<P, S>> {

   /**
    * Creates using a synchronized map for the "plus" object regardless of the isSynchronized flag, which is passed on
    * to the HashCollectionPlus super object.
    * 
    * @see HashCollectionPlus#HashCollectionPlus(boolean, Class, IPlusProvider)
    */
   public HashCollectionPlusMap(boolean isSynchronized, Class<? extends Collection<?>> collectionType) {
      super(isSynchronized, collectionType, new IPlusProvider<Map<P, S>>() {
         public Map<P, S> newObject() {
            return Collections.synchronizedMap(new HashMap<P, S>());
         }
      });
   }

   /**
    * Returns the value from the "plus" map associated with the two keys provided
    * 
    * @param key The key to the hashCollection
    * @param mapKey The key to the "plus" map
    * @return The associated value, or null if either key had no associated entry.
    */
   public S getMapValue(K key, P mapKey) {
      S value = null;
      Map<P, S> theMap = this.getPlusObject(key);
      if (theMap != null) {
         value = theMap.get(mapKey);
      }
      return value;
   }

   /**
    * Sets the value from the "plus" map associated with the two keys provided
    * 
    * @param key The key to the hashCollection
    * @param mapKey The key to the "plus" map
    * @param value The value to set into the "plus" map for the mapKey
    * @return true if the value was successfully set
    */
   public boolean setMapValue(K key, P mapKey, S value) {
      Map<P, S> theMap = this.getPlusObject(key);
      if (theMap != null) {
         theMap.put(mapKey, value);
         return true;
      }
      return false;
   }

}
