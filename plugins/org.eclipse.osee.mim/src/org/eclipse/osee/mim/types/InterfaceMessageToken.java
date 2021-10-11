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
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageToken extends PLGenericDBObject {
   public static final InterfaceMessageToken SENTINEL = new InterfaceMessageToken();

   private String Name; //required

   private String InterfaceMessageNumber; //required

   private String InterfaceMessagePeriodicity; //required

   private String InterfaceMessageRate;

   private Boolean InterfaceMessageWriteAccess; //required

   private String InterfaceMessageType; //required

   private String Description;
   private List<InterfaceSubMessageToken> subMessages = new LinkedList<InterfaceSubMessageToken>();
   private ApplicabilityToken applicability;
   private InterfaceNode initiatingNode;
   private ArtifactReadable artifactReadable;

   public InterfaceMessageToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceMessageToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setInterfaceMessageNumber(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageNumber, ""));
      this.setInterfaceMessagePeriodicity(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessagePeriodicity, ""));
      this.setInterfaceMessageRate(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, ""));
      this.setInterfaceMessageType(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType, ""));
      this.setInterfaceMessageWriteAccess(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageWriteAccess, false));
      this.setSubMessages(
         art.getRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceSubMessageToken(a)).collect(
               Collectors.toList()));
      this.artifactReadable = art;
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public InterfaceMessageToken(Long id, String name) {
      super(id, name);
   }

   public InterfaceMessageToken() {
      super();
   }

   /**
    * @return the interfaceMessageNumber
    */
   public String getInterfaceMessageNumber() {
      return InterfaceMessageNumber;
   }

   /**
    * @param interfaceMessageNumber the interfaceMessageNumber to set
    */
   public void setInterfaceMessageNumber(String interfaceMessageNumber) {
      InterfaceMessageNumber = interfaceMessageNumber;
   }

   /**
    * @return the interfaceMessagePeriodicity
    */
   public String getInterfaceMessagePeriodicity() {
      return InterfaceMessagePeriodicity;
   }

   /**
    * @param interfaceMessagePeriodicity the interfaceMessagePeriodicity to set
    */
   public void setInterfaceMessagePeriodicity(String interfaceMessagePeriodicity) {
      InterfaceMessagePeriodicity = interfaceMessagePeriodicity;
   }

   /**
    * @return the interfaceMessageRate
    */
   public String getInterfaceMessageRate() {
      return InterfaceMessageRate;
   }

   /**
    * @param interfaceMessageRate the interfaceMessageRate to set
    */
   public void setInterfaceMessageRate(String interfaceMessageRate) {
      InterfaceMessageRate = interfaceMessageRate;
   }

   /**
    * @return the interfaceMessageWriteAccess
    */
   public Boolean getInterfaceMessageWriteAccess() {
      return InterfaceMessageWriteAccess;
   }

   /**
    * @param interfaceMessageWriteAccess the interfaceMessageWriteAccess to set
    */
   public void setInterfaceMessageWriteAccess(Boolean interfaceMessageWriteAccess) {
      InterfaceMessageWriteAccess = interfaceMessageWriteAccess;
   }

   /**
    * @return the interfaceMessageType
    */
   public String getInterfaceMessageType() {
      return InterfaceMessageType;
   }

   /**
    * @param interfaceMessageType the interfaceMessageType to set
    */
   public void setInterfaceMessageType(String interfaceMessageType) {
      InterfaceMessageType = interfaceMessageType;
   }

   /**
    * @return the subMessages
    */
   public Collection<InterfaceSubMessageToken> getSubMessages() {
      return this.subMessages;
   }

   public void setSubMessages(List<InterfaceSubMessageToken> subMessages) {
      this.subMessages = subMessages;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.Description = description;
   }

   /**
    * @return the applicability
    */
   public ApplicabilityToken getApplicability() {
      return applicability;
   }

   /**
    * @param applicability the applicability to set
    */
   public void setApplicability(ApplicabilityToken applicability) {
      this.applicability = applicability;
   }

   /**
    * @return the initiating node name and id for this message
    */
   public InterfaceNode getInitiatingNode() {
      return initiatingNode;
   }

   /**
    * @param Node that sends this message
    */
   public void setInitiatingNode(InterfaceNode initiatingNode) {
      this.initiatingNode = initiatingNode;
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
   }

}
