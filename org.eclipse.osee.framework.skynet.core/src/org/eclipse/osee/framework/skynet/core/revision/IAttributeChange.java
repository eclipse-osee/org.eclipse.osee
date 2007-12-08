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
package org.eclipse.osee.framework.skynet.core.revision;

/**
 * @author Robert A. Fisher
 */
public interface IAttributeChange extends IRevisionChange {

   /**
    * @return Returns the change.
    */
   public String getChange();

   /**
    * @return Returns the wasValue.
    */
   public String getWasValue();

   /**
    * @return Returns the name.
    */
   public String getName();

}