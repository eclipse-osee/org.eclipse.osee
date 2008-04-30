/*
 * Created on Apr 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;


/**
 * @author Roberto E. Escobar
 */
public interface IResourceManager {

   public static final int OK = 1;
   public static final int FAIL = 2;
   public static final int RESOURCE_NOT_FOUND = 3;

   /**
    * Add listener to list
    * 
    * @param listener
    */
   public void addResourceListener(IResourceListener listener);

   /**
    * Remove listener from list
    * 
    * @param listener
    */
   public void removeResourceListener(IResourceListener listener);

   /**
    * Add a resource provider
    * 
    * @param resourceProvider to add
    */
   public void addResourceProvider(IResourceProvider resourceProvider);

   /**
    * Remove a resource provider
    * 
    * @param resourceProvider to remove
    */
   public void removeResourceProvider(IResourceProvider resourceProvider);

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
    * @param resource to store
    * @param options operation options
    * @return status
    */
   public IResourceLocator save(final IResourceLocator locatorHint, final IResource resource, final Options options) throws Exception;

   /**
    * Delete resource specified by resource locator
    * 
    * @param locator location of the resource to delete
    * @return status
    */
   public int delete(IResourceLocator locator) throws Exception;
}
