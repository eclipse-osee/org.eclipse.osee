/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class AttentionLocationToken extends ArtifactAccessorResult {

   public static final AttentionLocationToken SENTINEL = new AttentionLocationToken();

   private String locationId;
   private String locationTime;
   private String attentionMessage;
   private List<StackTraceToken> stackTraces;

   public AttentionLocationToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public AttentionLocationToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setLocationId(art.getSoleAttributeAsString(CoreAttributeTypes.LocationId, ""));
      this.setLocationTime(art.getSoleAttributeAsString(CoreAttributeTypes.LocationTime, ""));
      this.setAttentionMessage(art.getSoleAttributeAsString(CoreAttributeTypes.AttentionMessage, ""));
      this.setStackTraces(
         art.getRelated(CoreRelationTypes.AttentionLocationToStackTrace_StackTrace).getList().stream().filter(
            a -> a.getExistingAttributeTypes().isEmpty()).map(a -> new StackTraceToken(a)).collect(
               Collectors.toList()));
   }

   public AttentionLocationToken(Long id, String name) {
      super(id, name);
      this.setLocationId("");
      this.setLocationTime("");
      this.setAttentionMessage("");
      this.setStackTraces(new LinkedList<>());
   }

   public AttentionLocationToken() {
      super();
   }

   public String getLocationId() {
      return locationId;
   }

   public void setLocationId(String locationId) {
      this.locationId = locationId;
   }

   public String getLocationTime() {
      return locationTime;
   }

   public void setLocationTime(String locationTime) {
      this.locationTime = locationTime;
   }

   public String getAttentionMessage() {
      return attentionMessage;
   }

   public void setAttentionMessage(String attentionMessage) {
      this.attentionMessage = attentionMessage;
   }

   public List<StackTraceToken> getStackTraces() {
      return stackTraces;
   }

   public void setStackTraces(List<StackTraceToken> stackTraces) {
      this.stackTraces = stackTraces;
   }

   public CreateArtifact createArtifact(String key) {
      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.AttentionLocation.getIdString());

      List<Attribute> attrs = new LinkedList<>();
      if (Strings.isValid(getLocationId())) {
         Attribute attr = new Attribute(CoreAttributeTypes.LocationId.getIdString());
         attr.setValue(Arrays.asList(getLocationId()));
         attrs.add(attr);
      }
      if (Strings.isValid(getLocationTime())) {
         Attribute attr = new Attribute(CoreAttributeTypes.LocationTime.getIdString());
         attr.setValue(Arrays.asList(getLocationTime()));
         attrs.add(attr);
      }
      if (Strings.isValid(getAttentionMessage())) {
         Attribute attr = new Attribute(CoreAttributeTypes.AttentionMessage.getIdString());
         attr.setValue(Arrays.asList(getAttentionMessage()));
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

}
