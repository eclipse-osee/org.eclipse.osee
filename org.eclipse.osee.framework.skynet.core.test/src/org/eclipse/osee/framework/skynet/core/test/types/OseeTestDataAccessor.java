package org.eclipse.osee.framework.skynet.core.test.types;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.IOseeDataAccessor;
import org.eclipse.osee.framework.core.data.IOseeStorableType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class OseeTestDataAccessor<T extends IOseeStorableType> implements IOseeDataAccessor<T> {

   private boolean wasLoadCalled = false;
   private boolean wasStoreCalled = false;

   public void setLoadCalled(boolean wasLoadCalled) {
      this.wasLoadCalled = wasLoadCalled;
   }

   public void setStoreCalled(boolean wasStoreCalled) {
      this.wasStoreCalled = wasStoreCalled;
   }

   public boolean wasLoaded() {
      return wasLoadCalled;
   }

   public boolean wasStoreCalled() {
      return wasStoreCalled;
   }

   @Override
   public void load(AbstractOseeCache<T> cache) throws OseeCoreException {
      Assert.assertNotNull(cache);
      setLoadCalled(true);
   }

   @Override
   public void store(AbstractOseeCache<T> cache, Collection<T> types) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(types);
      setStoreCalled(true);
   }

}
