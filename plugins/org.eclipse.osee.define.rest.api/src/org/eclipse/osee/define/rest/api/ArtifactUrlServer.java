/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.rest.api;

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;
import static org.eclipse.osee.orcs.SystemProperties.OSEE_PERMANENT_URL;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.AbstractArtifactUrl;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class ArtifactUrlServer extends AbstractArtifactUrl {

   private final OrcsApi orcsApi;

   public ArtifactUrlServer(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public String getSessionId() {
      return null;
   }

   @Override
   public String getClientName() {
      return null;
   }

   @Override
   public String getClientPort() {
      return null;
   }

   @Override
   public Long getTransactionId(ArtifactToken artifact) {
      return ((ArtifactReadable) artifact).getTransaction().getId();
   }

   @Override
   public boolean isUseConnectedServerUrl() {
      return false;
   }

   @Override
   protected boolean isDeleted(ArtifactToken artifact) {
      return ((ArtifactReadable) artifact).isDeleted();
   }

   @Override
   protected boolean isHistorical(ArtifactToken artifact) {
      return ((ArtifactReadable) artifact).isHistorical();
   }

   @Override
   protected String getPermanentBaseUrl() {
      String address = orcsApi.getSystemProperties().getValue(OSEE_PERMANENT_URL);
      return normalize(address);
   }

   @Override
   public String getSelectedPermanentLinkUrl() {
      String address = null;
      try {
         address = getPermanentBaseUrl();
      } catch (Exception ex) {
         OseeLog.log(ArtifactUrlServer.class, Level.WARNING, ex);
      }
      if (!Strings.isValid(address)) {
         address = orcsApi.getSystemProperties().getValue(OSEE_APPLICATION_SERVER);
      }

      if (Strings.isInValid(address)) {
         address = "http://localhost:8089";
      }

      return normalize(address);
   }

}
