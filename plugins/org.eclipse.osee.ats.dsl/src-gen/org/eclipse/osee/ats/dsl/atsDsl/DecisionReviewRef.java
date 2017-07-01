/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Decision Review Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef#getDecisionReview <em>Decision Review</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getDecisionReviewRef()
 * @model
 * @generated
 */
public interface DecisionReviewRef extends EObject
{
  /**
   * Returns the value of the '<em><b>Decision Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Decision Review</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Decision Review</em>' reference.
   * @see #setDecisionReview(DecisionReviewDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getDecisionReviewRef_DecisionReview()
   * @model
   * @generated
   */
  DecisionReviewDef getDecisionReview();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef#getDecisionReview <em>Decision Review</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Decision Review</em>' reference.
   * @see #getDecisionReview()
   * @generated
   */
  void setDecisionReview(DecisionReviewDef value);

} // DecisionReviewRef
