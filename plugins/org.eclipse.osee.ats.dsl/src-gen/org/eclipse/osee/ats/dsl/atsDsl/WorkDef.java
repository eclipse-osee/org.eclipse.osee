/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Work Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStartState <em>Start State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getWidgetDefs <em>Widget Defs</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getDecisionReviewDefs <em>Decision Review Defs</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getPeerReviewDefs <em>Peer Review Defs</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStates <em>States</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef()
 * @model
 * @generated
 */
public interface WorkDef extends EObject
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_Id()
   * @model unique="false"
   * @generated
   */
  EList<String> getId();

  /**
   * Returns the value of the '<em><b>Start State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Start State</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Start State</em>' reference.
   * @see #setStartState(StateDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_StartState()
   * @model
   * @generated
   */
  StateDef getStartState();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef#getStartState <em>Start State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Start State</em>' reference.
   * @see #getStartState()
   * @generated
   */
  void setStartState(StateDef value);

  /**
   * Returns the value of the '<em><b>Widget Defs</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.WidgetDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Widget Defs</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Widget Defs</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_WidgetDefs()
   * @model containment="true"
   * @generated
   */
  EList<WidgetDef> getWidgetDefs();

  /**
   * Returns the value of the '<em><b>Decision Review Defs</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Decision Review Defs</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Decision Review Defs</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_DecisionReviewDefs()
   * @model containment="true"
   * @generated
   */
  EList<DecisionReviewDef> getDecisionReviewDefs();

  /**
   * Returns the value of the '<em><b>Peer Review Defs</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Peer Review Defs</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Peer Review Defs</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_PeerReviewDefs()
   * @model containment="true"
   * @generated
   */
  EList<PeerReviewDef> getPeerReviewDefs();

  /**
   * Returns the value of the '<em><b>States</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.StateDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>States</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>States</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkDef_States()
   * @model containment="true"
   * @generated
   */
  EList<StateDef> getStates();

} // WorkDef
