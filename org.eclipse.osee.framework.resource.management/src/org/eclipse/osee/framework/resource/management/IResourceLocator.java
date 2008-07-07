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
