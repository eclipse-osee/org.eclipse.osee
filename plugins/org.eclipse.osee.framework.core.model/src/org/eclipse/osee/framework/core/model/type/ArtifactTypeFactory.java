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
package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeFactory implements IOseeTypeFactory {

   private ArtifactType create(Long guid, boolean isAbstract, String name) {
      Conditions.checkNotNullOrEmpty(name, "artifact type name");
      return new ArtifactType(guid, name, isAbstract);
   }

   public ArtifactType createOrUpdate(ArtifactTypeCache cache, Long guid, boolean isAbstract, String name) {
      Conditions.checkNotNull(cache, "ArtifactTypeCache");
      ArtifactType artifactType = cache.getByGuid(guid);
      if (artifactType == null) {
         artifactType = create(guid, isAbstract, name);
         cache.cache(artifactType);
      } else {
         artifactType.setName(name);
         artifactType.setAbstract(isAbstract);
      }
      return artifactType;
   }
}