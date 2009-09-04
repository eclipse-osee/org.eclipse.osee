/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Type Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getArtifactTypes <em>Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getRelationTypes <em>Relation Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getAttributeTypes <em>Attribute Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getEnumTypes <em>Enum Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel#getEnumOverrides <em>Enum Overrides</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel()
 * @model
 * @generated
 */
public interface OseeTypeModel extends EObject
{
  /**
   * Returns the value of the '<em><b>Imports</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.Import}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Imports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Imports</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel_Imports()
   * @model containment="true"
   * @generated
   */
  EList<Import> getImports();

  /**
   * Returns the value of the '<em><b>Artifact Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.ArtifactType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel_ArtifactTypes()
   * @model containment="true"
   * @generated
   */
  EList<ArtifactType> getArtifactTypes();

  /**
   * Returns the value of the '<em><b>Relation Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.RelationType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relation Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relation Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel_RelationTypes()
   * @model containment="true"
   * @generated
   */
  EList<RelationType> getRelationTypes();

  /**
   * Returns the value of the '<em><b>Attribute Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.AttributeType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel_AttributeTypes()
   * @model containment="true"
   * @generated
   */
  EList<AttributeType> getAttributeTypes();

  /**
   * Returns the value of the '<em><b>Enum Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.OseeEnumType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel_EnumTypes()
   * @model containment="true"
   * @generated
   */
  EList<OseeEnumType> getEnumTypes();

  /**
   * Returns the value of the '<em><b>Enum Overrides</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Overrides</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Overrides</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeTypeModel_EnumOverrides()
   * @model containment="true"
   * @generated
   */
  EList<OseeEnumOverride> getEnumOverrides();

} // OseeTypeModel
