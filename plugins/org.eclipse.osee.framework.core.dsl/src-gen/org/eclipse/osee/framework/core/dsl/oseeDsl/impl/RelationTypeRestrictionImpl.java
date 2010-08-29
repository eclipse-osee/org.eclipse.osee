/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl#getRelationTypeRef <em>Relation Type Ref</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl#getRestrictedToSide <em>Restricted To Side</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationTypeRestrictionImpl extends ObjectRestrictionImpl implements RelationTypeRestriction
{
  /**
   * The cached value of the '{@link #getRelationTypeRef() <em>Relation Type Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelationTypeRef()
   * @generated
   * @ordered
   */
  protected XRelationType relationTypeRef;

  /**
   * The default value of the '{@link #getRestrictedToSide() <em>Restricted To Side</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRestrictedToSide()
   * @generated
   * @ordered
   */
  protected static final XRelationSideEnum RESTRICTED_TO_SIDE_EDEFAULT = XRelationSideEnum.SIDE_A;

  /**
   * The cached value of the '{@link #getRestrictedToSide() <em>Restricted To Side</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRestrictedToSide()
   * @generated
   * @ordered
   */
  protected XRelationSideEnum restrictedToSide = RESTRICTED_TO_SIDE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RelationTypeRestrictionImpl()
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
    return OseeDslPackage.Literals.RELATION_TYPE_RESTRICTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XRelationType getRelationTypeRef()
  {
    if (relationTypeRef != null && relationTypeRef.eIsProxy())
    {
      InternalEObject oldRelationTypeRef = (InternalEObject)relationTypeRef;
      relationTypeRef = (XRelationType)eResolveProxy(oldRelationTypeRef);
      if (relationTypeRef != oldRelationTypeRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF, oldRelationTypeRef, relationTypeRef));
      }
    }
    return relationTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XRelationType basicGetRelationTypeRef()
  {
    return relationTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelationTypeRef(XRelationType newRelationTypeRef)
  {
    XRelationType oldRelationTypeRef = relationTypeRef;
    relationTypeRef = newRelationTypeRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF, oldRelationTypeRef, relationTypeRef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XRelationSideEnum getRestrictedToSide()
  {
    return restrictedToSide;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRestrictedToSide(XRelationSideEnum newRestrictedToSide)
  {
    XRelationSideEnum oldRestrictedToSide = restrictedToSide;
    restrictedToSide = newRestrictedToSide == null ? RESTRICTED_TO_SIDE_EDEFAULT : newRestrictedToSide;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE, oldRestrictedToSide, restrictedToSide));
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
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        if (resolve) return getRelationTypeRef();
        return basicGetRelationTypeRef();
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        return getRestrictedToSide();
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
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        setRelationTypeRef((XRelationType)newValue);
        return;
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        setRestrictedToSide((XRelationSideEnum)newValue);
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
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        setRelationTypeRef((XRelationType)null);
        return;
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        setRestrictedToSide(RESTRICTED_TO_SIDE_EDEFAULT);
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
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        return relationTypeRef != null;
      case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        return restrictedToSide != RESTRICTED_TO_SIDE_EDEFAULT;
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
    result.append(" (restrictedToSide: ");
    result.append(restrictedToSide);
    result.append(')');
    return result.toString();
  }

} //RelationTypeRestrictionImpl
