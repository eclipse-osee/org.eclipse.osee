/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

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
   IResourceLocator generateResourceLocator(String protocol, String seed, String name) throws MalformedLocatorException;

   /**
    * Get resource locator based on protocol and path
    * 
    * @param path
    * @return a resource locator
    * @throws MalformedLocatorException
    */
   IResourceLocator getResourceLocator(String path) throws MalformedLocatorException;

   /**
    * Add resource locator provider
    * 
    * @param resourceLocatorProvider
    */
   public void addResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider);

   /**
    * Remove resource locator provider
    * 
    * @param resourceLocatorProvider
    */
   public void removeResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider);
}
