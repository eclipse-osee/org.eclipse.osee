/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Dsl</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactTypes <em>Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getRelationTypes <em>Relation Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getAttributeTypes <em>Attribute Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getEnumTypes <em>Enum Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getEnumOverrides <em>Enum Overrides</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactTypeOverrides <em>Artifact Type Overrides</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getArtifactMatchRefs <em>Artifact Match Refs</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getAccessDeclarations <em>Access Declarations</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl#getRoleDeclarations <em>Role Declarations</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl()
 * @model
 * @generated
 */
public interface OseeDsl extends EObject
{
  /**
   * Returns the value of the '<em><b>Imports</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.Import}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Imports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Imports</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_Imports()
   * @model containment="true"
   * @generated
   */
  EList<Import> getImports();

  /**
   * Returns the value of the '<em><b>Artifact Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_ArtifactTypes()
   * @model containment="true"
   * @generated
   */
  EList<XArtifactType> getArtifactTypes();

  /**
   * Returns the value of the '<em><b>Relation Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relation Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relation Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_RelationTypes()
   * @model containment="true"
   * @generated
   */
  EList<XRelationType> getRelationTypes();

  /**
   * Returns the value of the '<em><b>Attribute Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_AttributeTypes()
   * @model containment="true"
   * @generated
   */
  EList<XAttributeType> getAttributeTypes();

  /**
   * Returns the value of the '<em><b>Enum Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_EnumTypes()
   * @model containment="true"
   * @generated
   */
  EList<XOseeEnumType> getEnumTypes();

  /**
   * Returns the value of the '<em><b>Enum Overrides</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Overrides</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Overrides</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_EnumOverrides()
   * @model containment="true"
   * @generated
   */
  EList<XOseeEnumOverride> getEnumOverrides();

  /**
   * Returns the value of the '<em><b>Artifact Type Overrides</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Type Overrides</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Type Overrides</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_ArtifactTypeOverrides()
   * @model containment="true"
   * @generated
   */
  EList<XOseeArtifactTypeOverride> getArtifactTypeOverrides();

  /**
   * Returns the value of the '<em><b>Artifact Match Refs</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Match Refs</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Match Refs</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_ArtifactMatchRefs()
   * @model containment="true"
   * @generated
   */
  EList<XArtifactMatcher> getArtifactMatchRefs();

  /**
   * Returns the value of the '<em><b>Access Declarations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Access Declarations</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Access Declarations</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_AccessDeclarations()
   * @model containment="true"
   * @generated
   */
  EList<AccessContext> getAccessDeclarations();

  /**
   * Returns the value of the '<em><b>Role Declarations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Role Declarations</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Role Declarations</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getOseeDsl_RoleDeclarations()
   * @model containment="true"
   * @generated
   */
  EList<Role> getRoleDeclarations();

} // OseeDsl
