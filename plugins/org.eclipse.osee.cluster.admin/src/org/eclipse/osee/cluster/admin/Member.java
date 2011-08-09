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
package org.eclipse.osee.cluster.admin;

import java.net.InetSocketAddress;

/**
 * @author Roberto E. Escobar
 */
public interface Member {

   /**
    * Returns if this member is the local member.
    * 
    * @return <tt>true<tt> if this member is the
    *         local member, <tt>false</tt> otherwise.
    */
   boolean isLocal();

   /**
    * Returns the InetSocketAddress of this member.
    * 
    * @return InetSocketAddress of this member
    */
   InetSocketAddress getInetSocketAddress();

   /**
    * Returns if this cluster member holds any data on it.
    * 
    * @return <tt>true</tt> if this member has data, <tt>false</tt> otherwise
    */
   boolean hasData();
}