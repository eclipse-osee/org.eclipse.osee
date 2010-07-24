/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Artifact Type Restriction</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.ArtifactTypeRestrictionImpl#getArtifactType <em>Artifact
 * Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ArtifactTypeRestrictionImpl extends ObjectRestrictionImpl implements ArtifactTypeRestriction {
   /**
    * The cached value of the '{@link #getArtifactType() <em>Artifact Type</em>}' reference. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getArtifactType()
    * @generated
    * @ordered
    */
   protected XArtifactType artifactType;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected ArtifactTypeRestrictionImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OseeDslPackage.Literals.ARTIFACT_TYPE_RESTRICTION;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XArtifactType getArtifactType() {
      if (artifactType != null && artifactType.eIsProxy()) {
         InternalEObject oldArtifactType = (InternalEObject) artifactType;
         artifactType = (XArtifactType) eResolveProxy(oldArtifactType);
         if (artifactType != oldArtifactType) {
            if (eNotificationRequired()) {
               eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                  OseeDslPackage.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE, oldArtifactType, artifactType));
            }
         }
      }
      return artifactType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public XArtifactType basicGetArtifactType() {
      return artifactType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setArtifactType(XArtifactType newArtifactType) {
      XArtifactType oldArtifactType = artifactType;
      artifactType = newArtifactType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE,
            oldArtifactType, artifactType));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
            if (resolve) {
               return getArtifactType();
            }
            return basicGetArtifactType();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
            setArtifactType((XArtifactType) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eUnset(int featureID) {
      switch (featureID) {
         case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
            setArtifactType((XArtifactType) null);
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public boolean eIsSet(int featureID) {
      switch (featureID) {
         case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE:
            return artifactType != null;
      }
      return super.eIsSet(featureID);
   }

} //ArtifactTypeRestrictionImpl
