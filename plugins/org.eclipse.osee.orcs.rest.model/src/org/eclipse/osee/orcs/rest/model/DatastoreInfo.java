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
package org.eclipse.osee.orcs.rest.model;

import java.util.Collections;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

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
