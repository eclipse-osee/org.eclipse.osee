/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Tx Id Op Clause</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdOpClauseImpl#getOp <em>Op</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdOpClauseImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsTxIdOpClauseImpl extends OsTxIdClauseImpl implements OsTxIdOpClause {
   /**
    * The default value of the '{@link #getOp() <em>Op</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #getOp()
    * @generated
    * @ordered
    */
   protected static final OsNonEqualOperator OP_EDEFAULT = OsNonEqualOperator.NOT_EQUAL;

   /**
    * The cached value of the '{@link #getOp() <em>Op</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #getOp()
    * @generated
    * @ordered
    */
   protected OsNonEqualOperator op = OP_EDEFAULT;

   /**
    * The cached value of the '{@link #getId() <em>Id</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getId()
    * @generated
    * @ordered
    */
   protected OsExpression id;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsTxIdOpClauseImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_TX_ID_OP_CLAUSE;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsNonEqualOperator getOp() {
      return op;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setOp(OsNonEqualOperator newOp) {
      OsNonEqualOperator oldOp = op;
      op = newOp == null ? OP_EDEFAULT : newOp;
      if (eNotificationRequired()) {
         eNotify(
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__OP, oldOp, op));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsExpression getId() {
      return id;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetId(OsExpression newId, NotificationChain msgs) {
      OsExpression oldId = id;
      id = newId;
      if (eNotificationRequired()) {
         ENotificationImpl notification =
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID, oldId, newId);
         if (msgs == null) {
            msgs = notification;
         } else {
            msgs.add(notification);
         }
      }
      return msgs;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setId(OsExpression newId) {
      if (newId != id) {
         NotificationChain msgs = null;
         if (id != null) {
            msgs = ((InternalEObject) id).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID, null, msgs);
         }
         if (newId != null) {
            msgs = ((InternalEObject) newId).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID, null, msgs);
         }
         msgs = basicSetId(newId, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID, newId, newId));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID:
            return basicSetId(null, msgs);
      }
      return super.eInverseRemove(otherEnd, featureID, msgs);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__OP:
            return getOp();
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID:
            return getId();
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
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__OP:
            setOp((OsNonEqualOperator) newValue);
            return;
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID:
            setId((OsExpression) newValue);
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
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__OP:
            setOp(OP_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID:
            setId((OsExpression) null);
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
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__OP:
            return op != OP_EDEFAULT;
         case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE__ID:
            return id != null;
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
      result.append(" (op: ");
      result.append(op);
      result.append(')');
      return result.toString();
   }

} //OsTxIdOpClauseImpl
