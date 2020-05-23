/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model.cache;

import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCache extends AbstractOseeCache<ArtifactType> {

   public ArtifactTypeCache() {
      super(OseeCacheEnum.ARTIFACT_TYPE_CACHE);
   }

}
