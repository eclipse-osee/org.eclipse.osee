/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Christopher Rebuck
 */
public class CommandParameter extends NamedIdBase {

   public static final CommandParameter SENTINEL = new CommandParameter();

   public String type;
   private Map<String, String> attributes;
   //logic to determine Parameter type

   public CommandParameter() {
      super();
   }

   public CommandParameter(ArtifactReadable parameter) {
      super(parameter.getId(), parameter.getName());
      this.type = parameter.getArtifactType().toString();
      this.setAttributes(createAttributeMap(parameter));
   }

   public String getTypeAsString() {
      return this.type;
   }

   public Map<String, String> getAttributes() {
      return this.attributes;
   }

   public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
   }

   private Map<String, String> createAttributeMap(ArtifactReadable parameter) {
      Map<String, String> collectedAttributes = new HashMap<>();

      Collection<AttributeTypeToken> types = parameter.getExistingAttributeTypes();
      for (AttributeTypeToken type : types) {
         if (type.notEqual(Name)) {
            collectedAttributes.put(type.getName().toLowerCase(), parameter.getAttributeValuesAsString(type));
         }
      }

      return collectedAttributes;
   }
}
