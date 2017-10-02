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
package org.eclipse.osee.framework.resource.management;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceManager {

   public static final int OK = 1;
   public static final int FAIL = 2;
   public static final int RESOURCE_NOT_FOUND = 3;

   /**
    * Acquire resource specified by resource locator
    *
    * @param locator location of the resource needed
    * @param options operation options
    * @return the resource
    */
   IResource acquire(IResourceLocator locator, PropertyStore options) ;

   /**
    * Determines if a resource exists for the given locator.
    *
    * @param locator location of the data to check
    */
   boolean exists(IResourceLocator locator) ;

   /**
    * Save input to location specified by resource locator
    *
    * @param locator location where to store the data
    * @param resource to store
    * @param options operation options
    */
   IResourceLocator save(final IResourceLocator locatorHint, final IResource resource, final PropertyStore options) ;

   /**
    * Delete resource specified by resource locator
    *
    * @param locator location of the resource to delete
    */
   int delete(IResourceLocator locator) ;

   /**
    * Generate a resource locator based on protocol, seed and name
    *
    * @return a resource locator
    */
   IResourceLocator generateResourceLocator(String protocol, String seed, String name) ;

   /**
    * Get resource locator based on protocol and path
    *
    * @return a resource locator
    */
   IResourceLocator getResourceLocator(String path) ;

   /**
    * Supported Protocols
    *
    * @return supported protocols
    */
   Collection<String> getProtocols();

   byte[] acquire(DataResource dataResource);

   void save(long storageId, String storageName, DataResource dataResource, byte[] rawContent);

   void purge(DataResource dataResource);
}
