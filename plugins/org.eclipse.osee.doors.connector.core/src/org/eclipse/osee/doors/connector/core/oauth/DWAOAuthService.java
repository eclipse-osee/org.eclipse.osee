/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.scribe.extractors.TokenExtractorImpl;
import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.utils.StreamUtils;

/**
 * Class to get request Token, do authentication and to get the access token
 *
 * @author Swapna R
 */
public class DWAOAuthService {

   private final String consumer_key;
   private final String consumer_secret;

   private static final String VERSION = "1.0";

   private final IDWAOSLCProviderInfo config;

   private HttpClient httpClient;

   /**
    * Constructor to set the consumer key and secret
    *
    * @param config : OSLCDWAProvider object
    */
   public DWAOAuthService(final IDWAOSLCProviderInfo config, final String consumer_key, final String consumer_secret) {
      this.consumer_key = consumer_key;
      this.consumer_secret = consumer_secret;
      this.config = config;

   }

   public void createSession() {
      if (this.httpClient == null) {
         this.httpClient = new HttpClient();
      }
   }

   public Token getRequestToken() {
      createSession();
      PostMethod requestPostMethod = new PostMethod(this.config.getRequestTokenURL());
      signRequest(OAuthConstants.EMPTY_TOKEN, requestPostMethod, false);

      int requestTokenResponse = -1;

      try {
         requestTokenResponse = this.httpClient.executeMethod(requestPostMethod);
      } catch (HttpException e1) {
         OseeLog.log(DWAOAuthService.class, Level.WARNING, "Failed to obtain valid authentication response");
      } catch (IOException e1) {
         OseeLog.log(DWAOAuthService.class, Level.WARNING, "Failed to obtain valid authentication response");
      }

      Token requestToken = null;
      try {
         OseeLog.log(DWAOAuthService.class, Level.INFO, "requestTokenResponse: " + requestTokenResponse);
         OseeLog.log(DWAOAuthService.class, Level.INFO, "Request Token" + requestPostMethod.getResponseBodyAsString());
         InputStream responseBodyAsStream = requestPostMethod.getResponseBodyAsStream();

         TokenExtractorImpl tokenextractor = new TokenExtractorImpl();
         requestToken = tokenextractor.extract(StreamUtils.getStreamContents(responseBodyAsStream));

      } catch (IOException e1) {
         OseeLog.log(DWAOAuthService.class, Level.WARNING, "Failed to obtain valid authentication response");
      }

      return requestToken;
   }

   /**
    * Method to return the authorizationURL
    *
    * @return the authorizationURL
    */
   public String getAuthorizeURL(final Token requestToken) {
      String authorizationURL = this.config.getAuthorizeTokenURL() + "?oauth_token=%s";
      return String.format(authorizationURL, requestToken.getToken());
   }

   /**
    * Method to add all the outh parameters and signature to the request
    *
    * @param token : request token to add to teh request
    * @param requestMethod : request method to which parameters are added
    * @param isTokenRequired : boolean to check whether token is added to the request or not
    */
   public void signRequest(final Token token, final HttpMethodBase requestMethod, final boolean isTokenRequired) {
      // add OAuth parameters
      // generate signature
      // create the OAuth Authorization header
      // return the method
      Map<String, String> oauth_parameters = new HashMap<>();
      if (isTokenRequired) {
         oauth_parameters.put(OAuthConstants.TOKEN, token.getToken());
      }
      oauth_parameters.put(OAuthConstants.TIMESTAMP, this.config.getTimestampService().getTimestampInSeconds());
      oauth_parameters.put(OAuthConstants.NONCE, this.config.getTimestampService().getNonce());
      oauth_parameters.put(OAuthConstants.CONSUMER_KEY, this.consumer_key);
      oauth_parameters.put(OAuthConstants.SIGN_METHOD, this.config.getSignatureService().getSignatureMethod());
      oauth_parameters.put(OAuthConstants.VERSION, getVersion());
      oauth_parameters.put(OAuthConstants.TIMESTAMP, this.config.getTimestampService().getTimestampInSeconds());

      oauth_parameters.put(OAuthConstants.SIGNATURE, getSignature(requestMethod, token, oauth_parameters));

      appendSignature(requestMethod, oauth_parameters);

   }

   public String getVersion() {
      return VERSION;
   }

   /**
    * Method to get access token from the request token
    *
    * @param requestToken : token to generate access token
    */
   public Token getAccessToken(final Token requestToken) {

      PostMethod accessTokenPostMethod = new PostMethod(this.config.getAccessTokenURL());
      signRequest(requestToken, accessTokenPostMethod, true);

      int accessTokenRequestResponse = -1;

      try {
         accessTokenRequestResponse = this.httpClient.executeMethod(accessTokenPostMethod);
         OseeLog.log(DWAOAuthService.class, Level.INFO, "Response: " + accessTokenRequestResponse);
      } catch (HttpException e1) {
         OseeLog.log(getClass(), Level.SEVERE, e1);
      } catch (IOException e1) {
         OseeLog.log(getClass(), Level.SEVERE, e1);
      }

      Token accessToken = null;
      try {
         if ((accessTokenRequestResponse != 200)) {
            return null;
         }
         OseeLog.log(DWAOAuthService.class, Level.INFO, "Response: " + accessTokenRequestResponse);
         OseeLog.log(DWAOAuthService.class, Level.INFO,
            "Access Token" + accessTokenPostMethod.getResponseBodyAsString());
         InputStream responseBodyAsStream = accessTokenPostMethod.getResponseBodyAsStream();

         TokenExtractorImpl tokenextractor = new TokenExtractorImpl();
         accessToken = tokenextractor.extract(StreamUtils.getStreamContents(responseBodyAsStream));

      } catch (IOException e1) {

         OseeLog.log(getClass(), Level.SEVERE, e1);
      }
      return accessToken;
   }

   /**
    * Method to do authentication
    *
    * @param authorizationURL to authenticate
    * @param token : to sign request
    * @param username to authenticate
    * @param password to authenticate
    */
   public void doAuthentication(final String authorizationURL, final Token token, final String username, final String password) {
      createSession();

      GetMethod authorizeGetMethod = new GetMethod(authorizationURL);

      signRequest(token, authorizeGetMethod, true);
      int authorizeResponse = -1;
      try {
         authorizeResponse = this.httpClient.executeMethod(authorizeGetMethod);
      } catch (HttpException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      OseeLog.log(DWAOAuthService.class, Level.INFO, "Response: " + authorizeResponse);
      try {
         OseeLog.log(DWAOAuthService.class, Level.INFO, "Response: " + authorizeGetMethod.getResponseBodyAsString());
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      // POST method to request token url
      // construct Authorization header for oauth params
      PostMethod authenticateMethod =
         new PostMethod(this.config.getOSLCProviderAuthenticationURL() + "?oauth_token=" + token.getToken());
      authenticateMethod.addParameter("j_username", username);
      authenticateMethod.addParameter("j_password", password);
      authenticateMethod.addParameter("loginButton", "Login");

      authenticateMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

      int authenticateResponse = -1;
      try {
         authenticateResponse = this.httpClient.executeMethod(authenticateMethod);

      } catch (HttpException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      OseeLog.log(DWAOAuthService.class, Level.INFO, "Response: " + authenticateResponse);

      Header[] responseHeaders = authenticateMethod.getResponseHeaders();
      String url = null;
      for (Header header : responseHeaders) {
         if (header.getName().equals("Location")) {
            url = header.getValue();
            break;
         }
      }

      GetMethod getAuthorisetoken = new GetMethod(url);
      try {
         int executeMethod = this.httpClient.executeMethod(getAuthorisetoken);
         OseeLog.log(DWAOAuthService.class, Level.INFO, "executeMethod: " + executeMethod);

      } catch (HttpException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

   private String getSignature(final HttpMethodBase request, final Token token, final Map<String, String> oauth_params) {
      String baseString = this.config.getBaseStringExtractor().extract(request, oauth_params);
      String signature =
         this.config.getSignatureService().getSignature(baseString, this.consumer_secret, token.getSecret());

      return signature;
   }

   private void appendSignature(final HttpMethodBase request, final Map<String, String> oauth_params) {
      String oauthHeader = this.config.getHeaderExtractor().extract(request, oauth_params);
      request.addRequestHeader(OAuthConstants.HEADER, oauthHeader);
   }

   /**
    * Method to release the connection
    *
    * @param uri : Url to release the connection
    */
   public void releaseConnection(final String uri) {
      if (this.httpClient != null) {
         DeleteMethod deleteMethod = new DeleteMethod(uri);
         try {
            this.httpClient.executeMethod(deleteMethod);
         } catch (HttpException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         } catch (IOException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
      }
   }

   /**
    * Method to get the response from the access token from path
    *
    * @param accessToken to sign the request
    * @param path to get response
    * @param queryString to get the query response
    */
   public String getResponse(final Token accessToken, final String path, final String queryString) {
      GetMethod authorizeGetMethod = new GetMethod(path);

      authorizeGetMethod.addRequestHeader("Content-Type", "application/x-oslc-rm-requirement-collection-1.0+xml");
      authorizeGetMethod.addRequestHeader("OSLC-Core-Version", "2.0");
      authorizeGetMethod.addRequestHeader("Accept", "application/rdf+xml");
      authorizeGetMethod.addRequestHeader("Accept-Charset", "UTF-8");
      if ((queryString != null) && !queryString.isEmpty()) {
         try {
            authorizeGetMethod.setQueryString(URIUtil.encodeQuery(queryString));
         } catch (URIException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
      }
      signRequest(accessToken, authorizeGetMethod, true);
      String responseBodyAsString = "";
      @SuppressWarnings("unused")
      int accessTokenRequestResponse = -1;

      try {
         accessTokenRequestResponse = this.httpClient.executeMethod(authorizeGetMethod);
         responseBodyAsString = authorizeGetMethod.getResponseBodyAsString();
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      return responseBodyAsString;
   }

   public String getResourceUrl() {
      return this.config.ResourceUrl();

   }

   public HttpClient getHttpClient() {
      return this.httpClient;
   }
}
