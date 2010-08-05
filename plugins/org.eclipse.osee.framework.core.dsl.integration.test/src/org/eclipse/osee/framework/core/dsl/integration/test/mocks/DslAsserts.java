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
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.test.mocks.MockAccessDetailCollector;

/**
 * @author Roberto E. Escobar
 */
public final class DslAsserts {

   private DslAsserts() {
      // Utility class
   }

   public static void assertNullAccessDetail(RestrictionHandler<?> handler, ObjectRestriction restriction, ArtifactData artifactData) throws OseeCoreException {
      assertAccessDetail(handler, restriction, artifactData, null, null);
   }

   public static void assertAccessDetail(RestrictionHandler<?> handler, ObjectRestriction restriction, ArtifactData artifactData, Object expectedAccessObject, PermissionEnum expectedPermission) throws OseeCoreException {
      MockAccessDetailCollector collector = new MockAccessDetailCollector();
      handler.process(restriction, artifactData, collector);
      AccessDetail<?> actualDetail = collector.getAccessDetails();
      if (expectedAccessObject == null) {
         Assert.assertNull(actualDetail);
      } else {
         Assert.assertNotNull(actualDetail);
         Assert.assertEquals(expectedPermission, actualDetail.getPermission());
         Assert.assertEquals(expectedAccessObject, actualDetail.getAccessObject());
      }
   }

   public static void assertEquals(OseeDsl model1, OseeDsl model2) {
      Assert.assertEquals(model1.getAccessDeclarations().size(), model2.getAccessDeclarations().size());
      Assert.assertEquals(model1.getArtifactRefs().size(), model2.getArtifactRefs().size());
      Assert.assertEquals(model1.getArtifactTypes().size(), model2.getArtifactTypes().size());
      Assert.assertEquals(model1.getAttributeTypes().size(), model2.getAttributeTypes().size());
      Assert.assertEquals(model1.getBranchRefs().size(), model2.getBranchRefs().size());
      Assert.assertEquals(model1.getEnumOverrides().size(), model2.getEnumOverrides().size());
      Assert.assertEquals(model1.getEnumTypes().size(), model2.getEnumTypes().size());
      Assert.assertEquals(model1.getImports().size(), model2.getImports().size());
      Assert.assertEquals(model1.getRelationTypes().size(), model2.getRelationTypes().size());
   }
}
