/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.cache;

import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.ArtifactType;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeCache extends AbstractOseeCache<ArtifactType> {

   public ArtifactTypeCache(IOseeDataAccessor<ArtifactType> dataAccessor) {
      super(OseeCacheEnum.ARTIFACT_TYPE_CACHE, dataAccessor, true);
   }

}
