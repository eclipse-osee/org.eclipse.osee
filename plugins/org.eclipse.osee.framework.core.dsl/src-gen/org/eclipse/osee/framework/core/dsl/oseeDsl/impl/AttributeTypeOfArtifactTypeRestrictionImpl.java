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

import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute Type Of Artifact Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeOfArtifactTypeRestrictionImpl#getAttributeType <em>Attribute Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeOfArtifactTypeRestrictionImpl#getArtifactType <em>Artifact Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AttributeTypeOfArtifactTypeRestrictionImpl extends ObjectRestrictionImpl implements AttributeTypeOfArtifactTypeRestriction
{
  /**
   * The cached value of the '{@link #getAttributeType() <em>Attribute Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeType()
   * @generated
   * @ordered
   */
  protected XAttributeType attributeType;

  /**
   * The cached value of the '{@link #getArtifactType() <em>Artifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactType()
   * @generated
   * @ordered
   */
  protected XArtifactType artifactType;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AttributeTypeOfArtifactTypeRestrictionImpl()
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
    return OseeDslPackage.Literals.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType getAttributeType()
  {
    if (attributeType != null && attributeType.eIsProxy())
    {
      InternalEObject oldAttributeType = (InternalEObject)attributeType;
      attributeType = (XAttributeType)eResolveProxy(oldAttributeType);
      if (attributeType != oldAttributeType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE, oldAttributeType, attributeType));
      }
    }
    return attributeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType basicGetAttributeType()
  {
    return attributeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttributeType(XAttributeType newAttributeType)
  {
    XAttributeType oldAttributeType = attributeType;
    attributeType = newAttributeType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE, oldAttributeType, attributeType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType getArtifactType()
  {
    if (artifactType != null && artifactType.eIsProxy())
    {
      InternalEObject oldArtifactType = (InternalEObject)artifactType;
      artifactType = (XArtifactType)eResolveProxy(oldArtifactType);
      if (artifactType != oldArtifactType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE, oldArtifactType, artifactType));
      }
    }
    return artifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType basicGetArtifactType()
  {
    return artifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArtifactType(XArtifactType newArtifactType)
  {
    XArtifactType oldArtifactType = artifactType;
    artifactType = newArtifactType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE, oldArtifactType, artifactType));
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
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE:
        if (resolve) return getAttributeType();
        return basicGetAttributeType();
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
        if (resolve) return getArtifactType();
        return basicGetArtifactType();
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
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE:
        setAttributeType((XAttributeType)newValue);
        return;
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
        setArtifactType((XArtifactType)newValue);
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
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE:
        setAttributeType((XAttributeType)null);
        return;
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
        setArtifactType((XArtifactType)null);
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
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ATTRIBUTE_TYPE:
        return attributeType != null;
      case OseeDslPackage.ATTRIBUTE_TYPE_OF_ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
        return artifactType != null;
    }
    return super.eIsSet(featureID);
  }

} //AttributeTypeOfArtifactTypeRestrictionImpl
