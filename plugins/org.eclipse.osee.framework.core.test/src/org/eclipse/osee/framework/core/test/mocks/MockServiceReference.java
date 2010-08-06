package org.eclipse.osee.framework.core.test.mocks;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

public class MockServiceReference implements ServiceReference {

   @Override
   public Object getProperty(String key) {
      return null;
   }

   @Override
   public String[] getPropertyKeys() {
      return null;
   }

   @Override
   public Bundle getBundle() {
      return null;
   }

   @Override
   public Bundle[] getUsingBundles() {
      return null;
   }

   @Override
   public boolean isAssignableTo(Bundle bundle, String className) {
      return false;
   }

   @Override
   public int compareTo(Object reference) {
      return 0;
   }

}