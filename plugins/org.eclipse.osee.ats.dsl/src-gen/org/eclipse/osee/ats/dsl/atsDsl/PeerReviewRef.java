/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Peer Review Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef#getPeerReview <em>Peer Review</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewRef()
 * @model
 * @generated
 */
public interface PeerReviewRef extends EObject
{
  /**
   * Returns the value of the '<em><b>Peer Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Peer Review</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Peer Review</em>' reference.
   * @see #setPeerReview(PeerReviewDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getPeerReviewRef_PeerReview()
   * @model
   * @generated
   */
  PeerReviewDef getPeerReview();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef#getPeerReview <em>Peer Review</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Peer Review</em>' reference.
   * @see #getPeerReview()
   * @generated
   */
  void setPeerReview(PeerReviewDef value);

} // PeerReviewRef
