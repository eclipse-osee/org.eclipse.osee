/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.framework.oseeTypes.AddEnum;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Add Enum</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl#getEnumEntry <em>Enum Entry</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl#getOrdinal <em>Ordinal</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.AddEnumImpl#getEntryGuid <em>Entry Guid</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AddEnumImpl extends OverrideOptionImpl implements AddEnum
{
  /**
   * The default value of the '{@link #getEnumEntry() <em>Enum Entry</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumEntry()
   * @generated
   * @ordered
   */
  protected static final String ENUM_ENTRY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getEnumEntry() <em>Enum Entry</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumEntry()
   * @generated
   * @ordered
   */
  protected String enumEntry = ENUM_ENTRY_EDEFAULT;

  /**
   * The default value of the '{@link #getOrdinal() <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrdinal()
   * @generated
   * @ordered
   */
  protected static final String ORDINAL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getOrdinal() <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrdinal()
   * @generated
   * @ordered
   */
  protected String ordinal = ORDINAL_EDEFAULT;

  /**
   * The default value of the '{@link #getEntryGuid() <em>Entry Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEntryGuid()
   * @generated
   * @ordered
   */
  protected static final String ENTRY_GUID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getEntryGuid() <em>Entry Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEntryGuid()
   * @generated
   * @ordered
   */
  protected String entryGuid = ENTRY_GUID_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AddEnumImpl()
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
    return OseeTypesPackage.Literals.ADD_ENUM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getEnumEntry()
  {
    return enumEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEnumEntry(String newEnumEntry)
  {
    String oldEnumEntry = enumEntry;
    enumEntry = newEnumEntry;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.ADD_ENUM__ENUM_ENTRY, oldEnumEntry, enumEntry));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getOrdinal()
  {
    return ordinal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOrdinal(String newOrdinal)
  {
    String oldOrdinal = ordinal;
    ordinal = newOrdinal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.ADD_ENUM__ORDINAL, oldOrdinal, ordinal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getEntryGuid()
  {
    return entryGuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEntryGuid(String newEntryGuid)
  {
    String oldEntryGuid = entryGuid;
    entryGuid = newEntryGuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.ADD_ENUM__ENTRY_GUID, oldEntryGuid, entryGuid));
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
      case OseeTypesPackage.ADD_ENUM__ENUM_ENTRY:
        return getEnumEntry();
      case OseeTypesPackage.ADD_ENUM__ORDINAL:
        return getOrdinal();
      case OseeTypesPackage.ADD_ENUM__ENTRY_GUID:
        return getEntryGuid();
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
      case OseeTypesPackage.ADD_ENUM__ENUM_ENTRY:
        setEnumEntry((String)newValue);
        return;
      case OseeTypesPackage.ADD_ENUM__ORDINAL:
        setOrdinal((String)newValue);
        return;
      case OseeTypesPackage.ADD_ENUM__ENTRY_GUID:
        setEntryGuid((String)newValue);
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
      case OseeTypesPackage.ADD_ENUM__ENUM_ENTRY:
        setEnumEntry(ENUM_ENTRY_EDEFAULT);
        return;
      case OseeTypesPackage.ADD_ENUM__ORDINAL:
        setOrdinal(ORDINAL_EDEFAULT);
        return;
      case OseeTypesPackage.ADD_ENUM__ENTRY_GUID:
        setEntryGuid(ENTRY_GUID_EDEFAULT);
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
      case OseeTypesPackage.ADD_ENUM__ENUM_ENTRY:
        return ENUM_ENTRY_EDEFAULT == null ? enumEntry != null : !ENUM_ENTRY_EDEFAULT.equals(enumEntry);
      case OseeTypesPackage.ADD_ENUM__ORDINAL:
        return ORDINAL_EDEFAULT == null ? ordinal != null : !ORDINAL_EDEFAULT.equals(ordinal);
      case OseeTypesPackage.ADD_ENUM__ENTRY_GUID:
        return ENTRY_GUID_EDEFAULT == null ? entryGuid != null : !ENTRY_GUID_EDEFAULT.equals(entryGuid);
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
    result.append(" (enumEntry: ");
    result.append(enumEntry);
    result.append(", ordinal: ");
    result.append(ordinal);
    result.append(", entryGuid: ");
    result.append(entryGuid);
    result.append(')');
    return result.toString();
  }

} //AddEnumImpl
