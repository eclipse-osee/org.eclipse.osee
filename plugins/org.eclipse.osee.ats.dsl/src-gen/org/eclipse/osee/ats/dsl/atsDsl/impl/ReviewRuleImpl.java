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

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.AttrDef;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl#getAssignees <em>Assignees</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl#getRelatedToState <em>Related To State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl#getBlockingType <em>Blocking Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl#getStateEvent <em>State Event</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.ReviewRuleImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReviewRuleImpl extends RuleImpl implements ReviewRule
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
   * The default value of the '{@link #getRelatedToState() <em>Related To State</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedToState()
   * @generated
   * @ordered
   */
  protected static final String RELATED_TO_STATE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRelatedToState() <em>Related To State</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedToState()
   * @generated
   * @ordered
   */
  protected String relatedToState = RELATED_TO_STATE_EDEFAULT;

  /**
   * The default value of the '{@link #getBlockingType() <em>Blocking Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBlockingType()
   * @generated
   * @ordered
   */
  protected static final ReviewBlockingType BLOCKING_TYPE_EDEFAULT = ReviewBlockingType.TRANSITION;

  /**
   * The cached value of the '{@link #getBlockingType() <em>Blocking Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBlockingType()
   * @generated
   * @ordered
   */
  protected ReviewBlockingType blockingType = BLOCKING_TYPE_EDEFAULT;

  /**
   * The default value of the '{@link #getStateEvent() <em>State Event</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStateEvent()
   * @generated
   * @ordered
   */
  protected static final WorkflowEventType STATE_EVENT_EDEFAULT = WorkflowEventType.TRANSITION_TO;

  /**
   * The cached value of the '{@link #getStateEvent() <em>State Event</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStateEvent()
   * @generated
   * @ordered
   */
  protected WorkflowEventType stateEvent = STATE_EVENT_EDEFAULT;

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
  protected ReviewRuleImpl()
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
    return AtsDslPackage.Literals.REVIEW_RULE;
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
      assignees = new EObjectContainmentEList<UserDef>(UserDef.class, this, AtsDslPackage.REVIEW_RULE__ASSIGNEES);
    }
    return assignees;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRelatedToState()
  {
    return relatedToState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelatedToState(String newRelatedToState)
  {
    String oldRelatedToState = relatedToState;
    relatedToState = newRelatedToState;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.REVIEW_RULE__RELATED_TO_STATE, oldRelatedToState, relatedToState));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewBlockingType getBlockingType()
  {
    return blockingType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBlockingType(ReviewBlockingType newBlockingType)
  {
    ReviewBlockingType oldBlockingType = blockingType;
    blockingType = newBlockingType == null ? BLOCKING_TYPE_EDEFAULT : newBlockingType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.REVIEW_RULE__BLOCKING_TYPE, oldBlockingType, blockingType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkflowEventType getStateEvent()
  {
    return stateEvent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStateEvent(WorkflowEventType newStateEvent)
  {
    WorkflowEventType oldStateEvent = stateEvent;
    stateEvent = newStateEvent == null ? STATE_EVENT_EDEFAULT : newStateEvent;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.REVIEW_RULE__STATE_EVENT, oldStateEvent, stateEvent));
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
      attributes = new EObjectContainmentEList<AttrDef>(AttrDef.class, this, AtsDslPackage.REVIEW_RULE__ATTRIBUTES);
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
      case AtsDslPackage.REVIEW_RULE__ASSIGNEES:
        return ((InternalEList<?>)getAssignees()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.REVIEW_RULE__ATTRIBUTES:
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
      case AtsDslPackage.REVIEW_RULE__ASSIGNEES:
        return getAssignees();
      case AtsDslPackage.REVIEW_RULE__RELATED_TO_STATE:
        return getRelatedToState();
      case AtsDslPackage.REVIEW_RULE__BLOCKING_TYPE:
        return getBlockingType();
      case AtsDslPackage.REVIEW_RULE__STATE_EVENT:
        return getStateEvent();
      case AtsDslPackage.REVIEW_RULE__ATTRIBUTES:
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
      case AtsDslPackage.REVIEW_RULE__ASSIGNEES:
        getAssignees().clear();
        getAssignees().addAll((Collection<? extends UserDef>)newValue);
        return;
      case AtsDslPackage.REVIEW_RULE__RELATED_TO_STATE:
        setRelatedToState((String)newValue);
        return;
      case AtsDslPackage.REVIEW_RULE__BLOCKING_TYPE:
        setBlockingType((ReviewBlockingType)newValue);
        return;
      case AtsDslPackage.REVIEW_RULE__STATE_EVENT:
        setStateEvent((WorkflowEventType)newValue);
        return;
      case AtsDslPackage.REVIEW_RULE__ATTRIBUTES:
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
      case AtsDslPackage.REVIEW_RULE__ASSIGNEES:
        getAssignees().clear();
        return;
      case AtsDslPackage.REVIEW_RULE__RELATED_TO_STATE:
        setRelatedToState(RELATED_TO_STATE_EDEFAULT);
        return;
      case AtsDslPackage.REVIEW_RULE__BLOCKING_TYPE:
        setBlockingType(BLOCKING_TYPE_EDEFAULT);
        return;
      case AtsDslPackage.REVIEW_RULE__STATE_EVENT:
        setStateEvent(STATE_EVENT_EDEFAULT);
        return;
      case AtsDslPackage.REVIEW_RULE__ATTRIBUTES:
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
      case AtsDslPackage.REVIEW_RULE__ASSIGNEES:
        return assignees != null && !assignees.isEmpty();
      case AtsDslPackage.REVIEW_RULE__RELATED_TO_STATE:
        return RELATED_TO_STATE_EDEFAULT == null ? relatedToState != null : !RELATED_TO_STATE_EDEFAULT.equals(relatedToState);
      case AtsDslPackage.REVIEW_RULE__BLOCKING_TYPE:
        return blockingType != BLOCKING_TYPE_EDEFAULT;
      case AtsDslPackage.REVIEW_RULE__STATE_EVENT:
        return stateEvent != STATE_EVENT_EDEFAULT;
      case AtsDslPackage.REVIEW_RULE__ATTRIBUTES:
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
    result.append(" (relatedToState: ");
    result.append(relatedToState);
    result.append(", blockingType: ");
    result.append(blockingType);
    result.append(", stateEvent: ");
    result.append(stateEvent);
    result.append(')');
    return result.toString();
  }

} //ReviewRuleImpl
