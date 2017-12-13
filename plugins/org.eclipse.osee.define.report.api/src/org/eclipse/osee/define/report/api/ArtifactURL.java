/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.api;

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;
import static org.eclipse.osee.orcs.SystemPreferences.OSEE_PERMANENT_URL;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class ArtifactURL {

   public static URL getOpenInOseeLink(final ArtifactReadable artifact, String cmd, PresentationType presentationType, String sessionId, OrcsApi orcsApi) {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("sessionId", sessionId);
      parameters.put("context", "osee/loopback");
      parameters.put("guid", artifact.getGuid());
      parameters.put("branchUuid", artifact.getBranch().getIdString());
      parameters.put("isDeleted", String.valueOf(artifact.isDeleted()));

      if (artifact.isHistorical() && presentationType != PresentationType.DIFF && presentationType != PresentationType.F5_DIFF) {
         parameters.put("transactionId", String.valueOf(artifact.getTransaction()));
      }

      parameters.put("cmd", cmd);
      String urlString = getPermanentLinkBaseUrl(OseeServerContext.CLIENT_LOOPBACK_CONTEXT, parameters, orcsApi);
      URL url = null;
      try {
         url = new URL(urlString);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return url;
   }

   public static URL getOpenInOseeLink(final ArtifactReadable artifact, PresentationType presentationType, String sessionId, OrcsApi orcsApi) {
      return getOpenInOseeLink(artifact, "open.artifact", presentationType, sessionId, orcsApi);
   }

   public static String getPermanentLinkBaseUrl(String context, Map<String, String> parameters, OrcsApi orcsApi) {
      try {
         return HttpUrlBuilder.createURL(getSelectedPermanentLinkUrl(orcsApi), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static String getSelectedPermanentLinkUrl(OrcsApi orcsApi) throws OseeCoreException {
      String address = null;
      try {
         address = getPermanentBaseUrl(orcsApi);
      } catch (Exception ex) {
         OseeLog.log(ArtifactURL.class, Level.WARNING, ex);
      }
      if (!Strings.isValid(address)) {
         address = orcsApi.getSystemPreferences().getValue(OSEE_APPLICATION_SERVER);
      }

      if (Strings.isInValid(address)) {
         address = "http://localhost:8089";
      }

      return normalize(address);
   }

   public static String getPermanentBaseUrl(OrcsApi orcsApi) throws OseeCoreException {
      String address = orcsApi.getSystemPreferences().getValue(OSEE_PERMANENT_URL);
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
