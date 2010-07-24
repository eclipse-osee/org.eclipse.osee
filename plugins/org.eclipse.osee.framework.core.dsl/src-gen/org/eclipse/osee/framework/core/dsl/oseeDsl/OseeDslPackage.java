/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

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
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory
 * @model kind="package"
 * @generated
 */
public interface OseeDslPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "oseeDsl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/osee/framework/core/dsl/OseeDsl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "oseeDsl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  OseeDslPackage eINSTANCE = org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl <em>Osee Dsl</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOseeDsl()
   * @generated
   */
  int OSEE_DSL = 0;

  /**
   * The feature id for the '<em><b>Imports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__IMPORTS = 0;

  /**
   * The feature id for the '<em><b>Artifact Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ARTIFACT_TYPES = 1;

  /**
   * The feature id for the '<em><b>Relation Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__RELATION_TYPES = 2;

  /**
   * The feature id for the '<em><b>Attribute Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ATTRIBUTE_TYPES = 3;

  /**
   * The feature id for the '<em><b>Enum Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ENUM_TYPES = 4;

  /**
   * The feature id for the '<em><b>Enum Overrides</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ENUM_OVERRIDES = 5;

  /**
   * The feature id for the '<em><b>Branch Refs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__BRANCH_REFS = 6;

  /**
   * The feature id for the '<em><b>Artifact Refs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ARTIFACT_REFS = 7;

  /**
   * The feature id for the '<em><b>Access Declarations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ACCESS_DECLARATIONS = 8;

  /**
   * The number of structural features of the '<em>Osee Dsl</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL_FEATURE_COUNT = 9;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ImportImpl <em>Import</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ImportImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getImport()
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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeElementImpl <em>Osee Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeElementImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOseeElement()
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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeTypeImpl <em>Osee Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeTypeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOseeType()
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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl <em>XArtifact Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXArtifactType()
   * @generated
   */
  int XARTIFACT_TYPE = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Abstract</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE__ABSTRACT = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Super Artifact Types</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Valid Attribute Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES = OSEE_TYPE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>XArtifact Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeRefImpl <em>XAttribute Type Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeRefImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXAttributeTypeRef()
   * @generated
   */
  int XATTRIBUTE_TYPE_REF = 5;

  /**
   * The feature id for the '<em><b>Valid Attribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE = 0;

  /**
   * The feature id for the '<em><b>Branch Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE_REF__BRANCH_GUID = 1;

  /**
   * The number of structural features of the '<em>XAttribute Type Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE_REF_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeImpl <em>XAttribute Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXAttributeType()
   * @generated
   */
  int XATTRIBUTE_TYPE = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Base Attribute Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Override</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__OVERRIDE = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Data Provider</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__DATA_PROVIDER = OSEE_TYPE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__MIN = OSEE_TYPE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__MAX = OSEE_TYPE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Tagger Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__TAGGER_ID = OSEE_TYPE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Enum Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__ENUM_TYPE = OSEE_TYPE_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__DESCRIPTION = OSEE_TYPE_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Default Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__DEFAULT_VALUE = OSEE_TYPE_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>File Extension</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__FILE_EXTENSION = OSEE_TYPE_FEATURE_COUNT + 9;

  /**
   * The number of structural features of the '<em>XAttribute Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 10;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumTypeImpl <em>XOsee Enum Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumTypeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeEnumType()
   * @generated
   */
  int XOSEE_ENUM_TYPE = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Enum Entries</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_TYPE__ENUM_ENTRIES = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>XOsee Enum Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumEntryImpl <em>XOsee Enum Entry</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumEntryImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeEnumEntry()
   * @generated
   */
  int XOSEE_ENUM_ENTRY = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_ENTRY__NAME = 0;

  /**
   * The feature id for the '<em><b>Ordinal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_ENTRY__ORDINAL = 1;

  /**
   * The feature id for the '<em><b>Entry Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_ENTRY__ENTRY_GUID = 2;

  /**
   * The number of structural features of the '<em>XOsee Enum Entry</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_ENTRY_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl <em>XOsee Enum Override</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeEnumOverride()
   * @generated
   */
  int XOSEE_ENUM_OVERRIDE = 9;

  /**
   * The feature id for the '<em><b>Overriden Enum Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE = OSEE_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Inherit All</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_OVERRIDE__INHERIT_ALL = OSEE_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Override Options</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS = OSEE_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>XOsee Enum Override</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_OVERRIDE_FEATURE_COUNT = OSEE_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OverrideOptionImpl <em>Override Option</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OverrideOptionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOverrideOption()
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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddEnumImpl <em>Add Enum</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddEnumImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAddEnum()
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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveEnumImpl <em>Remove Enum</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveEnumImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRemoveEnum()
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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl <em>XRelation Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXRelationType()
   * @generated
   */
  int XRELATION_TYPE = 13;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__TYPE_GUID = OSEE_TYPE__TYPE_GUID;

  /**
   * The feature id for the '<em><b>Side AName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__SIDE_ANAME = OSEE_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Side AArtifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__SIDE_AARTIFACT_TYPE = OSEE_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Side BName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__SIDE_BNAME = OSEE_TYPE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Side BArtifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__SIDE_BARTIFACT_TYPE = OSEE_TYPE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Default Order Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__DEFAULT_ORDER_TYPE = OSEE_TYPE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__MULTIPLICITY = OSEE_TYPE_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>XRelation Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactRefImpl <em>XArtifact Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactRefImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXArtifactRef()
   * @generated
   */
  int XARTIFACT_REF = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_REF__NAME = 0;

  /**
   * The feature id for the '<em><b>Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_REF__GUID = 1;

  /**
   * The number of structural features of the '<em>XArtifact Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_REF_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XBranchRefImpl <em>XBranch Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XBranchRefImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXBranchRef()
   * @generated
   */
  int XBRANCH_REF = 15;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XBRANCH_REF__NAME = 0;

  /**
   * The feature id for the '<em><b>Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XBRANCH_REF__GUID = 1;

  /**
   * The number of structural features of the '<em>XBranch Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XBRANCH_REF_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl <em>Access Context</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAccessContext()
   * @generated
   */
  int ACCESS_CONTEXT = 16;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT__NAME = 0;

  /**
   * The feature id for the '<em><b>Super Access Contexts</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS = 1;

  /**
   * The feature id for the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT__TYPE_GUID = 2;

  /**
   * The feature id for the '<em><b>Access Rules</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT__ACCESS_RULES = 3;

  /**
   * The feature id for the '<em><b>Hierarchy Restrictions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS = 4;

  /**
   * The number of structural features of the '<em>Access Context</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.HierarchyRestrictionImpl <em>Hierarchy Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.HierarchyRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getHierarchyRestriction()
   * @generated
   */
  int HIERARCHY_RESTRICTION = 17;

  /**
   * The feature id for the '<em><b>Artifact</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HIERARCHY_RESTRICTION__ARTIFACT = 0;

  /**
   * The feature id for the '<em><b>Access Rules</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HIERARCHY_RESTRICTION__ACCESS_RULES = 1;

  /**
   * The number of structural features of the '<em>Hierarchy Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HIERARCHY_RESTRICTION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.PermissionRuleImpl <em>Permission Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.PermissionRuleImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getPermissionRule()
   * @generated
   */
  int PERMISSION_RULE = 18;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PERMISSION_RULE__PERMISSION = 0;

  /**
   * The feature id for the '<em><b>Object Restriction</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PERMISSION_RULE__OBJECT_RESTRICTION = 1;

  /**
   * The number of structural features of the '<em>Permission Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PERMISSION_RULE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ObjectRestrictionImpl <em>Object Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ObjectRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getObjectRestriction()
   * @generated
   */
  int OBJECT_RESTRICTION = 19;

  /**
   * The number of structural features of the '<em>Object Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OBJECT_RESTRICTION_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactInstanceRestrictionImpl <em>Artifact Instance Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactInstanceRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactInstanceRestriction()
   * @generated
   */
  int ARTIFACT_INSTANCE_RESTRICTION = 20;

  /**
   * The feature id for the '<em><b>Artifact Name</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Artifact Instance Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_INSTANCE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl <em>Artifact Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactTypeRestriction()
   * @generated
   */
  int ARTIFACT_TYPE_RESTRICTION = 21;

  /**
   * The feature id for the '<em><b>Artifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Artifact Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl <em>Relation Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeRestriction()
   * @generated
   */
  int RELATION_TYPE_RESTRICTION = 22;

  /**
   * The feature id for the '<em><b>Relation Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__RELATION_TYPE = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Restricted To</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__RESTRICTED_TO = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Relation Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl <em>Attribute Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeTypeRestriction()
   * @generated
   */
  int ATTRIBUTE_TYPE_RESTRICTION = 23;

  /**
   * The feature id for the '<em><b>Attribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Attribute Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeOfArtifactTypeRestrictionImpl <em>Attribute Type Of Artifact Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeOfArtifactTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeTypeOfArtifactTypeRestriction()
   * @generated
   */
  int ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION = 24;

  /**
   * The feature id for the '<em><b>Attribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Artifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attribute Type Of Artifact Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationMultiplicityEnum()
   * @generated
   */
  int RELATION_MULTIPLICITY_ENUM = 25;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum <em>Access Permission Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAccessPermissionEnum()
   * @generated
   */
  int ACCESS_PERMISSION_ENUM = 26;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction <em>Relation Type Side Restriction</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeSideRestriction()
   * @generated
   */
  int RELATION_TYPE_SIDE_RESTRICTION = 27;


  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl <em>Osee Dsl</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Dsl</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl
   * @generated
   */
  EClass getOseeDsl();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getImports <em>Imports</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Imports</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getImports()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_Imports();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactTypes <em>Artifact Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Artifact Types</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactTypes()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_ArtifactTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getRelationTypes <em>Relation Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Relation Types</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getRelationTypes()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_RelationTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getAttributeTypes <em>Attribute Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attribute Types</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getAttributeTypes()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_AttributeTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getEnumTypes <em>Enum Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Enum Types</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getEnumTypes()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_EnumTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getEnumOverrides <em>Enum Overrides</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Enum Overrides</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getEnumOverrides()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_EnumOverrides();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getBranchRefs <em>Branch Refs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Branch Refs</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getBranchRefs()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_BranchRefs();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactRefs <em>Artifact Refs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Artifact Refs</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactRefs()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_ArtifactRefs();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getAccessDeclarations <em>Access Declarations</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Access Declarations</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getAccessDeclarations()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_AccessDeclarations();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Import</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Import
   * @generated
   */
  EClass getImport();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Import#getImportURI <em>Import URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Import URI</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Import#getImportURI()
   * @see #getImport()
   * @generated
   */
  EAttribute getImport_ImportURI();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement <em>Osee Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Element</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement
   * @generated
   */
  EClass getOseeElement();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType <em>Osee Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Osee Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType
   * @generated
   */
  EClass getOseeType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType#getName()
   * @see #getOseeType()
   * @generated
   */
  EAttribute getOseeType_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType#getTypeGuid <em>Type Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType#getTypeGuid()
   * @see #getOseeType()
   * @generated
   */
  EAttribute getOseeType_TypeGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType <em>XArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XArtifact Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType
   * @generated
   */
  EClass getXArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#isAbstract <em>Abstract</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Abstract</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#isAbstract()
   * @see #getXArtifactType()
   * @generated
   */
  EAttribute getXArtifactType_Abstract();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#getSuperArtifactTypes <em>Super Artifact Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Super Artifact Types</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#getSuperArtifactTypes()
   * @see #getXArtifactType()
   * @generated
   */
  EReference getXArtifactType_SuperArtifactTypes();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#getValidAttributeTypes <em>Valid Attribute Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Valid Attribute Types</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#getValidAttributeTypes()
   * @see #getXArtifactType()
   * @generated
   */
  EReference getXArtifactType_ValidAttributeTypes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef <em>XAttribute Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XAttribute Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef
   * @generated
   */
  EClass getXAttributeTypeRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getValidAttributeType <em>Valid Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Valid Attribute Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getValidAttributeType()
   * @see #getXAttributeTypeRef()
   * @generated
   */
  EReference getXAttributeTypeRef_ValidAttributeType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchGuid <em>Branch Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Branch Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchGuid()
   * @see #getXAttributeTypeRef()
   * @generated
   */
  EAttribute getXAttributeTypeRef_BranchGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType <em>XAttribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XAttribute Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType
   * @generated
   */
  EClass getXAttributeType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getBaseAttributeType <em>Base Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Base Attribute Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getBaseAttributeType()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_BaseAttributeType();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getOverride <em>Override</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Override</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getOverride()
   * @see #getXAttributeType()
   * @generated
   */
  EReference getXAttributeType_Override();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDataProvider <em>Data Provider</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Data Provider</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDataProvider()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_DataProvider();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMin <em>Min</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Min</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMin()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_Min();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMax <em>Max</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Max</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMax()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_Max();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getTaggerId <em>Tagger Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Tagger Id</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getTaggerId()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_TaggerId();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getEnumType <em>Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Enum Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getEnumType()
   * @see #getXAttributeType()
   * @generated
   */
  EReference getXAttributeType_EnumType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDescription()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDefaultValue <em>Default Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default Value</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getDefaultValue()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_DefaultValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getFileExtension <em>File Extension</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>File Extension</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getFileExtension()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_FileExtension();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType <em>XOsee Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XOsee Enum Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType
   * @generated
   */
  EClass getXOseeEnumType();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType#getEnumEntries <em>Enum Entries</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Enum Entries</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType#getEnumEntries()
   * @see #getXOseeEnumType()
   * @generated
   */
  EReference getXOseeEnumType_EnumEntries();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry <em>XOsee Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XOsee Enum Entry</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry
   * @generated
   */
  EClass getXOseeEnumEntry();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getName()
   * @see #getXOseeEnumEntry()
   * @generated
   */
  EAttribute getXOseeEnumEntry_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getOrdinal <em>Ordinal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ordinal</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getOrdinal()
   * @see #getXOseeEnumEntry()
   * @generated
   */
  EAttribute getXOseeEnumEntry_Ordinal();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getEntryGuid <em>Entry Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Entry Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getEntryGuid()
   * @see #getXOseeEnumEntry()
   * @generated
   */
  EAttribute getXOseeEnumEntry_EntryGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride <em>XOsee Enum Override</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XOsee Enum Override</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride
   * @generated
   */
  EClass getXOseeEnumOverride();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverridenEnumType <em>Overriden Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Overriden Enum Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverridenEnumType()
   * @see #getXOseeEnumOverride()
   * @generated
   */
  EReference getXOseeEnumOverride_OverridenEnumType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#isInheritAll <em>Inherit All</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Inherit All</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#isInheritAll()
   * @see #getXOseeEnumOverride()
   * @generated
   */
  EAttribute getXOseeEnumOverride_InheritAll();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverrideOptions <em>Override Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Override Options</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverrideOptions()
   * @see #getXOseeEnumOverride()
   * @generated
   */
  EReference getXOseeEnumOverride_OverrideOptions();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption <em>Override Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Override Option</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption
   * @generated
   */
  EClass getOverrideOption();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum <em>Add Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Add Enum</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum
   * @generated
   */
  EClass getAddEnum();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getEnumEntry <em>Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enum Entry</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getEnumEntry()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_EnumEntry();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getOrdinal <em>Ordinal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ordinal</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getOrdinal()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_Ordinal();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getEntryGuid <em>Entry Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Entry Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getEntryGuid()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_EntryGuid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum <em>Remove Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Remove Enum</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum
   * @generated
   */
  EClass getRemoveEnum();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum#getEnumEntry <em>Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Enum Entry</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum#getEnumEntry()
   * @see #getRemoveEnum()
   * @generated
   */
  EReference getRemoveEnum_EnumEntry();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType <em>XRelation Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XRelation Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType
   * @generated
   */
  EClass getXRelationType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAName <em>Side AName</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Side AName</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAName()
   * @see #getXRelationType()
   * @generated
   */
  EAttribute getXRelationType_SideAName();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAArtifactType <em>Side AArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Side AArtifact Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAArtifactType()
   * @see #getXRelationType()
   * @generated
   */
  EReference getXRelationType_SideAArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBName <em>Side BName</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Side BName</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBName()
   * @see #getXRelationType()
   * @generated
   */
  EAttribute getXRelationType_SideBName();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBArtifactType <em>Side BArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Side BArtifact Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBArtifactType()
   * @see #getXRelationType()
   * @generated
   */
  EReference getXRelationType_SideBArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getDefaultOrderType <em>Default Order Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default Order Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getDefaultOrderType()
   * @see #getXRelationType()
   * @generated
   */
  EAttribute getXRelationType_DefaultOrderType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getMultiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Multiplicity</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getMultiplicity()
   * @see #getXRelationType()
   * @generated
   */
  EAttribute getXRelationType_Multiplicity();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef <em>XArtifact Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XArtifact Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef
   * @generated
   */
  EClass getXArtifactRef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef#getName()
   * @see #getXArtifactRef()
   * @generated
   */
  EAttribute getXArtifactRef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef#getGuid <em>Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef#getGuid()
   * @see #getXArtifactRef()
   * @generated
   */
  EAttribute getXArtifactRef_Guid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef <em>XBranch Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XBranch Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef
   * @generated
   */
  EClass getXBranchRef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getName()
   * @see #getXBranchRef()
   * @generated
   */
  EAttribute getXBranchRef_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getGuid <em>Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getGuid()
   * @see #getXBranchRef()
   * @generated
   */
  EAttribute getXBranchRef_Guid();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext <em>Access Context</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Access Context</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext
   * @generated
   */
  EClass getAccessContext();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getName()
   * @see #getAccessContext()
   * @generated
   */
  EAttribute getAccessContext_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getSuperAccessContexts <em>Super Access Contexts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Super Access Contexts</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getSuperAccessContexts()
   * @see #getAccessContext()
   * @generated
   */
  EReference getAccessContext_SuperAccessContexts();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getTypeGuid <em>Type Guid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type Guid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getTypeGuid()
   * @see #getAccessContext()
   * @generated
   */
  EAttribute getAccessContext_TypeGuid();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getAccessRules <em>Access Rules</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Access Rules</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getAccessRules()
   * @see #getAccessContext()
   * @generated
   */
  EReference getAccessContext_AccessRules();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getHierarchyRestrictions <em>Hierarchy Restrictions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Hierarchy Restrictions</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getHierarchyRestrictions()
   * @see #getAccessContext()
   * @generated
   */
  EReference getAccessContext_HierarchyRestrictions();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction <em>Hierarchy Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Hierarchy Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction
   * @generated
   */
  EClass getHierarchyRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getArtifact <em>Artifact</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getArtifact()
   * @see #getHierarchyRestriction()
   * @generated
   */
  EReference getHierarchyRestriction_Artifact();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getAccessRules <em>Access Rules</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Access Rules</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getAccessRules()
   * @see #getHierarchyRestriction()
   * @generated
   */
  EReference getHierarchyRestriction_AccessRules();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule <em>Permission Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Permission Rule</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule
   * @generated
   */
  EClass getPermissionRule();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getPermission <em>Permission</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Permission</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getPermission()
   * @see #getPermissionRule()
   * @generated
   */
  EAttribute getPermissionRule_Permission();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getObjectRestriction <em>Object Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Object Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getObjectRestriction()
   * @see #getPermissionRule()
   * @generated
   */
  EReference getPermissionRule_ObjectRestriction();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction <em>Object Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Object Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction
   * @generated
   */
  EClass getObjectRestriction();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction <em>Artifact Instance Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Artifact Instance Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction
   * @generated
   */
  EClass getArtifactInstanceRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction#getArtifactName <em>Artifact Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction#getArtifactName()
   * @see #getArtifactInstanceRestriction()
   * @generated
   */
  EReference getArtifactInstanceRestriction_ArtifactName();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction <em>Artifact Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Artifact Type Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction
   * @generated
   */
  EClass getArtifactTypeRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction#getArtifactType <em>Artifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction#getArtifactType()
   * @see #getArtifactTypeRestriction()
   * @generated
   */
  EReference getArtifactTypeRestriction_ArtifactType();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction <em>Relation Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Relation Type Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction
   * @generated
   */
  EClass getRelationTypeRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationType <em>Relation Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Relation Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationType()
   * @see #getRelationTypeRestriction()
   * @generated
   */
  EReference getRelationTypeRestriction_RelationType();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedTo <em>Restricted To</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Restricted To</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedTo()
   * @see #getRelationTypeRestriction()
   * @generated
   */
  EAttribute getRelationTypeRestriction_RestrictedTo();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction <em>Attribute Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Type Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction
   * @generated
   */
  EClass getAttributeTypeRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getAttributeType <em>Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Attribute Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getAttributeType()
   * @see #getAttributeTypeRestriction()
   * @generated
   */
  EReference getAttributeTypeRestriction_AttributeType();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction <em>Attribute Type Of Artifact Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Type Of Artifact Type Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction
   * @generated
   */
  EClass getAttributeTypeOfArtifactTypeRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getAttributeType <em>Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Attribute Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getAttributeType()
   * @see #getAttributeTypeOfArtifactTypeRestriction()
   * @generated
   */
  EReference getAttributeTypeOfArtifactTypeRestriction_AttributeType();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getArtifactType <em>Artifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getArtifactType()
   * @see #getAttributeTypeOfArtifactTypeRestriction()
   * @generated
   */
  EReference getAttributeTypeOfArtifactTypeRestriction_ArtifactType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Relation Multiplicity Enum</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum
   * @generated
   */
  EEnum getRelationMultiplicityEnum();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum <em>Access Permission Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Access Permission Enum</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @generated
   */
  EEnum getAccessPermissionEnum();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction <em>Relation Type Side Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Relation Type Side Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction
   * @generated
   */
  EEnum getRelationTypeSideRestriction();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  OseeDslFactory getOseeDslFactory();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl <em>Osee Dsl</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOseeDsl()
     * @generated
     */
    EClass OSEE_DSL = eINSTANCE.getOseeDsl();

    /**
     * The meta object literal for the '<em><b>Imports</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__IMPORTS = eINSTANCE.getOseeDsl_Imports();

    /**
     * The meta object literal for the '<em><b>Artifact Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ARTIFACT_TYPES = eINSTANCE.getOseeDsl_ArtifactTypes();

    /**
     * The meta object literal for the '<em><b>Relation Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__RELATION_TYPES = eINSTANCE.getOseeDsl_RelationTypes();

    /**
     * The meta object literal for the '<em><b>Attribute Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ATTRIBUTE_TYPES = eINSTANCE.getOseeDsl_AttributeTypes();

    /**
     * The meta object literal for the '<em><b>Enum Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ENUM_TYPES = eINSTANCE.getOseeDsl_EnumTypes();

    /**
     * The meta object literal for the '<em><b>Enum Overrides</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ENUM_OVERRIDES = eINSTANCE.getOseeDsl_EnumOverrides();

    /**
     * The meta object literal for the '<em><b>Branch Refs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__BRANCH_REFS = eINSTANCE.getOseeDsl_BranchRefs();

    /**
     * The meta object literal for the '<em><b>Artifact Refs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ARTIFACT_REFS = eINSTANCE.getOseeDsl_ArtifactRefs();

    /**
     * The meta object literal for the '<em><b>Access Declarations</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ACCESS_DECLARATIONS = eINSTANCE.getOseeDsl_AccessDeclarations();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ImportImpl <em>Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ImportImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getImport()
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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeElementImpl <em>Osee Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeElementImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOseeElement()
     * @generated
     */
    EClass OSEE_ELEMENT = eINSTANCE.getOseeElement();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeTypeImpl <em>Osee Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeTypeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOseeType()
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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl <em>XArtifact Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXArtifactType()
     * @generated
     */
    EClass XARTIFACT_TYPE = eINSTANCE.getXArtifactType();

    /**
     * The meta object literal for the '<em><b>Abstract</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XARTIFACT_TYPE__ABSTRACT = eINSTANCE.getXArtifactType_Abstract();

    /**
     * The meta object literal for the '<em><b>Super Artifact Types</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES = eINSTANCE.getXArtifactType_SuperArtifactTypes();

    /**
     * The meta object literal for the '<em><b>Valid Attribute Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES = eINSTANCE.getXArtifactType_ValidAttributeTypes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeRefImpl <em>XAttribute Type Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeRefImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXAttributeTypeRef()
     * @generated
     */
    EClass XATTRIBUTE_TYPE_REF = eINSTANCE.getXAttributeTypeRef();

    /**
     * The meta object literal for the '<em><b>Valid Attribute Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE = eINSTANCE.getXAttributeTypeRef_ValidAttributeType();

    /**
     * The meta object literal for the '<em><b>Branch Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE_REF__BRANCH_GUID = eINSTANCE.getXAttributeTypeRef_BranchGuid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeImpl <em>XAttribute Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXAttributeType()
     * @generated
     */
    EClass XATTRIBUTE_TYPE = eINSTANCE.getXAttributeType();

    /**
     * The meta object literal for the '<em><b>Base Attribute Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE = eINSTANCE.getXAttributeType_BaseAttributeType();

    /**
     * The meta object literal for the '<em><b>Override</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XATTRIBUTE_TYPE__OVERRIDE = eINSTANCE.getXAttributeType_Override();

    /**
     * The meta object literal for the '<em><b>Data Provider</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__DATA_PROVIDER = eINSTANCE.getXAttributeType_DataProvider();

    /**
     * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__MIN = eINSTANCE.getXAttributeType_Min();

    /**
     * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__MAX = eINSTANCE.getXAttributeType_Max();

    /**
     * The meta object literal for the '<em><b>Tagger Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__TAGGER_ID = eINSTANCE.getXAttributeType_TaggerId();

    /**
     * The meta object literal for the '<em><b>Enum Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XATTRIBUTE_TYPE__ENUM_TYPE = eINSTANCE.getXAttributeType_EnumType();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__DESCRIPTION = eINSTANCE.getXAttributeType_Description();

    /**
     * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__DEFAULT_VALUE = eINSTANCE.getXAttributeType_DefaultValue();

    /**
     * The meta object literal for the '<em><b>File Extension</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__FILE_EXTENSION = eINSTANCE.getXAttributeType_FileExtension();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumTypeImpl <em>XOsee Enum Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumTypeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeEnumType()
     * @generated
     */
    EClass XOSEE_ENUM_TYPE = eINSTANCE.getXOseeEnumType();

    /**
     * The meta object literal for the '<em><b>Enum Entries</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XOSEE_ENUM_TYPE__ENUM_ENTRIES = eINSTANCE.getXOseeEnumType_EnumEntries();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumEntryImpl <em>XOsee Enum Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumEntryImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeEnumEntry()
     * @generated
     */
    EClass XOSEE_ENUM_ENTRY = eINSTANCE.getXOseeEnumEntry();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XOSEE_ENUM_ENTRY__NAME = eINSTANCE.getXOseeEnumEntry_Name();

    /**
     * The meta object literal for the '<em><b>Ordinal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XOSEE_ENUM_ENTRY__ORDINAL = eINSTANCE.getXOseeEnumEntry_Ordinal();

    /**
     * The meta object literal for the '<em><b>Entry Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XOSEE_ENUM_ENTRY__ENTRY_GUID = eINSTANCE.getXOseeEnumEntry_EntryGuid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl <em>XOsee Enum Override</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeEnumOverride()
     * @generated
     */
    EClass XOSEE_ENUM_OVERRIDE = eINSTANCE.getXOseeEnumOverride();

    /**
     * The meta object literal for the '<em><b>Overriden Enum Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE = eINSTANCE.getXOseeEnumOverride_OverridenEnumType();

    /**
     * The meta object literal for the '<em><b>Inherit All</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XOSEE_ENUM_OVERRIDE__INHERIT_ALL = eINSTANCE.getXOseeEnumOverride_InheritAll();

    /**
     * The meta object literal for the '<em><b>Override Options</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS = eINSTANCE.getXOseeEnumOverride_OverrideOptions();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OverrideOptionImpl <em>Override Option</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OverrideOptionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getOverrideOption()
     * @generated
     */
    EClass OVERRIDE_OPTION = eINSTANCE.getOverrideOption();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddEnumImpl <em>Add Enum</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddEnumImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAddEnum()
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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveEnumImpl <em>Remove Enum</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveEnumImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRemoveEnum()
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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl <em>XRelation Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXRelationType()
     * @generated
     */
    EClass XRELATION_TYPE = eINSTANCE.getXRelationType();

    /**
     * The meta object literal for the '<em><b>Side AName</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XRELATION_TYPE__SIDE_ANAME = eINSTANCE.getXRelationType_SideAName();

    /**
     * The meta object literal for the '<em><b>Side AArtifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XRELATION_TYPE__SIDE_AARTIFACT_TYPE = eINSTANCE.getXRelationType_SideAArtifactType();

    /**
     * The meta object literal for the '<em><b>Side BName</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XRELATION_TYPE__SIDE_BNAME = eINSTANCE.getXRelationType_SideBName();

    /**
     * The meta object literal for the '<em><b>Side BArtifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XRELATION_TYPE__SIDE_BARTIFACT_TYPE = eINSTANCE.getXRelationType_SideBArtifactType();

    /**
     * The meta object literal for the '<em><b>Default Order Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XRELATION_TYPE__DEFAULT_ORDER_TYPE = eINSTANCE.getXRelationType_DefaultOrderType();

    /**
     * The meta object literal for the '<em><b>Multiplicity</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XRELATION_TYPE__MULTIPLICITY = eINSTANCE.getXRelationType_Multiplicity();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactRefImpl <em>XArtifact Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactRefImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXArtifactRef()
     * @generated
     */
    EClass XARTIFACT_REF = eINSTANCE.getXArtifactRef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XARTIFACT_REF__NAME = eINSTANCE.getXArtifactRef_Name();

    /**
     * The meta object literal for the '<em><b>Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XARTIFACT_REF__GUID = eINSTANCE.getXArtifactRef_Guid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XBranchRefImpl <em>XBranch Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XBranchRefImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXBranchRef()
     * @generated
     */
    EClass XBRANCH_REF = eINSTANCE.getXBranchRef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XBRANCH_REF__NAME = eINSTANCE.getXBranchRef_Name();

    /**
     * The meta object literal for the '<em><b>Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XBRANCH_REF__GUID = eINSTANCE.getXBranchRef_Guid();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl <em>Access Context</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAccessContext()
     * @generated
     */
    EClass ACCESS_CONTEXT = eINSTANCE.getAccessContext();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACCESS_CONTEXT__NAME = eINSTANCE.getAccessContext_Name();

    /**
     * The meta object literal for the '<em><b>Super Access Contexts</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS = eINSTANCE.getAccessContext_SuperAccessContexts();

    /**
     * The meta object literal for the '<em><b>Type Guid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACCESS_CONTEXT__TYPE_GUID = eINSTANCE.getAccessContext_TypeGuid();

    /**
     * The meta object literal for the '<em><b>Access Rules</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCESS_CONTEXT__ACCESS_RULES = eINSTANCE.getAccessContext_AccessRules();

    /**
     * The meta object literal for the '<em><b>Hierarchy Restrictions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS = eINSTANCE.getAccessContext_HierarchyRestrictions();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.HierarchyRestrictionImpl <em>Hierarchy Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.HierarchyRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getHierarchyRestriction()
     * @generated
     */
    EClass HIERARCHY_RESTRICTION = eINSTANCE.getHierarchyRestriction();

    /**
     * The meta object literal for the '<em><b>Artifact</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HIERARCHY_RESTRICTION__ARTIFACT = eINSTANCE.getHierarchyRestriction_Artifact();

    /**
     * The meta object literal for the '<em><b>Access Rules</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HIERARCHY_RESTRICTION__ACCESS_RULES = eINSTANCE.getHierarchyRestriction_AccessRules();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.PermissionRuleImpl <em>Permission Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.PermissionRuleImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getPermissionRule()
     * @generated
     */
    EClass PERMISSION_RULE = eINSTANCE.getPermissionRule();

    /**
     * The meta object literal for the '<em><b>Permission</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PERMISSION_RULE__PERMISSION = eINSTANCE.getPermissionRule_Permission();

    /**
     * The meta object literal for the '<em><b>Object Restriction</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PERMISSION_RULE__OBJECT_RESTRICTION = eINSTANCE.getPermissionRule_ObjectRestriction();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ObjectRestrictionImpl <em>Object Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ObjectRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getObjectRestriction()
     * @generated
     */
    EClass OBJECT_RESTRICTION = eINSTANCE.getObjectRestriction();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactInstanceRestrictionImpl <em>Artifact Instance Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactInstanceRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactInstanceRestriction()
     * @generated
     */
    EClass ARTIFACT_INSTANCE_RESTRICTION = eINSTANCE.getArtifactInstanceRestriction();

    /**
     * The meta object literal for the '<em><b>Artifact Name</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME = eINSTANCE.getArtifactInstanceRestriction_ArtifactName();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl <em>Artifact Type Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactTypeRestriction()
     * @generated
     */
    EClass ARTIFACT_TYPE_RESTRICTION = eINSTANCE.getArtifactTypeRestriction();

    /**
     * The meta object literal for the '<em><b>Artifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE = eINSTANCE.getArtifactTypeRestriction_ArtifactType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl <em>Relation Type Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeRestriction()
     * @generated
     */
    EClass RELATION_TYPE_RESTRICTION = eINSTANCE.getRelationTypeRestriction();

    /**
     * The meta object literal for the '<em><b>Relation Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE_RESTRICTION__RELATION_TYPE = eINSTANCE.getRelationTypeRestriction_RelationType();

    /**
     * The meta object literal for the '<em><b>Restricted To</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE_RESTRICTION__RESTRICTED_TO = eINSTANCE.getRelationTypeRestriction_RestrictedTo();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl <em>Attribute Type Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeTypeRestriction()
     * @generated
     */
    EClass ATTRIBUTE_TYPE_RESTRICTION = eINSTANCE.getAttributeTypeRestriction();

    /**
     * The meta object literal for the '<em><b>Attribute Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE = eINSTANCE.getAttributeTypeRestriction_AttributeType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeOfArtifactTypeRestrictionImpl <em>Attribute Type Of Artifact Type Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeOfArtifactTypeRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeTypeOfArtifactTypeRestriction()
     * @generated
     */
    EClass ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION = eINSTANCE.getAttributeTypeOfArtifactTypeRestriction();

    /**
     * The meta object literal for the '<em><b>Attribute Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE = eINSTANCE.getAttributeTypeOfArtifactTypeRestriction_AttributeType();

    /**
     * The meta object literal for the '<em><b>Artifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE = eINSTANCE.getAttributeTypeOfArtifactTypeRestriction_ArtifactType();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationMultiplicityEnum()
     * @generated
     */
    EEnum RELATION_MULTIPLICITY_ENUM = eINSTANCE.getRelationMultiplicityEnum();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum <em>Access Permission Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAccessPermissionEnum()
     * @generated
     */
    EEnum ACCESS_PERMISSION_ENUM = eINSTANCE.getAccessPermissionEnum();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction <em>Relation Type Side Restriction</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeSideRestriction()
     * @generated
     */
    EEnum RELATION_TYPE_SIDE_RESTRICTION = eINSTANCE.getRelationTypeSideRestriction();

  }

} //OseeDslPackage
