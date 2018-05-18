/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Users And Groups</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UsersAndGroupsImpl#getUserOrGroupId <em>User Or Group Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UsersAndGroupsImpl extends MinimalEObjectImpl.Container implements UsersAndGroups
{
  /**
   * The default value of the '{@link #getUserOrGroupId() <em>User Or Group Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserOrGroupId()
   * @generated
   * @ordered
   */
  protected static final String USER_OR_GROUP_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUserOrGroupId() <em>User Or Group Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserOrGroupId()
   * @generated
   * @ordered
   */
  protected String userOrGroupId = USER_OR_GROUP_ID_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected UsersAndGroupsImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return OseeDslPackage.Literals.USERS_AND_GROUPS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getUserOrGroupId()
  {
    return userOrGroupId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUserOrGroupId(String newUserOrGroupId)
  {
    String oldUserOrGroupId = userOrGroupId;
    userOrGroupId = newUserOrGroupId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_ID, oldUserOrGroupId, userOrGroupId));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_ID:
        return getUserOrGroupId();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_ID:
        setUserOrGroupId((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_ID:
        setUserOrGroupId(USER_OR_GROUP_ID_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_ID:
        return USER_OR_GROUP_ID_EDEFAULT == null ? userOrGroupId != null : !USER_OR_GROUP_ID_EDEFAULT.equals(userOrGroupId);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (userOrGroupId: ");
    result.append(userOrGroupId);
    result.append(')');
    return result.toString();
  }

} //UsersAndGroupsImpl
