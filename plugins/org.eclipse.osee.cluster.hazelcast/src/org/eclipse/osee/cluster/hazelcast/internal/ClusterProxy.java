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
package org.eclipse.osee.cluster.hazelcast.internal;

import com.hazelcast.core.HazelcastInstance;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.cluster.Cluster;
import org.eclipse.osee.cluster.Member;

/**
 * @author Roberto E. Escobar
 */
public class ClusterProxy implements Cluster {

   private final HazelcastInstance instance;

   public ClusterProxy(HazelcastInstance instance) {
      this.instance = instance;
   }

   @Override
   public Set<Member> getMembers() {
      Set<Member> members = new HashSet<>();
      for (com.hazelcast.core.Member member : getProxyObject().getMembers()) {
         members.add(new MemberProxy(member));
      }
      return members;
   }

   @Override
   public Member getLocalMember() {
      return new MemberProxy(getProxyObject().getLocalMember());
   }

   @Override
   public long getClusterTime() {
      return getProxyObject().getClusterTime();
   }

   protected com.hazelcast.core.Cluster getProxyObject() {
      return instance.getCluster();
   }
}
