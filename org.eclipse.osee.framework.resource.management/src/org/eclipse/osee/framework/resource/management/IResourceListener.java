/*
 * Created on Apr 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceListener {

   /**
    * Event triggered before a resource is deleted
    * 
    * @param locator
    */
   public void onPreDelete(IResourceLocator locator);

   /**
    * Event triggered after a resource is deleted
    * 
    * @param locator
    */
   public void onPostDelete(IResourceLocator locator);

   /**
    * Event triggered before a resource is saved
    * 
    * @param locator
 * @param options 
 * @param resource 
    */
   public void onPreSave(IResourceLocator locator, IResource resource, Options options);

   /**
    * Event triggered after a resource is saved
 * @param locator 
    * 
    * @param resource
 * @param options 
    */
   public void onPostSave(IResourceLocator locator, IResource resource, Options options);

   /**
    * Event triggered before a resource is acquired
    * 
    * @param locator
    */
   public void onPreAcquire(IResourceLocator locator);

   /**
    * Event triggered after a resource is acquired
    * 
    * @param resource
    */
   public void onPostAcquire(IResource resource);

}
