/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.FollowupRef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Followup Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.FollowupRefImpl#getAssigneeRefs <em>Assignee Refs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FollowupRefImpl extends MinimalEObjectImpl.Container implements FollowupRef
{
  /**
   * The cached value of the '{@link #getAssigneeRefs() <em>Assignee Refs</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAssigneeRefs()
   * @generated
   * @ordered
   */
  protected EList<UserRef> assigneeRefs;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FollowupRefImpl()
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
    return AtsDslPackage.Literals.FOLLOWUP_REF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserRef> getAssigneeRefs()
  {
    if (assigneeRefs == null)
    {
      assigneeRefs = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.FOLLOWUP_REF__ASSIGNEE_REFS);
    }
    return assigneeRefs;
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
      case AtsDslPackage.FOLLOWUP_REF__ASSIGNEE_REFS:
        return ((InternalEList<?>)getAssigneeRefs()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.FOLLOWUP_REF__ASSIGNEE_REFS:
        return getAssigneeRefs();
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
      case AtsDslPackage.FOLLOWUP_REF__ASSIGNEE_REFS:
        getAssigneeRefs().clear();
        getAssigneeRefs().addAll((Collection<? extends UserRef>)newValue);
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
      case AtsDslPackage.FOLLOWUP_REF__ASSIGNEE_REFS:
        getAssigneeRefs().clear();
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
      case AtsDslPackage.FOLLOWUP_REF__ASSIGNEE_REFS:
        return assigneeRefs != null && !assigneeRefs.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //FollowupRefImpl
