/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;

/**
 * @author Roberto E. Escobar
 */
public class MockResourceLocatorManager implements IResourceLocatorManager {

   @SuppressWarnings("unused")
   @Override
   public IResourceLocator generateResourceLocator(String protocol, String seed, String name) throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public IResourceLocator getResourceLocator(String path) throws OseeCoreException {
      return null;
   }

   @Override
   public boolean addResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      return false;
   }

   @Override
   public boolean removeResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      return false;
   }

   @Override
   public Collection<String> getProtocols() {
      return null;
   }

}
