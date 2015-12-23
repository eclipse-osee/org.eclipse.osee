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
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Query Expression</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl#getQuery <em>Query</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsQueryExpressionImpl#getClause <em>Clause</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsQueryExpressionImpl extends OsExpressionImpl implements OsQueryExpression {
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
    * The cached value of the '{@link #getQuery() <em>Query</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getQuery()
    * @generated
    * @ordered
    */
   protected OsQuery query;

   /**
    * The cached value of the '{@link #getClause() <em>Clause</em>}' containment reference list. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getClause()
    * @generated
    * @ordered
    */
   protected EList<OsClause> clause;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsQueryExpressionImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_QUERY_EXPRESSION;
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
            new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_QUERY_EXPRESSION__NAME, oldName, name));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsQuery getQuery() {
      return query;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetQuery(OsQuery newQuery, NotificationChain msgs) {
      OsQuery oldQuery = query;
      query = newQuery;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY, oldQuery, newQuery);
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
   public void setQuery(OsQuery newQuery) {
      if (newQuery != query) {
         NotificationChain msgs = null;
         if (query != null) {
            msgs = ((InternalEObject) query).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY, null, msgs);
         }
         if (newQuery != null) {
            msgs = ((InternalEObject) newQuery).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY, null, msgs);
         }
         msgs = basicSetQuery(newQuery, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY, newQuery, newQuery));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EList<OsClause> getClause() {
      if (clause == null) {
         clause = new EObjectContainmentEList<>(OsClause.class, this, OrcsScriptDslPackage.OS_QUERY_EXPRESSION__CLAUSE);
      }
      return clause;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY:
            return basicSetQuery(null, msgs);
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__CLAUSE:
            return ((InternalEList<?>) getClause()).basicRemove(otherEnd, msgs);
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
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__NAME:
            return getName();
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY:
            return getQuery();
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__CLAUSE:
            return getClause();
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
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__NAME:
            setName((String) newValue);
            return;
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY:
            setQuery((OsQuery) newValue);
            return;
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__CLAUSE:
            getClause().clear();
            getClause().addAll((Collection<? extends OsClause>) newValue);
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
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__NAME:
            setName(NAME_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY:
            setQuery((OsQuery) null);
            return;
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__CLAUSE:
            getClause().clear();
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
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__QUERY:
            return query != null;
         case OrcsScriptDslPackage.OS_QUERY_EXPRESSION__CLAUSE:
            return clause != null && !clause.isEmpty();
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

} //OsQueryExpressionImpl
