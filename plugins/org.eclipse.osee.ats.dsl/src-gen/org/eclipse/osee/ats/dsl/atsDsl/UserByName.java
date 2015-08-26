/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User By Name</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserByName#getUserName <em>User Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserByName()
 * @model
 * @generated
 */
public interface UserByName extends UserRef
{
  /**
   * Returns the value of the '<em><b>User Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User Name</em>' attribute.
   * @see #setUserName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserByName_UserName()
   * @model
   * @generated
   */
  String getUserName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserByName#getUserName <em>User Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>User Name</em>' attribute.
   * @see #getUserName()
   * @generated
   */
  void setUserName(String value);

} // UserByName
