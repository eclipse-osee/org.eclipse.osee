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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceLocatorManager {

   /**
    * Generate a resource locator based on protocol, seed and name
    * 
    * @param protocol
    * @param seed
    * @param name
    * @return a resource locator
    * @throws MalformedLocatorException
    */
   IResourceLocator generateResourceLocator(String protocol, String seed, String name) throws OseeCoreException;

   /**
    * Get resource locator based on protocol and path
    * 
    * @param path
    * @return a resource locator
    * @throws MalformedLocatorException
    */
   IResourceLocator getResourceLocator(String path) throws OseeCoreException;

   /**
    * Add resource locator provider
    * 
    * @param resourceLocatorProvider
    * @return <b>true<b> if the locator was added
    */
   boolean addResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider);

   /**
    * Remove resource locator provider
    * 
    * @param resourceLocatorProvider
    * @return <b>true<b> if the locator was removed
    */
   boolean removeResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider);

   /**
    * Supported Protocols
    * 
    * @return supported protocols
    */
   Collection<String> getProtocols();
}
