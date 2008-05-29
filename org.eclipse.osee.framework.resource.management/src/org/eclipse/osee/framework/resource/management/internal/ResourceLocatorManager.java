/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
public class ResourceLocatorManager implements IResourceLocatorManager {

   private List<IResourceLocatorProvider> resourceLocatorProviders;

   public ResourceLocatorManager() {
      this.resourceLocatorProviders = new CopyOnWriteArrayList<IResourceLocatorProvider>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorManager#addResourceLocatorProvider(org.eclipse.osee.framework.resource.management.IResourceLocatorProvider)
    */
   @Override
   public void addResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      this.resourceLocatorProviders.add(resourceLocatorProvider);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorManager#removeResourceLocatorProvider(org.eclipse.osee.framework.resource.management.IResourceLocatorProvider)
    */
   @Override
   public void removeResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      this.resourceLocatorProviders.remove(resourceLocatorProvider);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorManager#generateResourceLocator(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public IResourceLocator generateResourceLocator(String protocol, String seed, String name) throws MalformedLocatorException {
      IResourceLocatorProvider resourceLocatorProvider = getProvider(protocol);
      return resourceLocatorProvider.generateResourceLocator(seed, name);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorManager#getResourceLocator(java.lang.String, java.lang.String)
    */
   @Override
   public IResourceLocator getResourceLocator(String path) throws MalformedLocatorException {
      IResourceLocatorProvider resourceLocatorProvider = getProvider(path);
      return resourceLocatorProvider.getResourceLocator(path);
   }

   private IResourceLocatorProvider getProvider(String protocol) throws MalformedLocatorException {
      IResourceLocatorProvider toReturn = null;
      for (IResourceLocatorProvider provider : resourceLocatorProviders) {
         if (provider.isValid(protocol) != false) {
            toReturn = provider;
            break;
         }
      }
      if (toReturn == null) {
         throw new MalformedLocatorException(String.format("Error finding locator for [%s] in [%s]", protocol,
               resourceLocatorProviders));
      }
      return toReturn;
   }

}
