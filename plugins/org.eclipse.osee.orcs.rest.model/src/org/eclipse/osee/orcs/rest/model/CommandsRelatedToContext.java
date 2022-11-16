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
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Christopher Rebuck
 */
public class CommandsRelatedToContext extends NamedIdBase {

   private final String contextGroup;
   private Map<String, String> attributes;
   private CommandParameter parameter;

   public CommandsRelatedToContext(ArtifactReadable command, ArtifactReadable context) {
      super(command.getId(), command.getName());
      this.contextGroup = context.getName();
      this.setAttributes(createAttributeMap(command));
      this.setParameter(createParameter(command));
   }

   public String getContextGroup() {
      return contextGroup;
   }

   public Map<String, String> getAttributes() {
      return attributes;
   }

   public CommandParameter getParameter() {
      return parameter;
   }

   public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
   }

   public void setParameter(CommandParameter parameter) {
      this.parameter = parameter;
   }

   private Map<String, String> createAttributeMap(ArtifactReadable command) {
      Map<String, String> collectedAttributes = new HashMap<>();

      if (!command.getExistingAttributeTypes().isEmpty()) {
         Collection<AttributeTypeToken> types = command.getExistingAttributeTypes();
         for (AttributeTypeToken type : types) {
            if (type.notEqual(Name)) {
               collectedAttributes.put(type.getName().toLowerCase(), command.getAttributeValuesAsString(type));
            }
         }
      }
      return collectedAttributes;
   }

   private CommandParameter createParameter(ArtifactReadable command) {
      if (!command.getRelated(DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL).isEmpty()) {
         List<CommandParameter> paramList =
            command.getRelated(DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL).stream().map(
               param -> new CommandParameter(param)).collect(Collectors.toList());

         CommandParameter parameter = paramList.get(0);

         return parameter;
      } else {
         return CommandParameter.SENTINEL;
      }
   }
}
