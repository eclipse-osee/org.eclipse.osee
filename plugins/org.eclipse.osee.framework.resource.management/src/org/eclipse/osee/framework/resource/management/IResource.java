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

import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IResource {

   /**
    * Returns an open input stream of the contents of this resource.
    * 
    * @return an input stream containing the contents of this resource
    *  if this method fails.
    */
   public InputStream getContent() ;

   /**
    * Returns the absolute URI of this resource, or <code>null</code> if no URI can be determined.
    * 
    * @return the absolute URI of this resource, or <code>null</code> if no URI can be determined
    */
   public URI getLocation();

   /**
    * Get the name of this resource
    * 
    * @return name of this resource
    */
   public String getName();

   /**
    * Whether this resource is compressed or not.
    * 
    * @return <b>true</b> If this resource is compressed. <b>false</b> If this resource is not compressed.
    */
   public boolean isCompressed();

}
