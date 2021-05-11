/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Generic class object to make common operations (i.e. generic POST/PUT) operations easier
 * 
 * @author Luciano T. Vaglienti
 */
public class PLGenericDBObject extends NamedIdBase {
   public static final PLGenericDBObject SENTINEL = new PLGenericDBObject();

   @JsonIgnore
   protected final ConcurrentHashMap<AttributeTypeToken, Object> attributeLookup = new ConcurrentHashMap<>();

   public PLGenericDBObject(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PLGenericDBObject(ArtifactReadable art) {
      this(art.getId(), art.getName());
   }

   public PLGenericDBObject(Long id, String name) {
      this();
      this.setId(id);
      this.setName(name);
   }

   public PLGenericDBObject() {
      super(ArtifactId.SENTINEL.getId(), "");
      // Not doing anything
   }

   @Override
   public void setName(String name) {
      super.setName(name);
      this.attributeLookup.put(CoreAttributeTypes.Name, name);
   }

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }

   @Override
   @JsonIgnore
   public int getIdIntValue() {
      return super.getIdIntValue();
   }

   @JsonIgnore
   /**
    * Validates the type for missing elements in a rest call. NOTE: Doesn't work great right now, if element is missing,
    * it doesn't get added to the map because jackson doesn't end up calling the setter
    *
    * @return errors that occurred, empty string = no errors.
    */
   public String getErrors() {
      String result = "";
      for (AttributeTypeToken attrToken : this.attributeLookup.keySet()) {
         if (get(attrToken) == null || get(attrToken).equals("")) {
            result = "Platform Type must have a valid " + attrToken.getName();
         }
      }
      return result;
   }

   @JsonIgnore
   public static Collection<AttributeTypeId> getAttributeTypes() {
      List<AttributeTypeId> attrType = new LinkedList<AttributeTypeId>();
      attrType.add(CoreAttributeTypes.Name);
      return attrType;
   }

   @JsonIgnore
   private Object get(AttributeTypeToken token) {
      return this.attributeLookup.get(token);
   }

   @JsonIgnore
   public void updateValues(ArtifactToken token, TransactionBuilder tx) {
      for (AttributeTypeToken attrToken : this.attributeLookup.keySet()) {
         if (get(attrToken) != null && !get(attrToken).equals("")) {
            tx.setSoleAttributeValue(token, attrToken, get(attrToken));
         }
      }
   }

}