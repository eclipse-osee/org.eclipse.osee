/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.cache;

import java.util.List;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class AbstractOseeTypeCacheTest<T extends AbstractOseeType<String>> extends AbstractOseeCacheTest<String, T> {

   // john chage String in template area
   public AbstractOseeTypeCacheTest(List<T> artifactTypes, AbstractOseeCache<String, T> typeCache) {
      super(artifactTypes, typeCache);
   }

   @Override
   protected String createKey() {
      return GUID.create();
   }
}
