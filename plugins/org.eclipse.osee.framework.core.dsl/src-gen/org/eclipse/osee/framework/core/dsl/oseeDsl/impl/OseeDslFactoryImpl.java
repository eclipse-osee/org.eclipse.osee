/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Import;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class OseeDslFactoryImpl extends EFactoryImpl implements OseeDslFactory {
   /**
    * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OseeDslFactory init() {
      try {
         OseeDslFactory theOseeDslFactory =
            (OseeDslFactory) EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/osee/framework/core/dsl/OseeDsl");
         if (theOseeDslFactory != null) {
            return theOseeDslFactory;
         }
      } catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new OseeDslFactoryImpl();
   }

   /**
    * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public OseeDslFactoryImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EObject create(EClass eClass) {
      switch (eClass.getClassifierID()) {
         case OseeDslPackage.OSEE_DSL:
            return createOseeDsl();
         case OseeDslPackage.IMPORT:
            return createImport();
         case OseeDslPackage.OSEE_ELEMENT:
            return createOseeElement();
         case OseeDslPackage.OSEE_TYPE:
            return createOseeType();
         case OseeDslPackage.XARTIFACT_TYPE:
            return createXArtifactType();
         case OseeDslPackage.XATTRIBUTE_TYPE_REF:
            return createXAttributeTypeRef();
         case OseeDslPackage.XATTRIBUTE_TYPE:
            return createXAttributeType();
         case OseeDslPackage.XOSEE_ENUM_TYPE:
            return createXOseeEnumType();
         case OseeDslPackage.XOSEE_ENUM_ENTRY:
            return createXOseeEnumEntry();
         case OseeDslPackage.XOSEE_ENUM_OVERRIDE:
            return createXOseeEnumOverride();
         case OseeDslPackage.OVERRIDE_OPTION:
            return createOverrideOption();
         case OseeDslPackage.ADD_ENUM:
            return createAddEnum();
         case OseeDslPackage.REMOVE_ENUM:
            return createRemoveEnum();
         case OseeDslPackage.XRELATION_TYPE:
            return createXRelationType();
         case OseeDslPackage.XARTIFACT_REF:
            return createXArtifactRef();
         case OseeDslPackage.XBRANCH_REF:
            return createXBranchRef();
         case OseeDslPackage.ACCESS_CONTEXT:
            return createAccessContext();
         case OseeDslPackage.HIERARCHY_RESTRICTION:
            return createHierarchyRestriction();
         case OseeDslPackage.PERMISSION_RULE:
            return createPermissionRule();
         case OseeDslPackage.OBJECT_RESTRICTION:
            return createObjectRestriction();
         case OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION:
            return createArtifactInstanceRestriction();
         case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION:
            return createArtifactTypeRestriction();
         case OseeDslPackage.RELATION_TYPE_RESTRICTION:
            return createRelationTypeRestriction();
         case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION:
            return createAttributeTypeRestriction();
         case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION:
            return createAttributeTypeOfArtifactTypeRestriction();
         default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object createFromString(EDataType eDataType, String initialValue) {
      switch (eDataType.getClassifierID()) {
         case OseeDslPackage.RELATION_MULTIPLICITY_ENUM:
            return createRelationMultiplicityEnumFromString(eDataType, initialValue);
         case OseeDslPackage.ACCESS_PERMISSION_ENUM:
            return createAccessPermissionEnumFromString(eDataType, initialValue);
         case OseeDslPackage.RELATION_TYPE_SIDE_RESTRICTION:
            return createRelationTypeSideRestrictionFromString(eDataType, initialValue);
         default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String convertToString(EDataType eDataType, Object instanceValue) {
      switch (eDataType.getClassifierID()) {
         case OseeDslPackage.RELATION_MULTIPLICITY_ENUM:
            return convertRelationMultiplicityEnumToString(eDataType, instanceValue);
         case OseeDslPackage.ACCESS_PERMISSION_ENUM:
            return convertAccessPermissionEnumToString(eDataType, instanceValue);
         case OseeDslPackage.RELATION_TYPE_SIDE_RESTRICTION:
            return convertRelationTypeSideRestrictionToString(eDataType, instanceValue);
         default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OseeDsl createOseeDsl() {
      OseeDslImpl oseeDsl = new OseeDslImpl();
      return oseeDsl;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Import createImport() {
      ImportImpl import_ = new ImportImpl();
      return import_;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OseeElement createOseeElement() {
      OseeElementImpl oseeElement = new OseeElementImpl();
      return oseeElement;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OseeType createOseeType() {
      OseeTypeImpl oseeType = new OseeTypeImpl();
      return oseeType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XArtifactType createXArtifactType() {
      XArtifactTypeImpl xArtifactType = new XArtifactTypeImpl();
      return xArtifactType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XAttributeTypeRef createXAttributeTypeRef() {
      XAttributeTypeRefImpl xAttributeTypeRef = new XAttributeTypeRefImpl();
      return xAttributeTypeRef;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XAttributeType createXAttributeType() {
      XAttributeTypeImpl xAttributeType = new XAttributeTypeImpl();
      return xAttributeType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XOseeEnumType createXOseeEnumType() {
      XOseeEnumTypeImpl xOseeEnumType = new XOseeEnumTypeImpl();
      return xOseeEnumType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XOseeEnumEntry createXOseeEnumEntry() {
      XOseeEnumEntryImpl xOseeEnumEntry = new XOseeEnumEntryImpl();
      return xOseeEnumEntry;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XOseeEnumOverride createXOseeEnumOverride() {
      XOseeEnumOverrideImpl xOseeEnumOverride = new XOseeEnumOverrideImpl();
      return xOseeEnumOverride;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OverrideOption createOverrideOption() {
      OverrideOptionImpl overrideOption = new OverrideOptionImpl();
      return overrideOption;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public AddEnum createAddEnum() {
      AddEnumImpl addEnum = new AddEnumImpl();
      return addEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public RemoveEnum createRemoveEnum() {
      RemoveEnumImpl removeEnum = new RemoveEnumImpl();
      return removeEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XRelationType createXRelationType() {
      XRelationTypeImpl xRelationType = new XRelationTypeImpl();
      return xRelationType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XArtifactRef createXArtifactRef() {
      XArtifactRefImpl xArtifactRef = new XArtifactRefImpl();
      return xArtifactRef;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XBranchRef createXBranchRef() {
      XBranchRefImpl xBranchRef = new XBranchRefImpl();
      return xBranchRef;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public AccessContext createAccessContext() {
      AccessContextImpl accessContext = new AccessContextImpl();
      return accessContext;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public HierarchyRestriction createHierarchyRestriction() {
      HierarchyRestrictionImpl hierarchyRestriction = new HierarchyRestrictionImpl();
      return hierarchyRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public PermissionRule createPermissionRule() {
      PermissionRuleImpl permissionRule = new PermissionRuleImpl();
      return permissionRule;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public ObjectRestriction createObjectRestriction() {
      ObjectRestrictionImpl objectRestriction = new ObjectRestrictionImpl();
      return objectRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public ArtifactInstanceRestriction createArtifactInstanceRestriction() {
      ArtifactInstanceRestrictionImpl artifactInstanceRestriction = new ArtifactInstanceRestrictionImpl();
      return artifactInstanceRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public ArtifactTypeRestriction createArtifactTypeRestriction() {
      ArtifactTypeRestrictionImpl artifactTypeRestriction = new ArtifactTypeRestrictionImpl();
      return artifactTypeRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public RelationTypeRestriction createRelationTypeRestriction() {
      RelationTypeRestrictionImpl relationTypeRestriction = new RelationTypeRestrictionImpl();
      return relationTypeRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public AttributeTypeRestriction createAttributeTypeRestriction() {
      AttributeTypeRestrictionImpl attributeTypeRestriction = new AttributeTypeRestrictionImpl();
      return attributeTypeRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public AttributeTypeOfArtifactTypeRestriction createAttributeTypeOfArtifactTypeRestriction() {
      AttributeTypeOfArtifactTypeRestrictionImpl attributeTypeOfArtifactTypeRestriction =
         new AttributeTypeOfArtifactTypeRestrictionImpl();
      return attributeTypeOfArtifactTypeRestriction;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public RelationMultiplicityEnum createRelationMultiplicityEnumFromString(EDataType eDataType, String initialValue) {
      RelationMultiplicityEnum result = RelationMultiplicityEnum.get(initialValue);
      if (result == null) {
         throw new IllegalArgumentException(
            "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
      }
      return result;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public String convertRelationMultiplicityEnumToString(EDataType eDataType, Object instanceValue) {
      return instanceValue == null ? null : instanceValue.toString();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public AccessPermissionEnum createAccessPermissionEnumFromString(EDataType eDataType, String initialValue) {
      AccessPermissionEnum result = AccessPermissionEnum.get(initialValue);
      if (result == null) {
         throw new IllegalArgumentException(
            "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
      }
      return result;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public String convertAccessPermissionEnumToString(EDataType eDataType, Object instanceValue) {
      return instanceValue == null ? null : instanceValue.toString();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public RelationTypeSideRestriction createRelationTypeSideRestrictionFromString(EDataType eDataType, String initialValue) {
      RelationTypeSideRestriction result = RelationTypeSideRestriction.get(initialValue);
      if (result == null) {
         throw new IllegalArgumentException(
            "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
      }
      return result;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public String convertRelationTypeSideRestrictionToString(EDataType eDataType, Object instanceValue) {
      return instanceValue == null ? null : instanceValue.toString();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OseeDslPackage getOseeDslPackage() {
      return (OseeDslPackage) getEPackage();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @deprecated
    * @generated
    */
   @Deprecated
   public static OseeDslPackage getPackage() {
      return OseeDslPackage.eINSTANCE;
   }

} //OseeDslFactoryImpl
