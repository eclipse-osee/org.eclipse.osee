/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Referenced Context</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ReferencedContextImpl#getAccessContextRef <em>Access Context Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReferencedContextImpl extends MinimalEObjectImpl.Container implements ReferencedContext
{
  /**
   * The default value of the '{@link #getAccessContextRef() <em>Access Context Ref</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessContextRef()
   * @generated
   * @ordered
   */
  protected static final String ACCESS_CONTEXT_REF_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAccessContextRef() <em>Access Context Ref</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessContextRef()
   * @generated
   * @ordered
   */
  protected String accessContextRef = ACCESS_CONTEXT_REF_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ReferencedContextImpl()
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
    return OseeDslPackage.Literals.REFERENCED_CONTEXT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getAccessContextRef()
  {
    return accessContextRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAccessContextRef(String newAccessContextRef)
  {
    String oldAccessContextRef = accessContextRef;
    accessContextRef = newAccessContextRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.REFERENCED_CONTEXT__ACCESS_CONTEXT_REF, oldAccessContextRef, accessContextRef));
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
      case OseeDslPackage.REFERENCED_CONTEXT__ACCESS_CONTEXT_REF:
        return getAccessContextRef();
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
      case OseeDslPackage.REFERENCED_CONTEXT__ACCESS_CONTEXT_REF:
        setAccessContextRef((String)newValue);
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
      case OseeDslPackage.REFERENCED_CONTEXT__ACCESS_CONTEXT_REF:
        setAccessContextRef(ACCESS_CONTEXT_REF_EDEFAULT);
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
      case OseeDslPackage.REFERENCED_CONTEXT__ACCESS_CONTEXT_REF:
        return ACCESS_CONTEXT_REF_EDEFAULT == null ? accessContextRef != null : !ACCESS_CONTEXT_REF_EDEFAULT.equals(accessContextRef);
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
    result.append(" (accessContextRef: ");
    result.append(accessContextRef);
    result.append(')');
    return result.toString();
  }

} //ReferencedContextImpl
