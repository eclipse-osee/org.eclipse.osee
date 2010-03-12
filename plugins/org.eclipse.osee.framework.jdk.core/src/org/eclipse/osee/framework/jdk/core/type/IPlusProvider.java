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
package org.eclipse.osee.framework.jdk.core.type;

/**
 * This object is designed for use with the HashCollectionPlus in order to provide new objects of a particular type.
 * 
 * @author David Diepenbrock
 */
public interface IPlusProvider<O> {

   /**
    * @return a new object of type O.
    */
   public O newObject();

}
