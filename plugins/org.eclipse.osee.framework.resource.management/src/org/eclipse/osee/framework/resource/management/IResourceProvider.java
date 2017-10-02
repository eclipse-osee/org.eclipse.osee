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

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceProvider {

   /**
    * Determines whether this provider is valid for this locator
    * 
    * @param locator location of the resource needed
    */
   public boolean isValid(IResourceLocator locator);

   /**
    * Acquire resource specified by resource locator
    * 
    * @param locator location of the resource needed
    * @param options operation options
    * @return the resource
    */
   public IResource acquire(IResourceLocator locator, PropertyStore options) ;

   /**
    * Determines if a resource exists for the given locator.
    * 
    * @param locator location of the data to check
    */
   public boolean exists(IResourceLocator locator) ;

   /**
    * Save input to location specified by resource locator
    * 
    * @param locator location where to store the data
    * @param options operation options
    * @param resource the resource to save
    */
   public IResourceLocator save(IResourceLocator locator, IResource resource, PropertyStore options) ;

   /**
    * Delete resource specified by resource locator
    * 
    * @param locator location of the resource to delete
    */
   public int delete(IResourceLocator locator) ;

   /**
    * Get provider supported protocols
    * 
    * @return supported protocols
    */
   public Collection<String> getSupportedProtocols();
}
