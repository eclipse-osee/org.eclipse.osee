/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute Type Of Artifact Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getAttributeType <em>Attribute Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getArtifactType <em>Artifact Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAttributeTypeOfArtifactTypeRestriction()
 * @model
 * @generated
 */
public interface AttributeTypeOfArtifactTypeRestriction extends ObjectRestriction
{
  /**
   * Returns the value of the '<em><b>Attribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Type</em>' reference.
   * @see #setAttributeType(XAttributeType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAttributeTypeOfArtifactTypeRestriction_AttributeType()
   * @model
   * @generated
   */
  XAttributeType getAttributeType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getAttributeType <em>Attribute Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute Type</em>' reference.
   * @see #getAttributeType()
   * @generated
   */
  void setAttributeType(XAttributeType value);

  /**
   * Returns the value of the '<em><b>Artifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Type</em>' reference.
   * @see #setArtifactType(XArtifactType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAttributeTypeOfArtifactTypeRestriction_ArtifactType()
   * @model
   * @generated
   */
  XArtifactType getArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction#getArtifactType <em>Artifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Type</em>' reference.
   * @see #getArtifactType()
   * @generated
   */
  void setArtifactType(XArtifactType value);

} // AttributeTypeOfArtifactTypeRestriction
