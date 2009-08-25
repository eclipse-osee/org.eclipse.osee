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

import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl#getSuperArtifactType <em>Super Artifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl#getValidAttributeTypes <em>Valid Attribute Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArtifactTypeImpl extends OseeTypeImpl implements ArtifactType
{
  /**
   * The cached value of the '{@link #getSuperArtifactType() <em>Super Artifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSuperArtifactType()
   * @generated
   * @ordered
   */
  protected ArtifactType superArtifactType;

  /**
   * The cached value of the '{@link #getValidAttributeTypes() <em>Valid Attribute Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValidAttributeTypes()
   * @generated
   * @ordered
   */
  protected EList<AttributeTypeRef> validAttributeTypes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ArtifactTypeImpl()
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
    return OseeTypesPackage.Literals.ARTIFACT_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactType getSuperArtifactType()
  {
    if (superArtifactType != null && superArtifactType.eIsProxy())
    {
      InternalEObject oldSuperArtifactType = (InternalEObject)superArtifactType;
      superArtifactType = (ArtifactType)eResolveProxy(oldSuperArtifactType);
      if (superArtifactType != oldSuperArtifactType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE, oldSuperArtifactType, superArtifactType));
      }
    }
    return superArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactType basicGetSuperArtifactType()
  {
    return superArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSuperArtifactType(ArtifactType newSuperArtifactType)
  {
    ArtifactType oldSuperArtifactType = superArtifactType;
    superArtifactType = newSuperArtifactType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE, oldSuperArtifactType, superArtifactType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AttributeTypeRef> getValidAttributeTypes()
  {
    if (validAttributeTypes == null)
    {
      validAttributeTypes = new EObjectContainmentEList<AttributeTypeRef>(AttributeTypeRef.class, this, OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES);
    }
    return validAttributeTypes;
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
      case OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        return ((InternalEList<?>)getValidAttributeTypes()).basicRemove(otherEnd, msgs);
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
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE:
        if (resolve) return getSuperArtifactType();
        return basicGetSuperArtifactType();
      case OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        return getValidAttributeTypes();
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
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE:
        setSuperArtifactType((ArtifactType)newValue);
        return;
      case OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        getValidAttributeTypes().clear();
        getValidAttributeTypes().addAll((Collection<? extends AttributeTypeRef>)newValue);
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
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE:
        setSuperArtifactType((ArtifactType)null);
        return;
      case OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        getValidAttributeTypes().clear();
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
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPE:
        return superArtifactType != null;
      case OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        return validAttributeTypes != null && !validAttributeTypes.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //ArtifactTypeImpl
