/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XAttribute Type Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeRefImpl#getValidAttributeType <em>Valid Attribute Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XAttributeTypeRefImpl#getBranchUuid <em>Branch Uuid</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XAttributeTypeRefImpl extends MinimalEObjectImpl.Container implements XAttributeTypeRef
{
  /**
   * The cached value of the '{@link #getValidAttributeType() <em>Valid Attribute Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValidAttributeType()
   * @generated
   * @ordered
   */
  protected XAttributeType validAttributeType;

  /**
   * The default value of the '{@link #getBranchUuid() <em>Branch Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBranchUuid()
   * @generated
   * @ordered
   */
  protected static final String BRANCH_UUID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBranchUuid() <em>Branch Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBranchUuid()
   * @generated
   * @ordered
   */
  protected String branchUuid = BRANCH_UUID_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected XAttributeTypeRefImpl()
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
    return OseeDslPackage.Literals.XATTRIBUTE_TYPE_REF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType getValidAttributeType()
  {
    if (validAttributeType != null && validAttributeType.eIsProxy())
    {
      InternalEObject oldValidAttributeType = (InternalEObject)validAttributeType;
      validAttributeType = (XAttributeType)eResolveProxy(oldValidAttributeType);
      if (validAttributeType != oldValidAttributeType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE, oldValidAttributeType, validAttributeType));
      }
    }
    return validAttributeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType basicGetValidAttributeType()
  {
    return validAttributeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValidAttributeType(XAttributeType newValidAttributeType)
  {
    XAttributeType oldValidAttributeType = validAttributeType;
    validAttributeType = newValidAttributeType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE, oldValidAttributeType, validAttributeType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBranchUuid()
  {
    return branchUuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBranchUuid(String newBranchUuid)
  {
    String oldBranchUuid = branchUuid;
    branchUuid = newBranchUuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XATTRIBUTE_TYPE_REF__BRANCH_UUID, oldBranchUuid, branchUuid));
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
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE:
        if (resolve) return getValidAttributeType();
        return basicGetValidAttributeType();
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__BRANCH_UUID:
        return getBranchUuid();
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
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE:
        setValidAttributeType((XAttributeType)newValue);
        return;
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__BRANCH_UUID:
        setBranchUuid((String)newValue);
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
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE:
        setValidAttributeType((XAttributeType)null);
        return;
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__BRANCH_UUID:
        setBranchUuid(BRANCH_UUID_EDEFAULT);
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
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE:
        return validAttributeType != null;
      case OseeDslPackage.XATTRIBUTE_TYPE_REF__BRANCH_UUID:
        return BRANCH_UUID_EDEFAULT == null ? branchUuid != null : !BRANCH_UUID_EDEFAULT.equals(branchUuid);
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
    result.append(" (branchUuid: ");
    result.append(branchUuid);
    result.append(')');
    return result.toString();
  }

} //XAttributeTypeRefImpl
