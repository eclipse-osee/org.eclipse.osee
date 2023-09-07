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
package org.eclipse.osee.mim.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureApiImpl implements InterfaceStructureApi {

   private ArtifactAccessor<InterfaceStructureToken> accessor;
   private final InterfaceElementApi interfaceElementApi;
   private final InterfaceMessageApi interfaceMessageApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;
   private final List<AttributeTypeId> structureAttributeList;
   private final List<AttributeTypeId> elementAttributeList;
   private final List<RelationTypeSide> affectedRelations;

   InterfaceStructureApiImpl(OrcsApi orcsApi, InterfaceConnectionViewApi connectionApi, InterfaceElementApi interfaceElementApi, InterfaceMessageApi interfaceMessageApi) {
      this.setAccessor(new InterfaceStructureAccessor(orcsApi));
      this.interfaceConnectionApi = connectionApi;
      this.interfaceElementApi = interfaceElementApi;
      this.interfaceMessageApi = interfaceMessageApi;
      this.structureAttributeList = this.createStructureAttributeList();
      this.elementAttributeList = this.createElementAttributeList();
      this.affectedRelations = this.createAffectedRelationTypeSideList();
   }

   private ArtifactAccessor<InterfaceStructureToken> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceStructureToken> accessor) {
      this.accessor = accessor;
   }

   private List<RelationTypeSide> createAffectedRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceSubMessageContent_Structure);
      return relations;
   }

   private List<AttributeTypeId> createStructureAttributeList() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceStructureCategory);
      attributes.add(CoreAttributeTypes.InterfaceMinSimultaneity);
      attributes.add(CoreAttributeTypes.InterfaceMaxSimultaneity);
      attributes.add(CoreAttributeTypes.InterfaceTaskFileType);
      return attributes;
   }

   private List<AttributeTypeId> createElementAttributeList() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceDefaultValue);
      attributes.add(CoreAttributeTypes.InterfaceElementAlterable);
      attributes.add(CoreAttributeTypes.Notes);
      attributes.add(CoreAttributeTypes.InterfaceElementEnumLiteral);
      attributes.add(CoreAttributeTypes.InterfaceElementAlterable);
      attributes.add(CoreAttributeTypes.InterfaceElementIndexStart);
      attributes.add(CoreAttributeTypes.InterfaceElementIndexEnd);
      return attributes;
   }

   private InterfaceStructureElementToken defaultSetUpElement(InterfaceStructureElementToken element,
      InterfaceStructureElementToken previousElement, boolean shouldValidate, int validationSize) {
      if (previousElement.isInvalid() && !previousElement.isAutogenerated()) {
         element.setBeginByte((double) 0);
         element.setBeginWord((double) 0);
      } else {
         element.setBeginByte((previousElement.getEndByte() + 1) % (validationSize / 2));
         element.setBeginWord(Math.floor(
            ((previousElement.getEndWord() * (validationSize / 2)) + previousElement.getEndByte() + 1) / (validationSize / 2)));
      }
      element.setShouldValidate(shouldValidate);
      element.setValidationSize(validationSize);
      return element;
   }

   private InterfaceStructureToken parseStructure(BranchId branch, ArtifactId connectionId,
      InterfaceStructureToken structure) {
      return this.parseStructure(branch, connectionId, structure, ArtifactId.SENTINEL,
         new LinkedList<InterfaceStructureElementToken>());
   }

   private InterfaceStructureToken parseStructure(BranchId branch, ArtifactId connectionId,
      InterfaceStructureToken structure, ArtifactId viewId) {
      return this.parseStructure(branch, connectionId, structure, viewId,
         new LinkedList<InterfaceStructureElementToken>());
   }

   private InterfaceStructureToken parseStructure(BranchId branch, ArtifactId connectionId,
      InterfaceStructureToken structure, String elementFilter) {
      return this.parseStructure(branch, connectionId, structure, ArtifactId.SENTINEL,
         this.interfaceElementApi.getAllRelatedAndFilter(branch, ArtifactId.valueOf(structure.getId()), elementFilter));
   }

   private InterfaceStructureToken parseStructure(BranchId branch, ArtifactId connectionId,
      InterfaceStructureToken structure, List<InterfaceStructureElementToken> defaultElements) {
      return this.parseStructure(branch, connectionId, structure, ArtifactId.SENTINEL, defaultElements);
   }

   private InterfaceStructureToken parseStructure(BranchId branch, ArtifactId connectionId,
      InterfaceStructureToken structure, ArtifactId viewId, List<InterfaceStructureElementToken> defaultElements) {
      InterfaceConnection connection = this.interfaceConnectionApi.get(branch, connectionId);
      boolean shouldValidate = connection.getTransportType().isByteAlignValidation();
      int validationSize = connection.getTransportType().getByteAlignValidationSize();
      Collection<InterfaceStructureElementToken> elements = new LinkedList<>();
      elements.addAll(defaultElements.size() > 0 ? defaultElements : interfaceElementApi.getAllRelated(branch,
         structure.getArtifactId(), viewId));
      structure.setElements(
         parseElements(elements, InterfaceStructureElementToken.SENTINEL, validationSize, shouldValidate, false));
      return structure;
   }

   private List<InterfaceStructureElementToken> parseElements(Collection<InterfaceStructureElementToken> elements,
      InterfaceStructureElementToken parentPrevious, int validationSize, boolean shouldValidate, boolean isArray) {
      List<InterfaceStructureElementToken> tempElements = new LinkedList<>();
      if (elements.size() >= 2) {
         Iterator<InterfaceStructureElementToken> elementIterator = elements.iterator();
         InterfaceStructureElementToken previousElement = elementIterator.next();
         InterfaceStructureElementToken currentElement = elementIterator.next();

         if (previousElement.getArrayElements().isEmpty()) {
            previousElement = this.defaultSetUpElement(previousElement, parentPrevious, shouldValidate, validationSize);
         } else {
            List<InterfaceStructureElementToken> arrayElements = parseElements(previousElement.getArrayElements(),
               InterfaceStructureElementToken.SENTINEL, validationSize, shouldValidate, true);
            previousElement.setArrayElements(arrayElements);
            previousElement.setBeginByte(arrayElements.get(0).getBeginByte());
            previousElement.setBeginWord(arrayElements.get(0).getBeginWord());
         }
         tempElements.add(previousElement);

         while (true) {
            if (currentElement.getArrayElements().isEmpty()) {
               currentElement =
                  this.defaultSetUpElement(currentElement, previousElement, shouldValidate, validationSize);
               if (!isArray && currentElement.getInterfacePlatformTypeByteSize() >= (validationSize / 2) && shouldValidate) {
                  if (previousElement.getEndByte() != ((validationSize / 2) - 1) && !isArray) {
                     /**
                      * Make sure elements of size word or greater start on 0
                      */
                     previousElement = new InterfaceStructureElementToken("Insert Spare",
                        "byte align spare for aligning to word start",
                        (previousElement.getEndByte() + 1) % (validationSize / 2),
                        Math.floor(
                           ((previousElement.getEndWord() * (validationSize / 2)) + previousElement.getEndByte() + 1) / (validationSize / 2)),
                        (int) Math.floor(((validationSize / 2) - 1) - (previousElement.getEndByte())), true);
                     previousElement.setShouldValidate(shouldValidate);
                     previousElement.setValidationSize(validationSize);
                     tempElements.add(previousElement);
                  }
                  if (currentElement.getInterfacePlatformTypeWordSize() > 1 && (previousElement.getEndWord() + 1) % currentElement.getInterfacePlatformTypeWordSize() != 0) {
                     /**
                      * Make sure elements of size larger than 2 words start on m*n indexed words
                      */
                     previousElement = new InterfaceStructureElementToken("Insert Spare",
                        "byte align spare for byte alignment",
                        (previousElement.getEndByte() + 1) % (validationSize / 2),
                        Math.floor(
                           ((previousElement.getEndWord() * (validationSize / 2)) + previousElement.getEndByte() + 1) / (validationSize / 2)),
                        (int) (Math.floor(
                           (currentElement.getInterfacePlatformTypeWordSize() - ((previousElement.getEndWord() + 1) % currentElement.getInterfacePlatformTypeWordSize()))) * (validationSize / 2)) - 1);
                     previousElement.setShouldValidate(shouldValidate);
                     previousElement.setValidationSize(validationSize);
                     tempElements.add(previousElement);
                     //make a spare to fill remaining area until beginWord % WordSize=1
                  }
                  //re-set up current Element based on spare
                  currentElement =
                     this.defaultSetUpElement(currentElement, previousElement, shouldValidate, validationSize);
               }
               tempElements.add(currentElement);
               previousElement = currentElement;

               if (!elementIterator.hasNext()) {
                  if (!isArray && currentElement.getEndByte() != ((validationSize / 2) - 1)) {
                     /**
                      * Rule for making sure last element ends on last byte of word(no partials)
                      */
                     InterfaceStructureElementToken tempElement = new InterfaceStructureElementToken("Insert Spare",
                        "byte align spare for aligning to word start",
                        ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) % (validationSize / 2),
                        Math.floor(
                           ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) / (validationSize / 2)),
                        (int) Math.floor(((validationSize / 2) - 1) - (currentElement.getEndByte())), true);
                     tempElement.setShouldValidate(shouldValidate);
                     tempElement.setValidationSize(validationSize);
                     tempElements.add(tempElement);
                  }
                  if (!isArray && currentElement.getEndWord() % 2 != 1 && shouldValidate) {
                     /**
                      * Rule for making sure next element on next structure sent is on boundary of 2n
                      */
                     currentElement = new InterfaceStructureElementToken("Insert Spare",
                        "byte align spare for byte alignment",
                        ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) % (validationSize / 2),
                        Math.floor(
                           ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) / (validationSize / 2)),
                        validationSize / 2);
                     currentElement.setShouldValidate(shouldValidate);
                     currentElement.setValidationSize(validationSize);
                     tempElements.add(currentElement);
                  }
                  break;
               } else {
                  currentElement = elementIterator.next();
               }
            } else {
               List<InterfaceStructureElementToken> arrayElements = parseElements(currentElement.getArrayElements(),
                  previousElement, validationSize, shouldValidate, true);
               currentElement.setArrayElements(arrayElements);
               currentElement.setBeginByte(arrayElements.get(0).getBeginByte());
               currentElement.setBeginWord(arrayElements.get(0).getBeginWord());

               tempElements.add(currentElement);
               previousElement = currentElement;

               if (!elementIterator.hasNext()) {
                  if (!isArray && currentElement.getEndByte() != ((validationSize / 2) - 1)) {
                     /**
                      * Rule for making sure last element ends on last byte of word(no partials)
                      */
                     InterfaceStructureElementToken tempElement = new InterfaceStructureElementToken("Insert Spare",
                        "byte align spare for aligning to word start",
                        ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) % (validationSize / 2),
                        Math.floor(
                           ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) / (validationSize / 2)),
                        (int) Math.floor(((validationSize / 2) - 1) - (currentElement.getEndByte())), true);
                     tempElement.setShouldValidate(shouldValidate);
                     tempElement.setValidationSize(validationSize);
                     tempElements.add(tempElement);
                  }
                  if (!isArray && currentElement.getEndWord() % 2 != 1 && shouldValidate) {
                     /**
                      * Rule for making sure next element on next structure sent is on boundary of 2n
                      */
                     currentElement = new InterfaceStructureElementToken("Insert Spare",
                        "byte align spare for byte alignment",
                        ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) % (validationSize / 2),
                        Math.floor(
                           ((currentElement.getEndWord() * (validationSize / 2)) + currentElement.getEndByte() + 1) / (validationSize / 2)),
                        validationSize / 2);
                     currentElement.setShouldValidate(shouldValidate);
                     currentElement.setValidationSize(validationSize);
                     tempElements.add(currentElement);
                  }
                  break;
               } else {
                  currentElement = elementIterator.next();
               }
            }
         }
      } else {
         /**
          * Condition for when less than 2 elements
          */
         InterfaceStructureElementToken lastElement = new InterfaceStructureElementToken("Insert Spare",
            "byte align spare for aligning to word start", 0.0, 0.0, 0);
         lastElement.setShouldValidate(shouldValidate);
         lastElement.setValidationSize(validationSize);
         for (InterfaceStructureElementToken element : elements) {
            if (element.getArrayElements().isEmpty()) {
               element = this.defaultSetUpElement(element, parentPrevious, shouldValidate, validationSize);
            } else {
               List<InterfaceStructureElementToken> arrayElements =
                  parseElements(element.getArrayElements(), parentPrevious, validationSize, shouldValidate, true);
               element.setArrayElements(arrayElements);
               element.setBeginByte(arrayElements.get(0).getBeginByte());
               element.setBeginWord(arrayElements.get(0).getBeginWord());
            }
            lastElement = element;
         }
         tempElements.addAll(elements);
         if (!isArray && lastElement.getEndByte() != ((validationSize / 2) - 1) && shouldValidate) {
            /**
             * Rule for making sure last element ends on last byte of word(no partials)
             */
            InterfaceStructureElementToken tempElement = new InterfaceStructureElementToken("Insert Spare",
               "byte align spare for aligning to word start",
               ((lastElement.getEndWord() * (validationSize / 2)) + lastElement.getEndByte() + 1) % (validationSize / 2),
               Math.floor(
                  ((lastElement.getEndWord() * (validationSize / 2)) + lastElement.getEndByte() + 1) / (validationSize / 2)),
               (int) Math.floor(((validationSize / 2) - 1) - (lastElement.getEndByte())), true);
            tempElement.setShouldValidate(shouldValidate);
            tempElement.setValidationSize(validationSize);
            tempElements.add(tempElement);
         }
         if (!isArray && lastElement.getEndWord() % 2 != 1 && shouldValidate) {
            /**
             * Rule for making sure next element on next structure sent is on boundary of 2n
             */
            lastElement = new InterfaceStructureElementToken("Insert Spare", "byte align spare for byte alignment",
               ((lastElement.getEndWord() * (validationSize / 2)) + lastElement.getEndByte() + 1) % (validationSize / 2),
               Math.floor(
                  ((lastElement.getEndWord() * (validationSize / 2)) + lastElement.getEndByte() + 1) / (validationSize / 2)),
               validationSize / 2);
            lastElement.setShouldValidate(shouldValidate);
            lastElement.setValidationSize(validationSize);
            tempElements.add(lastElement);
         }
      }
      return tempElements;
   }

   @Override
   public List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId) {
      return this.getAllRelated(branch, connectionId, subMessageId, 0L, 0L);
   }

   @Override
   public List<InterfaceStructureToken> getAll(BranchId branch) {
      return this.getAll(branch, 0L, 0L);
   }

   @Override
   public List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch) {
      return this.getAllWithoutRelations(branch, 0L, 0L);
   }

   @Override
   public List<InterfaceStructureToken> getFiltered(BranchId branch, String filter) {
      return this.getFiltered(branch, filter, 0L, 0L);
   }

   @Override
   public List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter) {
      return this.getFilteredWithoutRelations(branch, filter, 0L, 0L);
   }

   @Override
   public InterfaceStructureToken get(BranchId branch, ArtifactId artId) {
      InterfaceStructureToken structure;
      try {
         structure = this.getAccessor().get(branch, artId, this.getFullFollowRelationDetails());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         structure = InterfaceStructureToken.SENTINEL;
      }
      return structure;
   }

   @Override
   public Collection<InterfaceStructureToken> get(BranchId branch, Collection<ArtifactId> artIds,
      Collection<FollowRelation> followRelations) {
      try {
         return this.getAccessor().get(branch, artIds, followRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<>();
   }

   @Override
   public InterfaceStructureToken getRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId) {
      return this.getRelated(branch, connectionId, subMessageId, structureId, ArtifactId.SENTINEL);
   }

   @Override
   public InterfaceStructureToken getRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId, ArtifactId viewId) {
      InterfaceStructureToken structure;
      try {
         structure = this.getAccessor().getByRelation(branch, structureId,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, this.getFullFollowRelationDetails(),
            viewId);
         return this.parseStructure(branch, connectionId, structure, viewId);
      } catch (Exception ex) {
         System.out.println(ex);
         return InterfaceStructureToken.SENTINEL;
      }
   }

   @Override
   public List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId, String filter) {
      List<InterfaceStructureToken> totalStructureList = new LinkedList<InterfaceStructureToken>();
      try {
         /**
          * Gets total list of all related structures for lookup later
          */
         totalStructureList = this.getAllRelated(branch, connectionId, subMessageId);
         for (InterfaceStructureToken structure : totalStructureList) {
            structure = this.parseStructure(branch, connectionId, structure);
         }

         List<InterfaceStructureToken> structureList = totalStructureList;

         for (InterfaceStructureToken struct : structureList) {
            double sizeInBytes = struct.getSizeInBytes();
            int numElements = struct.getNumElements();
            List<InterfaceStructureElementToken> elementList = new LinkedList<InterfaceStructureElementToken>();
            for (InterfaceStructureElementToken element : struct.getElements()) {
               ArtifactReadable art = element.getArtifactReadable();
               List<String> allAttributes =
                  art.getExistingAttributeTypes().stream().map(a -> art.getAttributeValuesAsString(a)).filter(
                     b -> b.toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
               if (!allAttributes.isEmpty()) {
                  elementList.add(element);
               }
            }
            struct.setElements(elementList);
            struct.setSizeInBytes(sizeInBytes); // Set the size back to the unfiltered size
            struct.setNumElements(numElements);
         }
         return structureList;
      } catch (Exception ex) {
         return totalStructureList;
      }
   }

   @Override
   public InterfaceStructureToken getRelatedAndFilter(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId, String filter) {
      return this.getRelatedAndFilter(branch, connectionId, subMessageId, structureId, filter, ArtifactId.SENTINEL);
   }

   @Override
   public InterfaceStructureToken getRelatedAndFilter(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId, String filter, ArtifactId viewId) {
      InterfaceStructureToken structure;
      try {
         structure = this.getAccessor().getByRelation(branch, structureId,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, this.getFullFollowRelationDetails(),
            viewId);
         return this.parseStructure(branch, connectionId, structure, filter);
      } catch (Exception ex) {
         System.out.println(ex);
         return InterfaceStructureToken.SENTINEL;
      }
   }

   @Override
   public Collection<InterfaceStructureToken> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   public List<FollowRelation> getFullFollowRelationDetails() {
      List<FollowRelation> followRelations = new LinkedList<>();
      followRelations.add(FollowRelation.follow(CoreRelationTypes.InterfaceStructureContent_DataElement));
      followRelations.add(FollowRelation.fork(CoreRelationTypes.InterfaceElementArrayElement_ArrayElement,
         FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType),
         FollowRelation.follow(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet),
         FollowRelation.follow(CoreRelationTypes.InterfaceEnumeration_EnumerationState)));
      followRelations.addAll(FollowRelation.followList(CoreRelationTypes.InterfaceElementPlatformType_PlatformType,
         CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet,
         CoreRelationTypes.InterfaceEnumeration_EnumerationState));
      return followRelations;
   }

   private List<FollowRelation> getFollowRelationDetails() {
      List<FollowRelation> followRelations = new LinkedList<>();
      followRelations.add(FollowRelation.follow(CoreRelationTypes.InterfaceStructureContent_DataElement));
      followRelations.add(FollowRelation.fork(CoreRelationTypes.InterfaceElementArrayElement_ArrayElement,
         FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType)));
      followRelations.add(FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType));
      return followRelations;
   }

   @Override
   public InterfaceStructureToken getMessageHeaderStructure(BranchId branch, ArtifactId connectionId,
      ArtifactId messageId, ArtifactId viewId) {
      InterfaceMessageToken message = interfaceMessageApi.getWithRelations(branch, messageId,
         FollowRelation.followList(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage,
            CoreRelationTypes.InterfaceSubMessageContent_Structure,
            CoreRelationTypes.InterfaceStructureContent_DataElement,
            CoreRelationTypes.InterfaceElementPlatformType_PlatformType),
         viewId);
      InterfaceConnection connection = this.interfaceConnectionApi.get(branch, connectionId);
      boolean shouldValidate = connection.getTransportType().isByteAlignValidation();
      int validationSize = connection.getTransportType().getByteAlignValidationSize();
      ApplicabilityToken applic = message.getApplicability();
      String initiatingNode = "Node"; // This should always be overwritten below, but initializing just in case.
      if (message.getPublisherNodes().size() > 0) {
         initiatingNode = message.getPublisherNodes().get(0).getName();
      }
      String messageNumber = message.getInterfaceMessageNumber();
      Long id = 0L;

      InterfaceStructureCategoryAttribute categoryEnum = new InterfaceStructureCategoryAttribute();

      InterfaceStructureToken messageHeader =
         new InterfaceStructureToken(id, initiatingNode + " M" + messageNumber + " Header");
      messageHeader.setAutogenerated(true);
      messageHeader.setNameAbbrev("");
      messageHeader.setDescription(
         "This structure represents the software interface header for " + initiatingNode + " initiated message " + messageNumber + ".  The " + initiatingNode + " shall send this structure each time its associated message is transmitted.");
      messageHeader.setInterfaceMinSimultaneity("1");
      messageHeader.setInterfaceMaxSimultaneity("1");
      messageHeader.setInterfaceTaskFileType(0);
      messageHeader.setInterfaceStructureCategory(
         categoryEnum.Network.getIdString() + " " + categoryEnum.Network.getName());
      messageHeader.setApplicability(applic);

      // Create elements
      List<InterfaceStructureElementToken> elements = new LinkedList<>();
      PlatformTypeToken doubleType = new PlatformTypeToken(0L, "DOUBLE", "double", "64", "0.0", "604800.00", "seconds");
      PlatformTypeToken messageNumberType =
         new PlatformTypeToken(0L, "UINTEGER", "unsigned integer", "32", messageNumber, messageNumber, "");

      // Timetag
      InterfaceStructureElementToken timetag = new InterfaceStructureElementToken(id, "Timetag", applic, doubleType);
      timetag.setValidationSize(validationSize);
      timetag.setShouldValidate(shouldValidate);
      timetag.setDescription("Indicates the time that the message was prepared for transmission.");
      timetag.setInterfaceElementAlterable(false);
      elements.add(timetag);

      // Message number
      InterfaceStructureElementToken element =
         new InterfaceStructureElementToken(id, "Message Number", applic, messageNumberType);
      element.setValidationSize(validationSize);
      element.setShouldValidate(shouldValidate);
      element.setDescription("Indicates the message number for this message.");
      elements.add(element);

      Map<ArtifactId, List<InterfaceStructureToken>> submessageStructures = new HashMap<>();

      // Submessages
      for (InterfaceSubMessageToken subMessage : message.getSubMessages()) {
         String number = subMessage.getInterfaceSubMessageNumber();
         List<InterfaceStructureToken> structures = subMessage.getArtifactReadable().getRelatedList(
            CoreRelationTypes.InterfaceSubMessageContent_Structure).stream().map(
               s -> new InterfaceStructureToken(s)).collect(Collectors.toList());
         submessageStructures.put(subMessage.getArtifactId(), structures);
         PlatformTypeToken structuresType = new PlatformTypeToken(0L, "UINTEGER", "unsigned integer", "32", "", "", "");

         Integer minSimult = null;
         Integer maxSimult = null;

         for (InterfaceStructureToken struct : structures) {
            String min = struct.getInterfaceMinSimultaneity();
            String max = struct.getInterfaceMaxSimultaneity();
            if (Strings.isNumeric(min) && Strings.isNumeric(max)) {
               minSimult = minSimult == null ? Integer.parseInt(min) : minSimult + Integer.parseInt(min);
               maxSimult = maxSimult == null ? Integer.parseInt(max) : maxSimult + Integer.parseInt(max);
            } else {
               structuresType.setInterfacePlatformTypeValidRangeDescription("Calculated");
            }
         }

         if (minSimult != null && maxSimult != null) {
            structuresType.setInterfacePlatformTypeMinval(minSimult + "");
            structuresType.setInterfacePlatformTypeMaxval(maxSimult + "");
         }

         element = new InterfaceStructureElementToken(id, "Number of Structures in Submessage " + number, applic,
            structuresType);
         if (minSimult != maxSimult) {
            element.setInterfaceElementAlterable(true);
            timetag.setInterfaceElementAlterable(true);
         }
         element.setValidationSize(validationSize);
         element.setShouldValidate(shouldValidate);
         element.setDescription(
            "Indicates the number of structures in the submessage for a given transmission of it's message.");
         elements.add(element);

         PlatformTypeToken offsetType = new PlatformTypeToken(0L, "UINTEGER", "unsigned integer", "32", "", "", "");
         element =
            new InterfaceStructureElementToken(id, "Submessage " + number + " Relative Offset", applic, offsetType);
         element.setDescription(
            "Indicates the byte offset from the beginning of the message to the start of the submessage data.");
         element.setValidationSize(validationSize);
         element.setShouldValidate(shouldValidate);
         elements.add(element);
      }

      parseStructure(branch, connectionId, messageHeader, elements);

      // We want spares to be included in counts for autogenerated headers
      messageHeader.getElements().stream().forEach(e -> {
         e.setIncludedInCounts(true);
         e.setShouldValidate(shouldValidate);
         e.setValidationSize(validationSize);
         if (e.getName().equals("Insert Spare")) {
            PlatformTypeToken spareType = new PlatformTypeToken(0L, "UINTEGER", "unsigned integer", "32", "", "", "");
            spareType.setInterfacePlatformTypeValidRangeDescription("n/a");
            e.setName("Byte Alignment Spare");
            e.setPlatformType(spareType);
            e.setLogicalType("unsigned integer");
         }
      });

      // Set the elements again so the byte count updates to include spares
      messageHeader.setElements(messageHeader.getElements());

      // Go back through the elements to set the relative offset valid ranges. This needs to be done
      // at the end because it depends on the full size of the header including any added spares.
      int offset = (int) messageHeader.getSizeInBytes().doubleValue();
      int submessageNumber = 1;
      int offsetIndex = (submessageNumber * 2) + 1;
      while (offsetIndex < messageHeader.getElements().size()) {
         InterfaceStructureElementToken offsetElement = messageHeader.getElements().get(offsetIndex);
         PlatformTypeToken offsetType = offsetElement.getPlatformType();
         offsetElement.setInterfacePlatformTypeMinval(offset + "");
         offsetElement.setInterfacePlatformTypeMaxval(offset + "");
         offsetType.setInterfacePlatformTypeMinval(offset + "");
         offsetType.setInterfacePlatformTypeMaxval(offset + "");

         // If the current submessage has a static size, calculate and store the total size of the submessage (sum of struct size * simult)
         // If not a static size, the rest of the offsets can not be calculated, so break the loop
         InterfaceStructureElementToken numStructsElement = messageHeader.getElements().get(offsetIndex - 1);
         String minNumElements = numStructsElement.getInterfacePlatformTypeMinval();
         String maxNumElements = numStructsElement.getInterfacePlatformTypeMaxval();
         if (Strings.isNumeric(minNumElements) && Strings.isNumeric(maxNumElements) && minNumElements.equals(
            maxNumElements)) {
            InterfaceSubMessageToken submessage =
               ((List<InterfaceSubMessageToken>) message.getSubMessages()).get(submessageNumber - 1);
            for (InterfaceStructureToken structure : submessageStructures.get(submessage.getArtifactId())) {
               offset += structure.getSizeInBytes() * Integer.parseInt(structure.getInterfaceMaxSimultaneity());
            }
         } else {
            break;
         }

         submessageNumber++;
         offsetIndex = (submessageNumber * 2) + 1;
      }

      return messageHeader;
   }

   @Override
   public Collection<InterfaceStructureToken> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceStructureToken> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      return this.query(branch, query, isExact, 0L, 0L);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      try {
         return this.getAccessor().getAffectedArtifacts(branch, relatedId, affectedRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<ArtifactMatch>();
   }

   @Override
   public Collection<InterfaceStructureToken> query(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceStructureToken> queryExact(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceStructureToken> query(BranchId branch, MimAttributeQuery query, boolean isExact,
      long pageNum, long pageSize) {
      try {
         List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAllByQuery(branch, query,
            this.getFullFollowRelationDetails(), isExact, pageNum, pageSize);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(branch, ArtifactId.valueOf(-1L), structure); //note: do we want validation to be done on queries?
         }
         return structureList;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceStructureToken>();
   }

   @Override
   public List<InterfaceStructureToken> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch, long pageNum, long pageSize) {
      return this.getAllWithoutRelations(branch, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public List<InterfaceStructureToken> getFiltered(BranchId branch, String filter, long pageNum, long pageSize) {
      return this.getFiltered(branch, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter, long pageNum,
      long pageSize) {
      return this.getFilteredWithoutRelations(branch, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      long pageNum, long pageSize) {
      return this.getAllRelated(branch, connectionId, subMessageId, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   /**
    * potentially not valid, need to decide the appropriate approach for structure filter
    */

   @Override
   public List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId subMessageId, String filter,
      long pageNum, long pageSize) {
      return null;
   }

   @Override
   public List<InterfaceStructureToken> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAllWithoutRelations(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public List<InterfaceStructureToken> getFiltered(BranchId branch, String filter, AttributeTypeId orderByAttribute) {
      return this.getFiltered(branch, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getFilteredWithoutRelations(branch, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      AttributeTypeId orderByAttribute) {
      return this.getAllRelated(branch, connectionId, subMessageId, 0L, 0L, orderByAttribute);
   }

   /**
    * potentially not valid, need to decide the appropriate approach for structure filter
    */

   @Override
   public List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId subMessageId, String filter,
      AttributeTypeId orderByAttribute) {
      return null;
   }

   @Override
   public List<InterfaceStructureToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAll(branch,
            this.getFullFollowRelationDetails(), pageNum, pageSize, orderByAttribute);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(branch, ArtifactId.valueOf(-1L), structure);
         }

         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   @Override
   public List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAll(branch,
            this.getFollowRelationDetails(), pageNum, pageSize, orderByAttribute);
         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   @Override
   public List<InterfaceStructureToken> getAllWithRelations(BranchId branch, List<FollowRelation> followRelations,
      String filter, Collection<AttributeTypeId> attributes, AttributeTypeId orderByAttribute) {
      try {
         return (List<InterfaceStructureToken>) this.getAccessor().getAll(branch, followRelations, filter, attributes,
            0, 0, orderByAttribute);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return new LinkedList<>();
      }
   }

   @Override
   public List<InterfaceStructureToken> getFiltered(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAllByFilter(branch, filter,
            this.structureAttributeList, this.getFullFollowRelationDetails(), pageNum, pageSize, orderByAttribute);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(branch, ArtifactId.valueOf(-1L), structure);
         }

         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   @Override
   public List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAllByFilter(branch, filter,
            this.structureAttributeList, this.getFollowRelationDetails(), pageNum, pageSize, orderByAttribute);
         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   @Override
   public List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      return this.getAllRelated(branch, connectionId, subMessageId, ArtifactId.SENTINEL, pageNum, pageSize,
         orderByAttribute);
   }

   @Override
   public List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId viewId, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, this.getFullFollowRelationDetails(),
            pageNum, pageSize, orderByAttribute, viewId);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(branch, connectionId, structure, viewId, structure.getElements());
         }

         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   @Override
   public int getAllRelatedCount(BranchId branch, ArtifactId subMessageId) {
      int count = 0;
      try {
         count = this.getAccessor().getAllByRelationAndCount(branch,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId);

         return count;
      } catch (Exception ex) {
         System.out.println(ex);
         return 0;
      }
   }

   @Override
   public List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId, String filter, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      return this.getAllRelatedAndFilter(branch, connectionId, subMessageId, ArtifactId.SENTINEL, filter, pageNum,
         pageSize, orderByAttribute);
   }

   /**
    * potentially not valid, need to decide the appropriate approach for structure filter
    */

   @Override
   public List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId, ArtifactId viewId, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) this.getAccessor().getAllByRelationAndFilter(branch,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, filter, this.structureAttributeList,
            this.getFullFollowRelationDetails(), pageNum, pageSize, orderByAttribute, this.elementAttributeList);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(branch, connectionId, structure, viewId, structure.getElements());
         }
         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   @Override
   public int getAllRelatedAndFilterCount(BranchId branch, ArtifactId subMessageId, String filter) {
      int count = 0;
      try {
         count = this.getAccessor().getAllByRelationAndFilterAndCount(branch,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, filter, this.structureAttributeList,
            this.getFollowRelationDetails(), this.elementAttributeList);
         return count;
      } catch (Exception ex) {
         System.out.println(ex);
         return 0;
      }
   }

   @Override
   public List<InterfaceStructureToken> getAllByName(BranchId branch, String name, long pageNum, long pageSize) {
      try {
         return (List<InterfaceStructureToken>) this.getAccessor().getAll(branch, new LinkedList<>(), name,
            Arrays.asList(CoreAttributeTypes.Name), pageNum, pageSize, CoreAttributeTypes.Name);
      } catch (Exception ex) {
         System.out.println(ex);
         return new LinkedList<>();
      }
   }

   @Override
   public int getAllByNameCount(BranchId branch, String name) {
      return this.getAccessor().getAllByFilterAndCount(branch, name, Arrays.asList(CoreAttributeTypes.Name));
   }

}
