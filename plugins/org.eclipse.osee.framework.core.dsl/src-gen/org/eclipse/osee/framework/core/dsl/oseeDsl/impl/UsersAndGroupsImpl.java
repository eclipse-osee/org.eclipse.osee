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
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.UsersAndGroupsImpl#getUserOrGroupGuid <em>User Or Group Guid</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UsersAndGroupsImpl extends MinimalEObjectImpl.Container implements UsersAndGroups
{
  /**
   * The default value of the '{@link #getUserOrGroupGuid() <em>User Or Group Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserOrGroupGuid()
   * @generated
   * @ordered
   */
  protected static final String USER_OR_GROUP_GUID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUserOrGroupGuid() <em>User Or Group Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserOrGroupGuid()
   * @generated
   * @ordered
   */
  protected String userOrGroupGuid = USER_OR_GROUP_GUID_EDEFAULT;

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
  public String getUserOrGroupGuid()
  {
    return userOrGroupGuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUserOrGroupGuid(String newUserOrGroupGuid)
  {
    String oldUserOrGroupGuid = userOrGroupGuid;
    userOrGroupGuid = newUserOrGroupGuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_GUID, oldUserOrGroupGuid, userOrGroupGuid));
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
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_GUID:
        return getUserOrGroupGuid();
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
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_GUID:
        setUserOrGroupGuid((String)newValue);
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
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_GUID:
        setUserOrGroupGuid(USER_OR_GROUP_GUID_EDEFAULT);
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
      case OseeDslPackage.USERS_AND_GROUPS__USER_OR_GROUP_GUID:
        return USER_OR_GROUP_GUID_EDEFAULT == null ? userOrGroupGuid != null : !USER_OR_GROUP_GUID_EDEFAULT.equals(userOrGroupGuid);
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
    result.append(" (userOrGroupGuid: ");
    result.append(userOrGroupGuid);
    result.append(')');
    return result.toString();
  }

} //UsersAndGroupsImpl
