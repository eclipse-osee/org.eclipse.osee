/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserDefOption <em>User Def Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getActive <em>Active</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserId <em>User Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getAdmin <em>Admin</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef()
 * @model
 * @generated
 */
public interface UserDef extends EObject
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>User Def Option</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User Def Option</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User Def Option</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef_UserDefOption()
   * @model unique="false"
   * @generated
   */
  EList<String> getUserDefOption();

  /**
   * Returns the value of the '<em><b>Active</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Active</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Active</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setActive(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef_Active()
   * @model
   * @generated
   */
  BooleanDef getActive();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getActive <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Active</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getActive()
   * @generated
   */
  void setActive(BooleanDef value);

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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef_UserId()
   * @model
   * @generated
   */
  String getUserId();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getUserId <em>User Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>User Id</em>' attribute.
   * @see #getUserId()
   * @generated
   */
  void setUserId(String value);

  /**
   * Returns the value of the '<em><b>Email</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Email</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Email</em>' attribute.
   * @see #setEmail(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef_Email()
   * @model
   * @generated
   */
  String getEmail();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getEmail <em>Email</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Email</em>' attribute.
   * @see #getEmail()
   * @generated
   */
  void setEmail(String value);

  /**
   * Returns the value of the '<em><b>Admin</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Admin</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Admin</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setAdmin(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getUserDef_Admin()
   * @model
   * @generated
   */
  BooleanDef getAdmin();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.UserDef#getAdmin <em>Admin</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Admin</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getAdmin()
   * @generated
   */
  void setAdmin(BooleanDef value);

} // UserDef
