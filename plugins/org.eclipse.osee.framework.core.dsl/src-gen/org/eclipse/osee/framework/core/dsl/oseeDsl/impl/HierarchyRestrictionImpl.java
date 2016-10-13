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
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hierarchy Restriction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.HierarchyRestrictionImpl#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.HierarchyRestrictionImpl#getAccessRules <em>Access Rules</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HierarchyRestrictionImpl extends MinimalEObjectImpl.Container implements HierarchyRestriction
{
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
   * The cached value of the '{@link #getAccessRules() <em>Access Rules</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessRules()
   * @generated
   * @ordered
   */
  protected EList<ObjectRestriction> accessRules;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HierarchyRestrictionImpl()
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
    return OseeDslPackage.Literals.HIERARCHY_RESTRICTION;
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
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF, oldArtifactMatcherRef, artifactMatcherRef));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF, oldArtifactMatcherRef, artifactMatcherRef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ObjectRestriction> getAccessRules()
  {
    if (accessRules == null)
    {
      accessRules = new EObjectContainmentEList<ObjectRestriction>(ObjectRestriction.class, this, OseeDslPackage.HIERARCHY_RESTRICTION__ACCESS_RULES);
    }
    return accessRules;
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
      case OseeDslPackage.HIERARCHY_RESTRICTION__ACCESS_RULES:
        return ((InternalEList<?>)getAccessRules()).basicRemove(otherEnd, msgs);
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
      case OseeDslPackage.HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF:
        if (resolve) return getArtifactMatcherRef();
        return basicGetArtifactMatcherRef();
      case OseeDslPackage.HIERARCHY_RESTRICTION__ACCESS_RULES:
        return getAccessRules();
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
      case OseeDslPackage.HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF:
        setArtifactMatcherRef((XArtifactMatcher)newValue);
        return;
      case OseeDslPackage.HIERARCHY_RESTRICTION__ACCESS_RULES:
        getAccessRules().clear();
        getAccessRules().addAll((Collection<? extends ObjectRestriction>)newValue);
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
      case OseeDslPackage.HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF:
        setArtifactMatcherRef((XArtifactMatcher)null);
        return;
      case OseeDslPackage.HIERARCHY_RESTRICTION__ACCESS_RULES:
        getAccessRules().clear();
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
      case OseeDslPackage.HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF:
        return artifactMatcherRef != null;
      case OseeDslPackage.HIERARCHY_RESTRICTION__ACCESS_RULES:
        return accessRules != null && !accessRules.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //HierarchyRestrictionImpl
