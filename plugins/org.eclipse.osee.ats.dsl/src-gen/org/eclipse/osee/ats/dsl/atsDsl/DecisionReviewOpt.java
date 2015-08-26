/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Decision Review Opt</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getFollowup <em>Followup</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getDecisionReviewOpt()
 * @model
 * @generated
 */
public interface DecisionReviewOpt extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getDecisionReviewOpt_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Followup</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Followup</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Followup</em>' containment reference.
   * @see #setFollowup(FollowupRef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getDecisionReviewOpt_Followup()
   * @model containment="true"
   * @generated
   */
  FollowupRef getFollowup();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt#getFollowup <em>Followup</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Followup</em>' containment reference.
   * @see #getFollowup()
   * @generated
   */
  void setFollowup(FollowupRef value);

} // DecisionReviewOpt
