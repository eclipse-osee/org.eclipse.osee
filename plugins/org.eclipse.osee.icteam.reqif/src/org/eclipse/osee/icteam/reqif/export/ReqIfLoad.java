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
 *     Boeing - update for RMF 0.13, use CoreReationTypes
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.icteam.reqif.util.ModuleUtil;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.DatatypeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.DatatypeDefinitionString;
import org.eclipse.rmf.reqif10.DatatypeDefinitionXHTML;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecRelation;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;
import org.eclipse.rmf.reqif10.XhtmlContent;
import org.eclipse.rmf.reqif10.xhtml.XhtmlDivType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlInlPresType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlObjectType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlPType;
import org.xml.sax.SAXException;;

/**
 * Load ReqIf file and create Artifacts in OSEE
 *
 * @author Manjunath Sangappa
 */
public class ReqIfLoad {

   Branch projectBranch = null;

   boolean lineBreak = false;

   StringBuilder stringbuilder = new StringBuilder();

   HashMap<SpecObject, Artifact> requirmentMap = new HashMap<SpecObject, Artifact>();

   /**
    * Load supplied *.reqif file
    *
    * @param moduleArtifact
    * @param fileName
    * @param moduleName
    * @throws Exception
    */
   public void load(final Artifact parentArtifact, final String fileName, final String moduleName) throws Exception {
      File file = null;
      UniqueIdentifierGenerator.resetNumber();
      if (fileName.endsWith(".xml")) {
         file = ReqIFUtil.changeExtension(fileName);
      } else {
         file = new File(fileName);
      }
      Artifact moduleArtifact = createModuleForReqif(parentArtifact, moduleName);
      Object reqIfObj = RMFUtility.LoadReqif(file);

      if (fileName.endsWith(".xml")) {
         file.delete();
      }

      if (reqIfObj instanceof ReqIF) {
         ReqIF reqif = (ReqIF) reqIfObj;

         ReqIFContent coreContent = reqif.getCoreContent();

         EList<Specification> specifications = coreContent.getSpecifications();
         for (Specification specification : specifications) {
            Artifact reqFolder = createReqFolder(specification, moduleArtifact);
            EList<SpecHierarchy> children = specification.getChildren();
            createSpecObject(children, reqFolder);
         }

         EList<SpecRelation> specRelations = coreContent.getSpecRelations();
         for (SpecRelation specRelation : specRelations) {
            createRelation(specRelation);
         }

         EList<DatatypeDefinition> datatypes = coreContent.getDatatypes();
         for (DatatypeDefinition datatypeDefinition : datatypes) {
            createDataType(datatypeDefinition, moduleArtifact);
         }

         EList<SpecType> specTypes = coreContent.getSpecTypes();
         for (SpecType specType : specTypes) {
            createSpecType(specType, moduleArtifact);
         }
      }

   }

   /**
    * Creation SpecType Artifact in Osee and link to Requirement.
    *
    * @param specType
    * @param moduleArtifact
    */
   private void createSpecType(SpecType specType, Artifact moduleArtifact) {
      String identifier = "";
      String longName = "";
      GregorianCalendar lastChange = null;

      if (specType instanceof SpecificationType) {
         lastChange = specType.getLastChange();
         identifier = specType.getIdentifier();
         longName = specType.getLongName();
      }

      SkynetTransaction transaction = TransactionManager.createTransaction(this.projectBranch,
         String.format("Created new %s \"%s\" in artifact explorer", "Requirement", "DataType"));
      Artifact newChild = moduleArtifact.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.SpecificationType,
         "Specfication Type");
      newChild.addAttribute(AtsAttributeTypes.Identifier, identifier);
      newChild.addAttribute(AtsAttributeTypes.LongName, longName);
      if (lastChange != null) {
         newChild.addAttribute(AtsAttributeTypes.LastChange, lastChange.toString());
      }

      moduleArtifact.persist(transaction);
      transaction.execute();

   }

   /**
    * Create DataType artifact in osee and link to Requirement.
    *
    * @param datatypeDefinition
    * @param moduleArtifact
    */
   private void createDataType(DatatypeDefinition datatypeDefinition, final Artifact moduleArtifact) {

      String identifier = "";
      BigInteger maxLength = null;
      GregorianCalendar lastChange = null;
      if (datatypeDefinition instanceof DatatypeDefinitionString) {
         lastChange = datatypeDefinition.getLastChange();
         maxLength = ((DatatypeDefinitionString) datatypeDefinition).getMaxLength();
         identifier = datatypeDefinition.getIdentifier();
         createDataTypeDefinitionString(moduleArtifact, lastChange, identifier, maxLength);
      }

      if (datatypeDefinition instanceof DatatypeDefinitionEnumeration) {
         EList<EnumValue> specifiedValues = ((DatatypeDefinitionEnumeration) datatypeDefinition).getSpecifiedValues();
         identifier = datatypeDefinition.getIdentifier();
         lastChange = datatypeDefinition.getLastChange();

         SkynetTransaction transaction = TransactionManager.createTransaction(this.projectBranch,
            String.format("Created new %s \"%s\" in artifact explorer", "Requirement", "DataType"));

         Artifact newChild =
            moduleArtifact.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.DataTypeDefinitionEnum, "DataType");
         Artifact specfiedValues =
            newChild.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.SpecifiedValues, "Specified Values");

         for (EnumValue enumValue : specifiedValues) {
            identifier = enumValue.getIdentifier();
            Artifact enumValues =
               specfiedValues.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.EnumValues, "Enums Values");
            enumValues.addAttribute(AtsAttributeTypes.Identifier, identifier);
         }

         moduleArtifact.persist(transaction);
         transaction.execute();
      }

      if (datatypeDefinition instanceof DatatypeDefinitionXHTML) {
         identifier = datatypeDefinition.getIdentifier();
         lastChange = datatypeDefinition.getLastChange();
         createDataTypeDefinitionString(moduleArtifact, lastChange, identifier, maxLength);
      }

   }

   /**
    * @param moduleArtifact
    * @param lastChange
    * @param identifier
    * @param maxLength
    */
   private void createDataTypeDefinitionString(Artifact moduleArtifact, GregorianCalendar lastChange, String identifier, BigInteger maxLength) {
      SkynetTransaction transaction = TransactionManager.createTransaction(this.projectBranch,
         String.format("Created new %s \"%s\" in artifact explorer", "Requirement", "DataType"));
      Artifact newChild =
         moduleArtifact.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.DataTypeDefinitionString, "DataType");

      newChild.addAttribute(AtsAttributeTypes.Identifier, identifier);
      if (maxLength != null) {
         newChild.addAttribute(AtsAttributeTypes.MaxLength, maxLength.toString());
      }
      if (lastChange != null) {
         newChild.addAttribute(AtsAttributeTypes.LastChange, lastChange.toString());
      }

      moduleArtifact.persist(transaction);
      transaction.execute();
   }

   /**
    * Create Relation between SpecObjects
    *
    * @param specRelation
    * @throws OseeCoreException
    */
   private void createRelation(SpecRelation specRelation) throws OseeCoreException {
      SpecObject source = specRelation.getSource();
      Artifact srcArtifact = requirmentMap.get(source);
      SpecObject target = specRelation.getTarget();
      Artifact destArtifact = requirmentMap.get(target);
      srcArtifact.addRelation(CoreRelationTypes.RequirementTrace_HigherLevelRequirement, destArtifact);
      srcArtifact.persist("Relation added");
   }

   /**
    * Create Spec Objects and add them to Requirement
    *
    * @param children2
    * @param reqFolder
    * @throws OseeCoreException
    * @throws IOException
    * @throws SAXException
    * @throws ParserConfigurationException
    */
   private void createSpecObject(final EList<SpecHierarchy> children, final Artifact reqFolder) throws OseeCoreException, ParserConfigurationException, SAXException, IOException {

      for (SpecHierarchy specHierarchy : children) {
         SpecObject object = specHierarchy.getObject();
         Artifact req = null;
         if (object != null) {
            req = createRequirement(object, reqFolder);
         }
         if (specHierarchy.getChildren().size() > 0) {
            createSpecObject(specHierarchy.getChildren(), req);
         }
      }
   }

   /**
    * Create Requirement for Spec Objects
    *
    * @param object
    * @param reqFolder
    * @throws OseeCoreException
    */
   private Artifact createRequirement(final SpecObject object, final Artifact reqFolder) throws OseeCoreException {

      String theValue = "";
      EList<AttributeValue> values = object.getValues();
      for (AttributeValue attributeValue : values) {
         if (attributeValue instanceof AttributeValueString) {
            AttributeValueString value = (AttributeValueString) attributeValue;
            theValue = value.getTheValue();
            if (theValue.length() != 0) {
               break;
            }
         }
      }

      if (theValue.length() == 0) {
         theValue = UniqueIdentifierGenerator.createUniqueName("Requirement");
      }

      SkynetTransaction transaction = TransactionManager.createTransaction(this.projectBranch,
         String.format("Created new %s \"%s\" in artifact explorer", "Requirement", theValue));
      Artifact newChild = reqFolder.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.Software_ReQ, theValue);

      for (AttributeValue attributeValue : values) {
         if (attributeValue instanceof AttributeValueXHTML) {
            AttributeValueXHTML value = (AttributeValueXHTML) attributeValue;
            XhtmlContent theValue2 = value.getTheValue();
            createXhmtlContent(theValue2, newChild);
         }
      }

      reqFolder.persist(transaction);
      transaction.execute();

      requirmentMap.put(object, newChild);
      return newChild;

   }

   /**
    * Create XHtml content and add it to WordTemplateContent
    *
    * @param xhmtContent
    * @param newChild
    * @throws OseeCoreException
    */
   private void createXhmtlContent(final XhtmlContent xhmtContent, final Artifact newChild) throws OseeCoreException {
      if (xhmtContent.getXhtml() != null) {
         XhtmlDivType div = (XhtmlDivType) xhmtContent.getXhtml();

         FeatureMap mixed = div.getMixed();

         parseXhtmlPtype(mixed);

         parseXhtmlObject(mixed);

         List<Attribute<?>> attributes = newChild.getAttributes();
         mixed.get(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT, true);

         Attribute wordTemplateContent = null;
         for (Attribute<?> attribute : attributes) {
            if (attribute.getAttributeType().getName().equals(CoreAttributeTypes.WordTemplateContent)) {
               wordTemplateContent = attribute;
            }
         }
         if (wordTemplateContent == null) {
            newChild.addAttribute(CoreAttributeTypes.WordTemplateContent);
         }
         newChild.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent, this.stringbuilder.toString());
         this.stringbuilder.delete(0, this.stringbuilder.length());
      }

   }

   /**
    * Parse XHTML Ptype from SpecObject
    *
    * @param mixed
    */
   private void parseXhtmlObject(final FeatureMap mixed) {
      List<WordMLContent> listWordMLContent = new ArrayList<WordMLContent>();
      for (Entry entry : mixed) {
         if (entry.getValue() instanceof XhtmlObjectType) {
            XhtmlObjectType div1 = (XhtmlObjectType) entry.getValue();
            FeatureMap mixed2 = div1.getMixed();
            if (mixed2.size() == 0) {
               WordMLContent con = new WordMLContent();
               con.setInputString("");
               con.setBold(false);
               con.setItalic(false);
               listWordMLContent.add(con);
            } else {

               for (Entry entry2 : mixed2) {
                  WordMLContent content = new WordMLContent();
                  parseXhtmlPSubType(entry2, content);
                  listWordMLContent.add(content);
               }
            }
            WordMLCreator wordml = new WordMLCreator();
            StringBuilder wordML = wordml.createWordML(listWordMLContent);
            this.stringbuilder.append(wordML);
            listWordMLContent.clear();
         }
      }

   }

   /**
    * Parse XHTML Ptype from SpecObject
    *
    * @param mixed
    */
   private void parseXhtmlPtype(final FeatureMap mixed) {
      List<WordMLContent> listWordMLContent = new ArrayList<WordMLContent>();
      for (Entry entry : mixed) {
         if (entry.getValue() instanceof XhtmlPType) {
            XhtmlPType div1 = (XhtmlPType) entry.getValue();
            FeatureMap mixed2 = div1.getMixed();
            if (mixed2.size() == 0) {
               WordMLContent con = new WordMLContent();
               con.setInputString("");
               con.setBold(false);
               con.setItalic(false);
               listWordMLContent.add(con);
            } else {

               for (Entry entry2 : mixed2) {
                  WordMLContent content = new WordMLContent();
                  parseXhtmlPSubType(entry2, content);
                  listWordMLContent.add(content);
               }
            }
            WordMLCreator wordml = new WordMLCreator();
            StringBuilder wordML = wordml.createWordML(listWordMLContent);
            this.stringbuilder.append(wordML);
            listWordMLContent.clear();
         }
      }

   }

   private void parseXhtmlPSubType(Object object, WordMLContent content) {

      if (object instanceof Entry) {
         Entry entry = (Entry) object;
         if (entry.getValue() instanceof XhtmlInlPresType) {
            XhtmlInlPresType i = (XhtmlInlPresType) entry.getValue();
            getBoldItalic(i.eContainer(), content);
            FeatureMap mixed = i.getMixed();
            for (Entry entry2 : mixed) {
               parseXhtmlPSubType(entry2, content);
            }
         } else if (entry.getEStructuralFeature() instanceof EAttribute) {
            content.setInputString((String) entry.getValue());
         }
      }
   }

   /**
    * @param eContainer
    * @param content
    */
   private void getBoldItalic(final EObject eContainer, final WordMLContent content) {
      if (eContainer instanceof XhtmlDivType) {
         XhtmlDivType div = (XhtmlDivType) eContainer;
         EList<XhtmlInlPresType> boldList = div.getB();
         if (boldList.size() > 0) {
            content.setBold(true);
         }
         EList<XhtmlInlPresType> italicList = div.getI();
         if (italicList.size() > 0) {
            content.setItalic(true);
         }
      } else if (eContainer instanceof XhtmlInlPresType) {
         XhtmlInlPresType type = (XhtmlInlPresType) eContainer;
         EList<XhtmlInlPresType> boldList = type.getB();
         if (boldList.size() > 0) {
            content.setBold(true);
         }
         EList<XhtmlInlPresType> italicList = type.getI();
         if (italicList.size() > 0) {
            content.setItalic(true);
         }
      } else if (eContainer instanceof XhtmlPType) {
         XhtmlPType ptype = (XhtmlPType) eContainer;
         EList<XhtmlInlPresType> boldList = ptype.getB();
         if (boldList.size() > 0) {
            content.setBold(true);
         }
         EList<XhtmlInlPresType> italicList = ptype.getI();
         if (italicList.size() > 0) {
            content.setItalic(true);
         }
      }

   }

   /**
    * Create a module for ReqIf
    *
    * @param parentArtifact
    * @param moduleName
    * @throws OseeCoreException
    */
   private Artifact createModuleForReqif(final Artifact parentArtifact, final String moduleName) throws OseeCoreException {

      Artifact moduleArtifact = null;

      try {
         this.projectBranch = BranchManager.getBranch(parentArtifact.getBranch());
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      List<Artifact> children = parentArtifact.getChildren();
      for (Artifact artifact : children) {
         if (artifact.getArtifactType().equals(AtsArtifactTypes.RequirementDocument)) {
            if (artifact.getName().equals(moduleName)) {
               moduleArtifact = artifact;
               break;
            }

         }
      }
      if (moduleArtifact == null) {
         moduleArtifact = ReqIFUtil.createModuleReqFolder(this.projectBranch, parentArtifact, moduleName);
      }

      return moduleArtifact;
   }

   /**
    * Create a ReqFolder Artifact if it doesn't exist.
    *
    * @param specification
    * @param moduleArtifact
    * @throws OseeCoreException
    */
   private Artifact createReqFolder(final Specification specification, final Artifact moduleArtifact) throws OseeCoreException {

      Artifact reqParent = null;

      String longName = specification.getLongName();
      if ((longName == null) || (longName.length() == 0)) {
         String uniqueName = UniqueIdentifierGenerator.createUniqueNameWithID("Requirement_Folder");
         longName = uniqueName;
      }

      ModuleUtil.addRequirementChildForModule(moduleArtifact, this.projectBranch, longName, "");

      List<Artifact> children2 = moduleArtifact.getChildren();
      for (Artifact artifact : children2) {
         if (artifact.getName().equals(longName)) {
            reqParent = artifact;
         }
      }
      return reqParent;
   }
}