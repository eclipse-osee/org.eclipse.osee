/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.oseeTypes.OseeEnum;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Osee Enum Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumTypeImpl#getEnums <em>Enums</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OseeEnumTypeImpl extends OseeTypeImpl implements OseeEnumType
{
  /**
   * The cached value of the '{@link #getEnums() <em>Enums</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnums()
   * @generated
   * @ordered
   */
  protected EList<OseeEnum> enums;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseeEnumTypeImpl()
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
    return OseeTypesPackage.Literals.OSEE_ENUM_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OseeEnum> getEnums()
  {
    if (enums == null)
    {
      enums = new EObjectContainmentEList<OseeEnum>(OseeEnum.class, this, OseeTypesPackage.OSEE_ENUM_TYPE__ENUMS);
    }
    return enums;
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
      case OseeTypesPackage.OSEE_ENUM_TYPE__ENUMS:
        return ((InternalEList<?>)getEnums()).basicRemove(otherEnd, msgs);
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
      case OseeTypesPackage.OSEE_ENUM_TYPE__ENUMS:
        return getEnums();
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
      case OseeTypesPackage.OSEE_ENUM_TYPE__ENUMS:
        getEnums().clear();
        getEnums().addAll((Collection<? extends OseeEnum>)newValue);
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
      case OseeTypesPackage.OSEE_ENUM_TYPE__ENUMS:
        getEnums().clear();
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
      case OseeTypesPackage.OSEE_ENUM_TYPE__ENUMS:
        return enums != null && !enums.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //OseeEnumTypeImpl
