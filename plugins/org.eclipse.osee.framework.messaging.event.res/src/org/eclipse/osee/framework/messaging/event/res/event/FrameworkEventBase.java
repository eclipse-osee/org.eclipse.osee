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
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEvent;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEventBase implements IFrameworkEvent {

   private NetworkSender networkSender;
   private String eventGuid;

   public FrameworkEventBase(NetworkSender networkSender, String eventGuid) {
      this.networkSender = networkSender;
      this.eventGuid = eventGuid;
   }

   public FrameworkEventBase(String xml) {
      fromXml(xml);
   }

   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   public String getEventGuid() {
      return eventGuid;
   }

   public void toXml(StringBuffer sb) {
      sb.append(AXml.addTagData("eventGuid", eventGuid));
      this.networkSender.toXml(sb);
   }

   public void fromXml(String xml) {
      this.eventGuid = AXml.getTagData(xml, "eventGuid");
      this.networkSender = new NetworkSender(xml);
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      toXml(sb);
      return sb.toString();
   }

}
