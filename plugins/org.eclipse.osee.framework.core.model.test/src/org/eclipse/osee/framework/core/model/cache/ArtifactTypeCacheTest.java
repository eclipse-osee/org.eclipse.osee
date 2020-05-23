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

package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.junit.BeforeClass;

/**
 * Test Case for {@link ArtifactTypeCache}
 *
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCacheTest extends AbstractOseeTypeCacheTest<ArtifactType> {

   private static List<ArtifactType> artifactTypes;
   private static ArtifactTypeCache artCache;

   @BeforeClass
   public static void prepareTestData() {
      artifactTypes = new ArrayList<>();
      artCache = new ArtifactTypeCache();

      long typeId = 100;
      for (int index = 0; index < 10; index++) {
         ArtifactType item = MockDataFactory.createArtifactType(index, typeId++);
         artifactTypes.add(item);
         artCache.cache(item);
      }
   }

   public ArtifactTypeCacheTest() {
      super(artifactTypes, artCache);
   }

}
