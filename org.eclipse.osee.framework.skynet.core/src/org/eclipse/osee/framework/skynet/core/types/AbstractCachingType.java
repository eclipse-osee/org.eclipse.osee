/*
 * Created on Nov 16, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.types;

import org.eclipse.osee.framework.core.data.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.AbstractOseeType;
import org.eclipse.osee.framework.core.data.IOseeStorableType;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractCachingType extends AbstractOseeType {

   private final AbstractOseeCache<? extends IOseeStorableType> cache;

   protected AbstractCachingType(AbstractOseeCache<? extends IOseeStorableType> cache, String guid, String name) {
      super(guid, name);
      this.cache = cache;
      initializeFields();
   }

   protected AbstractOseeCache<? extends IOseeStorableType> getCache() {
      return cache;
   }

   protected abstract void initializeFields();
}
