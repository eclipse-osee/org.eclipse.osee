/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Collect Clause</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl#getExpression <em>Expression</em>}
 * </li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectClauseImpl#getLimit <em>Limit</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsCollectClauseImpl extends MinimalEObjectImpl.Container implements OsCollectClause {
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
    * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference. <!-- begin-user-doc
    * --> <!-- end-user-doc -->
    * 
    * @see #getExpression()
    * @generated
    * @ordered
    */
   protected OsCollectExpression expression;

   /**
    * The cached value of the '{@link #getLimit() <em>Limit</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getLimit()
    * @generated
    * @ordered
    */
   protected OsLimitClause limit;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsCollectClauseImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_COLLECT_CLAUSE;
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
         eNotify(
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_COLLECT_CLAUSE__NAME, oldName, name));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsCollectExpression getExpression() {
      return expression;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetExpression(OsCollectExpression newExpression, NotificationChain msgs) {
      OsCollectExpression oldExpression = expression;
      expression = newExpression;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION, oldExpression, newExpression);
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
   public void setExpression(OsCollectExpression newExpression) {
      if (newExpression != expression) {
         NotificationChain msgs = null;
         if (expression != null) {
            msgs = ((InternalEObject) expression).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION, null, msgs);
         }
         if (newExpression != null) {
            msgs = ((InternalEObject) newExpression).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION, null, msgs);
         }
         msgs = basicSetExpression(newExpression, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION, newExpression, newExpression));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsLimitClause getLimit() {
      return limit;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetLimit(OsLimitClause newLimit, NotificationChain msgs) {
      OsLimitClause oldLimit = limit;
      limit = newLimit;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT, oldLimit, newLimit);
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
   public void setLimit(OsLimitClause newLimit) {
      if (newLimit != limit) {
         NotificationChain msgs = null;
         if (limit != null) {
            msgs = ((InternalEObject) limit).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT, null, msgs);
         }
         if (newLimit != null) {
            msgs = ((InternalEObject) newLimit).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT, null, msgs);
         }
         msgs = basicSetLimit(newLimit, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT, newLimit, newLimit));
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
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION:
            return basicSetExpression(null, msgs);
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT:
            return basicSetLimit(null, msgs);
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
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__NAME:
            return getName();
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION:
            return getExpression();
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT:
            return getLimit();
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
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__NAME:
            setName((String) newValue);
            return;
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION:
            setExpression((OsCollectExpression) newValue);
            return;
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT:
            setLimit((OsLimitClause) newValue);
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
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__NAME:
            setName(NAME_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION:
            setExpression((OsCollectExpression) null);
            return;
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT:
            setLimit((OsLimitClause) null);
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
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__EXPRESSION:
            return expression != null;
         case OrcsScriptDslPackage.OS_COLLECT_CLAUSE__LIMIT:
            return limit != null;
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

} //OsCollectClauseImpl
