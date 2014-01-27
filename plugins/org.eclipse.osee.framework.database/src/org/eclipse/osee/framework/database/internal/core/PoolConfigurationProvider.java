/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.database.internal.core.PoolFactory.PoolConfiguration;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public final class PoolConfigurationProvider extends LazyObject<PoolConfiguration> {

   private URL getUserSpecifiedConfigURL() {
      URL configUri = null;
      String uri = OseeProperties.getOseeDbConnectionPoolConfigUri();
      if (Strings.isValid(uri)) {
         try {
            if (!uri.contains("://")) {
               configUri = new File(uri).toURI().toURL();
            } else {
               configUri = new URI(uri).toURL();
            }
         } catch (Exception ex) {
            OseeLog.logf(PoolConfigurationProvider.class, Level.SEVERE, ex,
               "Error get user specified connection pool config uri [%s]", uri);
         }
      }
      return configUri;
   }

   private Properties readConfig(URL url) {
      Properties props = new Properties();

      if (url != null) {
         InputStream inputStream = null;
         try {
            inputStream = new BufferedInputStream(url.openStream());
            props.loadFromXML(inputStream);
         } catch (Exception ex) {
            OseeLog.logf(PoolConfigurationProvider.class, Level.SEVERE, ex,
               "Error reading user specified connection pool config uri [%s]", url.toString());
         } finally {
            Lib.close(inputStream);
         }
      }
      return props;
   }

   @Override
   protected FutureTask<PoolConfiguration> createLoaderTask() {
      Callable<PoolConfiguration> callable = new Callable<PoolConfiguration>() {
         @Override
         public PoolConfiguration call() throws Exception {
            URL userURL = getUserSpecifiedConfigURL();
            Properties props = readConfig(userURL);
            return new PoolConfiguration(props);
         }
      };
      return new FutureTask<PoolConfiguration>(callable);
   }

}