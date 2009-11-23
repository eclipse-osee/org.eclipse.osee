/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Osee Enum Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl#getOrdinal <em>Ordinal</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumEntryImpl#getEntryGuid <em>Entry Guid</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OseeEnumEntryImpl extends MinimalEObjectImpl.Container implements OseeEnumEntry
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
  protected OseeEnumEntryImpl()
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
    return OseeTypesPackage.Literals.OSEE_ENUM_ENTRY;
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.OSEE_ENUM_ENTRY__NAME, oldName, name));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.OSEE_ENUM_ENTRY__ORDINAL, oldOrdinal, ordinal));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.OSEE_ENUM_ENTRY__ENTRY_GUID, oldEntryGuid, entryGuid));
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
      case OseeTypesPackage.OSEE_ENUM_ENTRY__NAME:
        return getName();
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ORDINAL:
        return getOrdinal();
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ENTRY_GUID:
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
      case OseeTypesPackage.OSEE_ENUM_ENTRY__NAME:
        setName((String)newValue);
        return;
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ORDINAL:
        setOrdinal((String)newValue);
        return;
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ENTRY_GUID:
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
      case OseeTypesPackage.OSEE_ENUM_ENTRY__NAME:
        setName(NAME_EDEFAULT);
        return;
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ORDINAL:
        setOrdinal(ORDINAL_EDEFAULT);
        return;
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ENTRY_GUID:
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
      case OseeTypesPackage.OSEE_ENUM_ENTRY__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ORDINAL:
        return ORDINAL_EDEFAULT == null ? ordinal != null : !ORDINAL_EDEFAULT.equals(ordinal);
      case OseeTypesPackage.OSEE_ENUM_ENTRY__ENTRY_GUID:
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
    result.append(" (name: ");
    result.append(name);
    result.append(", ordinal: ");
    result.append(ordinal);
    result.append(", entryGuid: ");
    result.append(entryGuid);
    result.append(')');
    return result.toString();
  }

} //OseeEnumEntryImpl
