/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.junit.Assert;

/**
 * @author Andrew M. Finkbeiner
 */
public class DefaultNodeInfos {

   public static final String OSEE_JMS_DEFAULT_PORT = "61616";
   public static final String OSEE_JMS_NODE = "osee-jms";
   public static final String OSEE_JMS_BROKER_URI = "tcp://localhost:61616";

   public static final String OSEE_VM_NODE = "osee-vm";
   public static final String OSEE_VM_BROKER_URI = "vm://localhost?broker.persistent=false";

   public static NodeInfo OSEE_JMS_NODE_INFO;
   public static NodeInfo OSEE_VM_NODE_INFO;

   static {
      try {
         OSEE_JMS_NODE_INFO = new NodeInfo(OSEE_JMS_NODE, new URI(OSEE_JMS_BROKER_URI));
         OSEE_VM_NODE_INFO = new NodeInfo(OSEE_VM_NODE, new URI(OSEE_VM_BROKER_URI));
      } catch (URISyntaxException ex) {
         OseeLog.log(DefaultNodeInfos.class, Level.SEVERE, ex);
      }
   }

   public static String generateBrokerUri(int port) {
      return String.format("tcp://localhost:%s", port);
   }

   public static URI asURI(String value) throws Exception {
      Assert.assertNotNull(value);
      return new URI(value);
   }

}
