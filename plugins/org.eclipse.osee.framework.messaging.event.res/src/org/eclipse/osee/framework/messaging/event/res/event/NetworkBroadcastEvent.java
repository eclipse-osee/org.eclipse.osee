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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class NetworkBroadcastEvent extends FrameworkEventBase {

   public static String GUID = "AYxiaIdXukmXPJlYWbQA";
   private String message;
   private String broadcastEventType;
   private Collection<String> userIds;

   public NetworkBroadcastEvent(String broadcastEventType, String message, Collection<String> userIds, NetworkSender networkSender) {
      super(networkSender, GUID);
      this.broadcastEventType = broadcastEventType;
      this.message = message;
      if (userIds == null) {
         this.userIds = java.util.Collections.emptyList();
      } else {
         this.userIds = userIds;
      }
   }

   public NetworkBroadcastEvent(String broadcastEventType, String message, NetworkSender networkSender) {
      this(broadcastEventType, message, null, networkSender);
   }

   public NetworkBroadcastEvent(String xml) {
      super(xml);
   }

   @Override
   public void fromXml(String xml) {
      super.fromXml(xml);
      this.userIds = new ArrayList<String>();
      this.userIds.addAll(Collections.fromString(AXml.getTagData(xml, "bcUserId"), ";"));
      this.broadcastEventType = AXml.getTagData(xml, "bcEventType");
      this.message = AXml.getTagData(xml, "msg");
   }

   @Override
   public void toXml(StringBuffer sb) {
      super.toXml(sb);
      sb.append(AXml.addTagData("bcUserId", Collections.toString(";", userIds)));
      sb.append(AXml.addTagData("bcEventType", broadcastEventType));
      sb.append(AXml.addTagData("msg", message));
   }

   public Collection<String> getUserIds() {
      return userIds;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getBroadcastEventTypeName() {
      return broadcastEventType;
   }

}
