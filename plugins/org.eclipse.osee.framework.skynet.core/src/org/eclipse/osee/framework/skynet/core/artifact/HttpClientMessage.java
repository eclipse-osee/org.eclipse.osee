/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslatorId;
import org.eclipse.osee.framework.core.util.HttpMessage;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class HttpClientMessage {

   private HttpClientMessage() {
   }

   public static <J, K> J send(String context, Map<String, String> parameters, ITranslatorId requestId, K requestData, ITranslatorId responseId) throws OseeCoreException {
      if (!parameters.containsKey("sessionId")) {
         parameters.put("sessionId", ClientSessionManager.getSessionId());
      }
      IDataTranslationService service = Activator.getInstance().getTranslationService();
      String urlString = HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(context, parameters);
      return HttpMessage.send(urlString, service, requestId, requestData, responseId);
   }
}
