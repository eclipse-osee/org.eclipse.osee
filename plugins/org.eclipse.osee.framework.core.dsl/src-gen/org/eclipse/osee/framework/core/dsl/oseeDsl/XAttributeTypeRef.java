/**
 * <copyright>
 * </copyright>
 *

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
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getValidAttributeType <em>Valid Attribute Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchGuid <em>Branch Guid</em>}</li>
 * </ul>
 * </p>
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
   * Returns the value of the '<em><b>Branch Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Branch Guid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Branch Guid</em>' attribute.
   * @see #setBranchGuid(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXAttributeTypeRef_BranchGuid()
   * @model
   * @generated
   */
  String getBranchGuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef#getBranchGuid <em>Branch Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Branch Guid</em>' attribute.
   * @see #getBranchGuid()
   * @generated
   */
  void setBranchGuid(String value);

} // XAttributeTypeRef
