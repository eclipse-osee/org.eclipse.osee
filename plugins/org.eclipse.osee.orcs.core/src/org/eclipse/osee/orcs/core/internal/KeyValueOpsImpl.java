/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;

/**
 * @author Angel Avila
 */
public class KeyValueOpsImpl implements KeyValueOps {

   private final KeyValueStore keyValueStore;

   public KeyValueOpsImpl(KeyValueStore keyValueStore) {
      this.keyValueStore = keyValueStore;
   }

   @Override
   public Long putIfAbsent(String value) {
      return keyValueStore.putIfAbsent(value);
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
   public boolean putByKey(Id key, String value) {
      return keyValueStore.putByKey(key.getId(), value);
   }
}
