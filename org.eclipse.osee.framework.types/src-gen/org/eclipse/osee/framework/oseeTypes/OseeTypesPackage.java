/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesFactory
 * @model kind="package"
 * @generated
 */
public interface OseeTypesPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "oseeTypes";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/osee/framework/OseeTypes";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "oseeTypes";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  OseeTypesPackage eINSTANCE = org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl <em>Osee Type Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeTypeModel()
   * @generated
   */
  int OSEE_TYPE_MODEL = 0;

  /**
   * The feature id for the '<em><b>Imports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL__IMPORTS = 0;

  /**
   * The feature id for the '<em><b>Artifact Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL__ARTIFACT_TYPES = 1;

  /**
   * The feature id for the '<em><b>Relation Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL__RELATION_TYPES = 2;

  /**
   * The feature id for the '<em><b>Attribute Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL__ATTRIBUTE_TYPES = 3;

  /**
   * The feature id for the '<em><b>Enum Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL__ENUM_TYPES = 4;

  /**
   * The feature id for the '<em><b>Enum Overrides</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL__ENUM_OVERRIDES = 5;

  /**
   * The number of structural features of the '<em>Osee Type Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_MODEL_FEATURE_COUNT = 6;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ImportImpl <em>Import</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.ImportImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getImport()
   * @generated
   */
  int IMPORT = 1;

  /**
   * The feature id for the '<em><b>Import URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMPORT__IMPORT_URI = 0;

  /**
   * The number of structural features of the '<em>Import</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMPORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeElementImpl <em>Osee Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeElementImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeElement()
   * @generated
   */
  int OSEE_ELEMENT = 2;

  /**
   * The number of structural features of the '<em>Osee Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ELEMENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeImpl <em>Osee Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeType()
   * @generated
   */
  int OSEE_TYPE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE__NAME = OSEE_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE__TYPE_GUID = OSEE_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Osee Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE_FEATURE_COUNT = OSEE_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl <em>Artifact Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getArtifactType()
   * @generated
   */
  int ARTIFACT_TYPE = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Abstract</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__ABSTRACT = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Super Artifact Types</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Valid Attribute Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES = OSEE_TYPE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Artifact Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeRefImpl <em>Attribute Type Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeRefImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAttributeTypeRef()
   * @generated
   */
  int ATTRIBUTE_TYPE_REF = 5;

  /**
   * The feature id for the '<em><b>Valid Attribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE = 0;

  /**
   * The feature id for the '<em><b>Branch Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_REF__BRANCH_GUID = 1;

  /**
   * The number of structural features of the '<em>Attribute Type Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_REF_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeImpl <em>Attribute Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAttributeType()
   * @generated
   */
  int ATTRIBUTE_TYPE = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Base Attribute Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Override</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__OVERRIDE = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Data Provider</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__DATA_PROVIDER = OSEE_TYPE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__MIN = OSEE_TYPE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__MAX = OSEE_TYPE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Tagger Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__TAGGER_ID = OSEE_TYPE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Enum Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__ENUM_TYPE = OSEE_TYPE_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__DESCRIPTION = OSEE_TYPE_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Default Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__DEFAULT_VALUE = OSEE_TYPE_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>File Extension</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__FILE_EXTENSION = OSEE_TYPE_FEATURE_COUNT + 9;

  /**
   * The number of structural features of the '<em>Attribute Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 10;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumTypeImpl <em>Osee Enum Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeEnumTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeEnumType()
   * @generated
   */
  int OSEE_ENUM_TYPE = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Enum Entries</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_TYPE__ENUM_ENTRIES = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Osee Enum Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl <em>Osee Enum Entry</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeEnumEntry()
   * @generated
   */
  int OSEE_ENUM_ENTRY = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_ENTRY__NAME = 0;

  /**
   * The feature id for the '<em><b>Ordinal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_ENTRY__ORDINAL = 1;

  /**
   * The feature id for the '<em><b>Entry Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_ENTRY__ENTRY_GUID = 2;

  /**
   * The number of structural features of the '<em>Osee Enum Entry</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_ENTRY_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl <em>Osee Enum Override</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeEnumOverride()
   * @generated
   */
  int OSEE_ENUM_OVERRIDE = 9;

  /**
   * The feature id for the '<em><b>Overriden Enum Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE = OSEE_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Inherit All</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_OVERRIDE__INHERIT_ALL = OSEE_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Override Options</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS = OSEE_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Osee Enum Override</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_ENUM_OVERRIDE_FEATURE_COUNT = OSEE_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OverrideOptionImpl <em>Override Option</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.OverrideOptionImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOverrideOption()
   * @generated
   */
  int OVERRIDE_OPTION = 10;

  /**
   * The number of structural features of the '<em>Override Option</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OVERRIDE_OPTION_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl <em>Add Enum</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAddEnum()
   * @generated
   */
  int ADD_ENUM = 11;

  /**
   * The feature id for the '<em><b>Enum Entry</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ENUM__ENUM_ENTRY = OVERRIDE_OPTION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Ordinal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ENUM__ORDINAL = OVERRIDE_OPTION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Entry Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ENUM__ENTRY_GUID = OVERRIDE_OPTION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Add Enum</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ENUM_FEATURE_COUNT = OVERRIDE_OPTION_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RemoveEnumImpl <em>Remove Enum</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.RemoveEnumImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRemoveEnum()
   * @generated
   */
  int REMOVE_ENUM = 12;

  /**
   * The feature id for the '<em><b>Enum Entry</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REMOVE_ENUM__ENUM_ENTRY = OVERRIDE_OPTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Remove Enum</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REMOVE_ENUM_FEATURE_COUNT = OVERRIDE_OPTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl <em>Relation Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationType()
   * @generated
   */
  int RELATION_TYPE = 13;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Side AName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__SIDE_ANAME = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Side AArtifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__SIDE_AARTIFACT_TYPE = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Side BName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__SIDE_BNAME = OSEE_TYPE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Side BArtifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__SIDE_BARTIFACT_TYPE = OSEE_TYPE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Default Order Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__DEFAULT_ORDER_TYPE = OSEE_TYPE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__MULTIPLICITY = OSEE_TYPE_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Relation Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationMultiplicityEnum()
   * @generated
   */
  int RELATION_MULTIPLICITY_ENUM = 14;


  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel <em>Osee Type Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Type Model</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel
   * @generated
   */
  EClass getOseeTypeModel();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getImports <em>Imports</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Imports</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getImports()
   * @see #getOseeTypeModel()
   * @generated
   */
  EReference getOseeTypeModel_Imports();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getArtifactTypes <em>Artifact Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Artifact Types</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getArtifactTypes()
   * @see #getOseeTypeModel()
   * @generated
   */
  EReference getOseeTypeModel_ArtifactTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getRelationTypes <em>Relation Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Relation Types</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getRelationTypes()
   * @see #getOseeTypeModel()
   * @generated
   */
  EReference getOseeTypeModel_RelationTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getAttributeTypes <em>Attribute Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attribute Types</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getAttributeTypes()
   * @see #getOseeTypeModel()
   * @generated
   */
  EReference getOseeTypeModel_AttributeTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getEnumTypes <em>Enum Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Enum Types</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getEnumTypes()
   * @see #getOseeTypeModel()
   * @generated
   */
  EReference getOseeTypeModel_EnumTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getEnumOverrides <em>Enum Overrides</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Enum Overrides</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getEnumOverrides()
   * @see #getOseeTypeModel()
   * @generated
   */
  EReference getOseeTypeModel_EnumOverrides();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Import</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Import
   * @generated
   */
  EClass getImport();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.Import#getImportURI <em>Import URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Import URI</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Import#getImportURI()
   * @see #getImport()
   * @generated
   */
  EAttribute getImport_ImportURI();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OseeElement <em>Osee Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Element</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeElement
   * @generated
   */
  EClass getOseeElement();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OseeType <em>Osee Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeType
   * @generated
   */
  EClass getOseeType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.OseeType#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeType#getName()
   * @see #getOseeType()
   * @generated
   */
  EAttribute getOseeType_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.OseeType#getTypeGuid <em>Type Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type Guid</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeType#getTypeGuid()
   * @see #getOseeType()
   * @generated
   */
  EAttribute getOseeType_TypeGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType <em>Artifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Artifact Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.ArtifactType
   * @generated
   */
  EClass getArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#isAbstract <em>Abstract</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Abstract</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.ArtifactType#isAbstract()
   * @see #getArtifactType()
   * @generated
   */
  EAttribute getArtifactType_Abstract();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperArtifactTypes <em>Super Artifact Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Super Artifact Types</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperArtifactTypes()
   * @see #getArtifactType()
   * @generated
   */
  EReference getArtifactType_SuperArtifactTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getValidAttributeTypes <em>Valid Attribute Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Valid Attribute Types</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.ArtifactType#getValidAttributeTypes()
   * @see #getArtifactType()
   * @generated
   */
  EReference getArtifactType_ValidAttributeTypes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.AttributeTypeRef <em>Attribute Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Type Ref</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeTypeRef
   * @generated
   */
  EClass getAttributeTypeRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.AttributeTypeRef#getValidAttributeType <em>Valid Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Valid Attribute Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeTypeRef#getValidAttributeType()
   * @see #getAttributeTypeRef()
   * @generated
   */
  EReference getAttributeTypeRef_ValidAttributeType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeTypeRef#getBranchGuid <em>Branch Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Branch Guid</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeTypeRef#getBranchGuid()
   * @see #getAttributeTypeRef()
   * @generated
   */
  EAttribute getAttributeTypeRef_BranchGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.AttributeType <em>Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType
   * @generated
   */
  EClass getAttributeType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getBaseAttributeType <em>Base Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Base Attribute Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getBaseAttributeType()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_BaseAttributeType();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getOverride <em>Override</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Override</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getOverride()
   * @see #getAttributeType()
   * @generated
   */
  EReference getAttributeType_Override();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getDataProvider <em>Data Provider</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Data Provider</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getDataProvider()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_DataProvider();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getMin <em>Min</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Min</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getMin()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_Min();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getMax <em>Max</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Max</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getMax()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_Max();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getTaggerId <em>Tagger Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Tagger Id</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getTaggerId()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_TaggerId();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getEnumType <em>Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Enum Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getEnumType()
   * @see #getAttributeType()
   * @generated
   */
  EReference getAttributeType_EnumType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getDescription()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getDefaultValue <em>Default Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default Value</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getDefaultValue()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_DefaultValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getFileExtension <em>File Extension</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>File Extension</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getFileExtension()
   * @see #getAttributeType()
   * @generated
   */
  EAttribute getAttributeType_FileExtension();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumType <em>Osee Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Enum Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumType
   * @generated
   */
  EClass getOseeEnumType();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumType#getEnumEntries <em>Enum Entries</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Enum Entries</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumType#getEnumEntries()
   * @see #getOseeEnumType()
   * @generated
   */
  EReference getOseeEnumType_EnumEntries();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry <em>Osee Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Enum Entry</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumEntry
   * @generated
   */
  EClass getOseeEnumEntry();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getName()
   * @see #getOseeEnumEntry()
   * @generated
   */
  EAttribute getOseeEnumEntry_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getOrdinal <em>Ordinal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ordinal</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getOrdinal()
   * @see #getOseeEnumEntry()
   * @generated
   */
  EAttribute getOseeEnumEntry_Ordinal();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getEntryGuid <em>Entry Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Entry Guid</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getEntryGuid()
   * @see #getOseeEnumEntry()
   * @generated
   */
  EAttribute getOseeEnumEntry_EntryGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride <em>Osee Enum Override</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Enum Override</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumOverride
   * @generated
   */
  EClass getOseeEnumOverride();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverridenEnumType <em>Overriden Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Overriden Enum Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverridenEnumType()
   * @see #getOseeEnumOverride()
   * @generated
   */
  EReference getOseeEnumOverride_OverridenEnumType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#isInheritAll <em>Inherit All</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Inherit All</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#isInheritAll()
   * @see #getOseeEnumOverride()
   * @generated
   */
  EAttribute getOseeEnumOverride_InheritAll();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverrideOptions <em>Override Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Override Options</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverrideOptions()
   * @see #getOseeEnumOverride()
   * @generated
   */
  EReference getOseeEnumOverride_OverrideOptions();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.OverrideOption <em>Override Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Override Option</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.OverrideOption
   * @generated
   */
  EClass getOverrideOption();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.AddEnum <em>Add Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Add Enum</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AddEnum
   * @generated
   */
  EClass getAddEnum();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getEnumEntry <em>Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enum Entry</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AddEnum#getEnumEntry()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_EnumEntry();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getOrdinal <em>Ordinal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ordinal</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AddEnum#getOrdinal()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_Ordinal();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getEntryGuid <em>Entry Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Entry Guid</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AddEnum#getEntryGuid()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_EntryGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.RemoveEnum <em>Remove Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Remove Enum</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RemoveEnum
   * @generated
   */
  EClass getRemoveEnum();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.RemoveEnum#getEnumEntry <em>Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Enum Entry</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RemoveEnum#getEnumEntry()
   * @see #getRemoveEnum()
   * @generated
   */
  EReference getRemoveEnum_EnumEntry();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.RelationType <em>Relation Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Relation Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType
   * @generated
   */
  EClass getRelationType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideAName <em>Side AName</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Side AName</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getSideAName()
   * @see #getRelationType()
   * @generated
   */
  EAttribute getRelationType_SideAName();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideAArtifactType <em>Side AArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Side AArtifact Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getSideAArtifactType()
   * @see #getRelationType()
   * @generated
   */
  EReference getRelationType_SideAArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideBName <em>Side BName</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Side BName</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getSideBName()
   * @see #getRelationType()
   * @generated
   */
  EAttribute getRelationType_SideBName();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideBArtifactType <em>Side BArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Side BArtifact Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getSideBArtifactType()
   * @see #getRelationType()
   * @generated
   */
  EReference getRelationType_SideBArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getDefaultOrderType <em>Default Order Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default Order Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getDefaultOrderType()
   * @see #getRelationType()
   * @generated
   */
  EAttribute getRelationType_DefaultOrderType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getMultiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Multiplicity</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getMultiplicity()
   * @see #getRelationType()
   * @generated
   */
  EAttribute getRelationType_Multiplicity();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Relation Multiplicity Enum</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum
   * @generated
   */
  EEnum getRelationMultiplicityEnum();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  OseeTypesFactory getOseeTypesFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl <em>Osee Type Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeTypeModel()
     * @generated
     */
    EClass OSEE_TYPE_MODEL = eINSTANCE.getOseeTypeModel();

    /**
     * The meta object literal for the '<em><b>Imports</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_TYPE_MODEL__IMPORTS = eINSTANCE.getOseeTypeModel_Imports();

    /**
     * The meta object literal for the '<em><b>Artifact Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_TYPE_MODEL__ARTIFACT_TYPES = eINSTANCE.getOseeTypeModel_ArtifactTypes();

    /**
     * The meta object literal for the '<em><b>Relation Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_TYPE_MODEL__RELATION_TYPES = eINSTANCE.getOseeTypeModel_RelationTypes();

    /**
     * The meta object literal for the '<em><b>Attribute Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_TYPE_MODEL__ATTRIBUTE_TYPES = eINSTANCE.getOseeTypeModel_AttributeTypes();

    /**
     * The meta object literal for the '<em><b>Enum Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_TYPE_MODEL__ENUM_TYPES = eINSTANCE.getOseeTypeModel_EnumTypes();

    /**
     * The meta object literal for the '<em><b>Enum Overrides</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_TYPE_MODEL__ENUM_OVERRIDES = eINSTANCE.getOseeTypeModel_EnumOverrides();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ImportImpl <em>Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.ImportImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getImport()
     * @generated
     */
    EClass IMPORT = eINSTANCE.getImport();

    /**
     * The meta object literal for the '<em><b>Import URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute IMPORT__IMPORT_URI = eINSTANCE.getImport_ImportURI();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeElementImpl <em>Osee Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeElementImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeElement()
     * @generated
     */
    EClass OSEE_ELEMENT = eINSTANCE.getOseeElement();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeImpl <em>Osee Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeType()
     * @generated
     */
    EClass OSEE_TYPE = eINSTANCE.getOseeType();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_TYPE__NAME = eINSTANCE.getOseeType_Name();

    /**
     * The meta object literal for the '<em><b>Type Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_TYPE__TYPE_GUID = eINSTANCE.getOseeType_TypeGuid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl <em>Artifact Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getArtifactType()
     * @generated
     */
    EClass ARTIFACT_TYPE = eINSTANCE.getArtifactType();

    /**
     * The meta object literal for the '<em><b>Abstract</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ARTIFACT_TYPE__ABSTRACT = eINSTANCE.getArtifactType_Abstract();

    /**
     * The meta object literal for the '<em><b>Super Artifact Types</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES = eINSTANCE.getArtifactType_SuperArtifactTypes();

    /**
     * The meta object literal for the '<em><b>Valid Attribute Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES = eINSTANCE.getArtifactType_ValidAttributeTypes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeRefImpl <em>Attribute Type Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeRefImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAttributeTypeRef()
     * @generated
     */
    EClass ATTRIBUTE_TYPE_REF = eINSTANCE.getAttributeTypeRef();

    /**
     * The meta object literal for the '<em><b>Valid Attribute Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE = eINSTANCE.getAttributeTypeRef_ValidAttributeType();

    /**
     * The meta object literal for the '<em><b>Branch Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE_REF__BRANCH_GUID = eINSTANCE.getAttributeTypeRef_BranchGuid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeImpl <em>Attribute Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAttributeType()
     * @generated
     */
    EClass ATTRIBUTE_TYPE = eINSTANCE.getAttributeType();

    /**
     * The meta object literal for the '<em><b>Base Attribute Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE = eINSTANCE.getAttributeType_BaseAttributeType();

    /**
     * The meta object literal for the '<em><b>Override</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE__OVERRIDE = eINSTANCE.getAttributeType_Override();

    /**
     * The meta object literal for the '<em><b>Data Provider</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__DATA_PROVIDER = eINSTANCE.getAttributeType_DataProvider();

    /**
     * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__MIN = eINSTANCE.getAttributeType_Min();

    /**
     * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__MAX = eINSTANCE.getAttributeType_Max();

    /**
     * The meta object literal for the '<em><b>Tagger Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__TAGGER_ID = eINSTANCE.getAttributeType_TaggerId();

    /**
     * The meta object literal for the '<em><b>Enum Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE__ENUM_TYPE = eINSTANCE.getAttributeType_EnumType();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__DESCRIPTION = eINSTANCE.getAttributeType_Description();

    /**
     * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__DEFAULT_VALUE = eINSTANCE.getAttributeType_DefaultValue();

    /**
     * The meta object literal for the '<em><b>File Extension</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_TYPE__FILE_EXTENSION = eINSTANCE.getAttributeType_FileExtension();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumTypeImpl <em>Osee Enum Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeEnumTypeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeEnumType()
     * @generated
     */
    EClass OSEE_ENUM_TYPE = eINSTANCE.getOseeEnumType();

    /**
     * The meta object literal for the '<em><b>Enum Entries</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_ENUM_TYPE__ENUM_ENTRIES = eINSTANCE.getOseeEnumType_EnumEntries();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl <em>Osee Enum Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeEnumEntry()
     * @generated
     */
    EClass OSEE_ENUM_ENTRY = eINSTANCE.getOseeEnumEntry();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_ENUM_ENTRY__NAME = eINSTANCE.getOseeEnumEntry_Name();

    /**
     * The meta object literal for the '<em><b>Ordinal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_ENUM_ENTRY__ORDINAL = eINSTANCE.getOseeEnumEntry_Ordinal();

    /**
     * The meta object literal for the '<em><b>Entry Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_ENUM_ENTRY__ENTRY_GUID = eINSTANCE.getOseeEnumEntry_EntryGuid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl <em>Osee Enum Override</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOseeEnumOverride()
     * @generated
     */
    EClass OSEE_ENUM_OVERRIDE = eINSTANCE.getOseeEnumOverride();

    /**
     * The meta object literal for the '<em><b>Overriden Enum Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE = eINSTANCE.getOseeEnumOverride_OverridenEnumType();

    /**
     * The meta object literal for the '<em><b>Inherit All</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_ENUM_OVERRIDE__INHERIT_ALL = eINSTANCE.getOseeEnumOverride_InheritAll();

    /**
     * The meta object literal for the '<em><b>Override Options</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS = eINSTANCE.getOseeEnumOverride_OverrideOptions();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.OverrideOptionImpl <em>Override Option</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.OverrideOptionImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getOverrideOption()
     * @generated
     */
    EClass OVERRIDE_OPTION = eINSTANCE.getOverrideOption();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl <em>Add Enum</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAddEnum()
     * @generated
     */
    EClass ADD_ENUM = eINSTANCE.getAddEnum();

    /**
     * The meta object literal for the '<em><b>Enum Entry</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ADD_ENUM__ENUM_ENTRY = eINSTANCE.getAddEnum_EnumEntry();

    /**
     * The meta object literal for the '<em><b>Ordinal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ADD_ENUM__ORDINAL = eINSTANCE.getAddEnum_Ordinal();

    /**
     * The meta object literal for the '<em><b>Entry Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ADD_ENUM__ENTRY_GUID = eINSTANCE.getAddEnum_EntryGuid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RemoveEnumImpl <em>Remove Enum</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.RemoveEnumImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRemoveEnum()
     * @generated
     */
    EClass REMOVE_ENUM = eINSTANCE.getRemoveEnum();

    /**
     * The meta object literal for the '<em><b>Enum Entry</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REMOVE_ENUM__ENUM_ENTRY = eINSTANCE.getRemoveEnum_EnumEntry();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl <em>Relation Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationType()
     * @generated
     */
    EClass RELATION_TYPE = eINSTANCE.getRelationType();

    /**
     * The meta object literal for the '<em><b>Side AName</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE__SIDE_ANAME = eINSTANCE.getRelationType_SideAName();

    /**
     * The meta object literal for the '<em><b>Side AArtifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE__SIDE_AARTIFACT_TYPE = eINSTANCE.getRelationType_SideAArtifactType();

    /**
     * The meta object literal for the '<em><b>Side BName</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE__SIDE_BNAME = eINSTANCE.getRelationType_SideBName();

    /**
     * The meta object literal for the '<em><b>Side BArtifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE__SIDE_BARTIFACT_TYPE = eINSTANCE.getRelationType_SideBArtifactType();

    /**
     * The meta object literal for the '<em><b>Default Order Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE__DEFAULT_ORDER_TYPE = eINSTANCE.getRelationType_DefaultOrderType();

    /**
     * The meta object literal for the '<em><b>Multiplicity</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE__MULTIPLICITY = eINSTANCE.getRelationType_Multiplicity();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationMultiplicityEnum()
     * @generated
     */
    EEnum RELATION_MULTIPLICITY_ENUM = eINSTANCE.getRelationMultiplicityEnum();

  }

} //OseeTypesPackage
