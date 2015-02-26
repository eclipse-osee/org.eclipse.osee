/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.util;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;

/**
 * @author Roberto E. Escobar
 */
public class DataStoreInfoImpl implements DataStoreInfo {

   private Map<String, String> configProperties = new HashMap<String, String>();
   private Set<URL> configResources;

   @Override
   public Map<String, String> getProperties() {
      return configProperties;
   }

   public void setProperties(Map<String, String> configProperties) {
      this.configProperties = configProperties;
   }

   @Override
   public Set<URL> getConfigurationPaths() {
      return configResources != null ? configResources : Collections.<URL> emptySet();
   }

   public void setConfigurationResources(Set<URL> configResources) {
      this.configResources = configResources;
   }

}
