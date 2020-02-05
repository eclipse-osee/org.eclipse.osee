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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Roberto E. Escobar
 */
public class TokenTypeIndex<TOKEN extends Id, DSLTYPE> {

   private final Map<Long, TOKEN> uuidToToken;
   private final BiMap<TOKEN, DSLTYPE> tokenToType;

   public TokenTypeIndex(TOKEN sentinel) {
      uuidToToken = Maps.newHashMap();
      tokenToType = HashBiMap.create();
      uuidToToken.put(sentinel.getId(), sentinel);
   }

   public Collection<TOKEN> getAllTokens() {
      return tokenToType.keySet();
   }

   public TOKEN get(Long id) {
      return uuidToToken.get(id);
   }

   public TOKEN get(Id id) {
      return uuidToToken.get(id.getId());
   }

   public DSLTYPE getDslTypeByToken(Id key) {
      DSLTYPE type = tokenToType.get(key);
      if (type == null) {
         TOKEN tokenByUuid = get(key.getId());
         if (tokenByUuid != null) {
            type = tokenToType.get(tokenByUuid);
         }
      }
      return type;
   }

   public TOKEN getTokenByDslType(DSLTYPE value) {
      return tokenToType.inverse().get(value);
   }

   public boolean existsByUuid(Long uuid) {
      return uuidToToken.containsKey(uuid);
   }

   public boolean exists(Id id) {
      return uuidToToken.containsKey(id.getId());
   }

   public void put(TOKEN token, DSLTYPE dslType) {
      if (uuidToToken.containsKey(token)) {
         throw new OseeStateException("Duplicate tokens with same id %s: %s and %s", token.getId(), dslType.toString(),
            uuidToToken.get(token));
      }
      uuidToToken.put(token.getId(), token);
      tokenToType.put(token, dslType);
   }

   public boolean isEmpty() {
      return uuidToToken.isEmpty();
   }

   public int size() {
      return uuidToToken.size();
   }
}