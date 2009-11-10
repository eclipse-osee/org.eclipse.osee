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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

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
   IResourceLocator generateResourceLocator(String seed, String name) throws OseeCoreException;

   /**
    * Get resource locator
    * 
    * @param path
    * @return a resource locator
    * @throws MalformedLocatorException
    */
   IResourceLocator getResourceLocator(String path) throws OseeCoreException;
}
