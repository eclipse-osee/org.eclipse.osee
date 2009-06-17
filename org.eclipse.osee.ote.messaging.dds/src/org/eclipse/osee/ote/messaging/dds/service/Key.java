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
package org.eclipse.osee.ote.messaging.dds.service;

/**
 * Provides the DDS system with a means of recognizing multiple instances of one type.
 * Users of the DDS system should provide an object that implements this interface
 * for topics which may have multiple instances.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface Key {
   
   /**
    * This method returns true iff the information in both sets of data
    * represent the same instance.
    * 
    * This method should be commutative, that is, isSameInstance(A,B) should
    * always return the same value as isSameInstance(B,A).
    * 
    * @return <b>true</b> if the data values are the same instance, <b>false</b> otherwise.
    */
   public boolean isSameInstance(byte[] data1, byte[] data2);
}
