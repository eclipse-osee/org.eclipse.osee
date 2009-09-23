package org.eclipse.osee.framework.skynet.core.test.types;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeStorableType;
import org.eclipse.osee.framework.skynet.core.types.IOseeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;

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
   public void load(AbstractOseeCache<T> cache, IOseeTypeFactory factory) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(factory);
      setLoadCalled(true);
   }

   @Override
   public void store(AbstractOseeCache<T> cache, Collection<T> types) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(types);
      setStoreCalled(true);
   }

}
