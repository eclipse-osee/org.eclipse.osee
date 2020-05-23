/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.resource.management.test.mocks;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ResourceProviderAdaptor implements IResourceProvider {

   @Override
   public IResource acquire(IResourceLocator locator, PropertyStore options) {
      return null;
   }

   @Override
   public int delete(IResourceLocator locator) {
      return 0;
   }

   @Override
   public boolean exists(IResourceLocator locator) {
      return false;
   }

   @Override
   public boolean isValid(IResourceLocator locator) {
      return false;
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, PropertyStore options) {
      return null;
   }

   @Override
   public Collection<String> getSupportedProtocols() {
      return Collections.emptyList();
   }

}
