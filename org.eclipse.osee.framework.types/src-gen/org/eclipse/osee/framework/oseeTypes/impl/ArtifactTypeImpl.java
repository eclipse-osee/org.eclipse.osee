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
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
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
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl#getSuperArtifactTypes <em>Super Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.ArtifactTypeImpl#getValidAttributeTypes <em>Valid Attribute Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArtifactTypeImpl extends OseeTypeImpl implements ArtifactType
{
  /**
   * The default value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAbstract()
   * @generated
   * @ordered
   */
  protected static final boolean ABSTRACT_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAbstract()
   * @generated
   * @ordered
   */
  protected boolean abstract_ = ABSTRACT_EDEFAULT;

  /**
   * The cached value of the '{@link #getSuperArtifactTypes() <em>Super Artifact Types</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSuperArtifactTypes()
   * @generated
   * @ordered
   */
  protected EList<ArtifactType> superArtifactTypes;

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
  public boolean isAbstract()
  {
    return abstract_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAbstract(boolean newAbstract)
  {
    boolean oldAbstract = abstract_;
    abstract_ = newAbstract;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.ARTIFACT_TYPE__ABSTRACT, oldAbstract, abstract_));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ArtifactType> getSuperArtifactTypes()
  {
    if (superArtifactTypes == null)
    {
      superArtifactTypes = new EObjectResolvingEList<ArtifactType>(ArtifactType.class, this, OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES);
    }
    return superArtifactTypes;
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
      case OseeTypesPackage.ARTIFACT_TYPE__ABSTRACT:
        return isAbstract();
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        return getSuperArtifactTypes();
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
      case OseeTypesPackage.ARTIFACT_TYPE__ABSTRACT:
        setAbstract((Boolean)newValue);
        return;
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        getSuperArtifactTypes().clear();
        getSuperArtifactTypes().addAll((Collection<? extends ArtifactType>)newValue);
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
      case OseeTypesPackage.ARTIFACT_TYPE__ABSTRACT:
        setAbstract(ABSTRACT_EDEFAULT);
        return;
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        getSuperArtifactTypes().clear();
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
      case OseeTypesPackage.ARTIFACT_TYPE__ABSTRACT:
        return abstract_ != ABSTRACT_EDEFAULT;
      case OseeTypesPackage.ARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        return superArtifactTypes != null && !superArtifactTypes.isEmpty();
      case OseeTypesPackage.ARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        return validAttributeTypes != null && !validAttributeTypes.isEmpty();
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
    result.append(" (abstract: ");
    result.append(abstract_);
    result.append(')');
    return result.toString();
  }

} //ArtifactTypeImpl
