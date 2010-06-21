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

import java.io.BufferedInputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public final class RoughAttributeSet {
   private HashCollection<CharSequence, String> attributes = new HashCollection<CharSequence, String>();
   private final Map<String, URI> uriAttributes;

   public RoughAttributeSet() {
      attributes = new HashCollection<CharSequence, String>();
      this.uriAttributes = new HashMap<String, URI>(2, 1);
   }

   public void clear() {
      attributes.clear();
      uriAttributes.clear();
   }

   public void addMultiple(String name, String[] values) {
      for (String value : values) {
         add(name, value);
      }
   }

   public void add(String name, String value) {
      attributes.put(new CaseInsensitiveString(name), value);
   }

   public Collection<String> getAttributeValueList(String attributeTypeName) {
      return attributes.getValues(attributeTypeName);
   }

   public String getSoleAttributeValue(String typeName) {
      Collection<String> valueAsCollection = getAttributeValueList(typeName);
      if (valueAsCollection == null) {
         return null;
      }
      return valueAsCollection.iterator().next();
   }

   public Collection<String> getAttributeValueList(IAttributeType attributeType) {
      return getAttributeValueList(attributeType.getName());
   }

   public void addURIAttribute(String name, URI url) {
      uriAttributes.put(name, url);
   }

   Map<String, URI> getURIAttributes() {
      return uriAttributes;
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

   protected void translateAttributes(Artifact artifact) throws OseeCoreException {
      for (CharSequence attrTypeName : attributes.keySet()) {
         Collection<String> values = attributes.getValues(attrTypeName);
         artifact.setAttributeValues(attrTypeName.toString(), values);
      }
      transferBinaryAttributes(artifact);
   }

   private void transferBinaryAttributes(Artifact artifact) throws OseeCoreException {
      for (Entry<String, URI> entry : getURIAttributes().entrySet()) {
         try {
            artifact.setSoleAttributeFromStream(entry.getKey(), new BufferedInputStream(
                     entry.getValue().toURL().openStream()));
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
   }

   public Set<String> getAttributeTypeNames() {
      Set<String> typeNames = new HashSet<String>();
      typeNames.addAll(uriAttributes.keySet());
      for (CharSequence attrTypeName : attributes.keySet()) {
         typeNames.add(attrTypeName.toString());
      }
      return typeNames;
   }
}