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
package org.eclipse.osee.framework.skynet.core.importing;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

public class RoughAttributeSet {
   private HashCollection<String, String> attributes = new HashCollection<String, String>();

   public RoughAttributeSet() {
      attributes = new HashCollection<String, String>();
   }

   public void clear() {
      attributes = new HashCollection<String, String>();
   }

   public void addMultiple(String name, String[] values) {
      for (String value : values) {
         add(name, value);
      }
   }

   public void add(String name, String value) {
      attributes.put(name.toUpperCase(), value);
   }

   public String getName() {
      return getSoleAttributeValue("Name");
   }

   public Collection<String> getAttributeValueList(String typeName) {
      return attributes.getValues(typeName.toUpperCase());
   }

   public String getSoleAttributeValue(String typeName) {
      Collection<String> valueAsCollection = getAttributeValueList(typeName);
      if (valueAsCollection == null) {
         return null;
      }
      return valueAsCollection.iterator().next();
   }

   public Collection<String> getKeys() {
      return attributes.keySet();
   }

   public HashCollection<String, String> getAllEntries() {
      return attributes;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RoughAttributeSet)) {
         return false;
      }
      RoughAttributeSet other = (RoughAttributeSet) obj;
      return this.attributes.equals(other.attributes);
   }

   @Override
   public int hashCode() {
      return attributes.hashCode();
   }

   @Override
   public String toString() {
      return attributes.toString();
   }
}
