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
package org.eclipse.osee.ote.messaging.dds.entity;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface EntityFactory {

   /**
    * Gets the enabled status.
    * 
    * @return Returns <b>true </b> if this has been enabled, otherwise <b>false </b>.
    */
   public boolean isEnabled();
}
