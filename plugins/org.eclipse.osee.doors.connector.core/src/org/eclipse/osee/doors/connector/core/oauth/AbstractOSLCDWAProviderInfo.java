/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core.oauth;

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.RequestTokenExtractor;
import org.scribe.extractors.TokenExtractorImpl;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.services.SignatureService;
import org.scribe.services.TimestampService;
import org.scribe.services.TimestampServiceImpl;

/**
 * Abstract Class which implements IOSLCDWAProviderInfo
 * 
 * @author Swapna R
 */
public abstract class AbstractOSLCDWAProviderInfo implements IDWAOSLCProviderInfo {

   // Timestamp service

   // HeaderExtractor

   // Base String Extractor

   /*
    * (non-Javadoc)
    * @see com.bosch.icteam.doors.core.oauth.IOSLCDWAProvider#getAccessTokenExtractor()
    */
   @Override
   public AccessTokenExtractor getAccessTokenExtractor() {
      return new TokenExtractorImpl();
   }

   /*
    * (non-Javadoc)
    * @see com.bosch.icteam.doors.core.oauth.IOSLCDWAProvider#getBaseStringExtractor()
    */
   @Override
   public RequestBaseStringExtractor getBaseStringExtractor() {
      return new RequestBaseStringExtractor();
   }

   /*
    * (non-Javadoc)
    * @see com.bosch.icteam.doors.core.oauth.IOSLCDWAProvider#getHeaderExtractor()
    */
   @Override
   public RequestHeaderExtractor getHeaderExtractor() {
      return new RequestHeaderExtractor();
   }

   /*
    * (non-Javadoc)
    * @see com.bosch.icteam.doors.core.oauth.IOSLCDWAProvider#getRequestTokenExtractor()
    */
   @Override
   public RequestTokenExtractor getRequestTokenExtractor() {
      return new TokenExtractorImpl();
   }

   /*
    * (non-Javadoc)
    * @see com.bosch.icteam.doors.core.oauth.IOSLCDWAProvider#getSignatureService()
    */
   @Override
   public SignatureService getSignatureService() {
      return new HMACSha1SignatureService();
   }

   /*
    * (non-Javadoc)
    * @see com.bosch.icteam.doors.core.oauth.IOSLCDWAProvider#getTimestampService()
    */
   @Override
   public TimestampService getTimestampService() {
      return new TimestampServiceImpl();
   }

}
