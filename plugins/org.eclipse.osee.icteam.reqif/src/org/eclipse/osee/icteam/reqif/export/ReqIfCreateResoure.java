/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 *     Boeing - update for RMF 0.13
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeDefinitionXHTML;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.DatatypeDefinitionBoolean;
import org.eclipse.rmf.reqif10.DatatypeDefinitionDate;
import org.eclipse.rmf.reqif10.DatatypeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.DatatypeDefinitionInteger;
import org.eclipse.rmf.reqif10.DatatypeDefinitionReal;
import org.eclipse.rmf.reqif10.DatatypeDefinitionString;
import org.eclipse.rmf.reqif10.DatatypeDefinitionXHTML;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.ReqIFHeader;
import org.eclipse.rmf.reqif10.ReqIFToolExtension;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecObjectType;
import org.eclipse.rmf.reqif10.SpecRelation;
import org.eclipse.rmf.reqif10.SpecRelationType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;
import org.eclipse.rmf.reqif10.XhtmlContent;
import org.eclipse.rmf.reqif10.xhtml.XhtmlDivType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlFactory;

/**
 * This Class is used to Create a REQIF file from the Requirements
 *
 * @author Manjunath Sangappa
 */
public class ReqIfCreateResoure {

   Map<String, List<Object>> typesMap = new HashMap<String, List<Object>>();

   /**
    * Creates a Reqif file with a given file name from the artifacts
    *
    * @param fileName : name of the File
    * @param requirmentChildren : Requirments Artifacts
    * @throws OseeCoreException : throws
    * @throws DatatypeConfigurationException
    * @throws IOException
    */
   public void createReqIfResource(String fileName, final List<Artifact> artifacts) throws OseeCoreException, DatatypeConfigurationException, IOException {

      fileName = fileName + ".reqif";
      UniqueIdentifierGenerator.resetNumber();
      ReqIF reqif = ReqIF10Factory.eINSTANCE.createReqIF();
      ReqIFHeader header = createReqIFHeader();
      reqif.setTheHeader(header);
      ReqIFContent coreContent = createCoreContent(artifacts);
      reqif.setCoreContent(coreContent);
      reqif.getToolExtensions().add(createReqifToolExtension());
      RMFUtility.saveReqIFFile(reqif, fileName);

   }

   /**
    * @return
    */
   private ReqIFToolExtension createReqifToolExtension() {
      ReqIFToolExtension toolExtension = ReqIF10Factory.eINSTANCE.createReqIFToolExtension();
      return toolExtension;
   }

   /**
    * Creats Core Content Type of REQIF format
    *
    * @param artifacts
    * @throws OseeCoreException
    * @throws DatatypeConfigurationException
    */
   private ReqIFContent createCoreContent(final List<Artifact> artifacts) throws OseeCoreException, DatatypeConfigurationException {

      ReqIFContent reqifContent = ReqIF10Factory.eINSTANCE.createReqIFContent();

      // Speciifications
      createSpecification(artifacts, reqifContent);

      // Spec Group Relations
      createSpecObjects(reqifContent);

      // Spec Relations
      createSpecRelations(artifacts, reqifContent);
      // SpecTypes
      createSpecTypes(reqifContent);

      createDataTypes(reqifContent);

      return reqifContent;
   }

   /**
    * Creates Spec Relations of REQIF format (Relation between Requirements)
    *
    * @param artifacts
    * @param reqifcontent
    * @throws OseeCoreException
    * @throws DatatypeConfigurationException
    */
   private void createSpecRelations(final List<Artifact> artifacts, final ReqIFContent reqifcontent) throws OseeCoreException, DatatypeConfigurationException {

      List<RelationLink> relationLink = new ArrayList<RelationLink>();
      for (Artifact artifact : artifacts) {
         List<Artifact> children = null;

         children = artifact.getChildren();
         for (Artifact artifactChild : children) {
            List<RelationLink> relationsHigerLevel =
               artifactChild.getRelations(CoreRelationTypes.RequirementTrace_HigherLevelRequirement);
            List<RelationLink> relationsLowerLevel =
               artifactChild.getRelations(CoreRelationTypes.RequirementTrace_LowerLevelRequirement);

            for (RelationLink relationLink2 : relationsLowerLevel) {
               if (!relationLink.contains(relationLink2)) {
                  relationLink.add(relationLink2);
               }
            }
            for (RelationLink relationLink2 : relationsHigerLevel) {
               if (!relationLink.contains(relationLink2)) {
                  relationLink.add(relationLink2);
               }
            }
         }
      }

      if (relationLink.size() > 0) {
         for (RelationLink relation : relationLink) {
            SpecObject specObjectA = findSpecObject(relation.getArtifactA(), reqifcontent);
            SpecObject specObjectB = findSpecObject(relation.getArtifactB(), reqifcontent);

            SpecRelation specRelation = ReqIF10Factory.eINSTANCE.createSpecRelation();
            specRelation.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("SpecRelation"));
            specRelation.setLastChange(ReqIFUtil.getGregorianCalendarNow());
            specRelation.setSource(specObjectA);
            specRelation.setTarget(specObjectB);

            List<Object> list = this.typesMap.get("SpecRelationType");
            SpecRelationType specRelationType;
            if (list != null) {
               specRelationType = (SpecRelationType) list.get(0);

            } else {
               specRelationType = createSpecRelationType();
            }
            specRelation.setType(specRelationType);
            reqifcontent.getSpecRelations().add(specRelation);

         }
      }
   }

   /**
    * Creates Spec Relation Type of Reqif Format
    *
    * @return : return identifier of Spec realtion type
    * @throws DatatypeConfigurationException
    */
   private SpecRelationType createSpecRelationType() throws DatatypeConfigurationException {
      SpecRelationType specRelationType = ReqIF10Factory.eINSTANCE.createSpecRelationType();
      String uniqueName = UniqueIdentifierGenerator.createUniqueNameWithID("SpecRelationType");
      specRelationType.setIdentifier(uniqueName);
      specRelationType.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      addObjectInToMap(specRelationType, "SpecRelationType");
      return specRelationType;
   }

   /**
    * Finds the Spec object for the give Requirment Artifact
    *
    * @param artifactA
    * @param specObject
    */
   private SpecObject findSpecObject(final Artifact artifactA, final ReqIFContent reqifcontent) {
      EList<SpecObject> specobjects = reqifcontent.getSpecObjects();

      for (SpecObject specObject : specobjects) {
         EList<AttributeValue> values = specObject.getValues();
         for (AttributeValue attributeValue : values) {
            if (attributeValue instanceof AttributeValueString) {
               AttributeValueString attr = (AttributeValueString) attributeValue;
               if (attr.getTheValue().equals(artifactA.getName())) {
                  return specObject;
               }
            }
         }
      }

      return null;
   }

   /**
    * Creates a Spec Object Type of Reqif Format
    *
    * @param reqifContent
    * @return spec object type
    */
   private void createSpecObjects(final ReqIFContent reqifContent) {
      List<Object> list = this.typesMap.get("SpecObject");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof SpecObject) {
               SpecObject specObject = (SpecObject) object;
               reqifContent.getSpecObjects().add(specObject);

            }
         }
      }
   }

   /**
    * Creates Data Type of Reqif format
    *
    * @return dataType
    */
   private void createDataTypes(final ReqIFContent reqifContent) {

      List<Object> list = this.typesMap.get("DataTypeDefinitionString");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionString) {
               DatatypeDefinitionString datatypedefString = (DatatypeDefinitionString) object;
               reqifContent.getDatatypes().add(datatypedefString);
            }
         }
      }

      list = this.typesMap.get("DataTypeDefinitionInteger");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionInteger) {
               DatatypeDefinitionInteger dataTypeDef = (DatatypeDefinitionInteger) object;
               reqifContent.getDatatypes().add(dataTypeDef);
            }
         }
      }

      list = this.typesMap.get("DataTypeDefinitionDate");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionDate) {
               DatatypeDefinitionDate dataTypeDef = (DatatypeDefinitionDate) object;
               reqifContent.getDatatypes().add(dataTypeDef);
            }
         }
      }

      list = this.typesMap.get("DataTypeDefinitionBoolean");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionBoolean) {
               DatatypeDefinitionBoolean dataTypeDef = (DatatypeDefinitionBoolean) object;
               reqifContent.getDatatypes().add(dataTypeDef);
            }
         }
      }

      list = this.typesMap.get("DataTypeDefinitionEnumeration");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionEnumeration) {
               DatatypeDefinitionEnumeration dataTypeDef = (DatatypeDefinitionEnumeration) object;
               reqifContent.getDatatypes().add(dataTypeDef);
            }
         }
      }

      list = this.typesMap.get("DataTypeDefinitionReal");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionReal) {
               DatatypeDefinitionReal dataTypeDef = (DatatypeDefinitionReal) object;
               reqifContent.getDatatypes().add(dataTypeDef);
            }
         }
      }

      list = this.typesMap.get("DataTypeDefinitionXhtml");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof DatatypeDefinitionXHTML) {
               DatatypeDefinitionXHTML dataTypeDef = (DatatypeDefinitionXHTML) object;
               reqifContent.getDatatypes().add(dataTypeDef);
            }
         }
      }
   }

   private void createSpecTypes(final ReqIFContent reqifContent) {

      // Specification Types
      List<Object> list = this.typesMap.get("SpecificationType");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof SpecificationType) {
               SpecificationType specificationType = (SpecificationType) object;
               reqifContent.getSpecTypes().add(specificationType);
            }
         }
      }

      // Spec Object Type
      list = this.typesMap.get("SpecObjectType");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof SpecObjectType) {
               SpecObjectType specObjectType = (SpecObjectType) object;
               reqifContent.getSpecTypes().add(specObjectType);
            }
         }
      }

      // Spec Relationds Type
      list = this.typesMap.get("SpecRelationType");
      if (list != null) {
         for (Object object : list) {
            if (object instanceof SpecRelationType) {
               SpecRelationType specRelationType = (SpecRelationType) object;
               reqifContent.getSpecTypes().add(specRelationType);
            }
         }
      }
   }

   /**
    * Create Speciification for every Requirment folder
    *
    * @param reqifContent
    * @param artifact
    * @throws OseeCoreException
    * @throws DatatypeConfigurationException
    */
   private void createSpecification(final List<Artifact> artifacts, final ReqIFContent reqifContent) throws OseeCoreException, DatatypeConfigurationException {

      for (Artifact artifact : artifacts) {
         Specification specification = ReqIF10Factory.eINSTANCE.createSpecification();
         specification.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("Specification"));
         specification.setLastChange(ReqIFUtil.getGregorianCalendarNow());
         specification.setLongName(artifact.getName());

         SpecificationType specificationTypeTemp;
         List<Object> list = this.typesMap.get("SpecificationType");
         if (list != null) {
            specificationTypeTemp = (SpecificationType) list.get(0);
         } else {
            specificationTypeTemp = createSpeciificationType();
         }
         specification.setType(specificationTypeTemp);

         List<Artifact> requirmentArtifacts = artifact.getChildren();
         for (Artifact reqArtifact : requirmentArtifacts) {
            specification.getChildren().add(createSpecHierarchy(reqArtifact));

         }
         reqifContent.getSpecifications().add(specification);
      }
   }

   /**
    * Create Hierarchy of the Spec Object
    *
    * @param reqArtifact
    * @throws OseeCoreException
    * @throws DatatypeConfigurationException
    */
   private SpecHierarchy createSpecHierarchy(final Artifact reqArtifact) throws OseeCoreException, DatatypeConfigurationException {
      SpecHierarchy spechierarchy = ReqIF10Factory.eINSTANCE.createSpecHierarchy();
      spechierarchy.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("SpecHierarchy"));
      spechierarchy.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      spechierarchy.setObject(createSpecObject(reqArtifact));

      List<Artifact> children = reqArtifact.getChildren();
      if (children.size() > 0) {

         for (Artifact artifact : children) {
            SpecHierarchy spech = createSpecHierarchy(artifact);
            spechierarchy.getChildren().add(spech);
         }
      }
      return spechierarchy;
   }

   /**
    * Create Spec Object for every Requirment Artifact
    *
    * @param reqArtifact
    * @return Identifier of Spec Object
    * @throws OseeCoreException
    * @throws DatatypeConfigurationException
    */
   private SpecObject createSpecObject(final Artifact reqArtifact) throws OseeCoreException, DatatypeConfigurationException {
      SpecObject specObject = ReqIF10Factory.eINSTANCE.createSpecObject();
      specObject.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("SpecObject"));
      specObject.setLastChange(ReqIFUtil.getGregorianCalendarNow());

      SpecObjectType specObjectType = null;
      List<Object> list = this.typesMap.get("SpecObjectType");
      if (list == null) {
         specObjectType = createSpecObjectType();
      } else {
         specObjectType = (SpecObjectType) list.get(0);
      }
      specObject.setType(specObjectType);

      specObject.getValues().add(createAttributeValueString(reqArtifact, specObjectType));
      specObject.getValues().add(createAttributeValueXhtml(specObjectType));

      List<Attribute<Object>> attributes = reqArtifact.getAttributes(CoreAttributeTypes.WordTemplateContent);
      if (attributes.size() > 0) {
         Attribute<Object> attribute = attributes.get(0);
         attribute.getValue();

         EList<AttributeValue> values = specObject.getValues();
         for (AttributeValue attributeValue : values) {
            if (attributeValue instanceof AttributeValueXHTML) {
               AttributeValueXHTML attr = (AttributeValueXHTML) attributeValue;
               try (
                  CustomisedXmlTextInputStream inputStream = new CustomisedXmlTextInputStream(
                     attribute.getValue().toString(), (XhtmlDivType) attr.getTheValue().getXhtml());
                  InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {

                  char[] chars = new char[16000];
                  reader.read(chars);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         }
      }

      addObjectInToMap(specObject, "SpecObject");
      return specObject;
   }

   /**
    * Create String Value for Spec Object
    *
    * @param reqArtifact
    * @param specObjectType
    * @param typeTypes6
    * @return Value of the string
    */
   private AttributeValueXHTML createAttributeValueXhtml(final SpecObjectType specObjectType) {
      AttributeValueXHTML attributeValueXhmtl = ReqIF10Factory.eINSTANCE.createAttributeValueXHTML();

      EList<AttributeDefinition> specAttributes = specObjectType.getSpecAttributes();
      for (AttributeDefinition attributeDefinition : specAttributes) {
         if (attributeDefinition instanceof AttributeDefinitionXHTML) {
            AttributeDefinitionXHTML attr = (AttributeDefinitionXHTML) attributeDefinition;
            attributeValueXhmtl.setDefinition(attr);
            break;
         }
      }
      XhtmlContent xhtmlContent = ReqIF10Factory.eINSTANCE.createXhtmlContent();
      XhtmlDivType xhmtlDivType = XhtmlFactory.eINSTANCE.createXhtmlDivType();
      xhtmlContent.setXhtml(xhmtlDivType);
      attributeValueXhmtl.setTheValue(xhtmlContent);
      return attributeValueXhmtl;
   }

   /**
    * Create String Value for Spec Object
    *
    * @param reqArtifact
    * @param specObjectType
    * @param typeTypes6
    * @return Value of the string
    */
   private AttributeValueString createAttributeValueString(final Artifact reqArtifact, final SpecObjectType specObjectType) {
      AttributeValueString attributeValueString = ReqIF10Factory.eINSTANCE.createAttributeValueString();

      EList<AttributeDefinition> specAttributes = specObjectType.getSpecAttributes();
      for (AttributeDefinition attributeDefinition : specAttributes) {
         if (attributeDefinition instanceof AttributeDefinitionString) {
            AttributeDefinitionString attr = (AttributeDefinitionString) attributeDefinition;
            attributeValueString.setDefinition(attr);
            break;
         }
      }
      attributeValueString.setTheValue(reqArtifact.getName());
      return attributeValueString;
   }

   /**
    * Create Spec Object Type
    *
    * @return spec object type
    * @throws DatatypeConfigurationException
    */
   private SpecObjectType createSpecObjectType() throws DatatypeConfigurationException {
      SpecObjectType specObjectType = ReqIF10Factory.eINSTANCE.createSpecObjectType();
      specObjectType.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("SpecObjectType"));
      specObjectType.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      specObjectType.getSpecAttributes().add(createAttributeDefinitionString());
      specObjectType.getSpecAttributes().add(createAttributeDefinitionXHTML());
      addObjectInToMap(specObjectType, "SpecObjectType");
      return specObjectType;
   }

   /**
    * create Specification Type
    *
    * @throws DatatypeConfigurationException
    */
   private SpecificationType createSpeciificationType() throws DatatypeConfigurationException {

      SpecificationType specificationType = ReqIF10Factory.eINSTANCE.createSpecificationType();
      String uniqueName = UniqueIdentifierGenerator.createUniqueNameWithID("SpecificationType");
      specificationType.setIdentifier(uniqueName);
      specificationType.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      specificationType.setLongName("Specification Type");
      specificationType.getSpecAttributes().add(createAttributeDefinitionString());
      addObjectInToMap(specificationType, "SpecificationType");
      return specificationType;
   }

   /**
    * Create Definition for the String for Spec Type
    *
    * @return attributedefinitionstring
    * @throws DatatypeConfigurationException
    */
   private AttributeDefinitionString createAttributeDefinitionString() throws DatatypeConfigurationException {
      AttributeDefinitionString attributeDefiniitonString = ReqIF10Factory.eINSTANCE.createAttributeDefinitionString();
      attributeDefiniitonString.setIdentifier(
         UniqueIdentifierGenerator.createUniqueNameWithID("AttributeDefinitionString"));
      attributeDefiniitonString.setLongName("Description");
      attributeDefiniitonString.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      attributeDefiniitonString.setType(createDataTypeString());
      return attributeDefiniitonString;
   }

   /**
    * Create Definition for Xhtml for Spec Type
    *
    * @return attributedefinitionxhtml
    * @throws DatatypeConfigurationException
    */
   private AttributeDefinitionXHTML createAttributeDefinitionXHTML() throws DatatypeConfigurationException {
      AttributeDefinitionXHTML attrDefXhmtl = ReqIF10Factory.eINSTANCE.createAttributeDefinitionXHTML();
      attrDefXhmtl.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("AttributeDefiniitonXHTML"));
      attrDefXhmtl.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      attrDefXhmtl.setLongName("Content");
      attrDefXhmtl.setType(createDataTypeXhtml());
      return attrDefXhmtl;

   }

   /**
    * Create Data type of type Xhtml
    *
    * @return identifier for Data Type xhtml
    * @throws DatatypeConfigurationException
    */
   private DatatypeDefinitionXHTML createDataTypeXhtml() throws DatatypeConfigurationException {
      DatatypeDefinitionXHTML dataTypeDefXhmtl = ReqIF10Factory.eINSTANCE.createDatatypeDefinitionXHTML();
      dataTypeDefXhmtl.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("DataTypeDefinitionXhtml"));
      dataTypeDefXhmtl.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      addObjectInToMap(dataTypeDefXhmtl, "DataTypeDefinitionXhtml");
      return dataTypeDefXhmtl;
   }

   /**
    * Creates data type for String
    *
    * @return identifier for Data Type String
    * @throws DatatypeConfigurationException
    */
   private DatatypeDefinitionString createDataTypeString() throws DatatypeConfigurationException {
      DatatypeDefinitionString dataTypeString = ReqIF10Factory.eINSTANCE.createDatatypeDefinitionString();
      dataTypeString.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("DataTypeDefinitionString"));
      dataTypeString.setLastChange(ReqIFUtil.getGregorianCalendarNow());
      dataTypeString.setMaxLength(new BigInteger("320000"));
      addObjectInToMap(dataTypeString, "DataTypeDefinitionString");
      return dataTypeString;
   }

   /**
    * Creates the header for the Reqif file
    *
    * @return header
    * @throws DatatypeConfigurationException
    */
   private ReqIFHeader createReqIFHeader() throws DatatypeConfigurationException {
      ReqIFHeader header = ReqIF10Factory.eINSTANCE.createReqIFHeader();
      header.setComment("Created by :" + System.getProperty("user.name"));
      header.setIdentifier(UniqueIdentifierGenerator.createUniqueNameWithID("Header"));
      header.setRepositoryId("Repository Id");
      header.setReqIFVersion("1.0.1");
      header.setSourceToolId("ICTEAM");
      header.setReqIFToolId("ICTEAM");
      header.setCreationTime(ReqIFUtil.getGregorianCalendarNow());
      header.setTitle("ReqIF File generated by ICTEAM");
      return header;
   }

   /**
    * Adds the Object to the global map
    *
    * @param obj : value
    * @param string : key
    */
   public void addObjectInToMap(final Object obj, final String string) {
      List<Object> list = this.typesMap.get(string);
      if (list == null) {
         list = new ArrayList<Object>();
         list.add(obj);
         this.typesMap.put(string, list);
      } else {
         list.add(obj);
      }
   }
}