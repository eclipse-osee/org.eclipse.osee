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

package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;

/**
 * @author Andrew M. Finkbeiner
 */
public class HealthRequestListener extends OseeMessagingListener {
   private final CompositeKeyHashMap<String, String, List<UpdateStatus>> mapForReplys;

   public HealthRequestListener(CompositeKeyHashMap<String, String, List<UpdateStatus>> mapForReplys) {
      super(ServiceHealthRequest.class);
      this.mapForReplys = mapForReplys;
   }

   @Override
   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      if (replyConnection.isReplyRequested()) {
         ServiceHealthRequest request = (ServiceHealthRequest) message;
         List<UpdateStatus> updates = mapForReplys.get(request.getServiceName(), request.getServiceVersion());
         if (updates != null) {
            for (UpdateStatus update : updates) {
               if (update != null) {
                  update.run();
               }
            }
         }
      }
   }

}
