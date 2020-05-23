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
