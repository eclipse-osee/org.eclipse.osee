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
public interface IResourceLocatorProvider {

   /**
    * Check if this provider is valid
    * 
    * @param protocol
    * @return <b>true</b> if this provider is valid
    */
   public boolean isValid(String protocol);

   /**
    * Generate a resource locator based on seed and name
    * 
    * @param seed
    * @param name
    * @return a resource locator
    * @throws MalformedLocatorException
    */
   IResourceLocator generateResourceLocator(String seed, String name) throws MalformedLocatorException;

   /**
    * Get resource locator
    * 
    * @param path
    * @return a resource locator
    * @throws MalformedLocatorException
    */
   IResourceLocator getResourceLocator(String path) throws MalformedLocatorException;
}
