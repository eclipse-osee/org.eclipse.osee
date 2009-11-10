/*
 * Created on Nov 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.test.mocks;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class ResourceProviderAdaptor implements IResourceProvider {

   @Override
   public IResource acquire(IResourceLocator locator, Options options) throws OseeCoreException {
      return null;
   }

   @Override
   public int delete(IResourceLocator locator) throws OseeCoreException {
      return 0;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws OseeCoreException {
      return false;
   }

   @Override
   public boolean isValid(IResourceLocator locator) {
      return false;
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<String> getSupportedProtocols() {
      return Collections.emptyList();
   }

}
