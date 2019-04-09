/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.mocks;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class MockServiceReference implements ServiceReference {

   @Override
   public Dictionary getProperties() {
      return null;
   }

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
