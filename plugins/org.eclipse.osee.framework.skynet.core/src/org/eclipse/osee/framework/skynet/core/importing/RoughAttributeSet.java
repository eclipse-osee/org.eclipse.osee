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
   private final HashCollection<CaseInsensitiveString, String> attributes =
      new HashCollection<CaseInsensitiveString, String>();
   private final Map<CaseInsensitiveString, URI> uriAttributes = new HashMap<CaseInsensitiveString, URI>(2, 1);

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
      return attributes.getValues(new CaseInsensitiveString(attributeTypeName));
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
      uriAttributes.put(new CaseInsensitiveString(name), url);
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
      for (CaseInsensitiveString attrTypeName : attributes.keySet()) {
         Collection<String> values = attributes.getValues(attrTypeName);
         artifact.setAttributeValues(attrTypeName.toString(), values);
      }
      transferBinaryAttributes(artifact);
   }

   private void transferBinaryAttributes(Artifact artifact) throws OseeCoreException {
      for (Entry<CaseInsensitiveString, URI> entry : uriAttributes.entrySet()) {
         try {
            artifact.setSoleAttributeFromStream(entry.getKey().toString(), new BufferedInputStream(
               entry.getValue().toURL().openStream()));
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
   }

   public Set<String> getAttributeTypeNames() {
      Set<String> typeNames = new HashSet<String>();
      for (CharSequence attrTypeName : attributes.keySet()) {
         typeNames.add(attrTypeName.toString());
      }
      for (CharSequence attrTypeName : uriAttributes.keySet()) {
         typeNames.add(attrTypeName.toString());
      }
      return typeNames;
   }
}