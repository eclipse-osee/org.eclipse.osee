/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl#getAttributeTypeRef <em>Attribute Type Ref</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AttributeTypeRestrictionImpl#getArtifactTypeRef <em>Artifact Type Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttributeTypeRestrictionImpl extends ObjectRestrictionImpl implements AttributeTypeRestriction
{
  /**
   * The cached value of the '{@link #getAttributeTypeRef() <em>Attribute Type Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeTypeRef()
   * @generated
   * @ordered
   */
  protected XAttributeType attributeTypeRef;

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
  protected AttributeTypeRestrictionImpl()
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
    return OseeDslPackage.Literals.ATTRIBUTE_TYPE_RESTRICTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType getAttributeTypeRef()
  {
    if (attributeTypeRef != null && attributeTypeRef.eIsProxy())
    {
      InternalEObject oldAttributeTypeRef = (InternalEObject)attributeTypeRef;
      attributeTypeRef = (XAttributeType)eResolveProxy(oldAttributeTypeRef);
      if (attributeTypeRef != oldAttributeTypeRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF, oldAttributeTypeRef, attributeTypeRef));
      }
    }
    return attributeTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType basicGetAttributeTypeRef()
  {
    return attributeTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttributeTypeRef(XAttributeType newAttributeTypeRef)
  {
    XAttributeType oldAttributeTypeRef = attributeTypeRef;
    attributeTypeRef = newAttributeTypeRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF, oldAttributeTypeRef, attributeTypeRef));
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
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF, oldArtifactTypeRef, artifactTypeRef));
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF, oldArtifactTypeRef, artifactTypeRef));
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
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF:
        if (resolve) return getAttributeTypeRef();
        return basicGetAttributeTypeRef();
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF:
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
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF:
        setAttributeTypeRef((XAttributeType)newValue);
        return;
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF:
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
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF:
        setAttributeTypeRef((XAttributeType)null);
        return;
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF:
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
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF:
        return attributeTypeRef != null;
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF:
        return artifactTypeRef != null;
    }
    return super.eIsSet(featureID);
  }

} //AttributeTypeRestrictionImpl
