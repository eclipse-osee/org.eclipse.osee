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
package org.eclipse.osee.framework.core.test.cache;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.mocks.MockOseeDataAccessor;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test Case for {@link ArtifactTypeCache}
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCacheTest extends AbstractOseeCacheTest<ArtifactType> {

   private static List<ArtifactType> artifactTypes;
   private static ArtifactTypeCache artCache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      artifactTypes = new ArrayList<ArtifactType>();

      ArtifactDataAccessor artData = new ArtifactDataAccessor(artifactTypes);
      artCache = new ArtifactTypeCache(artData);

      artCache.ensurePopulated();
      Assert.assertTrue(artData.wasLoaded());
   }

   public ArtifactTypeCacheTest() {
      super(artifactTypes, artCache);
   }

   private final static class ArtifactDataAccessor extends MockOseeDataAccessor<ArtifactType> {
      private final List<ArtifactType> artifactTypes;

      public ArtifactDataAccessor(List<ArtifactType> artifactTypes) {
         super();
         this.artifactTypes = artifactTypes;
      }

      @Override
      public void load(IOseeCache<ArtifactType> cache) throws OseeCoreException {
         super.load(cache);

         int typeId = 100;
         for (int index = 0; index < 10; index++) {
            ArtifactType item = MockDataFactory.createArtifactType(index);
            artifactTypes.add(item);
            item.setId(typeId++);
            cache.cache(item);
         }
      }
   }
}
