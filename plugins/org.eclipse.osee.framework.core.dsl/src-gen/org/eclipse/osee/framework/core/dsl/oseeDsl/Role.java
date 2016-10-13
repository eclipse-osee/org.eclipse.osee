/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getSuperRoles <em>Super Roles</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getUsersAndGroups <em>Users And Groups</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getReferencedContexts <em>Referenced Contexts</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRole()
 * @model
 * @generated
 */
public interface Role extends EObject
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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRole_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Super Roles</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Roles</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Roles</em>' reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRole_SuperRoles()
   * @model
   * @generated
   */
  EList<Role> getSuperRoles();

  /**
   * Returns the value of the '<em><b>Users And Groups</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Users And Groups</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Users And Groups</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRole_UsersAndGroups()
   * @model containment="true"
   * @generated
   */
  EList<UsersAndGroups> getUsersAndGroups();

  /**
   * Returns the value of the '<em><b>Referenced Contexts</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Referenced Contexts</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Referenced Contexts</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRole_ReferencedContexts()
   * @model containment="true"
   * @generated
   */
  EList<ReferencedContext> getReferencedContexts();

} // Role
