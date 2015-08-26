/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>State Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPageType <em>Page Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getOrdinal <em>Ordinal</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getTransitionStates <em>Transition States</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRules <em>Rules</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDecisionReviews <em>Decision Reviews</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPeerReviews <em>Peer Reviews</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPercentWeight <em>Percent Weight</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRecommendedPercentComplete <em>Recommended Percent Complete</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getColor <em>Color</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getLayout <em>Layout</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef()
 * @model
 * @generated
 */
public interface StateDef extends EObject
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Description</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>Page Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Page Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Page Type</em>' attribute.
   * @see #setPageType(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_PageType()
   * @model
   * @generated
   */
  String getPageType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPageType <em>Page Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Page Type</em>' attribute.
   * @see #getPageType()
   * @generated
   */
  void setPageType(String value);

  /**
   * Returns the value of the '<em><b>Ordinal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ordinal</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ordinal</em>' attribute.
   * @see #setOrdinal(int)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_Ordinal()
   * @model
   * @generated
   */
  int getOrdinal();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getOrdinal <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ordinal</em>' attribute.
   * @see #getOrdinal()
   * @generated
   */
  void setOrdinal(int value);

  /**
   * Returns the value of the '<em><b>Transition States</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.ToState}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transition States</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transition States</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_TransitionStates()
   * @model containment="true"
   * @generated
   */
  EList<ToState> getTransitionStates();

  /**
   * Returns the value of the '<em><b>Rules</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Rules</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Rules</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_Rules()
   * @model unique="false"
   * @generated
   */
  EList<String> getRules();

  /**
   * Returns the value of the '<em><b>Decision Reviews</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Decision Reviews</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Decision Reviews</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_DecisionReviews()
   * @model containment="true"
   * @generated
   */
  EList<DecisionReviewRef> getDecisionReviews();

  /**
   * Returns the value of the '<em><b>Peer Reviews</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Peer Reviews</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Peer Reviews</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_PeerReviews()
   * @model containment="true"
   * @generated
   */
  EList<PeerReviewRef> getPeerReviews();

  /**
   * Returns the value of the '<em><b>Percent Weight</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Percent Weight</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Percent Weight</em>' attribute.
   * @see #setPercentWeight(int)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_PercentWeight()
   * @model
   * @generated
   */
  int getPercentWeight();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getPercentWeight <em>Percent Weight</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Percent Weight</em>' attribute.
   * @see #getPercentWeight()
   * @generated
   */
  void setPercentWeight(int value);

  /**
   * Returns the value of the '<em><b>Recommended Percent Complete</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Recommended Percent Complete</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Recommended Percent Complete</em>' attribute.
   * @see #setRecommendedPercentComplete(int)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_RecommendedPercentComplete()
   * @model
   * @generated
   */
  int getRecommendedPercentComplete();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getRecommendedPercentComplete <em>Recommended Percent Complete</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Recommended Percent Complete</em>' attribute.
   * @see #getRecommendedPercentComplete()
   * @generated
   */
  void setRecommendedPercentComplete(int value);

  /**
   * Returns the value of the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Color</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Color</em>' attribute.
   * @see #setColor(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_Color()
   * @model
   * @generated
   */
  String getColor();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getColor <em>Color</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Color</em>' attribute.
   * @see #getColor()
   * @generated
   */
  void setColor(String value);

  /**
   * Returns the value of the '<em><b>Layout</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Layout</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Layout</em>' containment reference.
   * @see #setLayout(LayoutType)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getStateDef_Layout()
   * @model containment="true"
   * @generated
   */
  LayoutType getLayout();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.StateDef#getLayout <em>Layout</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Layout</em>' containment reference.
   * @see #getLayout()
   * @generated
   */
  void setLayout(LayoutType value);

} // StateDef
