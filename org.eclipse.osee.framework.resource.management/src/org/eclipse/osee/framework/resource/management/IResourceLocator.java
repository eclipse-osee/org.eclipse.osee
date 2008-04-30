/*
 * Created on Apr 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceLocator {

   /**
    * Location describing a resource
    * 
    * @return uri to resource
    */
   public URI getLocation();

   /**
    * Get this locators protocol
    * 
    * @return String
    */
   public String getProtocol();

   /**
    * Get the raw path.
    * 
    * @return raw path
    */
   public String getRawPath();
}
