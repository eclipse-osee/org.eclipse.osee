/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Users And Groups</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups#getUserOrGroupId <em>User Or Group Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getUsersAndGroups()
 * @model
 * @generated
 */
public interface UsersAndGroups extends EObject
{
  /**
   * Returns the value of the '<em><b>User Or Group Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User Or Group Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User Or Group Id</em>' attribute.
   * @see #setUserOrGroupId(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getUsersAndGroups_UserOrGroupId()
   * @model
   * @generated
   */
  String getUserOrGroupId();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups#getUserOrGroupId <em>User Or Group Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>User Or Group Id</em>' attribute.
   * @see #getUserOrGroupId()
   * @generated
   */
  void setUserOrGroupId(String value);

} // UsersAndGroups
