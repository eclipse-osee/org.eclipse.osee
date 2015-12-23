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
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Tx Timestamp Op Clause</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampOpClauseImpl#getOp <em>Op</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxTimestampOpClauseImpl#getTimestamp
 * <em>Timestamp</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsTxTimestampOpClauseImpl extends OsTxTimestampClauseImpl implements OsTxTimestampOpClause {
   /**
    * The default value of the '{@link #getOp() <em>Op</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #getOp()
    * @generated
    * @ordered
    */
   protected static final OsOperator OP_EDEFAULT = OsOperator.EQUAL;

   /**
    * The cached value of the '{@link #getOp() <em>Op</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #getOp()
    * @generated
    * @ordered
    */
   protected OsOperator op = OP_EDEFAULT;

   /**
    * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' containment reference. <!-- begin-user-doc
    * --> <!-- end-user-doc -->
    * 
    * @see #getTimestamp()
    * @generated
    * @ordered
    */
   protected OsExpression timestamp;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsTxTimestampOpClauseImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_OP_CLAUSE;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsOperator getOp() {
      return op;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setOp(OsOperator newOp) {
      OsOperator oldOp = op;
      op = newOp == null ? OP_EDEFAULT : newOp;
      if (eNotificationRequired()) {
         eNotify(
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__OP, oldOp, op));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsExpression getTimestamp() {
      return timestamp;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetTimestamp(OsExpression newTimestamp, NotificationChain msgs) {
      OsExpression oldTimestamp = timestamp;
      timestamp = newTimestamp;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP, oldTimestamp, newTimestamp);
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
   public void setTimestamp(OsExpression newTimestamp) {
      if (newTimestamp != timestamp) {
         NotificationChain msgs = null;
         if (timestamp != null) {
            msgs = ((InternalEObject) timestamp).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP, null, msgs);
         }
         if (newTimestamp != null) {
            msgs = ((InternalEObject) newTimestamp).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP, null, msgs);
         }
         msgs = basicSetTimestamp(newTimestamp, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP, newTimestamp, newTimestamp));
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
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP:
            return basicSetTimestamp(null, msgs);
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
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__OP:
            return getOp();
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP:
            return getTimestamp();
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
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__OP:
            setOp((OsOperator) newValue);
            return;
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP:
            setTimestamp((OsExpression) newValue);
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
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__OP:
            setOp(OP_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP:
            setTimestamp((OsExpression) null);
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
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__OP:
            return op != OP_EDEFAULT;
         case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP:
            return timestamp != null;
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

} //OsTxTimestampOpClauseImpl
