/*
 * Created on Dec 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.ITranslatorId;
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
