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
package org.eclipse.osee.framework.types.bridge.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.AttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum;
import org.eclipse.osee.framework.oseeTypes.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeExtensionManager;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public class ExcelToEMFModel implements IOseeDataTypeProcessor {
   private final OseeTypesFactory factory;
   private final Map<String, OseeTypeModel> oseeModels;
   private OseeTypeModel currentModel;

   public ExcelToEMFModel(Map<String, OseeTypeModel> oseeModels) {
      this.factory = OseeTypesFactory.eINSTANCE;
      this.oseeModels = oseeModels;
   }

   public void createModel(String name) {
      currentModel = factory.createOseeTypeModel();
      oseeModels.put(name, currentModel);
   }

   private OseeTypeModel getCurrentModel() {
      return currentModel;
   }

   private String toQualifiedName(String name) {
      return "\"" + name + "\"";
   }

   private OseeType getObject(String name, Class<? extends OseeType> classToLookFor) throws OseeArgumentException {
      EList<? extends OseeType> types;

      if (classToLookFor.equals(ArtifactType.class)) {
         types = getCurrentModel().getArtifactTypes();
      } else if (classToLookFor.equals(AttributeType.class)) {
         types = getCurrentModel().getAttributeTypes();
      } else if (classToLookFor.equals(RelationType.class)) {
         types = getCurrentModel().getRelationTypes();
      } else if (classToLookFor.equals(OseeEnumType.class)) {
         types = getCurrentModel().getEnumTypes();
      } else {
         throw new OseeArgumentException(classToLookFor.getName() + " not a supported type");
      }
      for (OseeType oseeTypes : types) {
         if (name.equals(oseeTypes.getName())) {
            return classToLookFor.cast(oseeTypes);
         }
      }
      return null;
   }

   @Override
   public void onArtifactTypeInheritance(String ancestor, Collection<String> descendants) throws OseeCoreException {
      ArtifactType ancestorType = (ArtifactType) getObject(ancestor, ArtifactType.class);
      if (ancestorType == null) {
         throw new OseeInvalidInheritanceException("Ancestor [%s]");
      }

      //      if (superArtifactTypeName != null) {
      //         ArtifactType superArtifactType = (ArtifactType) getObject(superArtifactTypeName, ArtifactType.class);
      //         if (superArtifactType == null) {
      //            boolean isAbstractSuper = false;
      //            onArtifactType(isAbstractSuper, superArtifactTypeName, null);
      //            superArtifactType = (ArtifactType) getObject(superArtifactTypeName, ArtifactType.class);
      //         }
      //         artifactType.setSuperArtifactType(superArtifactType);
      //      }
   }

   @Override
   public void onArtifactType(boolean isAbstract, String name) throws OseeCoreException {
      String id = toQualifiedName(name);
      OseeType types = getObject(id, ArtifactType.class);
      if (types == null) {
         ArtifactType artifactType = factory.createArtifactType();
         artifactType.setName(id);
         getCurrentModel().getArtifactTypes().add(artifactType);
      }
   }

   @Override
   public void onAttributeType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String name, String defaultValue, String validityXml, int minOccurrence, int maxOccurrence, String toolTipText, String taggerId) throws OseeCoreException {
      String id = toQualifiedName(name);
      OseeType types = getObject(id, AttributeType.class);
      if (types == null) {
         AttributeType attributeType = factory.createAttributeType();
         attributeType.setName(id);
         attributeType.setBaseAttributeType(Lib.getExtension(attributeBaseType));
         attributeType.setDataProvider(Lib.getExtension(attributeProviderTypeName));
         attributeType.setMin(String.valueOf(minOccurrence));

         String maxValue;
         if (maxOccurrence == Integer.MAX_VALUE) {
            maxValue = "unlimited";
         } else {
            maxValue = String.valueOf(maxOccurrence);
         }
         attributeType.setMax(maxValue);

         if (Strings.isValid(fileTypeExtension)) {
            attributeType.setFileExtension(fileTypeExtension);
         }
         if (Strings.isValid(defaultValue)) {
            attributeType.setDefaultValue(defaultValue);
         }
         if (Strings.isValid(toolTipText)) {
            attributeType.setDescription(toolTipText);
         }
         if (Strings.isValid(taggerId)) {
            attributeType.setTaggerId(taggerId);
         }

         OseeEnumType enumType = getEnumType(attributeBaseType, attributeProviderTypeName, name, validityXml);
         if (enumType != null) {
            attributeType.setEnumType(enumType);
         }
         getCurrentModel().getAttributeTypes().add(attributeType);
      }
   }

   @Override
   public boolean doesArtifactSuperTypeExist(String artifactSuperTypeName) throws OseeCoreException {
      return getObject(artifactSuperTypeName, ArtifactType.class) != null;
   }

   @Override
   public void onAttributeValidity(String attributeName, String artifactSuperTypeName, Collection<String> concreteTypes) throws OseeCoreException {
      ArtifactType superArtifactType =
            (ArtifactType) getObject(toQualifiedName(artifactSuperTypeName), ArtifactType.class);
      AttributeType attributeType = (AttributeType) getObject(toQualifiedName(attributeName), AttributeType.class);

      if (superArtifactType == null && "Artifact".equals(artifactSuperTypeName)) {
         onArtifactType(false, "Artifact");
         superArtifactType = (ArtifactType) getObject(toQualifiedName(artifactSuperTypeName), ArtifactType.class);
      }

      if (superArtifactType == null || attributeType == null) {
         throw new OseeStateException(String.format("Type Missing: %s - %s", artifactSuperTypeName, attributeName));
      }
      AttributeTypeRef reference = factory.createAttributeTypeRef();
      reference.setValidAttributeType(attributeType);
      superArtifactType.getValidAttributeTypes().add(reference);
   }

   @Override
   public void onRelationType(String name, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, String ordered, String defaultOrderTypeGuid) throws OseeCoreException {
      String id = toQualifiedName(name);
      OseeType types = getObject(id, RelationType.class);
      if (types == null) {
         RelationType relationType = factory.createRelationType();
         relationType.setName(id);
         relationType.setSideAName(sideAName);
         relationType.setSideBName(sideBName);

         String arranger;
         if ("Yes".equals(ordered)) {
            arranger = "Lexicographical_Ascending";
         } else {
            arranger = "Unordered";
         }
         relationType.setDefaultOrderType(arranger);
         getCurrentModel().getRelationTypes().add(relationType);
      }
   }

   @Override
   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax) throws OseeCoreException {
      RelationType relationType = (RelationType) getObject(toQualifiedName(relationTypeName), RelationType.class);
      ArtifactType artifactType = (ArtifactType) getObject(toQualifiedName(artifactTypeName), ArtifactType.class);

      if (sideAMax > 0) {
         relationType.setSideAArtifactType(artifactType);
      }
      if (sideBMax > 0) {
         relationType.setSideBArtifactType(artifactType);
      }

      RelationMultiplicityEnum multiplicity = relationType.getMultiplicity();
      if (sideAMax == Integer.MAX_VALUE && sideBMax == 1) {
         multiplicity = RelationMultiplicityEnum.ONE_TO_MANY;

      } else if (sideAMax == 1 && sideBMax == Integer.MAX_VALUE) {
         multiplicity = RelationMultiplicityEnum.MANY_TO_ONE;

      } else if (sideAMax == Integer.MAX_VALUE && sideBMax == Integer.MAX_VALUE) {
         multiplicity = RelationMultiplicityEnum.MANY_TO_MANY;
      } else if (sideAMax == 1 && sideBMax == 1) {
         multiplicity = RelationMultiplicityEnum.ONE_TO_ONE;
      } else {
         System.out.println("None detected - " + relationTypeName);
      }

      if (multiplicity != null && !multiplicity.equals(relationType.getMultiplicity())) {
         relationType.setMultiplicity(multiplicity);
      } else {
         System.out.println("Null multiplicity - " + relationTypeName);
      }
   }

   private static void checkEnumTypeName(String enumTypeName) throws OseeCoreException {
      if (!Strings.isValid(enumTypeName)) {
         throw new OseeArgumentException("Osee Enum Type Name cannot be null.");
      }
   }

   private OseeEnumType getEnumType(String attributeBaseType, String attributeProviderTypeName, String name, String validityXml) throws OseeCoreException {
      Class<? extends Attribute<?>> baseAttributeClass =
            AttributeExtensionManager.getAttributeClassFor(attributeBaseType);

      OseeEnumType oseeEnumType = null;
      if (EnumeratedAttribute.class.isAssignableFrom(baseAttributeClass)) {
         createEnumTypeFromXml(toQualifiedEnumName(name), validityXml);
      }
      return oseeEnumType;
   }

   private String toQualifiedEnumName(String name) {
      return "\"" + name + ".enum\"";
   }

   private OseeEnumType createEnumTypeFromXml(String attributeTypeName, String xmlDefinition) throws OseeCoreException {
      List<Pair<String, Integer>> entries = new ArrayList<Pair<String, Integer>>();
      String enumTypeName = "";

      if (!Strings.isValid(xmlDefinition)) {
         throw new OseeArgumentException("The enum xml definition must not be null or empty");
      }

      Document document;
      try {
         document = Jaxp.readXmlDocument(xmlDefinition);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      enumTypeName = attributeTypeName;
      Element choicesElement = document.getDocumentElement();
      NodeList enumerations = choicesElement.getChildNodes();
      Set<String> choices = new LinkedHashSet<String>();

      for (int i = 0; i < enumerations.getLength(); i++) {
         Node node = enumerations.item(i);
         if (node.getNodeName().equalsIgnoreCase("Enum")) {
            choices.add(node.getTextContent());
         } else {
            throw new OseeArgumentException("Validity Xml not of excepted enum format");
         }
      }

      int ordinal = 0;
      for (String choice : choices) {
         entries.add(new Pair<String, Integer>(choice, ordinal++));
      }

      return createEnumType(enumTypeName, entries);
   }

   private OseeEnumType createEnumType(String enumTypeName, List<Pair<String, Integer>> entries) throws OseeCoreException {
      checkEnumTypeName(enumTypeName);
      checkEntryIntegrity(enumTypeName, entries);

      OseeEnumType oseeEnumType = null;

      OseeType types = getObject(enumTypeName, OseeEnumType.class);
      if (types == null) {
         oseeEnumType = factory.createOseeEnumType();
         oseeEnumType.setName(enumTypeName);

         for (Pair<String, Integer> entry : entries) {
            OseeEnumEntry oseeEnum = factory.createOseeEnumEntry();
            oseeEnum.setName(entry.getFirst());
            oseeEnum.setOrdinal(String.valueOf(entry.getSecond()));
            oseeEnumType.getEnumEntries().add(oseeEnum);
         }
         getCurrentModel().getEnumTypes().add(oseeEnumType);
      } else {
         oseeEnumType = (OseeEnumType) types;
      }
      return oseeEnumType;
   }

   private static void checkEntryIntegrity(String enumTypeName, List<Pair<String, Integer>> entries) throws OseeCoreException {
      if (entries == null) {
         throw new OseeArgumentException(String.format("Osee Enum Type [%s] had null entries", enumTypeName));
      }

      //      if (entries.size() <= 0) throw new OseeArgumentException(String.format("Osee Enum Type [%s] had 0 entries",
      //            enumTypeName));
      Map<String, Integer> values = new HashMap<String, Integer>();
      for (Pair<String, Integer> entry : entries) {
         String name = entry.getFirst();
         int ordinal = entry.getSecond();
         if (!Strings.isValid(name)) {
            throw new OseeArgumentException("Enum entry name cannot be null");
         }
         if (ordinal < 0) {
            throw new OseeArgumentException("Enum entry ordinal cannot be of negative value");
         }
         if (values.containsKey(name)) {
            throw new OseeArgumentException(String.format("Unique enum entry name violation - [%s] already exists.",
                  name));
         }
         if (values.containsValue(ordinal)) {
            throw new OseeArgumentException(String.format("Unique enum entry ordinal violation - [%s] already exists.",
                  ordinal));
         }
         values.put(name, ordinal);
      }
   }

   @Override
   public void onFinish() throws OseeCoreException {

   }
}
