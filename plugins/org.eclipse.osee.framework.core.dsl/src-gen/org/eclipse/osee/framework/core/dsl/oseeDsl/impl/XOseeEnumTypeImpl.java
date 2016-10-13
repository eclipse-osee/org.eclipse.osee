/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XOsee Enum Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumTypeImpl#getEnumEntries <em>Enum Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XOseeEnumTypeImpl extends OseeTypeImpl implements XOseeEnumType
{
  /**
   * The cached value of the '{@link #getEnumEntries() <em>Enum Entries</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumEntries()
   * @generated
   * @ordered
   */
  protected EList<XOseeEnumEntry> enumEntries;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected XOseeEnumTypeImpl()
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
    return OseeDslPackage.Literals.XOSEE_ENUM_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XOseeEnumEntry> getEnumEntries()
  {
    if (enumEntries == null)
    {
      enumEntries = new EObjectContainmentEList<XOseeEnumEntry>(XOseeEnumEntry.class, this, OseeDslPackage.XOSEE_ENUM_TYPE__ENUM_ENTRIES);
    }
    return enumEntries;
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
      case OseeDslPackage.XOSEE_ENUM_TYPE__ENUM_ENTRIES:
        return ((InternalEList<?>)getEnumEntries()).basicRemove(otherEnd, msgs);
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
      case OseeDslPackage.XOSEE_ENUM_TYPE__ENUM_ENTRIES:
        return getEnumEntries();
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
      case OseeDslPackage.XOSEE_ENUM_TYPE__ENUM_ENTRIES:
        getEnumEntries().clear();
        getEnumEntries().addAll((Collection<? extends XOseeEnumEntry>)newValue);
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
      case OseeDslPackage.XOSEE_ENUM_TYPE__ENUM_ENTRIES:
        getEnumEntries().clear();
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
      case OseeDslPackage.XOSEE_ENUM_TYPE__ENUM_ENTRIES:
        return enumEntries != null && !enumEntries.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //XOseeEnumTypeImpl
