/**
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
   * The feature id for the '<em><b>Artifact Type Overrides</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ARTIFACT_TYPE_OVERRIDES = 6;

  /**
   * The feature id for the '<em><b>Artifact Match Refs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ARTIFACT_MATCH_REFS = 7;

  /**
   * The feature id for the '<em><b>Access Declarations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ACCESS_DECLARATIONS = 8;

  /**
   * The feature id for the '<em><b>Role Declarations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL__ROLE_DECLARATIONS = 9;

  /**
   * The number of structural features of the '<em>Osee Dsl</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_DSL_FEATURE_COUNT = 10;

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
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OSEE_TYPE__ID = OSEE_ELEMENT_FEATURE_COUNT + 1;

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
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_TYPE__ID = OSEE_TYPE__ID;

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
   * The feature id for the '<em><b>Branch Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE_REF__BRANCH_UUID = 1;

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
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__ID = OSEE_TYPE__ID;

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
   * The feature id for the '<em><b>Media Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE__MEDIA_TYPE = OSEE_TYPE_FEATURE_COUNT + 10;

  /**
   * The number of structural features of the '<em>XAttribute Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_TYPE_FEATURE_COUNT = OSEE_TYPE_FEATURE_COUNT + 11;

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
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_TYPE__ID = OSEE_TYPE__ID;

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
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ENUM_ENTRY__DESCRIPTION = 2;

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
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ENUM__DESCRIPTION = OVERRIDE_OPTION_FEATURE_COUNT + 2;

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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeArtifactTypeOverrideImpl <em>XOsee Artifact Type Override</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeArtifactTypeOverrideImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeArtifactTypeOverride()
   * @generated
   */
  int XOSEE_ARTIFACT_TYPE_OVERRIDE = 13;

  /**
   * The feature id for the '<em><b>Overriden Artifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ARTIFACT_TYPE_OVERRIDE__OVERRIDEN_ARTIFACT_TYPE = 0;

  /**
   * The feature id for the '<em><b>Inherit All</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ARTIFACT_TYPE_OVERRIDE__INHERIT_ALL = 1;

  /**
   * The feature id for the '<em><b>Override Options</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ARTIFACT_TYPE_OVERRIDE__OVERRIDE_OPTIONS = 2;

  /**
   * The number of structural features of the '<em>XOsee Artifact Type Override</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XOSEE_ARTIFACT_TYPE_OVERRIDE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeOverrideOptionImpl <em>Attribute Override Option</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeOverrideOptionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeOverrideOption()
   * @generated
   */
  int ATTRIBUTE_OVERRIDE_OPTION = 14;

  /**
   * The number of structural features of the '<em>Attribute Override Option</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddAttributeImpl <em>Add Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddAttributeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAddAttribute()
   * @generated
   */
  int ADD_ATTRIBUTE = 15;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ATTRIBUTE__ATTRIBUTE = ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Add Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADD_ATTRIBUTE_FEATURE_COUNT = ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveAttributeImpl <em>Remove Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveAttributeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRemoveAttribute()
   * @generated
   */
  int REMOVE_ATTRIBUTE = 16;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REMOVE_ATTRIBUTE__ATTRIBUTE = ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Remove Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REMOVE_ATTRIBUTE_FEATURE_COUNT = ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UpdateAttributeImpl <em>Update Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UpdateAttributeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getUpdateAttribute()
   * @generated
   */
  int UPDATE_ATTRIBUTE = 17;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UPDATE_ATTRIBUTE__ATTRIBUTE = ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Update Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UPDATE_ATTRIBUTE_FEATURE_COUNT = ATTRIBUTE_OVERRIDE_OPTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl <em>XRelation Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXRelationType()
   * @generated
   */
  int XRELATION_TYPE = 18;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__NAME = OSEE_TYPE__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_TYPE__ID = OSEE_TYPE__ID;

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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ConditionImpl <em>Condition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ConditionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getCondition()
   * @generated
   */
  int CONDITION = 19;

  /**
   * The number of structural features of the '<em>Condition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONDITION_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.SimpleConditionImpl <em>Simple Condition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.SimpleConditionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getSimpleCondition()
   * @generated
   */
  int SIMPLE_CONDITION = 20;

  /**
   * The feature id for the '<em><b>Field</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_CONDITION__FIELD = CONDITION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_CONDITION__OP = CONDITION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Expression</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_CONDITION__EXPRESSION = CONDITION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Simple Condition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_CONDITION_FEATURE_COUNT = CONDITION_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.CompoundConditionImpl <em>Compound Condition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.CompoundConditionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getCompoundCondition()
   * @generated
   */
  int COMPOUND_CONDITION = 21;

  /**
   * The feature id for the '<em><b>Conditions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_CONDITION__CONDITIONS = CONDITION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Operators</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_CONDITION__OPERATORS = CONDITION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Compound Condition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_CONDITION_FEATURE_COUNT = CONDITION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactMatcherImpl <em>XArtifact Matcher</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactMatcherImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXArtifactMatcher()
   * @generated
   */
  int XARTIFACT_MATCHER = 22;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_MATCHER__NAME = 0;

  /**
   * The feature id for the '<em><b>Conditions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_MATCHER__CONDITIONS = 1;

  /**
   * The feature id for the '<em><b>Operators</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_MATCHER__OPERATORS = 2;

  /**
   * The number of structural features of the '<em>XArtifact Matcher</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XARTIFACT_MATCHER_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl <em>Role</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRole()
   * @generated
   */
  int ROLE = 23;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__NAME = 0;

  /**
   * The feature id for the '<em><b>Super Roles</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__SUPER_ROLES = 1;

  /**
   * The feature id for the '<em><b>Users And Groups</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__USERS_AND_GROUPS = 2;

  /**
   * The feature id for the '<em><b>Referenced Contexts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__REFERENCED_CONTEXTS = 3;

  /**
   * The number of structural features of the '<em>Role</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ReferencedContextImpl <em>Referenced Context</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ReferencedContextImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getReferencedContext()
   * @generated
   */
  int REFERENCED_CONTEXT = 24;

  /**
   * The feature id for the '<em><b>Access Context Ref</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFERENCED_CONTEXT__ACCESS_CONTEXT_REF = 0;

  /**
   * The number of structural features of the '<em>Referenced Context</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFERENCED_CONTEXT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UsersAndGroupsImpl <em>Users And Groups</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UsersAndGroupsImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getUsersAndGroups()
   * @generated
   */
  int USERS_AND_GROUPS = 25;

  /**
   * The feature id for the '<em><b>User Or Group Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USERS_AND_GROUPS__USER_OR_GROUP_ID = 0;

  /**
   * The number of structural features of the '<em>Users And Groups</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USERS_AND_GROUPS_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl <em>Access Context</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAccessContext()
   * @generated
   */
  int ACCESS_CONTEXT = 26;

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
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_CONTEXT__ID = 2;

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
  int HIERARCHY_RESTRICTION = 27;

  /**
   * The feature id for the '<em><b>Artifact Matcher Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF = 0;

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
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypePredicateImpl <em>Relation Type Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypePredicateImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypePredicate()
   * @generated
   */
  int RELATION_TYPE_PREDICATE = 30;

  /**
   * The number of structural features of the '<em>Relation Type Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_PREDICATE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactTypePredicateImpl <em>Relation Type Artifact Type Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactTypePredicateImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeArtifactTypePredicate()
   * @generated
   */
  int RELATION_TYPE_ARTIFACT_TYPE_PREDICATE = 28;

  /**
   * The feature id for the '<em><b>Artifact Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF = RELATION_TYPE_PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Relation Type Artifact Type Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_ARTIFACT_TYPE_PREDICATE_FEATURE_COUNT = RELATION_TYPE_PREDICATE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactPredicateImpl <em>Relation Type Artifact Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactPredicateImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeArtifactPredicate()
   * @generated
   */
  int RELATION_TYPE_ARTIFACT_PREDICATE = 29;

  /**
   * The feature id for the '<em><b>Artifact Matcher Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_ARTIFACT_PREDICATE__ARTIFACT_MATCHER_REF = RELATION_TYPE_PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Relation Type Artifact Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_ARTIFACT_PREDICATE_FEATURE_COUNT = RELATION_TYPE_PREDICATE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ObjectRestrictionImpl <em>Object Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ObjectRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getObjectRestriction()
   * @generated
   */
  int OBJECT_RESTRICTION = 31;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OBJECT_RESTRICTION__PERMISSION = 0;

  /**
   * The number of structural features of the '<em>Object Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OBJECT_RESTRICTION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactMatchRestrictionImpl <em>Artifact Match Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactMatchRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactMatchRestriction()
   * @generated
   */
  int ARTIFACT_MATCH_RESTRICTION = 32;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_MATCH_RESTRICTION__PERMISSION = OBJECT_RESTRICTION__PERMISSION;

  /**
   * The feature id for the '<em><b>Artifact Matcher Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_MATCH_RESTRICTION__ARTIFACT_MATCHER_REF = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Artifact Match Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_MATCH_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl <em>Artifact Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactTypeRestriction()
   * @generated
   */
  int ARTIFACT_TYPE_RESTRICTION = 33;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_RESTRICTION__PERMISSION = OBJECT_RESTRICTION__PERMISSION;

  /**
   * The feature id for the '<em><b>Artifact Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE_REF = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Artifact Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl <em>Attribute Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeTypeRestriction()
   * @generated
   */
  int ATTRIBUTE_TYPE_RESTRICTION = 34;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_RESTRICTION__PERMISSION = OBJECT_RESTRICTION__PERMISSION;

  /**
   * The feature id for the '<em><b>Attribute Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Artifact Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attribute Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl <em>Legacy Relation Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getLegacyRelationTypeRestriction()
   * @generated
   */
  int LEGACY_RELATION_TYPE_RESTRICTION = 35;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION = 0;

  /**
   * The feature id for the '<em><b>Relation Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF = 1;

  /**
   * The feature id for the '<em><b>Restricted To Side</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE = 2;

  /**
   * The feature id for the '<em><b>Artifact Matcher Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF = 3;

  /**
   * The number of structural features of the '<em>Legacy Relation Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LEGACY_RELATION_TYPE_RESTRICTION_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl <em>Relation Type Restriction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeRestriction()
   * @generated
   */
  int RELATION_TYPE_RESTRICTION = 36;

  /**
   * The feature id for the '<em><b>Permission</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__PERMISSION = OBJECT_RESTRICTION__PERMISSION;

  /**
   * The feature id for the '<em><b>Relation Type Match</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__RELATION_TYPE_MATCH = OBJECT_RESTRICTION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Relation Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF = OBJECT_RESTRICTION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Restricted To Side</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE = OBJECT_RESTRICTION_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Predicate</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION__PREDICATE = OBJECT_RESTRICTION_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Relation Type Restriction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_RESTRICTION_FEATURE_COUNT = OBJECT_RESTRICTION_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum <em>Relation Multiplicity Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationMultiplicityEnum()
   * @generated
   */
  int RELATION_MULTIPLICITY_ENUM = 37;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp <em>Compare Op</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getCompareOp()
   * @generated
   */
  int COMPARE_OP = 38;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator <em>XLogic Operator</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXLogicOperator()
   * @generated
   */
  int XLOGIC_OPERATOR = 39;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField <em>Match Field</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getMatchField()
   * @generated
   */
  int MATCH_FIELD = 40;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum <em>Access Permission Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAccessPermissionEnum()
   * @generated
   */
  int ACCESS_PERMISSION_ENUM = 41;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch <em>Relation Type Match</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeMatch()
   * @generated
   */
  int RELATION_TYPE_MATCH = 42;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum <em>XRelation Side Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXRelationSideEnum()
   * @generated
   */
  int XRELATION_SIDE_ENUM = 43;


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
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactTypeOverrides <em>Artifact Type Overrides</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Artifact Type Overrides</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactTypeOverrides()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_ArtifactTypeOverrides();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactMatchRefs <em>Artifact Match Refs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Artifact Match Refs</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactMatchRefs()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_ArtifactMatchRefs();

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
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getRoleDeclarations <em>Role Declarations</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Role Declarations</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getRoleDeclarations()
   * @see #getOseeDsl()
   * @generated
   */
  EReference getOseeDsl_RoleDeclarations();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType#getId()
   * @see #getOseeType()
   * @generated
   */
  EAttribute getOseeType_Id();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchUuid <em>Branch Uuid</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Branch Uuid</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchUuid()
   * @see #getXAttributeTypeRef()
   * @generated
   */
  EAttribute getXAttributeTypeRef_BranchUuid();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMediaType <em>Media Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Media Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType#getMediaType()
   * @see #getXAttributeType()
   * @generated
   */
  EAttribute getXAttributeType_MediaType();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry#getDescription()
   * @see #getXOseeEnumEntry()
   * @generated
   */
  EAttribute getXOseeEnumEntry_Description();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum#getDescription()
   * @see #getAddEnum()
   * @generated
   */
  EAttribute getAddEnum_Description();

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
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride <em>XOsee Artifact Type Override</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XOsee Artifact Type Override</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride
   * @generated
   */
  EClass getXOseeArtifactTypeOverride();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverridenArtifactType <em>Overriden Artifact Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Overriden Artifact Type</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverridenArtifactType()
   * @see #getXOseeArtifactTypeOverride()
   * @generated
   */
  EReference getXOseeArtifactTypeOverride_OverridenArtifactType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#isInheritAll <em>Inherit All</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Inherit All</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#isInheritAll()
   * @see #getXOseeArtifactTypeOverride()
   * @generated
   */
  EAttribute getXOseeArtifactTypeOverride_InheritAll();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverrideOptions <em>Override Options</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Override Options</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverrideOptions()
   * @see #getXOseeArtifactTypeOverride()
   * @generated
   */
  EReference getXOseeArtifactTypeOverride_OverrideOptions();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption <em>Attribute Override Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Override Option</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption
   * @generated
   */
  EClass getAttributeOverrideOption();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute <em>Add Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Add Attribute</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute
   * @generated
   */
  EClass getAddAttribute();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Attribute</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute#getAttribute()
   * @see #getAddAttribute()
   * @generated
   */
  EReference getAddAttribute_Attribute();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute <em>Remove Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Remove Attribute</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute
   * @generated
   */
  EClass getRemoveAttribute();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Attribute</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute#getAttribute()
   * @see #getRemoveAttribute()
   * @generated
   */
  EReference getRemoveAttribute_Attribute();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute <em>Update Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Update Attribute</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute
   * @generated
   */
  EClass getUpdateAttribute();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Attribute</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute#getAttribute()
   * @see #getUpdateAttribute()
   * @generated
   */
  EReference getUpdateAttribute_Attribute();

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
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Condition <em>Condition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Condition</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Condition
   * @generated
   */
  EClass getCondition();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition <em>Simple Condition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Simple Condition</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition
   * @generated
   */
  EClass getSimpleCondition();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getField <em>Field</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Field</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getField()
   * @see #getSimpleCondition()
   * @generated
   */
  EAttribute getSimpleCondition_Field();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getOp()
   * @see #getSimpleCondition()
   * @generated
   */
  EAttribute getSimpleCondition_Op();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Expression</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getExpression()
   * @see #getSimpleCondition()
   * @generated
   */
  EAttribute getSimpleCondition_Expression();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition <em>Compound Condition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Compound Condition</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition
   * @generated
   */
  EClass getCompoundCondition();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition#getConditions <em>Conditions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Conditions</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition#getConditions()
   * @see #getCompoundCondition()
   * @generated
   */
  EReference getCompoundCondition_Conditions();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition#getOperators <em>Operators</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Operators</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition#getOperators()
   * @see #getCompoundCondition()
   * @generated
   */
  EAttribute getCompoundCondition_Operators();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher <em>XArtifact Matcher</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XArtifact Matcher</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher
   * @generated
   */
  EClass getXArtifactMatcher();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher#getName()
   * @see #getXArtifactMatcher()
   * @generated
   */
  EAttribute getXArtifactMatcher_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher#getConditions <em>Conditions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Conditions</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher#getConditions()
   * @see #getXArtifactMatcher()
   * @generated
   */
  EReference getXArtifactMatcher_Conditions();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher#getOperators <em>Operators</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Operators</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher#getOperators()
   * @see #getXArtifactMatcher()
   * @generated
   */
  EAttribute getXArtifactMatcher_Operators();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role <em>Role</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Role</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Role
   * @generated
   */
  EClass getRole();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getName()
   * @see #getRole()
   * @generated
   */
  EAttribute getRole_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getSuperRoles <em>Super Roles</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Super Roles</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getSuperRoles()
   * @see #getRole()
   * @generated
   */
  EReference getRole_SuperRoles();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getUsersAndGroups <em>Users And Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Users And Groups</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getUsersAndGroups()
   * @see #getRole()
   * @generated
   */
  EReference getRole_UsersAndGroups();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getReferencedContexts <em>Referenced Contexts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Referenced Contexts</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getReferencedContexts()
   * @see #getRole()
   * @generated
   */
  EReference getRole_ReferencedContexts();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext <em>Referenced Context</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Referenced Context</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext
   * @generated
   */
  EClass getReferencedContext();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext#getAccessContextRef <em>Access Context Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Access Context Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext#getAccessContextRef()
   * @see #getReferencedContext()
   * @generated
   */
  EAttribute getReferencedContext_AccessContextRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups <em>Users And Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Users And Groups</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups
   * @generated
   */
  EClass getUsersAndGroups();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups#getUserOrGroupId <em>User Or Group Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User Or Group Id</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups#getUserOrGroupId()
   * @see #getUsersAndGroups()
   * @generated
   */
  EAttribute getUsersAndGroups_UserOrGroupId();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getId()
   * @see #getAccessContext()
   * @generated
   */
  EAttribute getAccessContext_Id();

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
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Matcher Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getArtifactMatcherRef()
   * @see #getHierarchyRestriction()
   * @generated
   */
  EReference getHierarchyRestriction_ArtifactMatcherRef();

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
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate <em>Relation Type Artifact Type Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Relation Type Artifact Type Predicate</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate
   * @generated
   */
  EClass getRelationTypeArtifactTypePredicate();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate#getArtifactTypeRef <em>Artifact Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate#getArtifactTypeRef()
   * @see #getRelationTypeArtifactTypePredicate()
   * @generated
   */
  EReference getRelationTypeArtifactTypePredicate_ArtifactTypeRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate <em>Relation Type Artifact Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Relation Type Artifact Predicate</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate
   * @generated
   */
  EClass getRelationTypeArtifactPredicate();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Matcher Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate#getArtifactMatcherRef()
   * @see #getRelationTypeArtifactPredicate()
   * @generated
   */
  EReference getRelationTypeArtifactPredicate_ArtifactMatcherRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate <em>Relation Type Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Relation Type Predicate</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate
   * @generated
   */
  EClass getRelationTypePredicate();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction#getPermission <em>Permission</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Permission</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction#getPermission()
   * @see #getObjectRestriction()
   * @generated
   */
  EAttribute getObjectRestriction_Permission();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction <em>Artifact Match Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Artifact Match Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction
   * @generated
   */
  EClass getArtifactMatchRestriction();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Matcher Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction#getArtifactMatcherRef()
   * @see #getArtifactMatchRestriction()
   * @generated
   */
  EReference getArtifactMatchRestriction_ArtifactMatcherRef();

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
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction#getArtifactTypeRef <em>Artifact Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction#getArtifactTypeRef()
   * @see #getArtifactTypeRestriction()
   * @generated
   */
  EReference getArtifactTypeRestriction_ArtifactTypeRef();

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
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getAttributeTypeRef <em>Attribute Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Attribute Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getAttributeTypeRef()
   * @see #getAttributeTypeRestriction()
   * @generated
   */
  EReference getAttributeTypeRestriction_AttributeTypeRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getArtifactTypeRef <em>Artifact Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getArtifactTypeRef()
   * @see #getAttributeTypeRestriction()
   * @generated
   */
  EReference getAttributeTypeRestriction_ArtifactTypeRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction <em>Legacy Relation Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Legacy Relation Type Restriction</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction
   * @generated
   */
  EClass getLegacyRelationTypeRestriction();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getPermission <em>Permission</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Permission</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getPermission()
   * @see #getLegacyRelationTypeRestriction()
   * @generated
   */
  EAttribute getLegacyRelationTypeRestriction_Permission();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRelationTypeRef <em>Relation Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Relation Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRelationTypeRef()
   * @see #getLegacyRelationTypeRestriction()
   * @generated
   */
  EReference getLegacyRelationTypeRestriction_RelationTypeRef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRestrictedToSide <em>Restricted To Side</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Restricted To Side</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRestrictedToSide()
   * @see #getLegacyRelationTypeRestriction()
   * @generated
   */
  EAttribute getLegacyRelationTypeRestriction_RestrictedToSide();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Artifact Matcher Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getArtifactMatcherRef()
   * @see #getLegacyRelationTypeRestriction()
   * @generated
   */
  EReference getLegacyRelationTypeRestriction_ArtifactMatcherRef();

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
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#isRelationTypeMatch <em>Relation Type Match</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Relation Type Match</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#isRelationTypeMatch()
   * @see #getRelationTypeRestriction()
   * @generated
   */
  EAttribute getRelationTypeRestriction_RelationTypeMatch();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationTypeRef <em>Relation Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Relation Type Ref</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationTypeRef()
   * @see #getRelationTypeRestriction()
   * @generated
   */
  EReference getRelationTypeRestriction_RelationTypeRef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedToSide <em>Restricted To Side</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Restricted To Side</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedToSide()
   * @see #getRelationTypeRestriction()
   * @generated
   */
  EAttribute getRelationTypeRestriction_RestrictedToSide();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getPredicate <em>Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Predicate</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getPredicate()
   * @see #getRelationTypeRestriction()
   * @generated
   */
  EReference getRelationTypeRestriction_Predicate();

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
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp <em>Compare Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Compare Op</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp
   * @generated
   */
  EEnum getCompareOp();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator <em>XLogic Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>XLogic Operator</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator
   * @generated
   */
  EEnum getXLogicOperator();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField <em>Match Field</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Match Field</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField
   * @generated
   */
  EEnum getMatchField();

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
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch <em>Relation Type Match</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Relation Type Match</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch
   * @generated
   */
  EEnum getRelationTypeMatch();

  /**
   * Returns the meta object for enum '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum <em>XRelation Side Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>XRelation Side Enum</em>'.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
   * @generated
   */
  EEnum getXRelationSideEnum();

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
     * The meta object literal for the '<em><b>Artifact Type Overrides</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ARTIFACT_TYPE_OVERRIDES = eINSTANCE.getOseeDsl_ArtifactTypeOverrides();

    /**
     * The meta object literal for the '<em><b>Artifact Match Refs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ARTIFACT_MATCH_REFS = eINSTANCE.getOseeDsl_ArtifactMatchRefs();

    /**
     * The meta object literal for the '<em><b>Access Declarations</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ACCESS_DECLARATIONS = eINSTANCE.getOseeDsl_AccessDeclarations();

    /**
     * The meta object literal for the '<em><b>Role Declarations</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OSEE_DSL__ROLE_DECLARATIONS = eINSTANCE.getOseeDsl_RoleDeclarations();

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
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OSEE_TYPE__ID = eINSTANCE.getOseeType_Id();

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
     * The meta object literal for the '<em><b>Branch Uuid</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE_REF__BRANCH_UUID = eINSTANCE.getXAttributeTypeRef_BranchUuid();

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
     * The meta object literal for the '<em><b>Media Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE_TYPE__MEDIA_TYPE = eINSTANCE.getXAttributeType_MediaType();

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
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XOSEE_ENUM_ENTRY__DESCRIPTION = eINSTANCE.getXOseeEnumEntry_Description();

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
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ADD_ENUM__DESCRIPTION = eINSTANCE.getAddEnum_Description();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeArtifactTypeOverrideImpl <em>XOsee Artifact Type Override</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeArtifactTypeOverrideImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXOseeArtifactTypeOverride()
     * @generated
     */
    EClass XOSEE_ARTIFACT_TYPE_OVERRIDE = eINSTANCE.getXOseeArtifactTypeOverride();

    /**
     * The meta object literal for the '<em><b>Overriden Artifact Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XOSEE_ARTIFACT_TYPE_OVERRIDE__OVERRIDEN_ARTIFACT_TYPE = eINSTANCE.getXOseeArtifactTypeOverride_OverridenArtifactType();

    /**
     * The meta object literal for the '<em><b>Inherit All</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XOSEE_ARTIFACT_TYPE_OVERRIDE__INHERIT_ALL = eINSTANCE.getXOseeArtifactTypeOverride_InheritAll();

    /**
     * The meta object literal for the '<em><b>Override Options</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XOSEE_ARTIFACT_TYPE_OVERRIDE__OVERRIDE_OPTIONS = eINSTANCE.getXOseeArtifactTypeOverride_OverrideOptions();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeOverrideOptionImpl <em>Attribute Override Option</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeOverrideOptionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAttributeOverrideOption()
     * @generated
     */
    EClass ATTRIBUTE_OVERRIDE_OPTION = eINSTANCE.getAttributeOverrideOption();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddAttributeImpl <em>Add Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AddAttributeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getAddAttribute()
     * @generated
     */
    EClass ADD_ATTRIBUTE = eINSTANCE.getAddAttribute();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ADD_ATTRIBUTE__ATTRIBUTE = eINSTANCE.getAddAttribute_Attribute();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveAttributeImpl <em>Remove Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RemoveAttributeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRemoveAttribute()
     * @generated
     */
    EClass REMOVE_ATTRIBUTE = eINSTANCE.getRemoveAttribute();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REMOVE_ATTRIBUTE__ATTRIBUTE = eINSTANCE.getRemoveAttribute_Attribute();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UpdateAttributeImpl <em>Update Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UpdateAttributeImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getUpdateAttribute()
     * @generated
     */
    EClass UPDATE_ATTRIBUTE = eINSTANCE.getUpdateAttribute();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference UPDATE_ATTRIBUTE__ATTRIBUTE = eINSTANCE.getUpdateAttribute_Attribute();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ConditionImpl <em>Condition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ConditionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getCondition()
     * @generated
     */
    EClass CONDITION = eINSTANCE.getCondition();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.SimpleConditionImpl <em>Simple Condition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.SimpleConditionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getSimpleCondition()
     * @generated
     */
    EClass SIMPLE_CONDITION = eINSTANCE.getSimpleCondition();

    /**
     * The meta object literal for the '<em><b>Field</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SIMPLE_CONDITION__FIELD = eINSTANCE.getSimpleCondition_Field();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SIMPLE_CONDITION__OP = eINSTANCE.getSimpleCondition_Op();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SIMPLE_CONDITION__EXPRESSION = eINSTANCE.getSimpleCondition_Expression();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.CompoundConditionImpl <em>Compound Condition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.CompoundConditionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getCompoundCondition()
     * @generated
     */
    EClass COMPOUND_CONDITION = eINSTANCE.getCompoundCondition();

    /**
     * The meta object literal for the '<em><b>Conditions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOUND_CONDITION__CONDITIONS = eINSTANCE.getCompoundCondition_Conditions();

    /**
     * The meta object literal for the '<em><b>Operators</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPOUND_CONDITION__OPERATORS = eINSTANCE.getCompoundCondition_Operators();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactMatcherImpl <em>XArtifact Matcher</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactMatcherImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXArtifactMatcher()
     * @generated
     */
    EClass XARTIFACT_MATCHER = eINSTANCE.getXArtifactMatcher();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XARTIFACT_MATCHER__NAME = eINSTANCE.getXArtifactMatcher_Name();

    /**
     * The meta object literal for the '<em><b>Conditions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XARTIFACT_MATCHER__CONDITIONS = eINSTANCE.getXArtifactMatcher_Conditions();

    /**
     * The meta object literal for the '<em><b>Operators</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XARTIFACT_MATCHER__OPERATORS = eINSTANCE.getXArtifactMatcher_Operators();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl <em>Role</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRole()
     * @generated
     */
    EClass ROLE = eINSTANCE.getRole();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ROLE__NAME = eINSTANCE.getRole_Name();

    /**
     * The meta object literal for the '<em><b>Super Roles</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROLE__SUPER_ROLES = eINSTANCE.getRole_SuperRoles();

    /**
     * The meta object literal for the '<em><b>Users And Groups</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROLE__USERS_AND_GROUPS = eINSTANCE.getRole_UsersAndGroups();

    /**
     * The meta object literal for the '<em><b>Referenced Contexts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROLE__REFERENCED_CONTEXTS = eINSTANCE.getRole_ReferencedContexts();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ReferencedContextImpl <em>Referenced Context</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ReferencedContextImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getReferencedContext()
     * @generated
     */
    EClass REFERENCED_CONTEXT = eINSTANCE.getReferencedContext();

    /**
     * The meta object literal for the '<em><b>Access Context Ref</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REFERENCED_CONTEXT__ACCESS_CONTEXT_REF = eINSTANCE.getReferencedContext_AccessContextRef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UsersAndGroupsImpl <em>Users And Groups</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UsersAndGroupsImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getUsersAndGroups()
     * @generated
     */
    EClass USERS_AND_GROUPS = eINSTANCE.getUsersAndGroups();

    /**
     * The meta object literal for the '<em><b>User Or Group Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USERS_AND_GROUPS__USER_OR_GROUP_ID = eINSTANCE.getUsersAndGroups_UserOrGroupId();

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
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACCESS_CONTEXT__ID = eINSTANCE.getAccessContext_Id();

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
     * The meta object literal for the '<em><b>Artifact Matcher Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF = eINSTANCE.getHierarchyRestriction_ArtifactMatcherRef();

    /**
     * The meta object literal for the '<em><b>Access Rules</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HIERARCHY_RESTRICTION__ACCESS_RULES = eINSTANCE.getHierarchyRestriction_AccessRules();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactTypePredicateImpl <em>Relation Type Artifact Type Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactTypePredicateImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeArtifactTypePredicate()
     * @generated
     */
    EClass RELATION_TYPE_ARTIFACT_TYPE_PREDICATE = eINSTANCE.getRelationTypeArtifactTypePredicate();

    /**
     * The meta object literal for the '<em><b>Artifact Type Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF = eINSTANCE.getRelationTypeArtifactTypePredicate_ArtifactTypeRef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactPredicateImpl <em>Relation Type Artifact Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactPredicateImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeArtifactPredicate()
     * @generated
     */
    EClass RELATION_TYPE_ARTIFACT_PREDICATE = eINSTANCE.getRelationTypeArtifactPredicate();

    /**
     * The meta object literal for the '<em><b>Artifact Matcher Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE_ARTIFACT_PREDICATE__ARTIFACT_MATCHER_REF = eINSTANCE.getRelationTypeArtifactPredicate_ArtifactMatcherRef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypePredicateImpl <em>Relation Type Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypePredicateImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypePredicate()
     * @generated
     */
    EClass RELATION_TYPE_PREDICATE = eINSTANCE.getRelationTypePredicate();

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
     * The meta object literal for the '<em><b>Permission</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OBJECT_RESTRICTION__PERMISSION = eINSTANCE.getObjectRestriction_Permission();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactMatchRestrictionImpl <em>Artifact Match Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactMatchRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getArtifactMatchRestriction()
     * @generated
     */
    EClass ARTIFACT_MATCH_RESTRICTION = eINSTANCE.getArtifactMatchRestriction();

    /**
     * The meta object literal for the '<em><b>Artifact Matcher Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_MATCH_RESTRICTION__ARTIFACT_MATCHER_REF = eINSTANCE.getArtifactMatchRestriction_ArtifactMatcherRef();

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
     * The meta object literal for the '<em><b>Artifact Type Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE_REF = eINSTANCE.getArtifactTypeRestriction_ArtifactTypeRef();

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
     * The meta object literal for the '<em><b>Attribute Type Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF = eINSTANCE.getAttributeTypeRestriction_AttributeTypeRef();

    /**
     * The meta object literal for the '<em><b>Artifact Type Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF = eINSTANCE.getAttributeTypeRestriction_ArtifactTypeRef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl <em>Legacy Relation Type Restriction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getLegacyRelationTypeRestriction()
     * @generated
     */
    EClass LEGACY_RELATION_TYPE_RESTRICTION = eINSTANCE.getLegacyRelationTypeRestriction();

    /**
     * The meta object literal for the '<em><b>Permission</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION = eINSTANCE.getLegacyRelationTypeRestriction_Permission();

    /**
     * The meta object literal for the '<em><b>Relation Type Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF = eINSTANCE.getLegacyRelationTypeRestriction_RelationTypeRef();

    /**
     * The meta object literal for the '<em><b>Restricted To Side</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE = eINSTANCE.getLegacyRelationTypeRestriction_RestrictedToSide();

    /**
     * The meta object literal for the '<em><b>Artifact Matcher Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF = eINSTANCE.getLegacyRelationTypeRestriction_ArtifactMatcherRef();

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
     * The meta object literal for the '<em><b>Relation Type Match</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE_RESTRICTION__RELATION_TYPE_MATCH = eINSTANCE.getRelationTypeRestriction_RelationTypeMatch();

    /**
     * The meta object literal for the '<em><b>Relation Type Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF = eINSTANCE.getRelationTypeRestriction_RelationTypeRef();

    /**
     * The meta object literal for the '<em><b>Restricted To Side</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE = eINSTANCE.getRelationTypeRestriction_RestrictedToSide();

    /**
     * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE_RESTRICTION__PREDICATE = eINSTANCE.getRelationTypeRestriction_Predicate();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp <em>Compare Op</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getCompareOp()
     * @generated
     */
    EEnum COMPARE_OP = eINSTANCE.getCompareOp();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator <em>XLogic Operator</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXLogicOperator()
     * @generated
     */
    EEnum XLOGIC_OPERATOR = eINSTANCE.getXLogicOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField <em>Match Field</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getMatchField()
     * @generated
     */
    EEnum MATCH_FIELD = eINSTANCE.getMatchField();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch <em>Relation Type Match</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getRelationTypeMatch()
     * @generated
     */
    EEnum RELATION_TYPE_MATCH = eINSTANCE.getRelationTypeMatch();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum <em>XRelation Side Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
     * @see org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslPackageImpl#getXRelationSideEnum()
     * @generated
     */
    EEnum XRELATION_SIDE_ENUM = eINSTANCE.getXRelationSideEnum();

  }

} //OseeDslPackage
