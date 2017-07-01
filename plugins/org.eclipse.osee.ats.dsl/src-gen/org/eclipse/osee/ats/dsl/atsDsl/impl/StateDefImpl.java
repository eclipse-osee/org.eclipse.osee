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

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutType;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.ToState;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>State Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getPageType <em>Page Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getOrdinal <em>Ordinal</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getTransitionStates <em>Transition States</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getRules <em>Rules</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getDecisionReviews <em>Decision Reviews</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getPeerReviews <em>Peer Reviews</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getPercentWeight <em>Percent Weight</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getRecommendedPercentComplete <em>Recommended Percent Complete</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getColor <em>Color</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.StateDefImpl#getLayout <em>Layout</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StateDefImpl extends MinimalEObjectImpl.Container implements StateDef
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
   * The default value of the '{@link #getPageType() <em>Page Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPageType()
   * @generated
   * @ordered
   */
  protected static final String PAGE_TYPE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPageType() <em>Page Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPageType()
   * @generated
   * @ordered
   */
  protected String pageType = PAGE_TYPE_EDEFAULT;

  /**
   * The default value of the '{@link #getOrdinal() <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrdinal()
   * @generated
   * @ordered
   */
  protected static final int ORDINAL_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getOrdinal() <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrdinal()
   * @generated
   * @ordered
   */
  protected int ordinal = ORDINAL_EDEFAULT;

  /**
   * The cached value of the '{@link #getTransitionStates() <em>Transition States</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransitionStates()
   * @generated
   * @ordered
   */
  protected EList<ToState> transitionStates;

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
   * The cached value of the '{@link #getDecisionReviews() <em>Decision Reviews</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDecisionReviews()
   * @generated
   * @ordered
   */
  protected EList<DecisionReviewRef> decisionReviews;

  /**
   * The cached value of the '{@link #getPeerReviews() <em>Peer Reviews</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPeerReviews()
   * @generated
   * @ordered
   */
  protected EList<PeerReviewRef> peerReviews;

  /**
   * The default value of the '{@link #getPercentWeight() <em>Percent Weight</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPercentWeight()
   * @generated
   * @ordered
   */
  protected static final int PERCENT_WEIGHT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getPercentWeight() <em>Percent Weight</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPercentWeight()
   * @generated
   * @ordered
   */
  protected int percentWeight = PERCENT_WEIGHT_EDEFAULT;

  /**
   * The default value of the '{@link #getRecommendedPercentComplete() <em>Recommended Percent Complete</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRecommendedPercentComplete()
   * @generated
   * @ordered
   */
  protected static final int RECOMMENDED_PERCENT_COMPLETE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getRecommendedPercentComplete() <em>Recommended Percent Complete</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRecommendedPercentComplete()
   * @generated
   * @ordered
   */
  protected int recommendedPercentComplete = RECOMMENDED_PERCENT_COMPLETE_EDEFAULT;

  /**
   * The default value of the '{@link #getColor() <em>Color</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getColor()
   * @generated
   * @ordered
   */
  protected static final String COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getColor() <em>Color</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getColor()
   * @generated
   * @ordered
   */
  protected String color = COLOR_EDEFAULT;

  /**
   * The cached value of the '{@link #getLayout() <em>Layout</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLayout()
   * @generated
   * @ordered
   */
  protected LayoutType layout;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StateDefImpl()
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
    return AtsDslPackage.Literals.STATE_DEF;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__NAME, oldName, name));
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__DESCRIPTION, oldDescription, description));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPageType()
  {
    return pageType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPageType(String newPageType)
  {
    String oldPageType = pageType;
    pageType = newPageType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__PAGE_TYPE, oldPageType, pageType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getOrdinal()
  {
    return ordinal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOrdinal(int newOrdinal)
  {
    int oldOrdinal = ordinal;
    ordinal = newOrdinal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__ORDINAL, oldOrdinal, ordinal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ToState> getTransitionStates()
  {
    if (transitionStates == null)
    {
      transitionStates = new EObjectContainmentEList<ToState>(ToState.class, this, AtsDslPackage.STATE_DEF__TRANSITION_STATES);
    }
    return transitionStates;
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
      rules = new EDataTypeEList<String>(String.class, this, AtsDslPackage.STATE_DEF__RULES);
    }
    return rules;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DecisionReviewRef> getDecisionReviews()
  {
    if (decisionReviews == null)
    {
      decisionReviews = new EObjectContainmentEList<DecisionReviewRef>(DecisionReviewRef.class, this, AtsDslPackage.STATE_DEF__DECISION_REVIEWS);
    }
    return decisionReviews;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PeerReviewRef> getPeerReviews()
  {
    if (peerReviews == null)
    {
      peerReviews = new EObjectContainmentEList<PeerReviewRef>(PeerReviewRef.class, this, AtsDslPackage.STATE_DEF__PEER_REVIEWS);
    }
    return peerReviews;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getPercentWeight()
  {
    return percentWeight;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPercentWeight(int newPercentWeight)
  {
    int oldPercentWeight = percentWeight;
    percentWeight = newPercentWeight;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__PERCENT_WEIGHT, oldPercentWeight, percentWeight));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getRecommendedPercentComplete()
  {
    return recommendedPercentComplete;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRecommendedPercentComplete(int newRecommendedPercentComplete)
  {
    int oldRecommendedPercentComplete = recommendedPercentComplete;
    recommendedPercentComplete = newRecommendedPercentComplete;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__RECOMMENDED_PERCENT_COMPLETE, oldRecommendedPercentComplete, recommendedPercentComplete));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getColor()
  {
    return color;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setColor(String newColor)
  {
    String oldColor = color;
    color = newColor;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__COLOR, oldColor, color));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LayoutType getLayout()
  {
    return layout;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLayout(LayoutType newLayout, NotificationChain msgs)
  {
    LayoutType oldLayout = layout;
    layout = newLayout;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__LAYOUT, oldLayout, newLayout);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLayout(LayoutType newLayout)
  {
    if (newLayout != layout)
    {
      NotificationChain msgs = null;
      if (layout != null)
        msgs = ((InternalEObject)layout).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AtsDslPackage.STATE_DEF__LAYOUT, null, msgs);
      if (newLayout != null)
        msgs = ((InternalEObject)newLayout).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AtsDslPackage.STATE_DEF__LAYOUT, null, msgs);
      msgs = basicSetLayout(newLayout, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.STATE_DEF__LAYOUT, newLayout, newLayout));
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
      case AtsDslPackage.STATE_DEF__TRANSITION_STATES:
        return ((InternalEList<?>)getTransitionStates()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.STATE_DEF__DECISION_REVIEWS:
        return ((InternalEList<?>)getDecisionReviews()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.STATE_DEF__PEER_REVIEWS:
        return ((InternalEList<?>)getPeerReviews()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.STATE_DEF__LAYOUT:
        return basicSetLayout(null, msgs);
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
      case AtsDslPackage.STATE_DEF__NAME:
        return getName();
      case AtsDslPackage.STATE_DEF__DESCRIPTION:
        return getDescription();
      case AtsDslPackage.STATE_DEF__PAGE_TYPE:
        return getPageType();
      case AtsDslPackage.STATE_DEF__ORDINAL:
        return getOrdinal();
      case AtsDslPackage.STATE_DEF__TRANSITION_STATES:
        return getTransitionStates();
      case AtsDslPackage.STATE_DEF__RULES:
        return getRules();
      case AtsDslPackage.STATE_DEF__DECISION_REVIEWS:
        return getDecisionReviews();
      case AtsDslPackage.STATE_DEF__PEER_REVIEWS:
        return getPeerReviews();
      case AtsDslPackage.STATE_DEF__PERCENT_WEIGHT:
        return getPercentWeight();
      case AtsDslPackage.STATE_DEF__RECOMMENDED_PERCENT_COMPLETE:
        return getRecommendedPercentComplete();
      case AtsDslPackage.STATE_DEF__COLOR:
        return getColor();
      case AtsDslPackage.STATE_DEF__LAYOUT:
        return getLayout();
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
      case AtsDslPackage.STATE_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.STATE_DEF__DESCRIPTION:
        setDescription((String)newValue);
        return;
      case AtsDslPackage.STATE_DEF__PAGE_TYPE:
        setPageType((String)newValue);
        return;
      case AtsDslPackage.STATE_DEF__ORDINAL:
        setOrdinal((Integer)newValue);
        return;
      case AtsDslPackage.STATE_DEF__TRANSITION_STATES:
        getTransitionStates().clear();
        getTransitionStates().addAll((Collection<? extends ToState>)newValue);
        return;
      case AtsDslPackage.STATE_DEF__RULES:
        getRules().clear();
        getRules().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.STATE_DEF__DECISION_REVIEWS:
        getDecisionReviews().clear();
        getDecisionReviews().addAll((Collection<? extends DecisionReviewRef>)newValue);
        return;
      case AtsDslPackage.STATE_DEF__PEER_REVIEWS:
        getPeerReviews().clear();
        getPeerReviews().addAll((Collection<? extends PeerReviewRef>)newValue);
        return;
      case AtsDslPackage.STATE_DEF__PERCENT_WEIGHT:
        setPercentWeight((Integer)newValue);
        return;
      case AtsDslPackage.STATE_DEF__RECOMMENDED_PERCENT_COMPLETE:
        setRecommendedPercentComplete((Integer)newValue);
        return;
      case AtsDslPackage.STATE_DEF__COLOR:
        setColor((String)newValue);
        return;
      case AtsDslPackage.STATE_DEF__LAYOUT:
        setLayout((LayoutType)newValue);
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
      case AtsDslPackage.STATE_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__DESCRIPTION:
        setDescription(DESCRIPTION_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__PAGE_TYPE:
        setPageType(PAGE_TYPE_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__ORDINAL:
        setOrdinal(ORDINAL_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__TRANSITION_STATES:
        getTransitionStates().clear();
        return;
      case AtsDslPackage.STATE_DEF__RULES:
        getRules().clear();
        return;
      case AtsDslPackage.STATE_DEF__DECISION_REVIEWS:
        getDecisionReviews().clear();
        return;
      case AtsDslPackage.STATE_DEF__PEER_REVIEWS:
        getPeerReviews().clear();
        return;
      case AtsDslPackage.STATE_DEF__PERCENT_WEIGHT:
        setPercentWeight(PERCENT_WEIGHT_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__RECOMMENDED_PERCENT_COMPLETE:
        setRecommendedPercentComplete(RECOMMENDED_PERCENT_COMPLETE_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__COLOR:
        setColor(COLOR_EDEFAULT);
        return;
      case AtsDslPackage.STATE_DEF__LAYOUT:
        setLayout((LayoutType)null);
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
      case AtsDslPackage.STATE_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.STATE_DEF__DESCRIPTION:
        return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
      case AtsDslPackage.STATE_DEF__PAGE_TYPE:
        return PAGE_TYPE_EDEFAULT == null ? pageType != null : !PAGE_TYPE_EDEFAULT.equals(pageType);
      case AtsDslPackage.STATE_DEF__ORDINAL:
        return ordinal != ORDINAL_EDEFAULT;
      case AtsDslPackage.STATE_DEF__TRANSITION_STATES:
        return transitionStates != null && !transitionStates.isEmpty();
      case AtsDslPackage.STATE_DEF__RULES:
        return rules != null && !rules.isEmpty();
      case AtsDslPackage.STATE_DEF__DECISION_REVIEWS:
        return decisionReviews != null && !decisionReviews.isEmpty();
      case AtsDslPackage.STATE_DEF__PEER_REVIEWS:
        return peerReviews != null && !peerReviews.isEmpty();
      case AtsDslPackage.STATE_DEF__PERCENT_WEIGHT:
        return percentWeight != PERCENT_WEIGHT_EDEFAULT;
      case AtsDslPackage.STATE_DEF__RECOMMENDED_PERCENT_COMPLETE:
        return recommendedPercentComplete != RECOMMENDED_PERCENT_COMPLETE_EDEFAULT;
      case AtsDslPackage.STATE_DEF__COLOR:
        return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
      case AtsDslPackage.STATE_DEF__LAYOUT:
        return layout != null;
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
    result.append(", description: ");
    result.append(description);
    result.append(", pageType: ");
    result.append(pageType);
    result.append(", ordinal: ");
    result.append(ordinal);
    result.append(", rules: ");
    result.append(rules);
    result.append(", percentWeight: ");
    result.append(percentWeight);
    result.append(", recommendedPercentComplete: ");
    result.append(recommendedPercentComplete);
    result.append(", color: ");
    result.append(color);
    result.append(')');
    return result.toString();
  }

} //StateDefImpl
