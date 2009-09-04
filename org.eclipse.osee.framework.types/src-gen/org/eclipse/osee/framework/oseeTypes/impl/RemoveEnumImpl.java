/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.RemoveEnum;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Remove Enum</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RemoveEnumImpl#getEnumEntry <em>Enum Entry</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RemoveEnumImpl extends OverrideOptionImpl implements RemoveEnum
{
  /**
   * The cached value of the '{@link #getEnumEntry() <em>Enum Entry</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumEntry()
   * @generated
   * @ordered
   */
  protected OseeEnumEntry enumEntry;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RemoveEnumImpl()
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
    return OseeTypesPackage.Literals.REMOVE_ENUM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumEntry getEnumEntry()
  {
    if (enumEntry != null && enumEntry.eIsProxy())
    {
      InternalEObject oldEnumEntry = (InternalEObject)enumEntry;
      enumEntry = (OseeEnumEntry)eResolveProxy(oldEnumEntry);
      if (enumEntry != oldEnumEntry)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeTypesPackage.REMOVE_ENUM__ENUM_ENTRY, oldEnumEntry, enumEntry));
      }
    }
    return enumEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumEntry basicGetEnumEntry()
  {
    return enumEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEnumEntry(OseeEnumEntry newEnumEntry)
  {
    OseeEnumEntry oldEnumEntry = enumEntry;
    enumEntry = newEnumEntry;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.REMOVE_ENUM__ENUM_ENTRY, oldEnumEntry, enumEntry));
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
      case OseeTypesPackage.REMOVE_ENUM__ENUM_ENTRY:
        if (resolve) return getEnumEntry();
        return basicGetEnumEntry();
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
      case OseeTypesPackage.REMOVE_ENUM__ENUM_ENTRY:
        setEnumEntry((OseeEnumEntry)newValue);
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
      case OseeTypesPackage.REMOVE_ENUM__ENUM_ENTRY:
        setEnumEntry((OseeEnumEntry)null);
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
      case OseeTypesPackage.REMOVE_ENUM__ENUM_ENTRY:
        return enumEntry != null;
    }
    return super.eIsSet(featureID);
  }

} //RemoveEnumImpl
