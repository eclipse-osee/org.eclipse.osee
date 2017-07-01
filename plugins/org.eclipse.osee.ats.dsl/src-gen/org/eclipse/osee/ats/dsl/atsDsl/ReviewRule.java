/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Review Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getAssignees <em>Assignees</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getRelatedToState <em>Related To State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getBlockingType <em>Blocking Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getStateEvent <em>State Event</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getReviewRule()
 * @model
 * @generated
 */
public interface ReviewRule extends Rule
{
  /**
   * Returns the value of the '<em><b>Assignees</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.UserDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Assignees</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Assignees</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getReviewRule_Assignees()
   * @model containment="true"
   * @generated
   */
  EList<UserDef> getAssignees();

  /**
   * Returns the value of the '<em><b>Related To State</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Related To State</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Related To State</em>' attribute.
   * @see #setRelatedToState(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getReviewRule_RelatedToState()
   * @model
   * @generated
   */
  String getRelatedToState();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getRelatedToState <em>Related To State</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Related To State</em>' attribute.
   * @see #getRelatedToState()
   * @generated
   */
  void setRelatedToState(String value);

  /**
   * Returns the value of the '<em><b>Blocking Type</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Blocking Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Blocking Type</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType
   * @see #setBlockingType(ReviewBlockingType)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getReviewRule_BlockingType()
   * @model
   * @generated
   */
  ReviewBlockingType getBlockingType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getBlockingType <em>Blocking Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Blocking Type</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType
   * @see #getBlockingType()
   * @generated
   */
  void setBlockingType(ReviewBlockingType value);

  /**
   * Returns the value of the '<em><b>State Event</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>State Event</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>State Event</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType
   * @see #setStateEvent(WorkflowEventType)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getReviewRule_StateEvent()
   * @model
   * @generated
   */
  WorkflowEventType getStateEvent();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ReviewRule#getStateEvent <em>State Event</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State Event</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType
   * @see #getStateEvent()
   * @generated
   */
  void setStateEvent(WorkflowEventType value);

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getReviewRule_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<AttrDef> getAttributes();

} // ReviewRule
