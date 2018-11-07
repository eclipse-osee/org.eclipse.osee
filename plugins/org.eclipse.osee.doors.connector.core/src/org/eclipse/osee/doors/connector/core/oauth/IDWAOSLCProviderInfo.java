/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core.oauth;

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.RequestTokenExtractor;
import org.scribe.services.SignatureService;
import org.scribe.services.TimestampService;

/**
 * Interface to get request token url, access token url, authorization url, authentication url and teh dwa host name
 * 
 * @author Swapna R
 */
public interface IDWAOSLCProviderInfo {

   String getRequestTokenURL();

   String getAccessTokenURL();

   String getAuthorizeTokenURL();

   String getOSLCProviderAuthenticationURL();

   /**
    * Returns the DWAHostName Note: This is used to replace the DWAHost name from the response URl
    */
   String DWAHostName();

   String ResourceUrl();

   AccessTokenExtractor getAccessTokenExtractor();

   RequestBaseStringExtractor getBaseStringExtractor();

   RequestHeaderExtractor getHeaderExtractor();

   RequestTokenExtractor getRequestTokenExtractor();

   SignatureService getSignatureService();

   TimestampService getTimestampService();

}
