/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.oseeTypes.OseeEnumOverride;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.OverrideOption;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Osee Enum Override</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl#getOverridenEnumType <em>Overriden Enum Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl#isInheritAll <em>Inherit All</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumOverrideImpl#getOverrideOptions <em>Override Options</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OseeEnumOverrideImpl extends OseeElementImpl implements OseeEnumOverride
{
  /**
   * The cached value of the '{@link #getOverridenEnumType() <em>Overriden Enum Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOverridenEnumType()
   * @generated
   * @ordered
   */
  protected OseeEnumType overridenEnumType;

  /**
   * The default value of the '{@link #isInheritAll() <em>Inherit All</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isInheritAll()
   * @generated
   * @ordered
   */
  protected static final boolean INHERIT_ALL_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isInheritAll() <em>Inherit All</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isInheritAll()
   * @generated
   * @ordered
   */
  protected boolean inheritAll = INHERIT_ALL_EDEFAULT;

  /**
   * The cached value of the '{@link #getOverrideOptions() <em>Override Options</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOverrideOptions()
   * @generated
   * @ordered
   */
  protected EList<OverrideOption> overrideOptions;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseeEnumOverrideImpl()
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
    return OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumType getOverridenEnumType()
  {
    if (overridenEnumType != null && overridenEnumType.eIsProxy())
    {
      InternalEObject oldOverridenEnumType = (InternalEObject)overridenEnumType;
      overridenEnumType = (OseeEnumType)eResolveProxy(oldOverridenEnumType);
      if (overridenEnumType != oldOverridenEnumType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE, oldOverridenEnumType, overridenEnumType));
      }
    }
    return overridenEnumType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumType basicGetOverridenEnumType()
  {
    return overridenEnumType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOverridenEnumType(OseeEnumType newOverridenEnumType)
  {
    OseeEnumType oldOverridenEnumType = overridenEnumType;
    overridenEnumType = newOverridenEnumType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE, oldOverridenEnumType, overridenEnumType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isInheritAll()
  {
    return inheritAll;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInheritAll(boolean newInheritAll)
  {
    boolean oldInheritAll = inheritAll;
    inheritAll = newInheritAll;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.OSEE_ENUM_OVERRIDE__INHERIT_ALL, oldInheritAll, inheritAll));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OverrideOption> getOverrideOptions()
  {
    if (overrideOptions == null)
    {
      overrideOptions = new EObjectContainmentEList<OverrideOption>(OverrideOption.class, this, OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS);
    }
    return overrideOptions;
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
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
        return ((InternalEList<?>)getOverrideOptions()).basicRemove(otherEnd, msgs);
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
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        if (resolve) return getOverridenEnumType();
        return basicGetOverridenEnumType();
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__INHERIT_ALL:
        return isInheritAll();
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
        return getOverrideOptions();
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
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        setOverridenEnumType((OseeEnumType)newValue);
        return;
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__INHERIT_ALL:
        setInheritAll((Boolean)newValue);
        return;
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
        getOverrideOptions().clear();
        getOverrideOptions().addAll((Collection<? extends OverrideOption>)newValue);
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
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        setOverridenEnumType((OseeEnumType)null);
        return;
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__INHERIT_ALL:
        setInheritAll(INHERIT_ALL_EDEFAULT);
        return;
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
        getOverrideOptions().clear();
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
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        return overridenEnumType != null;
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__INHERIT_ALL:
        return inheritAll != INHERIT_ALL_EDEFAULT;
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
        return overrideOptions != null && !overrideOptions.isEmpty();
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
    result.append(" (inheritAll: ");
    result.append(inheritAll);
    result.append(')');
    return result.toString();
  }

} //OseeEnumOverrideImpl
