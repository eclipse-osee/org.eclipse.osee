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
package org.eclipse.osee.framework.core.model.cache;

import java.util.List;
import org.eclipse.osee.framework.core.model.AbstractOseeType;

/**
 * @author Roberto E. Escobar
 */
public class AbstractOseeTypeCacheTest<T extends AbstractOseeType<Long>> extends AbstractOseeCacheTest<T> {

   public AbstractOseeTypeCacheTest(List<T> artifactTypes, AbstractOseeCache<Long, T> typeCache) {
      super(artifactTypes, typeCache);
   }

   @Override
   protected Long createKey() {
      return 0x00L;
   }
}
