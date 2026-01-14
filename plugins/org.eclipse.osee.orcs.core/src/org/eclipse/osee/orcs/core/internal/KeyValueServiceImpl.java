/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;
import org.eclipse.osee.orcs.utility.KeyValueService;

/**
 * @author Angel Avila
 */
public class KeyValueServiceImpl implements KeyValueService {

   private final KeyValueStore keyValueStore;

   public KeyValueServiceImpl(KeyValueStore keyValueStore) {
      this.keyValueStore = keyValueStore;
   }

   @Override
   public Long putIfAbsent(String value) {
      return keyValueStore.putIfAbsent(value);
   }

   @Override
   public boolean putWithKeyIfAbsent(Long key, String value) {
      return keyValueStore.putWithKeyIfAbsent(key, value);
   }

   @Override
   public Long getByValue(String value) {
      return keyValueStore.getByValue(value);
   }

   @Override
   public String getByKey(Id key) {
      return keyValueStore.getByKey(key.getId());
   }

   @Override
   public String getByKey(Long key) {
      return keyValueStore.getByKey(key);
   }

   @Override
   public boolean putByKey(Long key, String value) {
      return keyValueStore.putByKey(key, value);
   }

   @Override
   public boolean updateByKey(Long key, String value) {
      return keyValueStore.updateByKey(key, value);
   }

   @Override
   public boolean putByKey(Id key, String value) {
      return keyValueStore.putByKey(key.getId(), value);
   }
}
