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
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Follow Relation Type</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl#getType <em>Type</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl#getSide <em>Side</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl#getCriteria <em>Criteria</em>
 * }</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsFollowRelationTypeImpl#getCollect <em>Collect</em>}
 * </li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsFollowRelationTypeImpl extends OsFollowStatementImpl implements OsFollowRelationType {
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
    * The cached value of the '{@link #getType() <em>Type</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getType()
    * @generated
    * @ordered
    */
   protected OsExpression type;

   /**
    * The default value of the '{@link #getSide() <em>Side</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
    * -->
    * 
    * @see #getSide()
    * @generated
    * @ordered
    */
   protected static final OsRelationSide SIDE_EDEFAULT = OsRelationSide.SIDE_A;

   /**
    * The cached value of the '{@link #getSide() <em>Side</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
    * -->
    * 
    * @see #getSide()
    * @generated
    * @ordered
    */
   protected OsRelationSide side = SIDE_EDEFAULT;

   /**
    * The cached value of the '{@link #getCriteria() <em>Criteria</em>}' containment reference list. <!-- begin-user-doc
    * --> <!-- end-user-doc -->
    * 
    * @see #getCriteria()
    * @generated
    * @ordered
    */
   protected EList<OsItemCriteria> criteria;

   /**
    * The cached value of the '{@link #getCollect() <em>Collect</em>}' containment reference. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getCollect()
    * @generated
    * @ordered
    */
   protected OsCollectClause collect;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsFollowRelationTypeImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_FOLLOW_RELATION_TYPE;
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
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__NAME, oldName, name));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsExpression getType() {
      return type;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetType(OsExpression newType, NotificationChain msgs) {
      OsExpression oldType = type;
      type = newType;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE, oldType, newType);
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
   public void setType(OsExpression newType) {
      if (newType != type) {
         NotificationChain msgs = null;
         if (type != null) {
            msgs = ((InternalEObject) type).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE, null, msgs);
         }
         if (newType != null) {
            msgs = ((InternalEObject) newType).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE, null, msgs);
         }
         msgs = basicSetType(newType, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE, newType, newType));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsRelationSide getSide() {
      return side;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setSide(OsRelationSide newSide) {
      OsRelationSide oldSide = side;
      side = newSide == null ? SIDE_EDEFAULT : newSide;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__SIDE, oldSide, side));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EList<OsItemCriteria> getCriteria() {
      if (criteria == null) {
         criteria = new EObjectContainmentEList<>(OsItemCriteria.class, this,
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__CRITERIA);
      }
      return criteria;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsCollectClause getCollect() {
      return collect;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetCollect(OsCollectClause newCollect, NotificationChain msgs) {
      OsCollectClause oldCollect = collect;
      collect = newCollect;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT, oldCollect, newCollect);
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
   public void setCollect(OsCollectClause newCollect) {
      if (newCollect != collect) {
         NotificationChain msgs = null;
         if (collect != null) {
            msgs = ((InternalEObject) collect).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT, null, msgs);
         }
         if (newCollect != null) {
            msgs = ((InternalEObject) newCollect).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT, null, msgs);
         }
         msgs = basicSetCollect(newCollect, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT, newCollect, newCollect));
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
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE:
            return basicSetType(null, msgs);
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__CRITERIA:
            return ((InternalEList<?>) getCriteria()).basicRemove(otherEnd, msgs);
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT:
            return basicSetCollect(null, msgs);
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
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__NAME:
            return getName();
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE:
            return getType();
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__SIDE:
            return getSide();
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__CRITERIA:
            return getCriteria();
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT:
            return getCollect();
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
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__NAME:
            setName((String) newValue);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE:
            setType((OsExpression) newValue);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__SIDE:
            setSide((OsRelationSide) newValue);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__CRITERIA:
            getCriteria().clear();
            getCriteria().addAll((Collection<? extends OsItemCriteria>) newValue);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT:
            setCollect((OsCollectClause) newValue);
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
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__NAME:
            setName(NAME_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE:
            setType((OsExpression) null);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__SIDE:
            setSide(SIDE_EDEFAULT);
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__CRITERIA:
            getCriteria().clear();
            return;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT:
            setCollect((OsCollectClause) null);
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
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__TYPE:
            return type != null;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__SIDE:
            return side != SIDE_EDEFAULT;
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__CRITERIA:
            return criteria != null && !criteria.isEmpty();
         case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE__COLLECT:
            return collect != null;
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
      result.append(", side: ");
      result.append(side);
      result.append(')');
      return result.toString();
   }

} //OsFollowRelationTypeImpl
