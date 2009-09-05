package org.eclipse.osee.framework.skynet.core.test.commit;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.commit.ChangeLocator;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Low-level Change Data Test - checks :
 * 
 * @author Roberto E. Escobar
 */
public class ChangeDataTest {
   private static ChangeDataAccessor dataAccessor;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      dataAccessor = new ChangeDataAccessor();
   }

   @Test
   public void testSomething() throws OseeCoreException {

   }

   private final static class ChangeDataAccessor extends ChangeDataAccessorAdapter {

      public ChangeDataAccessor() {
         super();
      }

      @Override
      public void loadChangeData(IProgressMonitor monitor, ChangeLocator locator, List<OseeChange> oseeChange) throws Exception {
         super.loadChangeData(monitor, locator, oseeChange);

         /// Add Data Here
      }
   }
}
