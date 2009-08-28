/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Super Types</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.ArtifactSuperTypes#getArtifactSuperType <em>Artifact Super Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactSuperTypes()
 * @model
 * @generated
 */
public interface ArtifactSuperTypes extends EObject
{
  /**
   * Returns the value of the '<em><b>Artifact Super Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Super Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Super Type</em>' reference.
   * @see #setArtifactSuperType(ArtifactType)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getArtifactSuperTypes_ArtifactSuperType()
   * @model
   * @generated
   */
  ArtifactType getArtifactSuperType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.ArtifactSuperTypes#getArtifactSuperType <em>Artifact Super Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Super Type</em>' reference.
   * @see #getArtifactSuperType()
   * @generated
   */
  void setArtifactSuperType(ArtifactType value);

} // ArtifactSuperTypes
