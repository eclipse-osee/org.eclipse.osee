/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XBranch Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getGuid <em>Guid</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXBranchRef()
 * @model
 * @generated
 */
public interface XBranchRef extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXBranchRef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Guid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Guid</em>' attribute.
   * @see #setGuid(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXBranchRef_Guid()
   * @model
   * @generated
   */
  String getGuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef#getGuid <em>Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Guid</em>' attribute.
   * @see #getGuid()
   * @generated
   */
  void setGuid(String value);

} // XBranchRef
