/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan D. Brooks
 */
public class ResourceRegistry implements IResourceRegistry {

   private final ConcurrentHashMap<Long, ResourceToken> registry = new ConcurrentHashMap<>();

   @Override
   public ResourceToken registerResource(Long universalId, ResourceToken token) {
      return registry.put(universalId, token);
   }

   @Override
   public InputStream getResource(Long universalId) {
      ResourceToken token = getResourceToken(universalId);
      try {
         return token.getUrl().openStream();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ResourceToken getResourceToken(Long universalId) {
      ResourceToken token = registry.get(universalId);
      if (token == null) {
         throw new OseeArgumentException("Resource with universal ID [%X] not found.", universalId);
      }
      return token;
   }

   @Override
   public void registerAll(Iterable<ResourceToken> tokens) {
      for (ResourceToken token : tokens) {
         registerResource(token.getGuid(), token);
      }
   }
}