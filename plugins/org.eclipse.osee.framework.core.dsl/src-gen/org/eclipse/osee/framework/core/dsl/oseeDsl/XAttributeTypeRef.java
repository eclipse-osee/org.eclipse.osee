/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XAttribute Type Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getValidAttributeType <em>Valid Attribute Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchUuid <em>Branch Uuid</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeTypeRef()
 * @model
 * @generated
 */
public interface XAttributeTypeRef extends EObject
{
  /**
   * Returns the value of the '<em><b>Valid Attribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Valid Attribute Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Valid Attribute Type</em>' reference.
   * @see #setValidAttributeType(XAttributeType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeTypeRef_ValidAttributeType()
   * @model
   * @generated
   */
  XAttributeType getValidAttributeType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getValidAttributeType <em>Valid Attribute Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Valid Attribute Type</em>' reference.
   * @see #getValidAttributeType()
   * @generated
   */
  void setValidAttributeType(XAttributeType value);

  /**
   * Returns the value of the '<em><b>Branch Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Branch Uuid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Branch Uuid</em>' attribute.
   * @see #setBranchUuid(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeTypeRef_BranchUuid()
   * @model
   * @generated
   */
  String getBranchUuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchUuid <em>Branch Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Branch Uuid</em>' attribute.
   * @see #getBranchUuid()
   * @generated
   */
  void setBranchUuid(String value);

} // XAttributeTypeRef
