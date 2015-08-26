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

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Peer Review Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getRelatedToState <em>Related To State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getBlockingType <em>Blocking Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getStateEvent <em>State Event</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewDefImpl#getAssigneeRefs <em>Assignee Refs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PeerReviewDefImpl extends MinimalEObjectImpl.Container implements PeerReviewDef
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
   * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTitle()
   * @generated
   * @ordered
   */
  protected static final String TITLE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTitle()
   * @generated
   * @ordered
   */
  protected String title = TITLE_EDEFAULT;

  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected String description = DESCRIPTION_EDEFAULT;

  /**
   * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocation()
   * @generated
   * @ordered
   */
  protected static final String LOCATION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocation()
   * @generated
   * @ordered
   */
  protected String location = LOCATION_EDEFAULT;

  /**
   * The cached value of the '{@link #getRelatedToState() <em>Related To State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelatedToState()
   * @generated
   * @ordered
   */
  protected StateDef relatedToState;

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
  protected PeerReviewDefImpl()
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
    return AtsDslPackage.Literals.PEER_REVIEW_DEF;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTitle(String newTitle)
  {
    String oldTitle = title;
    title = newTitle;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__TITLE, oldTitle, title));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDescription(String newDescription)
  {
    String oldDescription = description;
    description = newDescription;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__DESCRIPTION, oldDescription, description));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLocation()
  {
    return location;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLocation(String newLocation)
  {
    String oldLocation = location;
    location = newLocation;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__LOCATION, oldLocation, location));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateDef getRelatedToState()
  {
    if (relatedToState != null && relatedToState.eIsProxy())
    {
      InternalEObject oldRelatedToState = (InternalEObject)relatedToState;
      relatedToState = (StateDef)eResolveProxy(oldRelatedToState);
      if (relatedToState != oldRelatedToState)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, AtsDslPackage.PEER_REVIEW_DEF__RELATED_TO_STATE, oldRelatedToState, relatedToState));
      }
    }
    return relatedToState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateDef basicGetRelatedToState()
  {
    return relatedToState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelatedToState(StateDef newRelatedToState)
  {
    StateDef oldRelatedToState = relatedToState;
    relatedToState = newRelatedToState;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__RELATED_TO_STATE, oldRelatedToState, relatedToState));
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__BLOCKING_TYPE, oldBlockingType, blockingType));
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_DEF__STATE_EVENT, oldStateEvent, stateEvent));
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
      assigneeRefs = new EObjectContainmentEList<UserRef>(UserRef.class, this, AtsDslPackage.PEER_REVIEW_DEF__ASSIGNEE_REFS);
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
      case AtsDslPackage.PEER_REVIEW_DEF__ASSIGNEE_REFS:
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
      case AtsDslPackage.PEER_REVIEW_DEF__NAME:
        return getName();
      case AtsDslPackage.PEER_REVIEW_DEF__TITLE:
        return getTitle();
      case AtsDslPackage.PEER_REVIEW_DEF__DESCRIPTION:
        return getDescription();
      case AtsDslPackage.PEER_REVIEW_DEF__LOCATION:
        return getLocation();
      case AtsDslPackage.PEER_REVIEW_DEF__RELATED_TO_STATE:
        if (resolve) return getRelatedToState();
        return basicGetRelatedToState();
      case AtsDslPackage.PEER_REVIEW_DEF__BLOCKING_TYPE:
        return getBlockingType();
      case AtsDslPackage.PEER_REVIEW_DEF__STATE_EVENT:
        return getStateEvent();
      case AtsDslPackage.PEER_REVIEW_DEF__ASSIGNEE_REFS:
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
      case AtsDslPackage.PEER_REVIEW_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__TITLE:
        setTitle((String)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__DESCRIPTION:
        setDescription((String)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__LOCATION:
        setLocation((String)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__RELATED_TO_STATE:
        setRelatedToState((StateDef)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__BLOCKING_TYPE:
        setBlockingType((ReviewBlockingType)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__STATE_EVENT:
        setStateEvent((WorkflowEventType)newValue);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__ASSIGNEE_REFS:
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
      case AtsDslPackage.PEER_REVIEW_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__TITLE:
        setTitle(TITLE_EDEFAULT);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__DESCRIPTION:
        setDescription(DESCRIPTION_EDEFAULT);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__LOCATION:
        setLocation(LOCATION_EDEFAULT);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__RELATED_TO_STATE:
        setRelatedToState((StateDef)null);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__BLOCKING_TYPE:
        setBlockingType(BLOCKING_TYPE_EDEFAULT);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__STATE_EVENT:
        setStateEvent(STATE_EVENT_EDEFAULT);
        return;
      case AtsDslPackage.PEER_REVIEW_DEF__ASSIGNEE_REFS:
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
      case AtsDslPackage.PEER_REVIEW_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.PEER_REVIEW_DEF__TITLE:
        return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
      case AtsDslPackage.PEER_REVIEW_DEF__DESCRIPTION:
        return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
      case AtsDslPackage.PEER_REVIEW_DEF__LOCATION:
        return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
      case AtsDslPackage.PEER_REVIEW_DEF__RELATED_TO_STATE:
        return relatedToState != null;
      case AtsDslPackage.PEER_REVIEW_DEF__BLOCKING_TYPE:
        return blockingType != BLOCKING_TYPE_EDEFAULT;
      case AtsDslPackage.PEER_REVIEW_DEF__STATE_EVENT:
        return stateEvent != STATE_EVENT_EDEFAULT;
      case AtsDslPackage.PEER_REVIEW_DEF__ASSIGNEE_REFS:
        return assigneeRefs != null && !assigneeRefs.isEmpty();
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
    result.append(", title: ");
    result.append(title);
    result.append(", description: ");
    result.append(description);
    result.append(", location: ");
    result.append(location);
    result.append(", blockingType: ");
    result.append(blockingType);
    result.append(", stateEvent: ");
    result.append(stateEvent);
    result.append(')');
    return result.toString();
  }

} //PeerReviewDefImpl
