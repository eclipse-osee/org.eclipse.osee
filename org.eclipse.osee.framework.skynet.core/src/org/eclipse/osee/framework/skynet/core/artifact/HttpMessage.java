/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class HttpMessage {

   private HttpMessage() {
   }

   @SuppressWarnings("unchecked")
   public static <J, K> J send(String context, Map<String, String> parameters, K requestData, Class<J> clazzResponse) throws OseeCoreException {
      String urlString = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(context, parameters);
      InputStream inputStream = null;
      try {
         IDataTranslationService service = Activator.getInstance().getTranslationService();
         inputStream = service.convertToStream(requestData);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.post(new URL(urlString), inputStream, "text/xml", "UTF-8", buffer);
         if (result.wasSuccessful()) {
            if (AcquireResult.class == clazzResponse) {
               return (J) result;
            } else {
               return service.convert(new ByteArrayInputStream(buffer.toByteArray()), clazzResponse);
            }
         } else {
            throw new OseeCoreException(String.format("Request [%s] failed.", urlString));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      } finally {
         Lib.close(inputStream);
      }
   }
}
