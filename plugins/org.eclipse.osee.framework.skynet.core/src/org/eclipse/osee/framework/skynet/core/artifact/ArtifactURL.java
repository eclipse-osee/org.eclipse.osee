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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactURL {

   public static URL getOpenInOseeLink(final Artifact artifact, String cmd, PresentationType presentationType) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());
      parameters.put("context", "osee/loopback");
      parameters.put("guid", artifact.getGuid());
      parameters.put("branchUuid", String.valueOf(artifact.getBranch().getGuid()));
      parameters.put("isDeleted", String.valueOf(artifact.isDeleted()));

      if (artifact.isHistorical() && presentationType != PresentationType.DIFF && presentationType != PresentationType.F5_DIFF) {
         parameters.put("transactionId", String.valueOf(artifact.getTransaction()));
      }

      parameters.put("cmd", cmd);
      String urlString = getPermanentLinkBaseUrl(OseeServerContext.CLIENT_LOOPBACK_CONTEXT, parameters);
      URL url = null;
      try {
         url = new URL(urlString);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return url;
   }

   public static URL getOpenInOseeLink(final Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return getOpenInOseeLink(artifact, "open.artifact", presentationType);
   }

   public static String getPermanentLinkBaseUrl(String context, Map<String, String> parameters) throws OseeCoreException {
      try {
         return HttpUrlBuilder.createURL(getSelectedPermanenrLinkUrl(), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static String getSelectedPermanenrLinkUrl() throws OseeCoreException {
      HttpUrlBuilderClient httpBuilder = HttpUrlBuilderClient.getInstance();
      String address = null;
      if (httpBuilder.isUseConnectedServerUrl()) {
         address = httpBuilder.getApplicationServerPrefix();
      } else {
         try {
            address = getPermanentBaseUrl();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
         if (!Strings.isValid(address)) {
            address = httpBuilder.getApplicationServerPrefix();
         }
      }
      return normalize(address);
   }

   public static String getPermanentBaseUrl() throws OseeCoreException {
      String address = OseeInfo.getValue("osee.permanent.base.url");
      return normalize(address);
   }

   private static String normalize(String address) {
      String toReturn = address;
      if (Strings.isValid(toReturn) && !toReturn.endsWith("/")) {
         toReturn += "/";
      }
      return toReturn;
   }
}
