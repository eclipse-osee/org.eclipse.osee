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

import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.ProgramDef;
import org.eclipse.osee.ats.dsl.atsDsl.Rule;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ats Dsl</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getUserDef <em>User Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getTeamDef <em>Team Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getActionableItemDef <em>Actionable Item Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getWorkDef <em>Work Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getProgram <em>Program</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslImpl#getRule <em>Rule</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AtsDslImpl extends MinimalEObjectImpl.Container implements AtsDsl
{
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
   * The cached value of the '{@link #getWorkDef() <em>Work Def</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWorkDef()
   * @generated
   * @ordered
   */
  protected EList<WorkDef> workDef;

  /**
   * The cached value of the '{@link #getProgram() <em>Program</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProgram()
   * @generated
   * @ordered
   */
  protected EList<ProgramDef> program;

  /**
   * The cached value of the '{@link #getRule() <em>Rule</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRule()
   * @generated
   * @ordered
   */
  protected EList<Rule> rule;

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
  public EList<WorkDef> getWorkDef()
  {
    if (workDef == null)
    {
      workDef = new EObjectContainmentEList<WorkDef>(WorkDef.class, this, AtsDslPackage.ATS_DSL__WORK_DEF);
    }
    return workDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ProgramDef> getProgram()
  {
    if (program == null)
    {
      program = new EObjectContainmentEList<ProgramDef>(ProgramDef.class, this, AtsDslPackage.ATS_DSL__PROGRAM);
    }
    return program;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Rule> getRule()
  {
    if (rule == null)
    {
      rule = new EObjectContainmentEList<Rule>(Rule.class, this, AtsDslPackage.ATS_DSL__RULE);
    }
    return rule;
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
      case AtsDslPackage.ATS_DSL__USER_DEF:
        return ((InternalEList<?>)getUserDef()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        return ((InternalEList<?>)getTeamDef()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        return ((InternalEList<?>)getActionableItemDef()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        return ((InternalEList<?>)getWorkDef()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__PROGRAM:
        return ((InternalEList<?>)getProgram()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ATS_DSL__RULE:
        return ((InternalEList<?>)getRule()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.ATS_DSL__USER_DEF:
        return getUserDef();
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        return getTeamDef();
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        return getActionableItemDef();
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        return getWorkDef();
      case AtsDslPackage.ATS_DSL__PROGRAM:
        return getProgram();
      case AtsDslPackage.ATS_DSL__RULE:
        return getRule();
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
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        getWorkDef().clear();
        getWorkDef().addAll((Collection<? extends WorkDef>)newValue);
        return;
      case AtsDslPackage.ATS_DSL__PROGRAM:
        getProgram().clear();
        getProgram().addAll((Collection<? extends ProgramDef>)newValue);
        return;
      case AtsDslPackage.ATS_DSL__RULE:
        getRule().clear();
        getRule().addAll((Collection<? extends Rule>)newValue);
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
      case AtsDslPackage.ATS_DSL__USER_DEF:
        getUserDef().clear();
        return;
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        getTeamDef().clear();
        return;
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        getActionableItemDef().clear();
        return;
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        getWorkDef().clear();
        return;
      case AtsDslPackage.ATS_DSL__PROGRAM:
        getProgram().clear();
        return;
      case AtsDslPackage.ATS_DSL__RULE:
        getRule().clear();
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
      case AtsDslPackage.ATS_DSL__USER_DEF:
        return userDef != null && !userDef.isEmpty();
      case AtsDslPackage.ATS_DSL__TEAM_DEF:
        return teamDef != null && !teamDef.isEmpty();
      case AtsDslPackage.ATS_DSL__ACTIONABLE_ITEM_DEF:
        return actionableItemDef != null && !actionableItemDef.isEmpty();
      case AtsDslPackage.ATS_DSL__WORK_DEF:
        return workDef != null && !workDef.isEmpty();
      case AtsDslPackage.ATS_DSL__PROGRAM:
        return program != null && !program.isEmpty();
      case AtsDslPackage.ATS_DSL__RULE:
        return rule != null && !rule.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //AtsDslImpl
