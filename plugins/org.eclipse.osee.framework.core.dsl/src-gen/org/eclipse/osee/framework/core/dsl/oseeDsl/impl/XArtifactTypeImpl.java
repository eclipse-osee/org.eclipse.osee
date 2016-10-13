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

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XArtifact Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl#getSuperArtifactTypes <em>Super Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XArtifactTypeImpl#getValidAttributeTypes <em>Valid Attribute Types</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XArtifactTypeImpl extends OseeTypeImpl implements XArtifactType
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
  protected EList<XArtifactType> superArtifactTypes;

  /**
   * The cached value of the '{@link #getValidAttributeTypes() <em>Valid Attribute Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValidAttributeTypes()
   * @generated
   * @ordered
   */
  protected EList<XAttributeTypeRef> validAttributeTypes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected XArtifactTypeImpl()
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
    return OseeDslPackage.Literals.XARTIFACT_TYPE;
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XARTIFACT_TYPE__ABSTRACT, oldAbstract, abstract_));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XArtifactType> getSuperArtifactTypes()
  {
    if (superArtifactTypes == null)
    {
      superArtifactTypes = new EObjectResolvingEList<XArtifactType>(XArtifactType.class, this, OseeDslPackage.XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES);
    }
    return superArtifactTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XAttributeTypeRef> getValidAttributeTypes()
  {
    if (validAttributeTypes == null)
    {
      validAttributeTypes = new EObjectContainmentEList<XAttributeTypeRef>(XAttributeTypeRef.class, this, OseeDslPackage.XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES);
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
      case OseeDslPackage.XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
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
      case OseeDslPackage.XARTIFACT_TYPE__ABSTRACT:
        return isAbstract();
      case OseeDslPackage.XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        return getSuperArtifactTypes();
      case OseeDslPackage.XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
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
      case OseeDslPackage.XARTIFACT_TYPE__ABSTRACT:
        setAbstract((Boolean)newValue);
        return;
      case OseeDslPackage.XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        getSuperArtifactTypes().clear();
        getSuperArtifactTypes().addAll((Collection<? extends XArtifactType>)newValue);
        return;
      case OseeDslPackage.XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
        getValidAttributeTypes().clear();
        getValidAttributeTypes().addAll((Collection<? extends XAttributeTypeRef>)newValue);
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
      case OseeDslPackage.XARTIFACT_TYPE__ABSTRACT:
        setAbstract(ABSTRACT_EDEFAULT);
        return;
      case OseeDslPackage.XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        getSuperArtifactTypes().clear();
        return;
      case OseeDslPackage.XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
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
      case OseeDslPackage.XARTIFACT_TYPE__ABSTRACT:
        return abstract_ != ABSTRACT_EDEFAULT;
      case OseeDslPackage.XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES:
        return superArtifactTypes != null && !superArtifactTypes.isEmpty();
      case OseeDslPackage.XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES:
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

} //XArtifactTypeImpl
