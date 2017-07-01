/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Peer Review Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.PeerReviewRefImpl#getPeerReview <em>Peer Review</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PeerReviewRefImpl extends MinimalEObjectImpl.Container implements PeerReviewRef
{
  /**
   * The cached value of the '{@link #getPeerReview() <em>Peer Review</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPeerReview()
   * @generated
   * @ordered
   */
  protected PeerReviewDef peerReview;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PeerReviewRefImpl()
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
    return AtsDslPackage.Literals.PEER_REVIEW_REF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PeerReviewDef getPeerReview()
  {
    if (peerReview != null && peerReview.eIsProxy())
    {
      InternalEObject oldPeerReview = (InternalEObject)peerReview;
      peerReview = (PeerReviewDef)eResolveProxy(oldPeerReview);
      if (peerReview != oldPeerReview)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, AtsDslPackage.PEER_REVIEW_REF__PEER_REVIEW, oldPeerReview, peerReview));
      }
    }
    return peerReview;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PeerReviewDef basicGetPeerReview()
  {
    return peerReview;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPeerReview(PeerReviewDef newPeerReview)
  {
    PeerReviewDef oldPeerReview = peerReview;
    peerReview = newPeerReview;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.PEER_REVIEW_REF__PEER_REVIEW, oldPeerReview, peerReview));
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
      case AtsDslPackage.PEER_REVIEW_REF__PEER_REVIEW:
        if (resolve) return getPeerReview();
        return basicGetPeerReview();
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
      case AtsDslPackage.PEER_REVIEW_REF__PEER_REVIEW:
        setPeerReview((PeerReviewDef)newValue);
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
      case AtsDslPackage.PEER_REVIEW_REF__PEER_REVIEW:
        setPeerReview((PeerReviewDef)null);
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
      case AtsDslPackage.PEER_REVIEW_REF__PEER_REVIEW:
        return peerReview != null;
    }
    return super.eIsSet(featureID);
  }

} //PeerReviewRefImpl
