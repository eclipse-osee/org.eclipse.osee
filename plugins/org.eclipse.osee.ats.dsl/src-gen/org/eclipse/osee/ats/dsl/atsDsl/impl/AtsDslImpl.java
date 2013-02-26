/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

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

import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ats Dsl</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getWorkDef <em>Work Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getUserDef <em>User Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getTeamDef <em>Team Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getActionableItemDef <em>Actionable Item Def</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AtsDslImpl extends MinimalEObjectImpl.Container implements AtsDsl
{
  /**
   * The cached value of the '{@link #getWorkDef() <em>Work Def</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWorkDef()
   * @generated
   * @ordered
   */
  protected WorkDef workDef;

  /**
   * The cached value of the '{@link #getUserDef() <em>User Def</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserDef()
   * @generated
   * @ordered
   */
  protected EList<UserDef> userDef;

  /**
   * The cached value of the '{@link #getTeamDef() <em>Team Def</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamDef()
   * @generated
   * @ordered
   */
  protected EList<TeamDef> teamDef;

  /**
   * The cached value of the '{@link #getActionableItemDef() <em>Actionable Item Def</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActionableItemDef()
   * @generated
   * @ordered
   */
  protected EList<ActionableItemDef> actionableItemDef;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AtsDslImpl()
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
    return AtsDslPackage.Literals.ATS_DSL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkDef getWorkDef()
  {
    return workDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetWorkDef(WorkDef newWorkDef, NotificationChain msgs)
  {
    WorkDef oldWorkDef = workDef;
    workDef = newWorkDef;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AtsDslPackage.ATS_DSL__WORK_DEF, oldWorkDef, newWorkDef);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setWorkDef(WorkDef newWorkDef)
  {
    if (newWorkDef != workDef)
    {
      NotificationChain msgs = null;
      if (workDef != null)
        msgs = ((InternalEObject)workDef).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AtsDslPackage.ATS_DSL__WORK_DEF, null, msgs);
      if (newWorkDef != null)
        msgs = ((InternalEObject)newWorkDef).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AtsDslPackage.ATS_DSL__WORK_DEF, null, msgs);
      msgs = basicSetWorkDef(newWorkDef, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.ATS_DSL__WORK_DEF, newWorkDef, newWorkDef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserDef> getUserDef()
  {
    if (userDef == null)
    {
      userDef = new EObjectContainmentEList<UserDef>(UserDef.class, this, AtsDslPackage.ATS_DSL__USER_DEF);
    }
    return userDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<TeamDef> getTeamDef()
  {
    if (teamDef == null)
    {
      teamDef = new EObjectContainmentEList<TeamDef>(TeamDef.class, this, AtsDslPackage.ATS_DSL__TEAM_DEF);
    }
    return teamDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ActionableItemDef> getActionableItemDef()
  {
    if (actionableItemDef == null)
    {
      actionableItemDef = new EObjectContainmentEList<ActionableItemDef>(ActionableItemDef.class, this, AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF);
    }
    return actionableItemDef;
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
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        return basicSetWorkDef(null, msgs);
      case AtsDslPackage.ATS_DSL__USER_DEF:
        return ((InternalEList<?>)getUserDef()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        return ((InternalEList<?>)getTeamDef()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        return ((InternalEList<?>)getActionableItemDef()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        return getWorkDef();
      case AtsDslPackage.ATS_DSL__USER_DEF:
        return getUserDef();
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        return getTeamDef();
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        return getActionableItemDef();
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
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        setWorkDef((WorkDef)newValue);
        return;
      case AtsDslPackage.ATS_DSL__USER_DEF:
        getUserDef().clear();
        getUserDef().addAll((Collection<? extends UserDef>)newValue);
        return;
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        getTeamDef().clear();
        getTeamDef().addAll((Collection<? extends TeamDef>)newValue);
        return;
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        getActionableItemDef().clear();
        getActionableItemDef().addAll((Collection<? extends ActionableItemDef>)newValue);
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
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        setWorkDef((WorkDef)null);
        return;
      case AtsDslPackage.ATS_DSL__USER_DEF:
        getUserDef().clear();
        return;
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        getTeamDef().clear();
        return;
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        getActionableItemDef().clear();
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
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        return workDef != null;
      case AtsDslPackage.ATS_DSL__USER_DEF:
        return userDef != null && !userDef.isEmpty();
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        return teamDef != null && !teamDef.isEmpty();
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        return actionableItemDef != null && !actionableItemDef.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //AtsDslImpl
