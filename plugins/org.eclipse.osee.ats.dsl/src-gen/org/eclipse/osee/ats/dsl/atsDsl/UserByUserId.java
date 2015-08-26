/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User By User Id</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserByUserId#getUserId <em>User Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserByUserId()
 * @model
 * @generated
 */
public interface UserByUserId extends UserRef
{
  /**
   * Returns the value of the '<em><b>User Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User Id</em>' attribute.
   * @see #setUserId(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserByUserId_UserId()
   * @model
   * @generated
   */
  String getUserId();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByUserId#getUserId <em>User Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>User Id</em>' attribute.
   * @see #getUserId()
   * @generated
   */
  void setUserId(String value);

} // UserByUserId
