/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.core.data.Identity;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

/**
 * @author Roberto E. Escobar
 */
public class TokenTypeIndex<UUID_TYPE, TOKEN extends Identity<UUID_TYPE>, DSLTYPE> {

   private final Map<UUID_TYPE, TOKEN> uuidToToken;
   private final BiMap<TOKEN, DSLTYPE> tokenToType;

   public TokenTypeIndex() {
      uuidToToken = Maps.newHashMap();
      tokenToType = HashBiMap.create();
   }

   public Collection<? extends TOKEN> getAllTokens() {
      return tokenToType.keySet();
   }

   public TOKEN getTokenByUuid(UUID_TYPE uuid) {
      return uuidToToken.get(uuid);
   }

   public DSLTYPE getDslTypeByToken(TOKEN key) {
      return tokenToType.get(key);
   }

   public TOKEN getTokenByDslType(DSLTYPE value) {
      return tokenToType.inverse().get(value);
   }

   public boolean existsByUuid(UUID_TYPE uuid) {
      return uuidToToken.containsKey(uuid);
   }

   public void put(TOKEN token, DSLTYPE dslType) {
      uuidToToken.put(token.getGuid(), token);
      tokenToType.put(token, dslType);
   }

   public boolean isEmpty() {
      return uuidToToken.isEmpty();
   }

   public int size() {
      return uuidToToken.size();
   }
}