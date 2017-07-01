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
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Create Decision Review Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateDecisionReviewRuleImpl#getAutoTransitionToDecision <em>Auto Transition To Decision</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CreateDecisionReviewRuleImpl#getOptions <em>Options</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CreateDecisionReviewRuleImpl extends ReviewRuleImpl implements CreateDecisionReviewRule
{
  /**
   * The default value of the '{@link #getAutoTransitionToDecision() <em>Auto Transition To Decision</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAutoTransitionToDecision()
   * @generated
   * @ordered
   */
  protected static final BooleanDef AUTO_TRANSITION_TO_DECISION_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getAutoTransitionToDecision() <em>Auto Transition To Decision</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAutoTransitionToDecision()
   * @generated
   * @ordered
   */
  protected BooleanDef autoTransitionToDecision = AUTO_TRANSITION_TO_DECISION_EDEFAULT;

  /**
   * The cached value of the '{@link #getOptions() <em>Options</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOptions()
   * @generated
   * @ordered
   */
  protected EList<DecisionReviewOpt> options;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CreateDecisionReviewRuleImpl()
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
    return AtsDslPackage.Literals.CREATE_DECISION_REVIEW_RULE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getAutoTransitionToDecision()
  {
    return autoTransitionToDecision;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAutoTransitionToDecision(BooleanDef newAutoTransitionToDecision)
  {
    BooleanDef oldAutoTransitionToDecision = autoTransitionToDecision;
    autoTransitionToDecision = newAutoTransitionToDecision == null ? AUTO_TRANSITION_TO_DECISION_EDEFAULT : newAutoTransitionToDecision;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION, oldAutoTransitionToDecision, autoTransitionToDecision));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DecisionReviewOpt> getOptions()
  {
    if (options == null)
    {
      options = new EObjectContainmentEList<DecisionReviewOpt>(DecisionReviewOpt.class, this, AtsDslPackage.CREATE_DECISION_REVIEW_RULE__OPTIONS);
    }
    return options;
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
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__OPTIONS:
        return ((InternalEList<?>)getOptions()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION:
        return getAutoTransitionToDecision();
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__OPTIONS:
        return getOptions();
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
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION:
        setAutoTransitionToDecision((BooleanDef)newValue);
        return;
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__OPTIONS:
        getOptions().clear();
        getOptions().addAll((Collection<? extends DecisionReviewOpt>)newValue);
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
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION:
        setAutoTransitionToDecision(AUTO_TRANSITION_TO_DECISION_EDEFAULT);
        return;
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__OPTIONS:
        getOptions().clear();
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
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__AUTO_TRANSITION_TO_DECISION:
        return autoTransitionToDecision != AUTO_TRANSITION_TO_DECISION_EDEFAULT;
      case AtsDslPackage.CREATE_DECISION_REVIEW_RULE__OPTIONS:
        return options != null && !options.isEmpty();
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
    result.append(" (autoTransitionToDecision: ");
    result.append(autoTransitionToDecision);
    result.append(')');
    return result.toString();
  }

} //CreateDecisionReviewRuleImpl
