/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Instance Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction#getArtifactRef <em>Artifact Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactInstanceRestriction()
 * @model
 * @generated
 */
public interface ArtifactInstanceRestriction extends ObjectRestriction
{
  /**
   * Returns the value of the '<em><b>Artifact Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Ref</em>' reference.
   * @see #setArtifactRef(XArtifactRef)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactInstanceRestriction_ArtifactRef()
   * @model
   * @generated
   */
  XArtifactRef getArtifactRef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction#getArtifactRef <em>Artifact Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Ref</em>' reference.
   * @see #getArtifactRef()
   * @generated
   */
  void setArtifactRef(XArtifactRef value);

} // ArtifactInstanceRestriction
