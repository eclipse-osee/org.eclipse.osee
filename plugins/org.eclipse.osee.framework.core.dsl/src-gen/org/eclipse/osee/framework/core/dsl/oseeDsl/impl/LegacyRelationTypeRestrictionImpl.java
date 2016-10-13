/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Legacy Relation Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl#getRelationTypeRef <em>Relation Type Ref</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl#getRestrictedToSide <em>Restricted To Side</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.LegacyRelationTypeRestrictionImpl#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LegacyRelationTypeRestrictionImpl extends MinimalEObjectImpl.Container implements LegacyRelationTypeRestriction
{
  /**
   * The default value of the '{@link #getPermission() <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPermission()
   * @generated
   * @ordered
   */
  protected static final AccessPermissionEnum PERMISSION_EDEFAULT = AccessPermissionEnum.ALLOW;

  /**
   * The cached value of the '{@link #getPermission() <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPermission()
   * @generated
   * @ordered
   */
  protected AccessPermissionEnum permission = PERMISSION_EDEFAULT;

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
   * The cached value of the '{@link #getArtifactMatcherRef() <em>Artifact Matcher Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactMatcherRef()
   * @generated
   * @ordered
   */
  protected XArtifactMatcher artifactMatcherRef;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LegacyRelationTypeRestrictionImpl()
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
    return OseeDslPackage.Literals.LEGACY_RELATION_TYPE_RESTRICTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AccessPermissionEnum getPermission()
  {
    return permission;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPermission(AccessPermissionEnum newPermission)
  {
    AccessPermissionEnum oldPermission = permission;
    permission = newPermission == null ? PERMISSION_EDEFAULT : newPermission;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION, oldPermission, permission));
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
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF, oldRelationTypeRef, relationTypeRef));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF, oldRelationTypeRef, relationTypeRef));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE, oldRestrictedToSide, restrictedToSide));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactMatcher getArtifactMatcherRef()
  {
    if (artifactMatcherRef != null && artifactMatcherRef.eIsProxy())
    {
      InternalEObject oldArtifactMatcherRef = (InternalEObject)artifactMatcherRef;
      artifactMatcherRef = (XArtifactMatcher)eResolveProxy(oldArtifactMatcherRef);
      if (artifactMatcherRef != oldArtifactMatcherRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF, oldArtifactMatcherRef, artifactMatcherRef));
      }
    }
    return artifactMatcherRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactMatcher basicGetArtifactMatcherRef()
  {
    return artifactMatcherRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArtifactMatcherRef(XArtifactMatcher newArtifactMatcherRef)
  {
    XArtifactMatcher oldArtifactMatcherRef = artifactMatcherRef;
    artifactMatcherRef = newArtifactMatcherRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF, oldArtifactMatcherRef, artifactMatcherRef));
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
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION:
        return getPermission();
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        if (resolve) return getRelationTypeRef();
        return basicGetRelationTypeRef();
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        return getRestrictedToSide();
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF:
        if (resolve) return getArtifactMatcherRef();
        return basicGetArtifactMatcherRef();
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
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION:
        setPermission((AccessPermissionEnum)newValue);
        return;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        setRelationTypeRef((XRelationType)newValue);
        return;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        setRestrictedToSide((XRelationSideEnum)newValue);
        return;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF:
        setArtifactMatcherRef((XArtifactMatcher)newValue);
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
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION:
        setPermission(PERMISSION_EDEFAULT);
        return;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        setRelationTypeRef((XRelationType)null);
        return;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        setRestrictedToSide(RESTRICTED_TO_SIDE_EDEFAULT);
        return;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF:
        setArtifactMatcherRef((XArtifactMatcher)null);
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
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION:
        return permission != PERMISSION_EDEFAULT;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF:
        return relationTypeRef != null;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
        return restrictedToSide != RESTRICTED_TO_SIDE_EDEFAULT;
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF:
        return artifactMatcherRef != null;
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
    result.append(" (permission: ");
    result.append(permission);
    result.append(", restrictedToSide: ");
    result.append(restrictedToSide);
    result.append(')');
    return result.toString();
  }

} //LegacyRelationTypeRestrictionImpl
