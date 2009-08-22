/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ModelImpl <em>Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.ModelImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getModel()
   * @generated
   */
  int MODEL = 0;

  /**
   * The feature id for the '<em><b>Imports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__IMPORTS = 0;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__ELEMENTS = 1;

  /**
   * The number of structural features of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_FEATURE_COUNT = 2;

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
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.TypeImpl <em>Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.TypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getType()
   * @generated
   */
  int TYPE = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE__NAME = 0;

  /**
   * The number of structural features of the '<em>Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl <em>Artifact Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getArtifactType()
   * @generated
   */
  int ARTIFACT_TYPE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__NAME = TYPE__NAME;

  /**
   * The feature id for the '<em><b>Super Entity</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__SUPER_ENTITY = TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE__ATTRIBUTES = TYPE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Artifact Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARTIFACT_TYPE_FEATURE_COUNT = TYPE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.XRefImpl <em>XRef</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.XRefImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getXRef()
   * @generated
   */
  int XREF = 4;

  /**
   * The number of structural features of the '<em>XRef</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XREF_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeRefImpl <em>Relation Type Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.RelationTypeRefImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationTypeRef()
   * @generated
   */
  int RELATION_TYPE_REF = 5;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_REF__TYPE = XREF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Relation Type Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_REF_FEATURE_COUNT = XREF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeRefImpl <em>Attribute Type Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeRefImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAttributeTypeRef()
   * @generated
   */
  int ATTRIBUTE_TYPE_REF = 6;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_REF__TYPE = XREF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Attribute Type Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_REF_FEATURE_COUNT = XREF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeImpl <em>Attribute Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.AttributeTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getAttributeType()
   * @generated
   */
  int ATTRIBUTE_TYPE = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__NAME = TYPE__NAME;

  /**
   * The feature id for the '<em><b>Super Entity</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__SUPER_ENTITY = TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE__ATTRIBUTES = TYPE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attribute Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_TYPE_FEATURE_COUNT = TYPE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.XAttributeImpl <em>XAttribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.XAttributeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getXAttribute()
   * @generated
   */
  int XATTRIBUTE = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE__NAME = 0;

  /**
   * The number of structural features of the '<em>XAttribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XATTRIBUTE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl <em>Relation Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationType()
   * @generated
   */
  int RELATION_TYPE = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__NAME = TYPE__NAME;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE__ATTRIBUTES = TYPE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Relation Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELATION_TYPE_FEATURE_COUNT = TYPE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.osee.framework.oseeTypes.impl.XRelationImpl <em>XRelation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.osee.framework.oseeTypes.impl.XRelationImpl
   * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getXRelation()
   * @generated
   */
  int XRELATION = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION__TYPE = 1;

  /**
   * The number of structural features of the '<em>XRelation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XRELATION_FEATURE_COUNT = 2;


  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.Model <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Model
   * @generated
   */
  EClass getModel();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.Model#getImports <em>Imports</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Imports</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Model#getImports()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Imports();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.Model#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Model#getElements()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Elements();

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
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.Type <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Type
   * @generated
   */
  EClass getType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.Type#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.Type#getName()
   * @see #getType()
   * @generated
   */
  EAttribute getType_Name();

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
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperEntity <em>Super Entity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Super Entity</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperEntity()
   * @see #getArtifactType()
   * @generated
   */
  EReference getArtifactType_SuperEntity();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.ArtifactType#getAttributes()
   * @see #getArtifactType()
   * @generated
   */
  EReference getArtifactType_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.XRef <em>XRef</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XRef</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.XRef
   * @generated
   */
  EClass getXRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.RelationTypeRef <em>Relation Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Relation Type Ref</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationTypeRef
   * @generated
   */
  EClass getRelationTypeRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.RelationTypeRef#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationTypeRef#getType()
   * @see #getRelationTypeRef()
   * @generated
   */
  EReference getRelationTypeRef_Type();

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
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.AttributeTypeRef#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeTypeRef#getType()
   * @see #getAttributeTypeRef()
   * @generated
   */
  EReference getAttributeTypeRef_Type();

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
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getSuperEntity <em>Super Entity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Super Entity</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getSuperEntity()
   * @see #getAttributeType()
   * @generated
   */
  EReference getAttributeType_SuperEntity();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.AttributeType#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.AttributeType#getAttributes()
   * @see #getAttributeType()
   * @generated
   */
  EReference getAttributeType_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.XAttribute <em>XAttribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XAttribute</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.XAttribute
   * @generated
   */
  EClass getXAttribute();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.XAttribute#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.XAttribute#getName()
   * @see #getXAttribute()
   * @generated
   */
  EAttribute getXAttribute_Name();

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
   * Returns the meta object for the containment reference list '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.RelationType#getAttributes()
   * @see #getRelationType()
   * @generated
   */
  EReference getRelationType_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.osee.framework.oseeTypes.XRelation <em>XRelation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>XRelation</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.XRelation
   * @generated
   */
  EClass getXRelation();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.osee.framework.oseeTypes.XRelation#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.XRelation#getName()
   * @see #getXRelation()
   * @generated
   */
  EAttribute getXRelation_Name();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.osee.framework.oseeTypes.XRelation#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Type</em>'.
   * @see org.eclipse.osee.framework.oseeTypes.XRelation#getType()
   * @see #getXRelation()
   * @generated
   */
  EReference getXRelation_Type();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.ModelImpl <em>Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.ModelImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getModel()
     * @generated
     */
    EClass MODEL = eINSTANCE.getModel();

    /**
     * The meta object literal for the '<em><b>Imports</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__IMPORTS = eINSTANCE.getModel_Imports();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__ELEMENTS = eINSTANCE.getModel_Elements();

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
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.TypeImpl <em>Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.TypeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getType()
     * @generated
     */
    EClass TYPE = eINSTANCE.getType();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TYPE__NAME = eINSTANCE.getType_Name();

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
     * The meta object literal for the '<em><b>Super Entity</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_TYPE__SUPER_ENTITY = eINSTANCE.getArtifactType_SuperEntity();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARTIFACT_TYPE__ATTRIBUTES = eINSTANCE.getArtifactType_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.XRefImpl <em>XRef</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.XRefImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getXRef()
     * @generated
     */
    EClass XREF = eINSTANCE.getXRef();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeRefImpl <em>Relation Type Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.RelationTypeRefImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getRelationTypeRef()
     * @generated
     */
    EClass RELATION_TYPE_REF = eINSTANCE.getRelationTypeRef();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE_REF__TYPE = eINSTANCE.getRelationTypeRef_Type();

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
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE_REF__TYPE = eINSTANCE.getAttributeTypeRef_Type();

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
     * The meta object literal for the '<em><b>Super Entity</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE__SUPER_ENTITY = eINSTANCE.getAttributeType_SuperEntity();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_TYPE__ATTRIBUTES = eINSTANCE.getAttributeType_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.XAttributeImpl <em>XAttribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.XAttributeImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getXAttribute()
     * @generated
     */
    EClass XATTRIBUTE = eINSTANCE.getXAttribute();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XATTRIBUTE__NAME = eINSTANCE.getXAttribute_Name();

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
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELATION_TYPE__ATTRIBUTES = eINSTANCE.getRelationType_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.osee.framework.oseeTypes.impl.XRelationImpl <em>XRelation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.osee.framework.oseeTypes.impl.XRelationImpl
     * @see org.eclipse.osee.framework.oseeTypes.impl.OseeTypesPackageImpl#getXRelation()
     * @generated
     */
    EClass XRELATION = eINSTANCE.getXRelation();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XRELATION__NAME = eINSTANCE.getXRelation_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XRELATION__TYPE = eINSTANCE.getXRelation_Type();

  }

} //OseeTypesPackage
