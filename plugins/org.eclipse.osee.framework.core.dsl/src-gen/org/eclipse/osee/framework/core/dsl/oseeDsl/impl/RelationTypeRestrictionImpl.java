/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Relation Type Restriction</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl#getRelationType <em>Relation
 * Type</em>}</li>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RelationTypeRestrictionImpl#getRestrictedTo <em>
 * Restricted To</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RelationTypeRestrictionImpl extends ObjectRestrictionImpl implements RelationTypeRestriction {
   /**
    * The cached value of the '{@link #getRelationType() <em>Relation Type</em>}' reference. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getRelationType()
    * @generated
    * @ordered
    */
   protected XRelationType relationType;

   /**
    * The cached value of the '{@link #getRestrictedTo() <em>Restricted To</em>}' attribute list. <!-- begin-user-doc
    * --> <!-- end-user-doc -->
    * 
    * @see #getRestrictedTo()
    * @generated
    * @ordered
    */
   protected EList<RelationTypeSideRestriction> restrictedTo;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected RelationTypeRestrictionImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OseeDslPackage.Literals.RELATION_TYPE_RESTRICTION;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public XRelationType getRelationType() {
      if (relationType != null && relationType.eIsProxy()) {
         InternalEObject oldRelationType = (InternalEObject) relationType;
         relationType = (XRelationType) eResolveProxy(oldRelationType);
         if (relationType != oldRelationType) {
            if (eNotificationRequired()) {
               eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                  OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE, oldRelationType, relationType));
            }
         }
      }
      return relationType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public XRelationType basicGetRelationType() {
      return relationType;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setRelationType(XRelationType newRelationType) {
      XRelationType oldRelationType = relationType;
      relationType = newRelationType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE,
            oldRelationType, relationType));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EList<RelationTypeSideRestriction> getRestrictedTo() {
      if (restrictedTo == null) {
         restrictedTo =
            new EDataTypeEList<RelationTypeSideRestriction>(RelationTypeSideRestriction.class, this,
               OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO);
      }
      return restrictedTo;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE:
            if (resolve) {
               return getRelationType();
            }
            return basicGetRelationType();
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO:
            return getRestrictedTo();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @SuppressWarnings("unchecked")
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE:
            setRelationType((XRelationType) newValue);
            return;
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO:
            getRestrictedTo().clear();
            getRestrictedTo().addAll((Collection<? extends RelationTypeSideRestriction>) newValue);
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
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE:
            setRelationType((XRelationType) null);
            return;
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO:
            getRestrictedTo().clear();
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
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RELATION_TYPE:
            return relationType != null;
         case OseeDslPackage.RELATION_TYPE_RESTRICTION__RESTRICTED_TO:
            return restrictedTo != null && !restrictedTo.isEmpty();
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String toString() {
      if (eIsProxy()) {
         return super.toString();
      }

      StringBuffer result = new StringBuffer(super.toString());
      result.append(" (restrictedTo: ");
      result.append(restrictedTo);
      result.append(')');
      return result.toString();
   }

} //RelationTypeRestrictionImpl
