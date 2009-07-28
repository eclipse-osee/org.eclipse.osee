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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactURL {

   private ArtifactURL() {
   }

   public static URL getExternalArtifactLink(final Artifact artifact) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("guid", artifact.getGuid());
      parameters.put("branchId", String.valueOf(artifact.getBranch().getBranchId()));
      String urlString =
            HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.ARTIFACT_CONTEXT, parameters);
      URL url = null;
      try {
         url = new URL(urlString);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return url;
   }

   public static URL getOpenInOseeLink(final Artifact artifact, String cmd) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());
      parameters.put("context", "osee/loopback");
      parameters.put("guid", artifact.getGuid());
      parameters.put("branchId", String.valueOf(artifact.getBranch().getBranchId()));
      parameters.put("isDeleted", String.valueOf(artifact.isDeleted()));
      if (artifact.isHistorical()) {
         parameters.put("transactionId", String.valueOf(artifact.getTransactionNumber()));
      }
      parameters.put("cmd", cmd);
      String urlString =
            HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.CLIENT_LOOPBACK_CONTEXT, parameters);
      URL url = null;
      try {
         url = new URL(urlString);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return url;
   }

   public static URL getOpenInOseeLink(final Artifact artifact) throws OseeCoreException {
      return getOpenInOseeLink(artifact, "open.artifact");
   }
}
