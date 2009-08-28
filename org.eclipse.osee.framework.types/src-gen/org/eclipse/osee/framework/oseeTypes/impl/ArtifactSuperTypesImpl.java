/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.framework.oseeTypes.ArtifactSuperTypes;
import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact Super Types</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactSuperTypesImpl#getArtifactSuperType <em>Artifact Super Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArtifactSuperTypesImpl extends MinimalEObjectImpl.Container implements ArtifactSuperTypes
{
  /**
   * The cached value of the '{@link #getArtifactSuperType() <em>Artifact Super Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactSuperType()
   * @generated
   * @ordered
   */
  protected ArtifactType artifactSuperType;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ArtifactSuperTypesImpl()
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
    return OseeTypesPackage.Literals.ARTIFACT_SUPER_TYPES;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactType getArtifactSuperType()
  {
    if (artifactSuperType != null && artifactSuperType.eIsProxy())
    {
      InternalEObject oldArtifactSuperType = (InternalEObject)artifactSuperType;
      artifactSuperType = (ArtifactType)eResolveProxy(oldArtifactSuperType);
      if (artifactSuperType != oldArtifactSuperType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeTypesPackage.ARTIFACT_SUPER_TYPES__ARTIFACT_SUPER_TYPE, oldArtifactSuperType, artifactSuperType));
      }
    }
    return artifactSuperType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactType basicGetArtifactSuperType()
  {
    return artifactSuperType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArtifactSuperType(ArtifactType newArtifactSuperType)
  {
    ArtifactType oldArtifactSuperType = artifactSuperType;
    artifactSuperType = newArtifactSuperType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.ARTIFACT_SUPER_TYPES__ARTIFACT_SUPER_TYPE, oldArtifactSuperType, artifactSuperType));
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
      case OseeTypesPackage.ARTIFACT_SUPER_TYPES__ARTIFACT_SUPER_TYPE:
        if (resolve) return getArtifactSuperType();
        return basicGetArtifactSuperType();
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
      case OseeTypesPackage.ARTIFACT_SUPER_TYPES__ARTIFACT_SUPER_TYPE:
        setArtifactSuperType((ArtifactType)newValue);
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
      case OseeTypesPackage.ARTIFACT_SUPER_TYPES__ARTIFACT_SUPER_TYPE:
        setArtifactSuperType((ArtifactType)null);
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
      case OseeTypesPackage.ARTIFACT_SUPER_TYPES__ARTIFACT_SUPER_TYPE:
        return artifactSuperType != null;
    }
    return super.eIsSet(featureID);
  }

} //ArtifactSuperTypesImpl
