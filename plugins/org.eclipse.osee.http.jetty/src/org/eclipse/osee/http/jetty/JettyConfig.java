/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty;

import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__CONTEXT_PATH;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_NIO_AUTO_DETECT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_NIO_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__MULTIPLE_SLASH_TO_SINGLE;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__OTHER_INFO;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_KEYPASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_KEYSTORE;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_KEYSTORETYPE;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_NEEDS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_PASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_PROTOCOL;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_WANTS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__WORKING_DIRECTORY;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__CONTEXT_PATH;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_NIO_AUTO_DETECT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_NIO_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__MULTIPLE_SLASH_TO_SINGLE;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__OTHER_INFO;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_KEYPASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_KEYSTORE;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_KEYSTORETYPE;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_NEEDS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_PASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_PROTOCOL;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_WANTS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__WORKING_DIRECTORY;
import static org.eclipse.osee.http.jetty.internal.JettyUtil.get;
import static org.eclipse.osee.http.jetty.internal.JettyUtil.getBoolean;
import static org.eclipse.osee.http.jetty.internal.JettyUtil.getInt;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Roberto E. Escobar
 */
public class JettyConfig {

   private boolean nonBlockinIoEnabled;
   private boolean autoDetectNioSupport;

   private boolean isHttpEnabled;
   private int httpPort = -1;
   private String httpHost;
   private boolean useRandomHttpPort;

   private boolean isHttpsEnabled;
   private int httpsPort = -1;
   private String httpsHost;
   private boolean useRandomHttpsPort;

   private String sslKeystore;
   private String sslPassword;
   private String sslKeypassword;
   private String sslProtocol;
   private String sslKeystoretype;
   private boolean sslNeedClientAuth;
   private boolean sslWantClientAuth;

   private String contextPath;
   private boolean contextPathMultipleSlashToSingle;
   private int contextSessioninactiveinterval = -1;

   private String otherInfo;
   private String workingDirectory;

   private final Map<String, Object> otherProps = new LinkedHashMap<String, Object>();

   JettyConfig() {
      super();
      reset();
   }

   private void reset() {
      otherProps.clear();
      setAutoDetectNioSupport(DEFAULT_JETTY__HTTP_NIO_AUTO_DETECT);
      setNonBlockinIoEnabled(DEFAULT_JETTY__HTTP_NIO_ENABLED);

      setContextPath(DEFAULT_JETTY__CONTEXT_PATH);
      setContextSessioninactiveinterval(DEFAULT_JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL);
      setOtherInfo(DEFAULT_JETTY__OTHER_INFO);

      setHttpEnabled(DEFAULT_JETTY__HTTP_ENABLED);
      setHttpHost(DEFAULT_JETTY__HTTP_HOST);
      setHttpPort(DEFAULT_JETTY__HTTP_PORT);
      setUseRandomHttpPort(DEFAULT_JETTY__HTTP_USE_RANDOM_PORT);

      setHttpsEnabled(DEFAULT_JETTY__HTTPS_ENABLED);
      setHttpsHost(DEFAULT_JETTY__HTTPS_HOST);
      setHttpsPort(DEFAULT_JETTY__HTTPS_PORT);
      setUseRandomHttpsPort(DEFAULT_JETTY__HTTPS_USE_RANDOM_PORT);

      setSslKeypassword(DEFAULT_JETTY__SSL_KEYPASSWORD);
      setSslKeystore(DEFAULT_JETTY__SSL_KEYSTORE);
      setSslKeystoretype(DEFAULT_JETTY__SSL_KEYSTORETYPE);

      setSslPassword(DEFAULT_JETTY__SSL_PASSWORD);
      setSslProtocol(DEFAULT_JETTY__SSL_PROTOCOL);
      setSslNeedClientAuth(DEFAULT_JETTY__SSL_NEEDS_CLIENT_AUTH);
      setSslWantClientAuth(DEFAULT_JETTY__SSL_WANTS_CLIENT_AUTH);

      setWorkingDirectory(DEFAULT_JETTY__WORKING_DIRECTORY);
      setMultipleSlashToSingle(DEFAULT_JETTY__MULTIPLE_SLASH_TO_SINGLE);
   }

   void readProperties(Map<String, Object> src) {
      setAutoDetectNioSupport(getBoolean(src, JETTY__HTTP_NIO_AUTO_DETECT, DEFAULT_JETTY__HTTP_NIO_AUTO_DETECT));
      setNonBlockinIoEnabled(getBoolean(src, JETTY__HTTP_NIO_ENABLED, DEFAULT_JETTY__HTTP_NIO_ENABLED));

      setContextPath(get(src, JETTY__CONTEXT_PATH, DEFAULT_JETTY__CONTEXT_PATH));
      setContextSessioninactiveinterval(getInt(src, JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL,
         DEFAULT_JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL));
      setOtherInfo(get(src, JETTY__OTHER_INFO, DEFAULT_JETTY__OTHER_INFO));

      setHttpEnabled(getBoolean(src, JETTY__HTTP_ENABLED, DEFAULT_JETTY__HTTP_ENABLED));
      setHttpHost(get(src, JETTY__HTTP_HOST, DEFAULT_JETTY__HTTP_HOST));
      setHttpPort(getInt(src, JETTY__HTTP_PORT, DEFAULT_JETTY__HTTP_PORT));
      setUseRandomHttpPort(getBoolean(src, JETTY__HTTP_USE_RANDOM_PORT, DEFAULT_JETTY__HTTP_USE_RANDOM_PORT));

      setHttpsEnabled(getBoolean(src, JETTY__HTTPS_ENABLED, DEFAULT_JETTY__HTTPS_ENABLED));
      setHttpsHost(get(src, JETTY__HTTPS_HOST, DEFAULT_JETTY__HTTPS_HOST));
      setHttpsPort(getInt(src, JETTY__HTTPS_PORT, DEFAULT_JETTY__HTTPS_PORT));
      setUseRandomHttpsPort(getBoolean(src, JETTY__HTTPS_USE_RANDOM_PORT, DEFAULT_JETTY__HTTPS_USE_RANDOM_PORT));

      setSslKeypassword(get(src, JETTY__SSL_KEYPASSWORD, DEFAULT_JETTY__SSL_KEYPASSWORD));
      setSslKeystore(get(src, JETTY__SSL_KEYSTORE, DEFAULT_JETTY__SSL_KEYSTORE));
      setSslKeystoretype(get(src, JETTY__SSL_KEYSTORETYPE, DEFAULT_JETTY__SSL_KEYSTORETYPE));

      setSslPassword(get(src, JETTY__SSL_PASSWORD, DEFAULT_JETTY__SSL_PASSWORD));
      setSslProtocol(get(src, JETTY__SSL_PROTOCOL, DEFAULT_JETTY__SSL_PROTOCOL));
      setSslNeedClientAuth(getBoolean(src, JETTY__SSL_NEEDS_CLIENT_AUTH, DEFAULT_JETTY__SSL_NEEDS_CLIENT_AUTH));
      setSslWantClientAuth(getBoolean(src, JETTY__SSL_WANTS_CLIENT_AUTH, DEFAULT_JETTY__SSL_WANTS_CLIENT_AUTH));

      setWorkingDirectory(get(src, JETTY__WORKING_DIRECTORY, DEFAULT_JETTY__WORKING_DIRECTORY));
      setMultipleSlashToSingle(getBoolean(src, JETTY__MULTIPLE_SLASH_TO_SINGLE, DEFAULT_JETTY__MULTIPLE_SLASH_TO_SINGLE));

      for (Entry<String, Object> entry : src.entrySet()) {
         String key = entry.getKey();
         if (!key.startsWith(JettyConstants.NAMESPACE)) {
            Object value = entry.getValue();
            if (value != null) {
               addProp(key, String.valueOf(value));
            } else {
               removeProp(key);
            }
         }
      }
   }

   protected void copy(JettyConfig other) {
      this.nonBlockinIoEnabled = other.nonBlockinIoEnabled;
      this.autoDetectNioSupport = other.autoDetectNioSupport;
      this.isHttpEnabled = other.isHttpEnabled;
      this.httpPort = other.httpPort;
      this.httpHost = other.httpHost;
      this.useRandomHttpPort = other.useRandomHttpPort;
      this.isHttpsEnabled = other.isHttpsEnabled;
      this.httpsPort = other.httpsPort;
      this.httpsHost = other.httpsHost;
      this.useRandomHttpsPort = other.useRandomHttpsPort;
      this.sslKeystore = other.sslKeystore;
      this.sslPassword = other.sslPassword;
      this.sslKeypassword = other.sslKeypassword;
      this.sslProtocol = other.sslProtocol;
      this.sslKeystoretype = other.sslKeystoretype;
      this.sslNeedClientAuth = other.sslNeedClientAuth;
      this.sslWantClientAuth = other.sslWantClientAuth;
      this.contextPath = other.contextPath;
      this.contextSessioninactiveinterval = other.contextSessioninactiveinterval;
      this.otherInfo = other.otherInfo;
      this.workingDirectory = other.workingDirectory;
      this.contextPathMultipleSlashToSingle = other.contextPathMultipleSlashToSingle;
      this.otherProps.putAll(other.otherProps);
   }

   protected JettyConfig copy() {
      JettyConfig data = new JettyConfig();
      data.copy(this);
      return data;
   }

   /////////////////////////////////////////////// GETTERS

   public boolean isHttpEnabled() {
      return isHttpEnabled;
   }

   public int getHttpPort() {
      return httpPort;
   }

   public String getHttpHost() {
      return httpHost;
   }

   public boolean isHttpsEnabled() {
      return isHttpsEnabled;
   }

   public int getHttpsPort() {
      return httpsPort;
   }

   public String getHttpsHost() {
      return httpsHost;
   }

   public boolean isNonBlockinIoEnabled() {
      return nonBlockinIoEnabled;
   }

   public boolean isAutoDetectNioSupport() {
      return autoDetectNioSupport;
   }

   public String getSslKeystore() {
      return sslKeystore;
   }

   public String getSslPassword() {
      return sslPassword;
   }

   public String getSslKeypassword() {
      return sslKeypassword;
   }

   public String getSslProtocol() {
      return sslProtocol;
   }

   public String getSslKeystoretype() {
      return sslKeystoretype;
   }

   /**
    * if "true", clients must use a certificate
    */
   public boolean isSslNeedClientAuth() {
      return sslNeedClientAuth;
   }

   public boolean isSslWantClientAuth() {
      return sslWantClientAuth;
   }

   public String getContextPath() {
      return contextPath;
   }

   public int getContextSessioninactiveinterval() {
      return contextSessioninactiveinterval;
   }

   public String getOtherInfo() {
      return otherInfo;
   }

   public String getWorkingDirectory() {
      return workingDirectory;
   }

   public boolean isRandomHttpPort() {
      return useRandomHttpPort;
   }

   public boolean isRandomHttpsPort() {
      return useRandomHttpsPort;
   }

   /**
    * If “true”, replace multiple ‘/’s with a single ‘/’.
    */
   public boolean isMultipleSlashToSingle() {
      return contextPathMultipleSlashToSingle;
   }

   public Map<String, Object> getOtherProps() {
      return Collections.unmodifiableMap(otherProps);
   }

   //////////////////////////////////////// SETTERS

   void setAutoDetectNioSupport(boolean autoDetectNioSupport) {
      this.autoDetectNioSupport = autoDetectNioSupport;
   }

   void setNonBlockinIoEnabled(boolean nonBlockinIoEnabled) {
      this.nonBlockinIoEnabled = nonBlockinIoEnabled;
   }

   void setHttpEnabled(boolean isHttpEnabled) {
      this.isHttpEnabled = isHttpEnabled;
   }

   void setHttpPort(int httpPort) {
      this.httpPort = httpPort;
   }

   void setHttpHost(String httpHost) {
      this.httpHost = httpHost;
   }

   void setHttpsEnabled(boolean isHttpsEnabled) {
      this.isHttpsEnabled = isHttpsEnabled;
   }

   void setHttpsPort(int httpsPort) {
      this.httpsPort = httpsPort;
   }

   void setHttpsHost(String httpsHost) {
      this.httpsHost = httpsHost;
   }

   void setSslKeystore(String sslKeystore) {
      this.sslKeystore = sslKeystore;
   }

   void setSslPassword(String sslPassword) {
      this.sslPassword = sslPassword;
   }

   void setSslKeypassword(String sslKeypassword) {
      this.sslKeypassword = sslKeypassword;
   }

   void setSslProtocol(String sslProtocol) {
      this.sslProtocol = sslProtocol;
   }

   void setSslKeystoretype(String sslKeystoretype) {
      this.sslKeystoretype = sslKeystoretype;
   }

   void setSslNeedClientAuth(boolean sslNeedClientAuth) {
      this.sslNeedClientAuth = sslNeedClientAuth;
   }

   void setSslWantClientAuth(boolean sslWantClientAuth) {
      this.sslWantClientAuth = sslWantClientAuth;
   }

   void setContextPath(String contextPath) {
      this.contextPath = contextPath;
   }

   void setContextSessioninactiveinterval(int contextSessioninactiveinterval) {
      this.contextSessioninactiveinterval = contextSessioninactiveinterval;
   }

   void setOtherInfo(String otherInfo) {
      this.otherInfo = otherInfo;
   }

   void setWorkingDirectory(String workingDirectory) {
      this.workingDirectory = workingDirectory;
   }

   void setUseRandomHttpPort(boolean useRandomHttpPort) {
      this.useRandomHttpPort = useRandomHttpPort;
   }

   void setUseRandomHttpsPort(boolean useRandomHttpsPort) {
      this.useRandomHttpsPort = useRandomHttpsPort;
   }

   void addProp(String key, Object value) {
      if (value == null) {
         removeProp(key);
      } else {
         otherProps.put(key, value);
      }
   }

   void removeProp(String key) {
      otherProps.remove(key);
   }

   void setMultipleSlashToSingle(boolean contextPathMultipleSlashToSingle) {
      this.contextPathMultipleSlashToSingle = contextPathMultipleSlashToSingle;
   }

   @Override
   public String toString() {
      return "JettyConfig [nonBlockinIoEnabled=" + nonBlockinIoEnabled + ", autoDetectNioSupport=" + autoDetectNioSupport + ", isHttpEnabled=" + isHttpEnabled + ", httpPort=" + httpPort + ", httpHost=" + httpHost + ", useRandomHttpPort=" + useRandomHttpPort + ", isHttpsEnabled=" + isHttpsEnabled + ", httpsPort=" + httpsPort + ", httpsHost=" + httpsHost + ", useRandomHttpsPort=" + useRandomHttpsPort + ", sslKeystore=" + sslKeystore + ", sslPassword=" + sslPassword + ", sslKeypassword=" + sslKeypassword + ", sslProtocol=" + sslProtocol + ", sslKeystoretype=" + sslKeystoretype + ", sslNeedClientAuth=" + sslNeedClientAuth + ", sslWantClientAuth=" + sslWantClientAuth + ", contextPath=" + contextPath + ", contextPathMultipleSlashToSingle=" + contextPathMultipleSlashToSingle + ", contextSessioninactiveinterval=" + contextSessioninactiveinterval + ", otherInfo=" + otherInfo + ", workingDirectory=" + workingDirectory + ", otherProps=" + otherProps + "]";
   }

}
