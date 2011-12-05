/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.cache;

import java.util.List;
import org.eclipse.osee.framework.core.model.AbstractOseeType;

/**
 * @author Roberto E. Escobar
 */
public class AbstractOseeTypeCacheTest<T extends AbstractOseeType<Long>> extends AbstractOseeCacheTest<Long, T> {

   public AbstractOseeTypeCacheTest(List<T> artifactTypes, AbstractOseeCache<Long, T> typeCache) {
      super(artifactTypes, typeCache);
   }

   @Override
   protected Long createKey() {
      return 0x00L;
   }
}
