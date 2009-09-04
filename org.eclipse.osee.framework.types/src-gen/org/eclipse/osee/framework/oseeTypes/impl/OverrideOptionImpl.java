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

import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.OverrideOption;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Override Option</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OverrideOptionImpl#isOverrideOperation <em>Override Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OverrideOptionImpl extends MinimalEObjectImpl.Container implements OverrideOption
{
  /**
   * The default value of the '{@link #isOverrideOperation() <em>Override Operation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOverrideOperation()
   * @generated
   * @ordered
   */
  protected static final boolean OVERRIDE_OPERATION_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isOverrideOperation() <em>Override Operation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOverrideOperation()
   * @generated
   * @ordered
   */
  protected boolean overrideOperation = OVERRIDE_OPERATION_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OverrideOptionImpl()
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
    return OseeTypesPackage.Literals.OVERRIDE_OPTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isOverrideOperation()
  {
    return overrideOperation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOverrideOperation(boolean newOverrideOperation)
  {
    boolean oldOverrideOperation = overrideOperation;
    overrideOperation = newOverrideOperation;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.OVERRIDE_OPTION__OVERRIDE_OPERATION, oldOverrideOperation, overrideOperation));
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
      case OseeTypesPackage.OVERRIDE_OPTION__OVERRIDE_OPERATION:
        return isOverrideOperation();
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
      case OseeTypesPackage.OVERRIDE_OPTION__OVERRIDE_OPERATION:
        setOverrideOperation((Boolean)newValue);
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
      case OseeTypesPackage.OVERRIDE_OPTION__OVERRIDE_OPERATION:
        setOverrideOperation(OVERRIDE_OPERATION_EDEFAULT);
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
      case OseeTypesPackage.OVERRIDE_OPTION__OVERRIDE_OPERATION:
        return overrideOperation != OVERRIDE_OPERATION_EDEFAULT;
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
    result.append(" (overrideOperation: ");
    result.append(overrideOperation);
    result.append(')');
    return result.toString();
  }

} //OverrideOptionImpl
