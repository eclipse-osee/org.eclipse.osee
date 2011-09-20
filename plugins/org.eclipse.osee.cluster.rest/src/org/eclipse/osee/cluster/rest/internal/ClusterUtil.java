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
package org.eclipse.osee.cluster.rest.internal;

import java.net.InetSocketAddress;
import org.eclipse.osee.cluster.Member;

/**
 * @author Roberto E. Escobar
 */
public final class ClusterUtil {

   private ClusterUtil() {
      // Utility class
   }

   public static String asId(InetSocketAddress address) {
      return address.toString();
   }

   public static XmlMember fromMember(Member member) {
      return new XmlMember(member.getInetSocketAddress().toString(), member.hasData(), member.isLocal());
   }
}
