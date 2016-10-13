/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation Type Artifact Type Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeArtifactTypePredicateImpl#getArtifactTypeRef <em>Artifact Type Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RelationTypeArtifactTypePredicateImpl extends RelationTypePredicateImpl implements RelationTypeArtifactTypePredicate
{
  /**
   * The cached value of the '{@link #getArtifactTypeRef() <em>Artifact Type Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactTypeRef()
   * @generated
   * @ordered
   */
  protected XArtifactType artifactTypeRef;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RelationTypeArtifactTypePredicateImpl()
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
    return OseeDslPackage.Literals.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType getArtifactTypeRef()
  {
    if (artifactTypeRef != null && artifactTypeRef.eIsProxy())
    {
      InternalEObject oldArtifactTypeRef = (InternalEObject)artifactTypeRef;
      artifactTypeRef = (XArtifactType)eResolveProxy(oldArtifactTypeRef);
      if (artifactTypeRef != oldArtifactTypeRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF, oldArtifactTypeRef, artifactTypeRef));
      }
    }
    return artifactTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType basicGetArtifactTypeRef()
  {
    return artifactTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArtifactTypeRef(XArtifactType newArtifactTypeRef)
  {
    XArtifactType oldArtifactTypeRef = artifactTypeRef;
    artifactTypeRef = newArtifactTypeRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF, oldArtifactTypeRef, artifactTypeRef));
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
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF:
        if (resolve) return getArtifactTypeRef();
        return basicGetArtifactTypeRef();
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
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF:
        setArtifactTypeRef((XArtifactType)newValue);
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
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF:
        setArtifactTypeRef((XArtifactType)null);
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
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF:
        return artifactTypeRef != null;
    }
    return super.eIsSet(featureID);
  }

} //RelationTypeArtifactTypePredicateImpl
