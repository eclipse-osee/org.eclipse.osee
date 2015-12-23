/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Variable Reference</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsVariableReferenceImpl#getRef <em>Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsVariableReferenceImpl extends OsExpressionImpl implements OsVariableReference {
   /**
    * The cached value of the '{@link #getRef() <em>Ref</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #getRef()
    * @generated
    * @ordered
    */
   protected OsVariable ref;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsVariableReferenceImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_VARIABLE_REFERENCE;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsVariable getRef() {
      if (ref != null && ref.eIsProxy()) {
         InternalEObject oldRef = (InternalEObject) ref;
         ref = (OsVariable) eResolveProxy(oldRef);
         if (ref != oldRef) {
            if (eNotificationRequired()) {
               eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                  OrcsScriptDslPackage.OS_VARIABLE_REFERENCE__REF, oldRef, ref));
            }
         }
      }
      return ref;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public OsVariable basicGetRef() {
      return ref;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setRef(OsVariable newRef) {
      OsVariable oldRef = ref;
      ref = newRef;
      if (eNotificationRequired()) {
         eNotify(
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_VARIABLE_REFERENCE__REF, oldRef, ref));
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
         case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE__REF:
            if (resolve) {
               return getRef();
            }
            return basicGetRef();
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
         case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE__REF:
            setRef((OsVariable) newValue);
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
         case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE__REF:
            setRef((OsVariable) null);
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
         case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE__REF:
            return ref != null;
      }
      return super.eIsSet(featureID);
   }

} //OsVariableReferenceImpl
