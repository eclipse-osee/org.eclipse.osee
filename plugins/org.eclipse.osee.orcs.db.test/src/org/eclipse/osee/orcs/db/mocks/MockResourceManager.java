/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.DataResource;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;

/**
 * @author Roberto E. Escobar
 */
public class MockResourceManager implements IResourceManager {

   @Override
   public IResource acquire(IResourceLocator locator, PropertyStore options) {
      return null;
   }

   @Override
   public boolean exists(IResourceLocator locator) {
      return false;
   }

   @Override
   public IResourceLocator save(IResourceLocator locatorHint, IResource resource, PropertyStore options) {
      return null;
   }

   @Override
   public int delete(IResourceLocator locator) {
      return 0;
   }

   @Override
   public IResourceLocator generateResourceLocator(String protocol, String seed, String name) {
      return null;
   }

   @Override
   public IResourceLocator getResourceLocator(String path) {
      return null;
   }

   @Override
   public Collection<String> getProtocols() {
      return null;
   }

   @Override
   public byte[] acquire(DataResource dataResource) {
      return null;
   }

   @Override
   public void save(long storageId, String storageName, DataResource dataResource, byte[] rawContent) {
      // do nothing
   }

   @Override
   public void purge(DataResource dataResource) {
      // do nothing
   }
}
