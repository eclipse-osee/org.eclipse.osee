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

import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact Instance Restriction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactInstanceRestrictionImpl#getArtifactName <em>Artifact Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArtifactInstanceRestrictionImpl extends ObjectRestrictionImpl implements ArtifactInstanceRestriction
{
  /**
   * The cached value of the '{@link #getArtifactName() <em>Artifact Name</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactName()
   * @generated
   * @ordered
   */
  protected XArtifactRef artifactName;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ArtifactInstanceRestrictionImpl()
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
    return OseeDslPackage.Literals.ARTIFACT_INSTANCE_RESTRICTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactRef getArtifactName()
  {
    if (artifactName != null && artifactName.eIsProxy())
    {
      InternalEObject oldArtifactName = (InternalEObject)artifactName;
      artifactName = (XArtifactRef)eResolveProxy(oldArtifactName);
      if (artifactName != oldArtifactName)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME, oldArtifactName, artifactName));
      }
    }
    return artifactName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactRef basicGetArtifactName()
  {
    return artifactName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArtifactName(XArtifactRef newArtifactName)
  {
    XArtifactRef oldArtifactName = artifactName;
    artifactName = newArtifactName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME, oldArtifactName, artifactName));
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
      case OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME:
        if (resolve) return getArtifactName();
        return basicGetArtifactName();
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
      case OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME:
        setArtifactName((XArtifactRef)newValue);
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
      case OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME:
        setArtifactName((XArtifactRef)null);
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
      case OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_NAME:
        return artifactName != null;
    }
    return super.eIsSet(featureID);
  }

} //ArtifactInstanceRestrictionImpl
