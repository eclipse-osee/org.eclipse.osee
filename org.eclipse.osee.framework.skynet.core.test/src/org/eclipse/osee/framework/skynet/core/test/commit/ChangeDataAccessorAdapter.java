package org.eclipse.osee.framework.skynet.core.test.commit;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.commit.IChangeDataAccessor;
import org.eclipse.osee.framework.skynet.core.commit.IChangeFactory;
import org.eclipse.osee.framework.skynet.core.commit.IChangeLocator;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange;

public class ChangeDataAccessorAdapter implements IChangeDataAccessor {
   private boolean changeDataLoaded;

   public ChangeDataAccessorAdapter() {
      changeDataLoaded = false;
   }

   public boolean wasChangeDataLoaded() {
      return changeDataLoaded;
   }

   public void setChangeDataLoaded(boolean changeDataLoaded) {
      this.changeDataLoaded = changeDataLoaded;
   }

   @Override
   public void loadChangeData(IProgressMonitor monitor, IChangeFactory factory, IChangeLocator locator, Collection<OseeChange> oseeChange) throws Exception {
      setChangeDataLoaded(true);
   }

}
