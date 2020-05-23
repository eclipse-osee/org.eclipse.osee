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

package org.eclipse.osee.framework.core.model.cache;

import java.util.List;
import org.eclipse.osee.framework.core.model.AbstractOseeType;

/**
 * @author Roberto E. Escobar
 */
public class AbstractOseeTypeCacheTest<T extends AbstractOseeType> extends AbstractOseeCacheTest<T> {

   public AbstractOseeTypeCacheTest(List<T> artifactTypes, AbstractOseeCache<T> typeCache) {
      super(artifactTypes, typeCache);
   }

   @Override
   protected Long createKey() {
      return 0x00L;
   }
}
