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
package org.eclipse.osee.framework.jdk.core.util;

/**
 * Implemented by objects that are handled by a persistence manager.
 * 
 * @author Robert A. Fisher
 */
public interface PersistenceObject {

   /**
    * @return The <code>PersistenceMemo</code> set on this object.
    */
   public PersistenceMemo getPersistenceMemo();

   /**
    * Set the persistence memo for this object. This should only be called by the persistence manager that is handling
    * this object.
    * 
    * @param memo The persistence memo to assign to this object.
    */
   public void setPersistenceMemo(PersistenceMemo memo);
}
