/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeFactory implements IOseeTypeFactory {
   public ArtifactType createOrUpdate(ArtifactTypeCache cache, Long guid, boolean isAbstract, String name, OrcsTokenService tokenService) {
      Conditions.checkNotNull(cache, "ArtifactTypeCache");
      ArtifactType artifactType = cache.getByGuid(guid);
      if (artifactType == null) {
         Conditions.checkNotNullOrEmpty(name, "artifact type name");
         artifactType = new ArtifactType(guid, name, isAbstract, tokenService);
         cache.cache(artifactType);
      } else {
         artifactType.setName(name);
         artifactType.setAbstract(isAbstract);
      }
      return artifactType;
   }
}