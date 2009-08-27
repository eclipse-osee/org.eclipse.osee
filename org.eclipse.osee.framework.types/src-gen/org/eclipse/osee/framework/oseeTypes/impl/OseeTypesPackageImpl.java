/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.AttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.Import;
import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum;
import org.eclipse.osee.framework.oseeTypes.RelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OseeTypesPackageImpl extends EPackageImpl implements OseeTypesPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeTypeModelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass importEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass artifactTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeTypeRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeEnumTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeEnumEntryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass relationTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum relationMultiplicityEnumEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private OseeTypesPackageImpl()
  {
    super(eNS_URI, OseeTypesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link OseeTypesPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static OseeTypesPackage init()
  {
    if (isInited) return (OseeTypesPackage)EPackage.Registry.INSTANCE.getEPackage(OseeTypesPackage.eNS_URI);

    // Obtain or create and register package
    OseeTypesPackageImpl theOseeTypesPackage = (OseeTypesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof OseeTypesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new OseeTypesPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theOseeTypesPackage.createPackageContents();

    // Initialize created meta-data
    theOseeTypesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theOseeTypesPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(OseeTypesPackage.eNS_URI, theOseeTypesPackage);
    return theOseeTypesPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeTypeModel()
  {
    return oseeTypeModelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeTypeModel_Imports()
  {
    return (EReference)oseeTypeModelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeTypeModel_Types()
  {
    return (EReference)oseeTypeModelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getImport()
  {
    return importEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getImport_ImportURI()
  {
    return (EAttribute)importEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeType()
  {
    return oseeTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOseeType_Name()
  {
    return (EAttribute)oseeTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getArtifactType()
  {
    return artifactTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getArtifactType_SuperArtifactType()
  {
    return (EReference)artifactTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getArtifactType_ValidAttributeTypes()
  {
    return (EReference)artifactTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeTypeRef()
  {
    return attributeTypeRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeTypeRef_ValidAttributeType()
  {
    return (EReference)attributeTypeRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeType()
  {
    return attributeTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_BaseAttributeType()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_DataProvider()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_Min()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_Max()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_TaggerId()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeType_EnumType()
  {
    return (EReference)attributeTypeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_Description()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_DefaultValue()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeType_FileExtension()
  {
    return (EAttribute)attributeTypeEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeEnumType()
  {
    return oseeEnumTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeEnumType_EnumEntries()
  {
    return (EReference)oseeEnumTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeEnumEntry()
  {
    return oseeEnumEntryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOseeEnumEntry_Name()
  {
    return (EAttribute)oseeEnumEntryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOseeEnumEntry_Ordinal()
  {
    return (EAttribute)oseeEnumEntryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRelationType()
  {
    return relationTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationType_SideAName()
  {
    return (EAttribute)relationTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationType_SideAArtifactType()
  {
    return (EReference)relationTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationType_SideBName()
  {
    return (EAttribute)relationTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationType_SideBArtifactType()
  {
    return (EReference)relationTypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationType_DefaultOrderType()
  {
    return (EAttribute)relationTypeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationType_Multiplicity()
  {
    return (EAttribute)relationTypeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getRelationMultiplicityEnum()
  {
    return relationMultiplicityEnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeTypesFactory getOseeTypesFactory()
  {
    return (OseeTypesFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    oseeTypeModelEClass = createEClass(OSEE_TYPE_MODEL);
    createEReference(oseeTypeModelEClass, OSEE_TYPE_MODEL__IMPORTS);
    createEReference(oseeTypeModelEClass, OSEE_TYPE_MODEL__TYPES);

    importEClass = createEClass(IMPORT);
    createEAttribute(importEClass, IMPORT__IMPORT_URI);

    oseeTypeEClass = createEClass(OSEE_TYPE);
    createEAttribute(oseeTypeEClass, OSEE_TYPE__NAME);

    artifactTypeEClass = createEClass(ARTIFACT_TYPE);
    createEReference(artifactTypeEClass, ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE);
    createEReference(artifactTypeEClass, ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES);

    attributeTypeRefEClass = createEClass(ATTRIBUTE_TYPE_REF);
    createEReference(attributeTypeRefEClass, ATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE);

    attributeTypeEClass = createEClass(ATTRIBUTE_TYPE);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__DATA_PROVIDER);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__MIN);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__MAX);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__TAGGER_ID);
    createEReference(attributeTypeEClass, ATTRIBUTE_TYPE__ENUM_TYPE);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__DESCRIPTION);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__DEFAULT_VALUE);
    createEAttribute(attributeTypeEClass, ATTRIBUTE_TYPE__FILE_EXTENSION);

    oseeEnumTypeEClass = createEClass(OSEE_ENUM_TYPE);
    createEReference(oseeEnumTypeEClass, OSEE_ENUM_TYPE__ENUM_ENTRIES);

    oseeEnumEntryEClass = createEClass(OSEE_ENUM_ENTRY);
    createEAttribute(oseeEnumEntryEClass, OSEE_ENUM_ENTRY__NAME);
    createEAttribute(oseeEnumEntryEClass, OSEE_ENUM_ENTRY__ORDINAL);

    relationTypeEClass = createEClass(RELATION_TYPE);
    createEAttribute(relationTypeEClass, RELATION_TYPE__SIDE_ANAME);
    createEReference(relationTypeEClass, RELATION_TYPE__SIDE_AARTIFACT_TYPE);
    createEAttribute(relationTypeEClass, RELATION_TYPE__SIDE_BNAME);
    createEReference(relationTypeEClass, RELATION_TYPE__SIDE_BARTIFACT_TYPE);
    createEAttribute(relationTypeEClass, RELATION_TYPE__DEFAULT_ORDER_TYPE);
    createEAttribute(relationTypeEClass, RELATION_TYPE__MULTIPLICITY);

    // Create enums
    relationMultiplicityEnumEEnum = createEEnum(RELATION_MULTIPLICITY_ENUM);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    artifactTypeEClass.getESuperTypes().add(this.getOseeType());
    attributeTypeEClass.getESuperTypes().add(this.getOseeType());
    oseeEnumTypeEClass.getESuperTypes().add(this.getOseeType());
    relationTypeEClass.getESuperTypes().add(this.getOseeType());

    // Initialize classes and features; add operations and parameters
    initEClass(oseeTypeModelEClass, OseeTypeModel.class, "OseeTypeModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOseeTypeModel_Imports(), this.getImport(), null, "imports", null, 0, -1, OseeTypeModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeTypeModel_Types(), this.getOseeType(), null, "types", null, 0, -1, OseeTypeModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImport_ImportURI(), ecorePackage.getEString(), "importURI", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(oseeTypeEClass, OseeType.class, "OseeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getOseeType_Name(), ecorePackage.getEString(), "name", null, 0, 1, OseeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(artifactTypeEClass, ArtifactType.class, "ArtifactType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getArtifactType_SuperArtifactType(), this.getArtifactType(), null, "superArtifactType", null, 0, 1, ArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getArtifactType_ValidAttributeTypes(), this.getAttributeTypeRef(), null, "validAttributeTypes", null, 0, -1, ArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeTypeRefEClass, AttributeTypeRef.class, "AttributeTypeRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeTypeRef_ValidAttributeType(), this.getAttributeType(), null, "validAttributeType", null, 0, 1, AttributeTypeRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeTypeEClass, AttributeType.class, "AttributeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttributeType_BaseAttributeType(), ecorePackage.getEString(), "baseAttributeType", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_DataProvider(), ecorePackage.getEString(), "dataProvider", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_Min(), ecorePackage.getEString(), "min", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_Max(), ecorePackage.getEString(), "max", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_TaggerId(), ecorePackage.getEString(), "taggerId", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeType_EnumType(), this.getOseeEnumType(), null, "enumType", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_Description(), ecorePackage.getEString(), "description", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeType_FileExtension(), ecorePackage.getEString(), "fileExtension", null, 0, 1, AttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(oseeEnumTypeEClass, OseeEnumType.class, "OseeEnumType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOseeEnumType_EnumEntries(), this.getOseeEnumEntry(), null, "enumEntries", null, 0, -1, OseeEnumType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(oseeEnumEntryEClass, OseeEnumEntry.class, "OseeEnumEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getOseeEnumEntry_Name(), ecorePackage.getEString(), "name", null, 0, 1, OseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOseeEnumEntry_Ordinal(), ecorePackage.getEString(), "ordinal", null, 0, 1, OseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(relationTypeEClass, RelationType.class, "RelationType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRelationType_SideAName(), ecorePackage.getEString(), "sideAName", null, 0, 1, RelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRelationType_SideAArtifactType(), this.getArtifactType(), null, "sideAArtifactType", null, 0, 1, RelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelationType_SideBName(), ecorePackage.getEString(), "sideBName", null, 0, 1, RelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRelationType_SideBArtifactType(), this.getArtifactType(), null, "sideBArtifactType", null, 0, 1, RelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelationType_DefaultOrderType(), ecorePackage.getEString(), "defaultOrderType", null, 0, 1, RelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelationType_Multiplicity(), this.getRelationMultiplicityEnum(), "multiplicity", null, 0, 1, RelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.class, "RelationMultiplicityEnum");
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.ONE_TO_MANY);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.MANY_TO_MANY);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.MANY_TO_ONE);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.ONE_TO_ONE);

    // Create resource
    createResource(eNS_URI);
  }

} //OseeTypesPackageImpl
