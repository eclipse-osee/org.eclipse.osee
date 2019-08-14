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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.AbstractArtifactUrl;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class ArtifactUrlClient extends AbstractArtifactUrl {

   @Override
   public Long getTransactionId(ArtifactToken artifact) {
      return ((Artifact) artifact).getTransaction().getId();
   }

   @Override
   public boolean isUseConnectedServerUrl() {
      HttpUrlBuilderClient httpBuilder = HttpUrlBuilderClient.getInstance();
      return httpBuilder.isUseConnectedServerUrl();
   }

   @Override
   public boolean isHistorical(ArtifactToken artifact) {
      return ((Artifact) artifact).isHistorical();
   }

   @Override
   public boolean isDeleted(ArtifactToken artifact) {
      return ((Artifact) artifact).isDeleted();
   }

   @Override
   public String getSessionId() {
      return ClientSessionManager.getSessionId();
   }

   URL getOpenInOseeLink(final Artifact artifact, PresentationType presentationType) {
      return getOpenInOseeLink(artifact, "open.artifact", presentationType);
   }

   @Override
   public String getPermanentBaseUrl() {
      String address = OseeInfo.getValue("osee.permanent.base.url");
      return normalize(address);
   }

   public String getAppServerPrefix() {
      HttpUrlBuilderClient httpBuilder = HttpUrlBuilderClient.getInstance();
      return httpBuilder.getApplicationServerPrefix();
   }

   @Override
   public String getSelectedPermanentLinkUrl() {
      String address = null;
      if (isUseConnectedServerUrl()) {
         address = getAppServerPrefix();
      } else {
         try {
            address = getPermanentBaseUrl();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
         if (!Strings.isValid(address)) {
            address = getAppServerPrefix();
         }
      }
      return normalize(address);
   }

}
