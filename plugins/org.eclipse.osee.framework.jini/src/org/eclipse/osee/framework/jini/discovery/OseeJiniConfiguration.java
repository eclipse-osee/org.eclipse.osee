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
package org.eclipse.osee.framework.jini.discovery;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeJiniConfiguration implements Configuration {
   private Configuration config;

   public OseeJiniConfiguration() throws ConfigurationException {
      config = ConfigurationProvider.getInstance(null);

   }

   public OseeJiniConfiguration(ClassLoader loader) throws ConfigurationException {
      config = ConfigurationProvider.getInstance(null, loader);

   }

   @SuppressWarnings("unchecked")
   public Object getEntry(String arg0, String arg1, Class arg2) throws ConfigurationException {
      return config.getEntry(arg0, arg1, arg2);
   }

   @SuppressWarnings("unchecked")
   public Object getEntry(String arg0, String arg1, Class arg2, Object arg3) throws ConfigurationException {
      Object o = config.getEntry(arg0, arg1, arg2, arg3);
      return o;
   }

   @SuppressWarnings("unchecked")
   public Object getEntry(String arg0, String arg1, Class arg2, Object arg3, Object arg4) throws ConfigurationException {
      return config.getEntry(arg0, arg1, arg2, arg3, arg4);
   }

}
