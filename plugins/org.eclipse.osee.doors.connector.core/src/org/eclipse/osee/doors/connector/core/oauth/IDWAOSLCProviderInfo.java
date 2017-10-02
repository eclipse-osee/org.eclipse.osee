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

   /**
    * Returns the Request token URL
    * 
    * @return Request token URL
    */
   public abstract String getRequestTokenURL();

   /**
    * Returns the Access token URL
    * 
    * @return Access token URL
    */
   public abstract String getAccessTokenURL();

   /**
    * Returns the AuthorizeToken token URL
    * 
    * @return AuthorizeToken token URL
    */
   public abstract String getAuthorizeTokenURL();

   /**
    * Returns the Authentication URL
    * 
    * @return Authentication URL
    */
   public abstract String getOSLCProviderAuthenticationURL();

   /**
    * Returns the DWAHostName Note: This is used to replace the DWAHost name from the response URl
    * 
    * @return DWAHostName
    */
   public abstract String DWAHostName();

   /**
    * Returns the resource Url
    * 
    * @return DWAHostName
    */
   public abstract String ResourceUrl();

   /**
    * Returns the access token extractor.
    * 
    * @return access token extractor
    */
   public abstract AccessTokenExtractor getAccessTokenExtractor();

   /**
    * Returns the base string extractor.
    * 
    * @return base string extractor
    */
   public abstract RequestBaseStringExtractor getBaseStringExtractor();

   /**
    * Returns the header extractor.
    * 
    * @return header extractor
    */
   public abstract RequestHeaderExtractor getHeaderExtractor();

   /**
    * Returns the request token extractor.
    * 
    * @return request token extractor
    */
   public abstract RequestTokenExtractor getRequestTokenExtractor();

   /**
    * Returns the signature service.
    * 
    * @return signature service
    */
   public abstract SignatureService getSignatureService();

   /**
    * Returns the timestamp service.
    * 
    * @return timestamp service
    */
   public abstract TimestampService getTimestampService();

}