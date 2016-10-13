/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XArtifact Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#getSuperArtifactTypes <em>Super Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#getValidAttributeTypes <em>Valid Attribute Types</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXArtifactType()
 * @model
 * @generated
 */
public interface XArtifactType extends OseeType
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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXArtifactType_Abstract()
   * @model
   * @generated
   */
  boolean isAbstract();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType#isAbstract <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Abstract</em>' attribute.
   * @see #isAbstract()
   * @generated
   */
  void setAbstract(boolean value);

  /**
   * Returns the value of the '<em><b>Super Artifact Types</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Artifact Types</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Artifact Types</em>' reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXArtifactType_SuperArtifactTypes()
   * @model
   * @generated
   */
  EList<XArtifactType> getSuperArtifactTypes();

  /**
   * Returns the value of the '<em><b>Valid Attribute Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Valid Attribute Types</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Valid Attribute Types</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXArtifactType_ValidAttributeTypes()
   * @model containment="true"
   * @generated
   */
  EList<XAttributeTypeRef> getValidAttributeTypes();

} // XArtifactType
