/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperArtifactType <em>Super Artifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getValidAttributeTypes <em>Valid Attribute Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactType()
 * @model
 * @generated
 */
public interface ArtifactType extends OseeType
{
  /**
   * Returns the value of the '<em><b>Super Artifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Artifact Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Artifact Type</em>' reference.
   * @see #setSuperArtifactType(ArtifactType)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactType_SuperArtifactType()
   * @model
   * @generated
   */
  ArtifactType getSuperArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperArtifactType <em>Super Artifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Super Artifact Type</em>' reference.
   * @see #getSuperArtifactType()
   * @generated
   */
  void setSuperArtifactType(ArtifactType value);

  /**
   * Returns the value of the '<em><b>Valid Attribute Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.AttributeTypeRef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Valid Attribute Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Valid Attribute Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactType_ValidAttributeTypes()
   * @model containment="true"
   * @generated
   */
  EList<AttributeTypeRef> getValidAttributeTypes();

} // ArtifactType
