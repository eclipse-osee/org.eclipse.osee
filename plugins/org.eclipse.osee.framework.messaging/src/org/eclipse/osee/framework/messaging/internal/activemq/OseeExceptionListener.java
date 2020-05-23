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

package org.eclipse.osee.framework.messaging.internal.activemq;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.eclipse.osee.framework.messaging.internal.FailoverConnectionNode;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeExceptionListener implements ExceptionListener {

   private FailoverConnectionNode failoverConnectionNode;

   @Override
   public void onException(JMSException ex) {
      if (failoverConnectionNode != null) {
         failoverConnectionNode.onException(ex);
      }
   }

   public void setListener(FailoverConnectionNode failoverConnectionNode) {
      this.failoverConnectionNode = failoverConnectionNode;
   }

}
