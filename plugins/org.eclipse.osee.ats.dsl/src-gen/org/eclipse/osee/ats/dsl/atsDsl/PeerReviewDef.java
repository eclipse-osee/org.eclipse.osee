/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Peer Review Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getRelatedToState <em>Related To State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getBlockingType <em>Blocking Type</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getStateEvent <em>State Event</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getAssigneeRefs <em>Assignee Refs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef()
 * @model
 * @generated
 */
public interface PeerReviewDef extends EObject
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Title</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Title</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Title</em>' attribute.
   * @see #setTitle(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_Title()
   * @model
   * @generated
   */
  String getTitle();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getTitle <em>Title</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Title</em>' attribute.
   * @see #getTitle()
   * @generated
   */
  void setTitle(String value);

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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Location</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Location</em>' attribute.
   * @see #setLocation(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_Location()
   * @model
   * @generated
   */
  String getLocation();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getLocation <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Location</em>' attribute.
   * @see #getLocation()
   * @generated
   */
  void setLocation(String value);

  /**
   * Returns the value of the '<em><b>Related To State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Related To State</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Related To State</em>' reference.
   * @see #setRelatedToState(StateDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_RelatedToState()
   * @model
   * @generated
   */
  StateDef getRelatedToState();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getRelatedToState <em>Related To State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Related To State</em>' reference.
   * @see #getRelatedToState()
   * @generated
   */
  void setRelatedToState(StateDef value);

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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_BlockingType()
   * @model
   * @generated
   */
  ReviewBlockingType getBlockingType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getBlockingType <em>Blocking Type</em>}' attribute.
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_StateEvent()
   * @model
   * @generated
   */
  WorkflowEventType getStateEvent();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef#getStateEvent <em>State Event</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State Event</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType
   * @see #getStateEvent()
   * @generated
   */
  void setStateEvent(WorkflowEventType value);

  /**
   * Returns the value of the '<em><b>Assignee Refs</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.UserRef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Assignee Refs</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Assignee Refs</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewDef_AssigneeRefs()
   * @model containment="true"
   * @generated
   */
  EList<UserRef> getAssigneeRefs();

} // PeerReviewDef
