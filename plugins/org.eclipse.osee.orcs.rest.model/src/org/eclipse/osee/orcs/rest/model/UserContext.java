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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Christopher Rebuck
 */

public class UserContext extends NamedIdBase {

   private Map<String, String> attributes;
   private List<CommandsRelatedToContext> commands;

   public UserContext() {
      super();
   }

   public UserContext(ArtifactReadable context) {
      super(context.getId(), context.getName());
      this.setAttributes(createAttributeMap(context));
      this.setCommands(
         context.getRelated(CoreRelationTypes.ContextToCommand_Artifact, ArtifactTypeToken.SENTINEL).stream().filter(
            cmd -> cmd.isValid()).map(command -> new CommandsRelatedToContext(command, context)).collect(
               Collectors.toList()));
   }

   public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
   }

   public Map<String, String> getAttributes() {
      return this.attributes;
   }

   public void setCommands(List<CommandsRelatedToContext> commands) {
      this.commands = commands;
   }

   public List<CommandsRelatedToContext> getCommands() {
      return this.commands;
   }

   private Map<String, String> createAttributeMap(ArtifactReadable context) {
      Map<String, String> collectedAttributes = new HashMap<>();

      if (!context.getExistingAttributeTypes().isEmpty()) {
         Collection<AttributeTypeToken> types = context.getExistingAttributeTypes();
         for (AttributeTypeToken type : types) {
            if (type.notEqual(Name)) {
               collectedAttributes.put(type.getName().toLowerCase(), context.getAttributeValuesAsString(type));
            }
         }
      }
      return collectedAttributes;

   }

}
