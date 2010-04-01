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

import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class NetworkTransactionDeletedEvent extends FrameworkEventBase {

   private List<Integer> transactionIds;
   public static String GUID = "AYxiaIb0axwYCE5wXiAA";

   public List<Integer> getTransactionIds() {
      return transactionIds;
   }

   public NetworkTransactionDeletedEvent(NetworkSender networkSender, List<Integer> transactionIds) {
      super(networkSender, GUID);
      this.transactionIds = transactionIds;
   }

   public NetworkTransactionDeletedEvent(String xml) {
      super(xml);
   }

   @Override
   public void fromXml(String xml) {
      super.fromXml(xml);
      this.transactionIds = AXml.getTagIntegerDataArray(xml, "transId", ";");
   }

   @Override
   public void toXml(StringBuffer sb) {
      super.toXml(sb);
      sb.append(AXml.addTagData("transId", transactionIds, ";"));
   }

}
