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
package org.eclipse.osee.framework.resource.management.test.mocks;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class ResourceProviderAdaptor implements IResourceProvider {

   @Override
   public IResource acquire(IResourceLocator locator, Options options) throws OseeCoreException {
      return null;
   }

   @Override
   public int delete(IResourceLocator locator) throws OseeCoreException {
      return 0;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws OseeCoreException {
      return false;
   }

   @Override
   public boolean isValid(IResourceLocator locator) {
      return false;
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<String> getSupportedProtocols() {
      return Collections.emptyList();
   }

}
