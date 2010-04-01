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
package org.eclipse.osee.framework.messaging.event.res.event;

import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class NetworkSender {

   private String sourceObject;
   private String sessionId;
   private String machineName;
   private String userId;
   private String machineIp;
   private String clientVersion;
   private String port;

   public NetworkSender(String sourceObject, String sessionId, String machineName, String userId, String machineIp, String port, String clientVersion) {
      this.sessionId = sessionId;
      this.sourceObject = sourceObject;
      this.machineName = machineName;
      this.userId = userId;
      this.machineIp = machineIp;
      this.port = port;
      this.clientVersion = clientVersion;
   }

   public NetworkSender(String xml) {
      fromXml(xml);
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      toXml(sb);
      return sb.toString();
   }

   public void toXml(StringBuffer sb) {
      sb.append(AXml.addTagData("obj", "" + sourceObject));
      sb.append(AXml.addTagData("sId", "" + sessionId));
      sb.append(AXml.addTagData("macName", "" + machineName));
      sb.append(AXml.addTagData("userId", "" + userId));
      sb.append(AXml.addTagData("macIp", "" + machineIp));
      sb.append(AXml.addTagData("port", "" + port));
      sb.append(AXml.addTagData("cVer", "" + clientVersion));
   }

   private void fromXml(String xml) {
      this.sourceObject = AXml.getTagData(xml, "obj");
      this.sessionId = AXml.getTagData(xml, "sId");
      this.machineName = AXml.getTagData(xml, "macName");
      this.userId = AXml.getTagData(xml, "userId");
      this.machineIp = AXml.getTagData(xml, "macIp");
      this.port = AXml.getTagData(xml, "port");
      this.clientVersion = AXml.getTagData(xml, "cVer");
   }

   public String getSourceObject() {
      return sourceObject;
   }

   public String getSessionId() {
      return sessionId;
   }

   public String getMachineName() {
      return machineName;
   }

   public String getUserId() {
      return userId;
   }

   public String getMachineIp() {
      return machineIp;
   }

   public String getClientVersion() {
      return clientVersion;
   }

   public String getPort() {
      return port;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((clientVersion == null) ? 0 : clientVersion.hashCode());
      result = prime * result + ((machineIp == null) ? 0 : machineIp.hashCode());
      result = prime * result + ((machineName == null) ? 0 : machineName.hashCode());
      result = prime * result + ((port == null) ? 0 : port.hashCode());
      result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
      result = prime * result + ((sourceObject == null) ? 0 : sourceObject.hashCode());
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      NetworkSender other = (NetworkSender) obj;
      if (clientVersion == null) {
         if (other.clientVersion != null) return false;
      } else if (!clientVersion.equals(other.clientVersion)) return false;
      if (machineIp == null) {
         if (other.machineIp != null) return false;
      } else if (!machineIp.equals(other.machineIp)) return false;
      if (machineName == null) {
         if (other.machineName != null) return false;
      } else if (!machineName.equals(other.machineName)) return false;
      if (port == null) {
         if (other.port != null) return false;
      } else if (!port.equals(other.port)) return false;
      if (sessionId == null) {
         if (other.sessionId != null) return false;
      } else if (!sessionId.equals(other.sessionId)) return false;
      if (sourceObject == null) {
         if (other.sourceObject != null) return false;
      } else if (!sourceObject.equals(other.sourceObject)) return false;
      if (userId == null) {
         if (other.userId != null) return false;
      } else if (!userId.equals(other.userId)) return false;
      return true;
   }

}
