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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "member", namespace = "members")
public class XmlMember {

   private String socketAddress;
   private boolean hasData;
   private boolean localMember;

   public XmlMember() {
      super();
   }

   public XmlMember(String socketAddress, boolean hasData, boolean localMember) {
      super();
      this.socketAddress = socketAddress;
      this.hasData = hasData;
      this.localMember = localMember;
   }

   public boolean isLocal() {
      return localMember;
   }

   public String getSocketAddress() {
      return socketAddress;
   }

   public boolean hasData() {
      return hasData;
   }

   public void setSocketAddress(String socketAddress) {
      this.socketAddress = socketAddress;
   }

   public void setHasData(boolean hasData) {
      this.hasData = hasData;
   }

   public void setLocal(boolean local) {
      this.localMember = local;
   }

}
