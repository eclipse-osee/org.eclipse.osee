/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Artifact Id Criteria</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactIdCriteriaImpl#getIds <em>Ids</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsArtifactIdCriteriaImpl extends OsArtifactCriteriaImpl implements OsArtifactIdCriteria {
   /**
    * The cached value of the '{@link #getIds() <em>Ids</em>}' containment reference list. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getIds()
    * @generated
    * @ordered
    */
   protected EList<OsExpression> ids;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsArtifactIdCriteriaImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_ARTIFACT_ID_CRITERIA;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EList<OsExpression> getIds() {
      if (ids == null) {
         ids =
            new EObjectContainmentEList<>(OsExpression.class, this, OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA__IDS);
      }
      return ids;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA__IDS:
            return ((InternalEList<?>) getIds()).basicRemove(otherEnd, msgs);
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
         case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA__IDS:
            return getIds();
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
         case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA__IDS:
            getIds().clear();
            getIds().addAll((Collection<? extends OsExpression>) newValue);
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
         case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA__IDS:
            getIds().clear();
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
         case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA__IDS:
            return ids != null && !ids.isEmpty();
      }
      return super.eIsSet(featureID);
   }

} //OsArtifactIdCriteriaImpl
