/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Create Decision Review Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getAutoTransitionToDecision <em>Auto Transition To Decision</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getOptions <em>Options</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateDecisionReviewRule()
 * @model
 * @generated
 */
public interface CreateDecisionReviewRule extends ReviewRule
{
  /**
   * Returns the value of the '<em><b>Auto Transition To Decision</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Auto Transition To Decision</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Auto Transition To Decision</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setAutoTransitionToDecision(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateDecisionReviewRule_AutoTransitionToDecision()
   * @model
   * @generated
   */
  BooleanDef getAutoTransitionToDecision();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule#getAutoTransitionToDecision <em>Auto Transition To Decision</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Auto Transition To Decision</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getAutoTransitionToDecision()
   * @generated
   */
  void setAutoTransitionToDecision(BooleanDef value);

  /**
   * Returns the value of the '<em><b>Options</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Options</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Options</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateDecisionReviewRule_Options()
   * @model containment="true"
   * @generated
   */
  EList<DecisionReviewOpt> getOptions();

} // CreateDecisionReviewRule
