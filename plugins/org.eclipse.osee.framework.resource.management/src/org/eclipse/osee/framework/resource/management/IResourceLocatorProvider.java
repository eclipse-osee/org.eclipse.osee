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

/**
 * @author Roberto E. Escobar
 */
public interface IResourceLocatorProvider {

   /**
    * Supported Protocol
    */
   public String getSupportedProtocol();

   /**
    * Check if this provider is valid
    * 
    * @return <b>true</b> if this provider is valid
    */
   public boolean isValid(String protocol);

   /**
    * Generate a resource locator based on seed and name
    * 
    * @return a resource locator
    */
   IResourceLocator generateResourceLocator(String seed, String name) ;

   /**
    * Get resource locator
    * 
    * @return a resource locator
    */
   IResourceLocator getResourceLocator(String path) ;
}
