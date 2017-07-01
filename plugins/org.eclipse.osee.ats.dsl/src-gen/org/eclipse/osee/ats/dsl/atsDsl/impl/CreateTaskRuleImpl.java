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

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.AttrDef;
import org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule;
import org.eclipse.osee.ats.dsl.atsDsl.OnEventType;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Create Task Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl#getAssignees <em>Assignees</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl#getRelatedState <em>Related State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl#getTaskWorkDef <em>Task Work Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl#getOnEvent <em>On Event</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateTaskRuleImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CreateTaskRuleImpl extends RuleImpl implements CreateTaskRule
{
  /**
   * The cached value of the '{@link #getAssignees() <em>Assignees</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAssignees()
   * @generated
   * @ordered
   */
  protected EList<UserDef> assignees;

  /**
   * The default value of the '{@link #getRelatedState() <em>Related State</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedState()
   * @generated
   * @ordered
   */
  protected static final String RELATED_STATE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRelatedState() <em>Related State</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedState()
   * @generated
   * @ordered
   */
  protected String relatedState = RELATED_STATE_EDEFAULT;

  /**
   * The default value of the '{@link #getTaskWorkDef() <em>Task Work Def</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTaskWorkDef()
   * @generated
   * @ordered
   */
  protected static final String TASK_WORK_DEF_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTaskWorkDef() <em>Task Work Def</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTaskWorkDef()
   * @generated
   * @ordered
   */
  protected String taskWorkDef = TASK_WORK_DEF_EDEFAULT;

  /**
   * The cached value of the '{@link #getOnEvent() <em>On Event</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOnEvent()
   * @generated
   * @ordered
   */
  protected EList<OnEventType> onEvent;

  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EList<AttrDef> attributes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CreateTaskRuleImpl()
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
    return AtsDslPackage.Literals.CREATE_TASK_RULE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UserDef> getAssignees()
  {
    if (assignees == null)
    {
      assignees = new EObjectContainmentEList<UserDef>(UserDef.class, this, AtsDslPackage.CREATE_TASK_RULE__ASSIGNEES);
    }
    return assignees;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRelatedState()
  {
    return relatedState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelatedState(String newRelatedState)
  {
    String oldRelatedState = relatedState;
    relatedState = newRelatedState;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.CREATE_TASK_RULE__RELATED_STATE, oldRelatedState, relatedState));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTaskWorkDef()
  {
    return taskWorkDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTaskWorkDef(String newTaskWorkDef)
  {
    String oldTaskWorkDef = taskWorkDef;
    taskWorkDef = newTaskWorkDef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.CREATE_TASK_RULE__TASK_WORK_DEF, oldTaskWorkDef, taskWorkDef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OnEventType> getOnEvent()
  {
    if (onEvent == null)
    {
      onEvent = new EDataTypeEList<OnEventType>(OnEventType.class, this, AtsDslPackage.CREATE_TASK_RULE__ON_EVENT);
    }
    return onEvent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AttrDef> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EObjectContainmentEList<AttrDef>(AttrDef.class, this, AtsDslPackage.CREATE_TASK_RULE__ATTRIBUTES);
    }
    return attributes;
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
      case AtsDslPackage.CREATE_TASK_RULE__ASSIGNEES:
        return ((InternalEList<?>)getAssignees()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.CREATE_TASK_RULE__ATTRIBUTES:
        return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.CREATE_TASK_RULE__ASSIGNEES:
        return getAssignees();
      case AtsDslPackage.CREATE_TASK_RULE__RELATED_STATE:
        return getRelatedState();
      case AtsDslPackage.CREATE_TASK_RULE__TASK_WORK_DEF:
        return getTaskWorkDef();
      case AtsDslPackage.CREATE_TASK_RULE__ON_EVENT:
        return getOnEvent();
      case AtsDslPackage.CREATE_TASK_RULE__ATTRIBUTES:
        return getAttributes();
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
      case AtsDslPackage.CREATE_TASK_RULE__ASSIGNEES:
        getAssignees().clear();
        getAssignees().addAll((Collection<? extends UserDef>)newValue);
        return;
      case AtsDslPackage.CREATE_TASK_RULE__RELATED_STATE:
        setRelatedState((String)newValue);
        return;
      case AtsDslPackage.CREATE_TASK_RULE__TASK_WORK_DEF:
        setTaskWorkDef((String)newValue);
        return;
      case AtsDslPackage.CREATE_TASK_RULE__ON_EVENT:
        getOnEvent().clear();
        getOnEvent().addAll((Collection<? extends OnEventType>)newValue);
        return;
      case AtsDslPackage.CREATE_TASK_RULE__ATTRIBUTES:
        getAttributes().clear();
        getAttributes().addAll((Collection<? extends AttrDef>)newValue);
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
      case AtsDslPackage.CREATE_TASK_RULE__ASSIGNEES:
        getAssignees().clear();
        return;
      case AtsDslPackage.CREATE_TASK_RULE__RELATED_STATE:
        setRelatedState(RELATED_STATE_EDEFAULT);
        return;
      case AtsDslPackage.CREATE_TASK_RULE__TASK_WORK_DEF:
        setTaskWorkDef(TASK_WORK_DEF_EDEFAULT);
        return;
      case AtsDslPackage.CREATE_TASK_RULE__ON_EVENT:
        getOnEvent().clear();
        return;
      case AtsDslPackage.CREATE_TASK_RULE__ATTRIBUTES:
        getAttributes().clear();
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
      case AtsDslPackage.CREATE_TASK_RULE__ASSIGNEES:
        return assignees != null && !assignees.isEmpty();
      case AtsDslPackage.CREATE_TASK_RULE__RELATED_STATE:
        return RELATED_STATE_EDEFAULT == null ? relatedState != null : !RELATED_STATE_EDEFAULT.equals(relatedState);
      case AtsDslPackage.CREATE_TASK_RULE__TASK_WORK_DEF:
        return TASK_WORK_DEF_EDEFAULT == null ? taskWorkDef != null : !TASK_WORK_DEF_EDEFAULT.equals(taskWorkDef);
      case AtsDslPackage.CREATE_TASK_RULE__ON_EVENT:
        return onEvent != null && !onEvent.isEmpty();
      case AtsDslPackage.CREATE_TASK_RULE__ATTRIBUTES:
        return attributes != null && !attributes.isEmpty();
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
    result.append(" (relatedState: ");
    result.append(relatedState);
    result.append(", taskWorkDef: ");
    result.append(taskWorkDef);
    result.append(", onEvent: ");
    result.append(onEvent);
    result.append(')');
    return result.toString();
  }

} //CreateTaskRuleImpl
