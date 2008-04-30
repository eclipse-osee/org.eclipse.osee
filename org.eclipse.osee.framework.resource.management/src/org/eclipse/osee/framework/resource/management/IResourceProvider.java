/*
 * Created on Apr 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;


/**
 * @author Roberto E. Escobar
 */
public interface IResourceProvider {

   /**
    * Determines whether this provider is valid for this locator
    * 
    * @param locator location of the resource needed
    * @return boolean
    */
   public boolean isValid(IResourceLocator locator);

   /**
    * Acquire resource specified by resource locator
    * 
    * @param locator location of the resource needed
    * @param options operation options
    * @return the resource
    */
   public IResource acquire(IResourceLocator locator, Options options) throws Exception;

   /**
    * Determines if a resource exists for the given locator.
    * 
    * @param locator location of the data to check
    */
   public boolean exists(IResourceLocator locator) throws Exception;
   
   /**
    * Save input to location specified by resource locator
    * 
    * @param locator location where to store the data
    * @param options operation options
    * @param resource the resource to save
    * @return status
    */
   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws Exception;

   /**
    * Delete resource specified by resource locator
    * 
    * @param locator location of the resource to delete
    * @return status
    */
   public int delete(IResourceLocator locator) throws Exception;
}
