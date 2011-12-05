/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.cache;

import java.util.List;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeItemCacheTest<T extends AbstractOseeType<String>> extends AbstractOseeCacheTest<String, T> {

   public AbstractOseeItemCacheTest(List<T> artifactTypes, AbstractOseeCache<String, T> typeCache) {
      super(artifactTypes, typeCache);
   }

   @Override
   protected String createKey() {
      return GUID.create();
   }
}
