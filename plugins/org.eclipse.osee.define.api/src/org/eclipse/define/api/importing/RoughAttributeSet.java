/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.define.api.importing;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.define.api.importing.RoughAttributeSet.RoughAttribute;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;

/**
 * @author Ryan Brooks
 * @author David W. Miller
 */
public final class RoughAttributeSet implements Iterable<Entry<CaseInsensitiveString, Collection<RoughAttribute>>> {

   private final Map<CaseInsensitiveString, Collection<RoughAttribute>> attributes =
      new LinkedHashMap<CaseInsensitiveString, Collection<RoughAttribute>>();

   public void clear() {
      attributes.clear();
   }

   public void addAttribute(String name, String... values) {
      for (String value : values) {
         put(name, new RoughAttribute(value));
      }
   }

   public void addAttribute(String name, URI... uris) {
      for (URI uri : uris) {
         put(name, new RoughAttribute(uri));
      }
   }

   public void setAttribute(String type, String value) {
      attributes.put(new CaseInsensitiveString(type), Collections.singleton(new RoughAttribute(value)));
   }

   private void put(String name, RoughAttribute attr) {
      CaseInsensitiveString key = new CaseInsensitiveString(name);
      Collection<RoughAttribute> collection = attributes.get(key);
      if (collection == null) {
         collection = new LinkedList<>();
         attributes.put(key, collection);
      }
      collection.add(attr);
   }

   public Set<String> getAttributeTypeNames() {
      Set<String> typeNames = new LinkedHashSet<>();
      for (CharSequence attrTypeName : attributes.keySet()) {
         typeNames.add(attrTypeName.toString());
      }
      return typeNames;
   }

   public String getSoleAttributeValue(String typeName) {
      Collection<String> values = getAttributeValueList(typeName);
      return values != null && !values.isEmpty() ? values.iterator().next() : null;
   }

   public Collection<String> getAttributeValueList(AttributeTypeToken attributeType) {
      return getAttributeValueList(attributeType.getName());
   }

   /**
    * @return Same as getAttributeValueList, returns defaultList if getAttributeValueList is null.
    */
   public Collection<String> getAttributeValueList(AttributeTypeToken attributeType, Collection<String> defaultList) {
      Collection<String> list = getAttributeValueList(attributeType);
      return list != null ? list : defaultList;
   }

   public Collection<String> getAttributeValueList(String attributeTypeName) {
      Collection<RoughAttribute> roughAttributes = attributes.get(new CaseInsensitiveString(attributeTypeName));
      if (roughAttributes == null) {
         return null;
      }
      Collection<String> values = new ArrayList<>();
      for (RoughAttribute attribute : roughAttributes) {
         if (!attribute.hasURI()) {
            values.add(attribute.getValue());
         }
      }
      return values;
   }

   public Collection<URI> getURIAttributes() {
      Collection<URI> values = new ArrayList<>();
      for (Collection<RoughAttribute> attributeSets : attributes.values()) {
         for (RoughAttribute attribute : attributeSets) {
            if (attribute.hasURI()) {
               values.add(attribute.getURI());
            }
         }
      }
      return values;
   }

   @Override
   public Iterator<Entry<CaseInsensitiveString, Collection<RoughAttribute>>> iterator() {
      return attributes.entrySet().iterator();
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

   public final static class RoughAttribute {
      private final String value;
      private final URI uri;

      public RoughAttribute(String value) {
         this.value = value;
         this.uri = null;
      }

      public RoughAttribute(URI uri) {
         this.value = null;
         this.uri = uri;
      }

      public boolean hasURI() {
         return uri != null;
      }

      public InputStream getContent() throws Exception {
         InputStream inputStream;
         if (hasURI()) {
            inputStream = new BufferedInputStream(getURI().toURL().openStream());
         } else {
            inputStream = new ByteArrayInputStream(getValue().getBytes("UTF-8"));
         }
         return inputStream;
      }

      public String getValue() {
         return value;
      }

      public URI getURI() {
         return uri;
      }

      @Override
      public String toString() {
         String toReturn;
         if (hasURI()) {
            toReturn = getURI().toASCIIString();
         } else {
            toReturn = getValue();
         }
         return toReturn;
      }
   }
}