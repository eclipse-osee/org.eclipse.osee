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
package org.eclipse.osee.framework.jini.service.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.jini.entry.AbstractEntry;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;

/**
 * @author Ken J. Aguilar
 */
public class PropertyEntry extends AbstractEntry {

   /**
    * 
    */
   private static final long serialVersionUID = 8506398896518763116L;
   public HashMap<String, Serializable> map;

   public PropertyEntry() {
      map = new HashMap<String, Serializable>(64);
   }

   public PropertyEntry(Map<String, Serializable> properties) {
      this();
      this.map.putAll(properties);
   }

   public void setProperty(String key, Serializable value) {
      map.put(key, value);
   }

   public Serializable getProperty(String key, Serializable defaultValue) {
      Serializable value = map.get(key);
      return value == null ? defaultValue : value;
   }

   /**
    * fills the supplied {@link EnhancedProperties} object with all the properties contained in this entry
    * 
    * @param props
    */
   public void fillProps(Map<String, Serializable> props) {
      props.putAll(map);
   }

   @Override
   public boolean equals(Object other) {
      if (other instanceof PropertyEntry) {
         return ((PropertyEntry) other).map.equals(map);
      } else {
         return false;
      }
   }

}
