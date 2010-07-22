/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getAttributeType <em>Attribute Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAttributeTypeRestriction()
 * @model
 * @generated
 */
public interface AttributeTypeRestriction extends ObjectRestriction
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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAttributeTypeRestriction_AttributeType()
   * @model
   * @generated
   */
  XAttributeType getAttributeType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction#getAttributeType <em>Attribute Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute Type</em>' reference.
   * @see #getAttributeType()
   * @generated
   */
  void setAttributeType(XAttributeType value);

} // AttributeTypeRestriction
