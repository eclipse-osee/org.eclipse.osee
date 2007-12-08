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
package org.eclipse.osee.framework.skynet.core;

/**
 * @author Ryan D. Brooks
 */
public interface PersistenceManager {

   /**
    * Setup references to other managers. This has to be called seperate from the constructor since the other managers
    * will also setup links to this manager causing an infinite loop since none of the objects would finish
    * construction.
    * 
    * @throws Exception TODO
    */
   public abstract void onManagerWebInit() throws Exception;
}