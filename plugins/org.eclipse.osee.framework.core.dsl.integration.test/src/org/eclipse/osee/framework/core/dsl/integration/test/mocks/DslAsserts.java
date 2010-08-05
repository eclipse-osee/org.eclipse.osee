/*
 * Created on Aug 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.test.mocks.MockAccessDetailCollector;

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
}
