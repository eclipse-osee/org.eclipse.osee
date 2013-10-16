/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.jms.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.jms.OteServerJmsNodeProvider;



/**
 * @author Michael P. Masterson
 */
public final class ServerSideConnectionNodeProviderImpl implements OteServerJmsNodeProvider {
   private TestEnvironmentInterface testEnv;
   private MessageService messageService;

   private static OteServerJmsNodeProvider instance;

   public void start() {
      instance = this;
   }

   public void stop() {
   }

   public synchronized void setTestEnvironmentInterface(TestEnvironmentInterface testEnv) {
      this.testEnv = testEnv;
   }

   public synchronized void unsetTestEnvironmentInterface(TestEnvironmentInterface testEnv) {
      this.testEnv = null;
   }

   public synchronized void setMessageService(MessageService messageService) {
      this.messageService = messageService;
   }

   public synchronized void unsetMessageService(MessageService messageService) {
      this.messageService = null;
   }

   public static OteServerJmsNodeProvider getInstance() {
      return instance;
   }

   @Override
   public synchronized ConnectionNode getConnectionNode() {
      if (testEnv instanceof TestEnvironment) {
         NodeInfo info = ((TestEnvironment) testEnv).getOteNodeInfo();
         try {
            ConnectionNode connection = messageService.get(info);
            return connection;
         } catch (OseeCoreException ex) {
            OseeLog.log(this.getClass(), Level.SEVERE, ex);
         }
      }

      return null;
   }
}
