/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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

   private Map<String, String> configProperties = new HashMap<>();
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
