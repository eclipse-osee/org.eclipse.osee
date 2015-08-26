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
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Work Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getStartState <em>Start State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getWidgetDefs <em>Widget Defs</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getDecisionReviewDefs <em>Decision Review Defs</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getPeerReviewDefs <em>Peer Review Defs</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WorkDefImpl#getStates <em>States</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WorkDefImpl extends MinimalEObjectImpl.Container implements WorkDef
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
   * The cached value of the '{@link #getId() <em>Id</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected EList<String> id;

  /**
   * The cached value of the '{@link #getStartState() <em>Start State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStartState()
   * @generated
   * @ordered
   */
  protected StateDef startState;

  /**
   * The cached value of the '{@link #getWidgetDefs() <em>Widget Defs</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWidgetDefs()
   * @generated
   * @ordered
   */
  protected EList<WidgetDef> widgetDefs;

  /**
   * The cached value of the '{@link #getDecisionReviewDefs() <em>Decision Review Defs</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDecisionReviewDefs()
   * @generated
   * @ordered
   */
  protected EList<DecisionReviewDef> decisionReviewDefs;

  /**
   * The cached value of the '{@link #getPeerReviewDefs() <em>Peer Review Defs</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPeerReviewDefs()
   * @generated
   * @ordered
   */
  protected EList<PeerReviewDef> peerReviewDefs;

  /**
   * The cached value of the '{@link #getStates() <em>States</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStates()
   * @generated
   * @ordered
   */
  protected EList<StateDef> states;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WorkDefImpl()
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
    return AtsDslPackage.Literals.WORK_DEF;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WORK_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getId()
  {
    if (id == null)
    {
      id = new EDataTypeEList<String>(String.class, this, AtsDslPackage.WORK_DEF__ID);
    }
    return id;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateDef getStartState()
  {
    if (startState != null && startState.eIsProxy())
    {
      InternalEObject oldStartState = (InternalEObject)startState;
      startState = (StateDef)eResolveProxy(oldStartState);
      if (startState != oldStartState)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, AtsDslPackage.WORK_DEF__START_STATE, oldStartState, startState));
      }
    }
    return startState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateDef basicGetStartState()
  {
    return startState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStartState(StateDef newStartState)
  {
    StateDef oldStartState = startState;
    startState = newStartState;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WORK_DEF__START_STATE, oldStartState, startState));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<WidgetDef> getWidgetDefs()
  {
    if (widgetDefs == null)
    {
      widgetDefs = new EObjectContainmentEList<WidgetDef>(WidgetDef.class, this, AtsDslPackage.WORK_DEF__WIDGET_DEFS);
    }
    return widgetDefs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DecisionReviewDef> getDecisionReviewDefs()
  {
    if (decisionReviewDefs == null)
    {
      decisionReviewDefs = new EObjectContainmentEList<DecisionReviewDef>(DecisionReviewDef.class, this, AtsDslPackage.WORK_DEF__DECISION_REVIEW_DEFS);
    }
    return decisionReviewDefs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PeerReviewDef> getPeerReviewDefs()
  {
    if (peerReviewDefs == null)
    {
      peerReviewDefs = new EObjectContainmentEList<PeerReviewDef>(PeerReviewDef.class, this, AtsDslPackage.WORK_DEF__PEER_REVIEW_DEFS);
    }
    return peerReviewDefs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<StateDef> getStates()
  {
    if (states == null)
    {
      states = new EObjectContainmentEList<StateDef>(StateDef.class, this, AtsDslPackage.WORK_DEF__STATES);
    }
    return states;
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
      case AtsDslPackage.WORK_DEF__WIDGET_DEFS:
        return ((InternalEList<?>)getWidgetDefs()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.WORK_DEF__DECISION_REVIEW_DEFS:
        return ((InternalEList<?>)getDecisionReviewDefs()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.WORK_DEF__PEER_REVIEW_DEFS:
        return ((InternalEList<?>)getPeerReviewDefs()).basicRemove(otherEnd, msgs);
      case AtsDslPackage.WORK_DEF__STATES:
        return ((InternalEList<?>)getStates()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.WORK_DEF__NAME:
        return getName();
      case AtsDslPackage.WORK_DEF__ID:
        return getId();
      case AtsDslPackage.WORK_DEF__START_STATE:
        if (resolve) return getStartState();
        return basicGetStartState();
      case AtsDslPackage.WORK_DEF__WIDGET_DEFS:
        return getWidgetDefs();
      case AtsDslPackage.WORK_DEF__DECISION_REVIEW_DEFS:
        return getDecisionReviewDefs();
      case AtsDslPackage.WORK_DEF__PEER_REVIEW_DEFS:
        return getPeerReviewDefs();
      case AtsDslPackage.WORK_DEF__STATES:
        return getStates();
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
      case AtsDslPackage.WORK_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.WORK_DEF__ID:
        getId().clear();
        getId().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.WORK_DEF__START_STATE:
        setStartState((StateDef)newValue);
        return;
      case AtsDslPackage.WORK_DEF__WIDGET_DEFS:
        getWidgetDefs().clear();
        getWidgetDefs().addAll((Collection<? extends WidgetDef>)newValue);
        return;
      case AtsDslPackage.WORK_DEF__DECISION_REVIEW_DEFS:
        getDecisionReviewDefs().clear();
        getDecisionReviewDefs().addAll((Collection<? extends DecisionReviewDef>)newValue);
        return;
      case AtsDslPackage.WORK_DEF__PEER_REVIEW_DEFS:
        getPeerReviewDefs().clear();
        getPeerReviewDefs().addAll((Collection<? extends PeerReviewDef>)newValue);
        return;
      case AtsDslPackage.WORK_DEF__STATES:
        getStates().clear();
        getStates().addAll((Collection<? extends StateDef>)newValue);
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
      case AtsDslPackage.WORK_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.WORK_DEF__ID:
        getId().clear();
        return;
      case AtsDslPackage.WORK_DEF__START_STATE:
        setStartState((StateDef)null);
        return;
      case AtsDslPackage.WORK_DEF__WIDGET_DEFS:
        getWidgetDefs().clear();
        return;
      case AtsDslPackage.WORK_DEF__DECISION_REVIEW_DEFS:
        getDecisionReviewDefs().clear();
        return;
      case AtsDslPackage.WORK_DEF__PEER_REVIEW_DEFS:
        getPeerReviewDefs().clear();
        return;
      case AtsDslPackage.WORK_DEF__STATES:
        getStates().clear();
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
      case AtsDslPackage.WORK_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.WORK_DEF__ID:
        return id != null && !id.isEmpty();
      case AtsDslPackage.WORK_DEF__START_STATE:
        return startState != null;
      case AtsDslPackage.WORK_DEF__WIDGET_DEFS:
        return widgetDefs != null && !widgetDefs.isEmpty();
      case AtsDslPackage.WORK_DEF__DECISION_REVIEW_DEFS:
        return decisionReviewDefs != null && !decisionReviewDefs.isEmpty();
      case AtsDslPackage.WORK_DEF__PEER_REVIEW_DEFS:
        return peerReviewDefs != null && !peerReviewDefs.isEmpty();
      case AtsDslPackage.WORK_DEF__STATES:
        return states != null && !states.isEmpty();
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
    result.append(", id: ");
    result.append(id);
    result.append(')');
    return result.toString();
  }

} //WorkDefImpl
