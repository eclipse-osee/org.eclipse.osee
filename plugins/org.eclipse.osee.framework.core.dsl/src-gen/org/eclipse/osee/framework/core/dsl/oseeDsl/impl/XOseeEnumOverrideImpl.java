/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XOsee Enum Override</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl#getOverridenEnumType <em>Overriden Enum Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl#isInheritAll <em>Inherit All</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XOseeEnumOverrideImpl#getOverrideOptions <em>Override Options</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XOseeEnumOverrideImpl extends OseeElementImpl implements XOseeEnumOverride
{
  /**
   * The cached value of the '{@link #getOverridenEnumType() <em>Overriden Enum Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOverridenEnumType()
   * @generated
   * @ordered
   */
  protected XOseeEnumType overridenEnumType;

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
  protected XOseeEnumOverrideImpl()
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
    return OseeDslPackage.Literals.XOSEE_ENUM_OVERRIDE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XOseeEnumType getOverridenEnumType()
  {
    if (overridenEnumType != null && overridenEnumType.eIsProxy())
    {
      InternalEObject oldOverridenEnumType = (InternalEObject)overridenEnumType;
      overridenEnumType = (XOseeEnumType)eResolveProxy(oldOverridenEnumType);
      if (overridenEnumType != oldOverridenEnumType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE, oldOverridenEnumType, overridenEnumType));
      }
    }
    return overridenEnumType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XOseeEnumType basicGetOverridenEnumType()
  {
    return overridenEnumType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOverridenEnumType(XOseeEnumType newOverridenEnumType)
  {
    XOseeEnumType oldOverridenEnumType = overridenEnumType;
    overridenEnumType = newOverridenEnumType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE, oldOverridenEnumType, overridenEnumType));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XOSEE_ENUM_OVERRIDE__INHERIT_ALL, oldInheritAll, inheritAll));
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
      overrideOptions = new EObjectContainmentEList<OverrideOption>(OverrideOption.class, this, OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS);
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
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
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
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        if (resolve) return getOverridenEnumType();
        return basicGetOverridenEnumType();
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__INHERIT_ALL:
        return isInheritAll();
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
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
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        setOverridenEnumType((XOseeEnumType)newValue);
        return;
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__INHERIT_ALL:
        setInheritAll((Boolean)newValue);
        return;
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
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
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        setOverridenEnumType((XOseeEnumType)null);
        return;
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__INHERIT_ALL:
        setInheritAll(INHERIT_ALL_EDEFAULT);
        return;
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
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
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE:
        return overridenEnumType != null;
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__INHERIT_ALL:
        return inheritAll != INHERIT_ALL_EDEFAULT;
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
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

} //XOseeEnumOverrideImpl
