/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class DatastoreInfo {

   private Map<String, String> properties;

   public Map<String, String> getProperties() {
      return properties != null ? properties : Collections.<String, String> emptyMap();
   }

   public void setProperties(Map<String, String> data) {
      this.properties = data;
   }

}
