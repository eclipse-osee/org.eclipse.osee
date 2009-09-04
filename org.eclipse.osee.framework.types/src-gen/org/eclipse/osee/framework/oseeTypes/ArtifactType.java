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
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#getSuperArtifactTypes <em>Super Artifact Types</em>}</li>
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
   * Returns the value of the '<em><b>Abstract</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Abstract</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Abstract</em>' attribute.
   * @see #setAbstract(boolean)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactType_Abstract()
   * @model
   * @generated
   */
  boolean isAbstract();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.ArtifactType#isAbstract <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Abstract</em>' attribute.
   * @see #isAbstract()
   * @generated
   */
  void setAbstract(boolean value);

  /**
   * Returns the value of the '<em><b>Super Artifact Types</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.ArtifactType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Artifact Types</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Artifact Types</em>' reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactType_SuperArtifactTypes()
   * @model
   * @generated
   */
  EList<ArtifactType> getSuperArtifactTypes();

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
