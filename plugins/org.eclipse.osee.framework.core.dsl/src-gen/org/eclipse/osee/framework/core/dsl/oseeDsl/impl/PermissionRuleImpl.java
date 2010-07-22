/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Permission Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.PermissionRuleImpl#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.PermissionRuleImpl#getObjectRestriction <em>Object Restriction</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PermissionRuleImpl extends MinimalEObjectImpl.Container implements PermissionRule
{
  /**
   * The default value of the '{@link #getPermission() <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPermission()
   * @generated
   * @ordered
   */
  protected static final AccessPermissionEnum PERMISSION_EDEFAULT = AccessPermissionEnum.ALLOW;

  /**
   * The cached value of the '{@link #getPermission() <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPermission()
   * @generated
   * @ordered
   */
  protected AccessPermissionEnum permission = PERMISSION_EDEFAULT;

  /**
   * The cached value of the '{@link #getObjectRestriction() <em>Object Restriction</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getObjectRestriction()
   * @generated
   * @ordered
   */
  protected ObjectRestriction objectRestriction;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PermissionRuleImpl()
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
    return OseeDslPackage.Literals.PERMISSION_RULE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AccessPermissionEnum getPermission()
  {
    return permission;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPermission(AccessPermissionEnum newPermission)
  {
    AccessPermissionEnum oldPermission = permission;
    permission = newPermission == null ? PERMISSION_EDEFAULT : newPermission;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.PERMISSION_RULE__PERMISSION, oldPermission, permission));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ObjectRestriction getObjectRestriction()
  {
    return objectRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetObjectRestriction(ObjectRestriction newObjectRestriction, NotificationChain msgs)
  {
    ObjectRestriction oldObjectRestriction = objectRestriction;
    objectRestriction = newObjectRestriction;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION, oldObjectRestriction, newObjectRestriction);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setObjectRestriction(ObjectRestriction newObjectRestriction)
  {
    if (newObjectRestriction != objectRestriction)
    {
      NotificationChain msgs = null;
      if (objectRestriction != null)
        msgs = ((InternalEObject)objectRestriction).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION, null, msgs);
      if (newObjectRestriction != null)
        msgs = ((InternalEObject)newObjectRestriction).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION, null, msgs);
      msgs = basicSetObjectRestriction(newObjectRestriction, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION, newObjectRestriction, newObjectRestriction));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION:
        return basicSetObjectRestriction(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case OseeDslPackage.PERMISSION_RULE__PERMISSION:
        return getPermission();
      case OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION:
        return getObjectRestriction();
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
      case OseeDslPackage.PERMISSION_RULE__PERMISSION:
        setPermission((AccessPermissionEnum)newValue);
        return;
      case OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION:
        setObjectRestriction((ObjectRestriction)newValue);
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
      case OseeDslPackage.PERMISSION_RULE__PERMISSION:
        setPermission(PERMISSION_EDEFAULT);
        return;
      case OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION:
        setObjectRestriction((ObjectRestriction)null);
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
      case OseeDslPackage.PERMISSION_RULE__PERMISSION:
        return permission != PERMISSION_EDEFAULT;
      case OseeDslPackage.PERMISSION_RULE__OBJECT_RESTRICTION:
        return objectRestriction != null;
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
    result.append(" (permission: ");
    result.append(permission);
    result.append(')');
    return result.toString();
  }

} //PermissionRuleImpl
