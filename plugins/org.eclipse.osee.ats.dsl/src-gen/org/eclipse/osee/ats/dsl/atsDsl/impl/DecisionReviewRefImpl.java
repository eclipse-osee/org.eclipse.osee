/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Decision Review Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.DecisionReviewRefImpl#getDecisionReview <em>Decision Review</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DecisionReviewRefImpl extends MinimalEObjectImpl.Container implements DecisionReviewRef
{
  /**
   * The cached value of the '{@link #getDecisionReview() <em>Decision Review</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDecisionReview()
   * @generated
   * @ordered
   */
  protected DecisionReviewDef decisionReview;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DecisionReviewRefImpl()
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
    return AtsDslPackage.Literals.DECISION_REVIEW_REF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecisionReviewDef getDecisionReview()
  {
    if (decisionReview != null && decisionReview.eIsProxy())
    {
      InternalEObject oldDecisionReview = (InternalEObject)decisionReview;
      decisionReview = (DecisionReviewDef)eResolveProxy(oldDecisionReview);
      if (decisionReview != oldDecisionReview)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, AtsDslPackage.DECISION_REVIEW_REF__DECISION_REVIEW, oldDecisionReview, decisionReview));
      }
    }
    return decisionReview;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecisionReviewDef basicGetDecisionReview()
  {
    return decisionReview;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDecisionReview(DecisionReviewDef newDecisionReview)
  {
    DecisionReviewDef oldDecisionReview = decisionReview;
    decisionReview = newDecisionReview;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.DECISION_REVIEW_REF__DECISION_REVIEW, oldDecisionReview, decisionReview));
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
      case AtsDslPackage.DECISION_REVIEW_REF__DECISION_REVIEW:
        if (resolve) return getDecisionReview();
        return basicGetDecisionReview();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case AtsDslPackage.DECISION_REVIEW_REF__DECISION_REVIEW:
        setDecisionReview((DecisionReviewDef)newValue);
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
      case AtsDslPackage.DECISION_REVIEW_REF__DECISION_REVIEW:
        setDecisionReview((DecisionReviewDef)null);
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
      case AtsDslPackage.DECISION_REVIEW_REF__DECISION_REVIEW:
        return decisionReview != null;
    }
    return super.eIsSet(featureID);
  }

} //DecisionReviewRefImpl
