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
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Tx Id Range Clause</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl#getFromId <em>From Id</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxIdRangeClauseImpl#getToId <em>To Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsTxIdRangeClauseImpl extends OsTxIdClauseImpl implements OsTxIdRangeClause {
   /**
    * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
    * -->
    * 
    * @see #getName()
    * @generated
    * @ordered
    */
   protected static final String NAME_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
    * -->
    * 
    * @see #getName()
    * @generated
    * @ordered
    */
   protected String name = NAME_EDEFAULT;

   /**
    * The cached value of the '{@link #getFromId() <em>From Id</em>}' containment reference. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getFromId()
    * @generated
    * @ordered
    */
   protected OsExpression fromId;

   /**
    * The cached value of the '{@link #getToId() <em>To Id</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getToId()
    * @generated
    * @ordered
    */
   protected OsExpression toId;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsTxIdRangeClauseImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setName(String newName) {
      String oldName = name;
      name = newName;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__NAME, oldName, name));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsExpression getFromId() {
      return fromId;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetFromId(OsExpression newFromId, NotificationChain msgs) {
      OsExpression oldFromId = fromId;
      fromId = newFromId;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID, oldFromId, newFromId);
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
   public void setFromId(OsExpression newFromId) {
      if (newFromId != fromId) {
         NotificationChain msgs = null;
         if (fromId != null) {
            msgs = ((InternalEObject) fromId).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID, null, msgs);
         }
         if (newFromId != null) {
            msgs = ((InternalEObject) newFromId).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID, null, msgs);
         }
         msgs = basicSetFromId(newFromId, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID, newFromId, newFromId));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsExpression getToId() {
      return toId;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetToId(OsExpression newToId, NotificationChain msgs) {
      OsExpression oldToId = toId;
      toId = newToId;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID, oldToId, newToId);
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
   public void setToId(OsExpression newToId) {
      if (newToId != toId) {
         NotificationChain msgs = null;
         if (toId != null) {
            msgs = ((InternalEObject) toId).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID, null, msgs);
         }
         if (newToId != null) {
            msgs = ((InternalEObject) newToId).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID, null, msgs);
         }
         msgs = basicSetToId(newToId, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID, newToId, newToId));
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
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID:
            return basicSetFromId(null, msgs);
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID:
            return basicSetToId(null, msgs);
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
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__NAME:
            return getName();
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID:
            return getFromId();
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID:
            return getToId();
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
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__NAME:
            setName((String) newValue);
            return;
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID:
            setFromId((OsExpression) newValue);
            return;
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID:
            setToId((OsExpression) newValue);
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
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__NAME:
            setName(NAME_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID:
            setFromId((OsExpression) null);
            return;
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID:
            setToId((OsExpression) null);
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
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__FROM_ID:
            return fromId != null;
         case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE__TO_ID:
            return toId != null;
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
      result.append(" (name: ");
      result.append(name);
      result.append(')');
      return result.toString();
   }

} //OsTxIdRangeClauseImpl
