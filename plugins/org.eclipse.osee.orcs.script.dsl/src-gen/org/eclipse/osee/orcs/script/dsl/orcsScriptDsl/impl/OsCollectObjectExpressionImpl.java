/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Collect Object Expression</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectObjectExpressionImpl#getAlias <em>Alias</em>}
 * </li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsCollectObjectExpressionImpl#getExpressions
 * <em>Expressions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsCollectObjectExpressionImpl extends OsCollectExpressionImpl implements OsCollectObjectExpression {
   /**
    * The cached value of the '{@link #getAlias() <em>Alias</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getAlias()
    * @generated
    * @ordered
    */
   protected OsExpression alias;

   /**
    * The cached value of the '{@link #getExpressions() <em>Expressions</em>}' containment reference list. <!--
    * begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #getExpressions()
    * @generated
    * @ordered
    */
   protected EList<OsCollectExpression> expressions;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsCollectObjectExpressionImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_COLLECT_OBJECT_EXPRESSION;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsExpression getAlias() {
      return alias;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetAlias(OsExpression newAlias, NotificationChain msgs) {
      OsExpression oldAlias = alias;
      alias = newAlias;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS, oldAlias, newAlias);
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
   public void setAlias(OsExpression newAlias) {
      if (newAlias != alias) {
         NotificationChain msgs = null;
         if (alias != null) {
            msgs = ((InternalEObject) alias).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS, null, msgs);
         }
         if (newAlias != null) {
            msgs = ((InternalEObject) newAlias).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS, null, msgs);
         }
         msgs = basicSetAlias(newAlias, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS, newAlias, newAlias));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EList<OsCollectExpression> getExpressions() {
      if (expressions == null) {
         expressions = new EObjectContainmentEList<>(OsCollectExpression.class, this,
            OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS);
      }
      return expressions;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS:
            return basicSetAlias(null, msgs);
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS:
            return ((InternalEList<?>) getExpressions()).basicRemove(otherEnd, msgs);
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
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS:
            return getAlias();
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS:
            return getExpressions();
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
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS:
            setAlias((OsExpression) newValue);
            return;
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS:
            getExpressions().clear();
            getExpressions().addAll((Collection<? extends OsCollectExpression>) newValue);
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
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS:
            setAlias((OsExpression) null);
            return;
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS:
            getExpressions().clear();
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
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__ALIAS:
            return alias != null;
         case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS:
            return expressions != null && !expressions.isEmpty();
      }
      return super.eIsSet(featureID);
   }

} //OsCollectObjectExpressionImpl
