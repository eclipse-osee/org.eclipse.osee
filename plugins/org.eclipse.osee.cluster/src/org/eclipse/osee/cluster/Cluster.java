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
package org.eclipse.osee.cluster;

import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public interface Cluster {

   /**
    * Set of current cluster members
    * 
    * @return cluster members
    */
   Set<Member> getMembers();

   /**
    * Returns the member running on the local machine
    * 
    * @return this member
    */
   Member getLocalMember();

   /**
    * Returns the cluster-wide time
    * 
    * @return cluster-wide time
    */
   long getClusterTime();
}