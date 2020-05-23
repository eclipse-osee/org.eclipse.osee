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

package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertTrue;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.osee.framework.messaging.MessageService;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageServiceController implements HasMessageService {

   public enum BrokerType {
      EMBEDDED_BROKER,
      VM_BROKER
   }

   private MessageServiceProxy messageServiceProviderImpl = null;
   private ConcurrentHashMap<String, BrokerService> brokers;

   private final BrokerType brokerType;
   private final String brokerName;
   private final String brokerURI;

   public MessageServiceController(BrokerType brokerType, String brokerName, String brokerURI) {
      this.brokerType = brokerType;
      this.brokerName = brokerName;
      this.brokerURI = brokerURI;
   }

   public void start() throws Exception {
      brokers = new ConcurrentHashMap<>();

      messageServiceProviderImpl = new MessageServiceProxy();
      messageServiceProviderImpl.start();

      if (BrokerType.EMBEDDED_BROKER == brokerType) {
         BrokerService broker = createEmbeddedBroker();
         broker.start();
         brokers.put(brokerURI, broker);
      }
   }

   public void stop() throws Exception {
      if (BrokerType.EMBEDDED_BROKER == brokerType) {
         BrokerService broker = brokers.get(brokerURI);
         if (broker != null) {
            broker.stop();
         }
      }
      messageServiceProviderImpl.stop();
   }

   private BrokerService createEmbeddedBroker() throws Exception {
      BrokerService broker = new BrokerService();
      broker.setBrokerName(brokerName);
      broker.setPersistent(false);
      broker.setUseShutdownHook(true);
      broker.addConnector(brokerURI);
      return broker;
   }

   @Override
   public MessageService getMessageService() {
      MessageService messaging = null;
      messaging = messageServiceProviderImpl.getProxiedService();
      assertTrue(messaging != null);
      return messaging;
   }
}
