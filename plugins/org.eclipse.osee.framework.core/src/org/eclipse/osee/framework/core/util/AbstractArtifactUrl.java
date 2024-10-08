/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactUrl {

   public URL getOpenInOseeLink(final ArtifactToken artifact, String cmd, PresentationType presentationType) {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("id", artifact.getIdString());
      parameters.put("guid", artifact.getGuid());
      parameters.put("branchUuid", artifact.getBranch().getIdString());
      parameters.put("isDeleted", String.valueOf(isDeleted(artifact)));

      if (isHistorical(
         artifact) && presentationType != PresentationType.DIFF && presentationType != PresentationType.F5_DIFF) {
         parameters.put("transactionId", String.valueOf(getTransactionId(artifact)));
      }
      String urlString = "";
      parameters.put("cmd", cmd);
      if (getClientName() != null && getClientPort() != null) {
         String baseUrl = String.format("http://%s:%s/", getClientName(), getClientPort());
         try {
            urlString = HttpUrlBuilder.createURL(baseUrl, OseeServerContext.CLIENT_LOOPBACK_CONTEXT, parameters);
         } catch (UnsupportedEncodingException ex) {
            throw OseeCoreException.wrap(ex);
         }
      } else {
         urlString = getPermanentLinkBaseUrl(OseeServerContext.CLIENT_LOOPBACK_CONTEXT, parameters);
      }
      URL url = null;
      try {

         url = new URL(urlString);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return url;
   }

   public abstract String getSessionId();

   public abstract String getClientName();

   public abstract String getClientPort();

   public abstract Long getTransactionId(ArtifactToken artifact);

   public abstract boolean isUseConnectedServerUrl();

   public URL getOpenInOseeLink(final ArtifactToken artifact, PresentationType presentationType) {
      return getOpenInOseeLink(artifact, "open.artifact", presentationType);
   }

   public String getPermanentLinkBaseUrl(String context, Map<String, String> parameters) {
      try {
         return HttpUrlBuilder.createURL(getSelectedPermanentLinkUrl(), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public abstract String getSelectedPermanentLinkUrl();

   protected String normalize(String address) {
      String toReturn = address;
      if (Strings.isValid(toReturn) && !toReturn.endsWith("/")) {
         toReturn += "/";
      }
      return toReturn;
   }

   protected abstract boolean isDeleted(ArtifactToken artifact);

   protected abstract boolean isHistorical(ArtifactToken artifact);

   protected abstract String getPermanentBaseUrl();

}
