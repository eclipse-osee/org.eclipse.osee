/*********************************************************************
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.doors.connector.ui.oauth.extension;

import org.eclipse.osee.doors.connector.core.oauth.IDWAOSLCProviderInfo;
import org.eclipse.osee.doors.connector.core.oauth.RequestBaseStringExtractor;
import org.eclipse.osee.doors.connector.core.oauth.RequestHeaderExtractor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.RequestTokenExtractor;
import org.scribe.extractors.TokenExtractorImpl;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.services.SignatureService;
import org.scribe.services.TimestampService;
import org.scribe.services.TimestampServiceImpl;

/**
 * Class to provide the Constants
 *
 * @author Chandan Bandemutt
 */
public class DoorsOSLCDWAProviderInfoExtn implements IDWAOSLCProviderInfo {

   /**
    *
    */
   public DoorsOSLCDWAProviderInfoExtn() {
      //
   }

   @Override
   public String getRequestTokenURL() {
      return DWAHostName() + DoorsOSLCDWAConstants.REQUEST_TOKEN_URL;
   }

   @Override
   public String getAccessTokenURL() {
      return DWAHostName() + DoorsOSLCDWAConstants.ACCESS_TOKEN_URL;
   }

   @Override
   public String getAuthorizeTokenURL() {
      return DWAHostName() + DoorsOSLCDWAConstants.AUTHORIZE_URL;
   }

   @Override
   public String getOSLCProviderAuthenticationURL() {
      return DWAHostName() + DoorsOSLCDWAConstants.AUTHENTICATION_LOGIN_URL;
   }

   @Override
   public AccessTokenExtractor getAccessTokenExtractor() {
      return new TokenExtractorImpl();
   }

   @Override
   public RequestBaseStringExtractor getBaseStringExtractor() {
      return new RequestBaseStringExtractor();
   }

   @Override
   public RequestHeaderExtractor getHeaderExtractor() {
      return new RequestHeaderExtractor();
   }

   @Override
   public RequestTokenExtractor getRequestTokenExtractor() {
      return new TokenExtractorImpl();
   }

   @Override
   public SignatureService getSignatureService() {
      return new HMACSha1SignatureService();
   }

   @Override
   public TimestampService getTimestampService() {
      return new TimestampServiceImpl();
   }

   /**
    * {@inheritDoc} to use this doors connection, you must provide a system property containing the url of the Doors Web
    * Access Server
    */
   @Override
   public String DWAHostName() {
      String prop = System.getProperty("osee.dwa.url", null);
      if (prop == null || prop.isEmpty()) {
         throw new OseeCoreException("osee.dwa.url not set");
      }
      return prop;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String ResourceUrl() {
      return DWAHostName() + DoorsOSLCDWAConstants.PROTECTED_RESOURCE_URL;
   }

}
