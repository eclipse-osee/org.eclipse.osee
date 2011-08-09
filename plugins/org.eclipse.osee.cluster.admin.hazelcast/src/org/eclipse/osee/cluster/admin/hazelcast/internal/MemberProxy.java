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
package org.eclipse.osee.cluster.admin.hazelcast.internal;

import java.net.InetSocketAddress;
import org.eclipse.osee.cluster.admin.Member;

/**
 * @author Roberto E. Escobar
 */
public class MemberProxy implements Member {

   private final com.hazelcast.core.Member member;

   public MemberProxy(com.hazelcast.core.Member member) {
      super();
      this.member = member;
   }

   @Override
   public boolean isLocal() {
      return member.localMember();
   }

   @Override
   public InetSocketAddress getInetSocketAddress() {
      return member.getInetSocketAddress();
   }

   @Override
   public boolean hasData() {
      return !member.isSuperClient();
   }

   protected com.hazelcast.core.Member getProxyObject() {
      return member;
   }
}
