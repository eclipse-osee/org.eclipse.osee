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

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Actionable Item Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getAiDefOption <em>Ai Def Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getActive <em>Active</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getActionable <em>Actionable</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getLead <em>Lead</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getOwner <em>Owner</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getStaticId <em>Static Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getTeamDef <em>Team Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getAccessContextId <em>Access Context Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getRules <em>Rules</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ActionableItemDefImpl#getChildren <em>Children</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionableItemDefImpl extends MinimalEObjectImpl.Container implements ActionableItemDef
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getAiDefOption() <em>Ai Def Option</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAiDefOption()
   * @generated
   * @ordered
   */
  protected EList<String> aiDefOption;

  /**
   * The default value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUuid()
   * @generated
   * @ordered
   */
  protected static final int UUID_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUuid()
   * @generated
   * @ordered
   */
  protected int uuid = UUID_EDEFAULT;

  /**
   * The default value of the '{@link #getActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActive()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ACTIVE_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getActive() <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActive()
   * @generated
   * @ordered
   */
  protected BooleanDef active = ACTIVE_EDEFAULT;

  /**
   * The default value of the '{@link #getActionable() <em>Actionable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActionable()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ACTIONABLE_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getActionable() <em>Actionable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActionable()
   * @generated
   * @ordered
   */
  protected BooleanDef actionable = ACTIONABLE_EDEFAULT;

  /**
   * The cached value of the '{@link #getLead() <em>Lead</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLead()
   * @generated
   * @ordered
   */
  protected EList<UserRef> lead;

  /**
   * The cached value of the '{@link #getOwner() <em>Owner</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOwner()
   * @generated
   * @ordered
   */
  protected EList<UserRef> owner;

  /**
   * The cached value of the '{@link #getStaticId() <em>Static Id</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStaticId()
   * @generated
   * @ordered
   */
  protected EList<String> staticId;

  /**
   * The default value of the '{@link #getTeamDef() <em>Team Def</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamDef()
   * @generated
   * @ordered
   */
  protected static final String TEAM_DEF_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTeamDef() <em>Team Def</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTeamDef()
   * @generated
   * @ordered
   */
  protected String teamDef = TEAM_DEF_EDEFAULT;

  /**
   * The cached value of the '{@link #getAccessContextId() <em>Access Context Id</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessContextId()
   * @generated
   * @ordered
   */
  protected EList<String> accessContextId;

  /**
   * The cached value of the '{@link #getRules() <em>Rules</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRules()
   * @generated
   * @ordered
   */
  protected EList<String> rules;

  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<ActionableItemDef> children;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ActionableItemDefImpl()
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
    return AtsDslPackage.Literals.ACTIONABLE_ITEM_DEF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.ACTIONABLE_ITEM_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getAiDefOption()
  {
    if (aiDefOption == null)
    {
      aiDefOption = new EDataTypeEList<String>(String.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__AI_DEF_OPTION);
    }
    return aiDefOption;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getUuid()
  {
    return uuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUuid(int newUuid)
  {
    int oldUuid = uuid;
    uuid = newUuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.ACTIONABLE_ITEM_DEF__UUID, oldUuid, uuid));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getActive()
  {
    return active;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActive(BooleanDef newActive)
  {
    BooleanDef oldActive = active;
    active = newActive == null ? ACTIVE_EDEFAULT : newActive;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIVE, oldActive, active));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getActionable()
  {
    return actionable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActionable(BooleanDef newActionable)
  {
    BooleanDef oldActionable = actionable;
    actionable = newActionable == null ? ACTIONABLE_EDEFAULT : newActionable;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIONABLE, oldActionable, actionable));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserRef> getLead()
  {
    if (lead == null)
    {
      lead = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__LEAD);
    }
    return lead;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserRef> getOwner()
  {
    if (owner == null)
    {
      owner = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__OWNER);
    }
    return owner;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getStaticId()
  {
    if (staticId == null)
    {
      staticId = new EDataTypeEList<String>(String.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__STATIC_ID);
    }
    return staticId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTeamDef()
  {
    return teamDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTeamDef(String newTeamDef)
  {
    String oldTeamDef = teamDef;
    teamDef = newTeamDef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.ACTIONABLE_ITEM_DEF__TEAM_DEF, oldTeamDef, teamDef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getAccessContextId()
  {
    if (accessContextId == null)
    {
      accessContextId = new EDataTypeEList<String>(String.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID);
    }
    return accessContextId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getRules()
  {
    if (rules == null)
    {
      rules = new EDataTypeEList<String>(String.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__RULES);
    }
    return rules;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ActionableItemDef> getChildren()
  {
    if (children == null)
    {
      children = new EObjectContainmentEList<ActionableItemDef>(ActionableItemDef.class, this, AtsDslPackage.ACTIONABLE_ITEM_DEF__CHILDREN);
    }
    return children;
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
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__LEAD:
        return ((InternalEList<?>)getLead()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__OWNER:
        return ((InternalEList<?>)getOwner()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__CHILDREN:
        return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__NAME:
        return getName();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__AI_DEF_OPTION:
        return getAiDefOption();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__UUID:
        return getUuid();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIVE:
        return getActive();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIONABLE:
        return getActionable();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__LEAD:
        return getLead();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__OWNER:
        return getOwner();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__STATIC_ID:
        return getStaticId();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__TEAM_DEF:
        return getTeamDef();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID:
        return getAccessContextId();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__RULES:
        return getRules();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__CHILDREN:
        return getChildren();
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
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__AI_DEF_OPTION:
        getAiDefOption().clear();
        getAiDefOption().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__UUID:
        setUuid((Integer)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIVE:
        setActive((BooleanDef)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIONABLE:
        setActionable((BooleanDef)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__LEAD:
        getLead().clear();
        getLead().addAll((Collection<? extends UserRef>)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__OWNER:
        getOwner().clear();
        getOwner().addAll((Collection<? extends UserRef>)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__STATIC_ID:
        getStaticId().clear();
        getStaticId().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__TEAM_DEF:
        setTeamDef((String)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID:
        getAccessContextId().clear();
        getAccessContextId().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__RULES:
        getRules().clear();
        getRules().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__CHILDREN:
        getChildren().clear();
        getChildren().addAll((Collection<? extends ActionableItemDef>)newValue);
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
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__AI_DEF_OPTION:
        getAiDefOption().clear();
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__UUID:
        setUuid(UUID_EDEFAULT);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIVE:
        setActive(ACTIVE_EDEFAULT);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIONABLE:
        setActionable(ACTIONABLE_EDEFAULT);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__LEAD:
        getLead().clear();
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__OWNER:
        getOwner().clear();
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__STATIC_ID:
        getStaticId().clear();
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__TEAM_DEF:
        setTeamDef(TEAM_DEF_EDEFAULT);
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID:
        getAccessContextId().clear();
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__RULES:
        getRules().clear();
        return;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__CHILDREN:
        getChildren().clear();
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
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__AI_DEF_OPTION:
        return aiDefOption != null && !aiDefOption.isEmpty();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__UUID:
        return uuid != UUID_EDEFAULT;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIVE:
        return active != ACTIVE_EDEFAULT;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACTIONABLE:
        return actionable != ACTIONABLE_EDEFAULT;
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__LEAD:
        return lead != null && !lead.isEmpty();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__OWNER:
        return owner != null && !owner.isEmpty();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__STATIC_ID:
        return staticId != null && !staticId.isEmpty();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__TEAM_DEF:
        return TEAM_DEF_EDEFAULT == null ? teamDef != null : !TEAM_DEF_EDEFAULT.equals(teamDef);
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__ACCESS_CONTEXT_ID:
        return accessContextId != null && !accessContextId.isEmpty();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__RULES:
        return rules != null && !rules.isEmpty();
      case AtsDslPackage.ACTIONABLE_ITEM_DEF__CHILDREN:
        return children != null && !children.isEmpty();
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
    result.append(" (name: ");
    result.append(name);
    result.append(", aiDefOption: ");
    result.append(aiDefOption);
    result.append(", uuid: ");
    result.append(uuid);
    result.append(", active: ");
    result.append(active);
    result.append(", actionable: ");
    result.append(actionable);
    result.append(", staticId: ");
    result.append(staticId);
    result.append(", teamDef: ");
    result.append(teamDef);
    result.append(", accessContextId: ");
    result.append(accessContextId);
    result.append(", rules: ");
    result.append(rules);
    result.append(')');
    return result.toString();
  }

} //ActionableItemDefImpl
