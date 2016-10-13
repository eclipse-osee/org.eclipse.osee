/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XOsee Artifact Type Override</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverridenArtifactType <em>Overriden Artifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#isInheritAll <em>Inherit All</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverrideOptions <em>Override Options</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeArtifactTypeOverride()
 * @model
 * @generated
 */
public interface XOseeArtifactTypeOverride extends EObject
{
  /**
   * Returns the value of the '<em><b>Overriden Artifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Overriden Artifact Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Overriden Artifact Type</em>' reference.
   * @see #setOverridenArtifactType(XArtifactType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeArtifactTypeOverride_OverridenArtifactType()
   * @model
   * @generated
   */
  XArtifactType getOverridenArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#getOverridenArtifactType <em>Overriden Artifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Overriden Artifact Type</em>' reference.
   * @see #getOverridenArtifactType()
   * @generated
   */
  void setOverridenArtifactType(XArtifactType value);

  /**
   * Returns the value of the '<em><b>Inherit All</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Inherit All</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Inherit All</em>' attribute.
   * @see #setInheritAll(boolean)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeArtifactTypeOverride_InheritAll()
   * @model
   * @generated
   */
  boolean isInheritAll();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride#isInheritAll <em>Inherit All</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Inherit All</em>' attribute.
   * @see #isInheritAll()
   * @generated
   */
  void setInheritAll(boolean value);

  /**
   * Returns the value of the '<em><b>Override Options</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Override Options</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Override Options</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeArtifactTypeOverride_OverrideOptions()
   * @model containment="true"
   * @generated
   */
  EList<AttributeOverrideOption> getOverrideOptions();

} // XOseeArtifactTypeOverride
