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

package org.eclipse.osee.framework.messaging;

/**
 * @author Roberto E. Escobar
 */
public enum SystemTopic implements MessageID {

   JMS_HEALTH_STATUS("jms.health.status"),
   KILL_TEST_JMS_BROKER("jms.kill.broker");

   private String name;

   SystemTopic(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Class<?> getSerializationClass() {
      return null;
   }

   @Override
   public boolean isReplyRequired() {
      return false;
   }

   @Override
   public String getId() {
      return name;
   }

   @Override
   public boolean isTopic() {
      return true;
   }
}
