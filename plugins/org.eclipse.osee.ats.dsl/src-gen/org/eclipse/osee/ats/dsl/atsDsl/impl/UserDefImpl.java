/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl#getUserDefOption <em>User Def Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl#getActive <em>Active</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl#getUserId <em>User Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.UserDefImpl#getAdmin <em>Admin</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UserDefImpl extends MinimalEObjectImpl.Container implements UserDef
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getUserDefOption() <em>User Def Option</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserDefOption()
   * @generated
   * @ordered
   */
  protected EList<String> userDefOption;

  /**
   * The default value of the '{@link #getActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActive()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ACTIVE_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActive()
   * @generated
   * @ordered
   */
  protected BooleanDef active = ACTIVE_EDEFAULT;

  /**
   * The default value of the '{@link #getUserId() <em>User Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserId()
   * @generated
   * @ordered
   */
  protected static final String USER_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUserId() <em>User Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserId()
   * @generated
   * @ordered
   */
  protected String userId = USER_ID_EDEFAULT;

  /**
   * The default value of the '{@link #getEmail() <em>Email</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEmail()
   * @generated
   * @ordered
   */
  protected static final String EMAIL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getEmail() <em>Email</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEmail()
   * @generated
   * @ordered
   */
  protected String email = EMAIL_EDEFAULT;

  /**
   * The default value of the '{@link #getAdmin() <em>Admin</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAdmin()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ADMIN_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getAdmin() <em>Admin</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAdmin()
   * @generated
   * @ordered
   */
  protected BooleanDef admin = ADMIN_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected UserDefImpl()
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
    return AtsDslPackage.Literals.USER_DEF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.USER_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getUserDefOption()
  {
    if (userDefOption == null)
    {
      userDefOption = new EDataTypeEList<String>(String.class, this, AtsDslPackage.USER_DEF__USER_DEF_OPTION);
    }
    return userDefOption;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getActive()
  {
    return active;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActive(BooleanDef newActive)
  {
    BooleanDef oldActive = active;
    active = newActive == null ? ACTIVE_EDEFAULT : newActive;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.USER_DEF__ACTIVE, oldActive, active));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getUserId()
  {
    return userId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUserId(String newUserId)
  {
    String oldUserId = userId;
    userId = newUserId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.USER_DEF__USER_ID, oldUserId, userId));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getEmail()
  {
    return email;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEmail(String newEmail)
  {
    String oldEmail = email;
    email = newEmail;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.USER_DEF__EMAIL, oldEmail, email));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getAdmin()
  {
    return admin;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAdmin(BooleanDef newAdmin)
  {
    BooleanDef oldAdmin = admin;
    admin = newAdmin == null ? ADMIN_EDEFAULT : newAdmin;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.USER_DEF__ADMIN, oldAdmin, admin));
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
      case AtsDslPackage.USER_DEF__NAME:
        return getName();
      case AtsDslPackage.USER_DEF__USER_DEF_OPTION:
        return getUserDefOption();
      case AtsDslPackage.USER_DEF__ACTIVE:
        return getActive();
      case AtsDslPackage.USER_DEF__USER_ID:
        return getUserId();
      case AtsDslPackage.USER_DEF__EMAIL:
        return getEmail();
      case AtsDslPackage.USER_DEF__ADMIN:
        return getAdmin();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case AtsDslPackage.USER_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.USER_DEF__USER_DEF_OPTION:
        getUserDefOption().clear();
        getUserDefOption().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.USER_DEF__ACTIVE:
        setActive((BooleanDef)newValue);
        return;
      case AtsDslPackage.USER_DEF__USER_ID:
        setUserId((String)newValue);
        return;
      case AtsDslPackage.USER_DEF__EMAIL:
        setEmail((String)newValue);
        return;
      case AtsDslPackage.USER_DEF__ADMIN:
        setAdmin((BooleanDef)newValue);
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
      case AtsDslPackage.USER_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.USER_DEF__USER_DEF_OPTION:
        getUserDefOption().clear();
        return;
      case AtsDslPackage.USER_DEF__ACTIVE:
        setActive(ACTIVE_EDEFAULT);
        return;
      case AtsDslPackage.USER_DEF__USER_ID:
        setUserId(USER_ID_EDEFAULT);
        return;
      case AtsDslPackage.USER_DEF__EMAIL:
        setEmail(EMAIL_EDEFAULT);
        return;
      case AtsDslPackage.USER_DEF__ADMIN:
        setAdmin(ADMIN_EDEFAULT);
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
      case AtsDslPackage.USER_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.USER_DEF__USER_DEF_OPTION:
        return userDefOption != null && !userDefOption.isEmpty();
      case AtsDslPackage.USER_DEF__ACTIVE:
        return active != ACTIVE_EDEFAULT;
      case AtsDslPackage.USER_DEF__USER_ID:
        return USER_ID_EDEFAULT == null ? userId != null : !USER_ID_EDEFAULT.equals(userId);
      case AtsDslPackage.USER_DEF__EMAIL:
        return EMAIL_EDEFAULT == null ? email != null : !EMAIL_EDEFAULT.equals(email);
      case AtsDslPackage.USER_DEF__ADMIN:
        return admin != ADMIN_EDEFAULT;
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
    result.append(" (name: ");
    result.append(name);
    result.append(", userDefOption: ");
    result.append(userDefOption);
    result.append(", active: ");
    result.append(active);
    result.append(", userId: ");
    result.append(userId);
    result.append(", email: ");
    result.append(email);
    result.append(", admin: ");
    result.append(admin);
    result.append(')');
    return result.toString();
  }

} //UserDefImpl
