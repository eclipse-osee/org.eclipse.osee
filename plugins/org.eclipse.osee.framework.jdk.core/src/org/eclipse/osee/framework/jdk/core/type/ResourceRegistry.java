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
package org.eclipse.osee.framework.jdk.core.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan D. Brooks
 */
public class ResourceRegistry implements IResourceRegistry {

   private final ConcurrentHashMap<Long, ResourceToken> registry = new ConcurrentHashMap<Long, ResourceToken>();

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
         throw new OseeCoreException(ex);
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