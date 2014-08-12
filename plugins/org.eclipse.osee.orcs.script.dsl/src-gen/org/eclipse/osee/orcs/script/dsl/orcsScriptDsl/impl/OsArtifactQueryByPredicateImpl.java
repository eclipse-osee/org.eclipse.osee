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
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Os Artifact Query By Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryByPredicateImpl#getCriteria <em>Criteria</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsArtifactQueryByPredicateImpl extends OsArtifactQueryImpl implements OsArtifactQueryByPredicate
{
  /**
   * The cached value of the '{@link #getCriteria() <em>Criteria</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCriteria()
   * @generated
   * @ordered
   */
  protected EList<OsItemCriteria> criteria;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OsArtifactQueryByPredicateImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return OrcsScriptDslPackage.Literals.OS_ARTIFACT_QUERY_BY_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OsItemCriteria> getCriteria()
  {
    if (criteria == null)
    {
      criteria = new EObjectContainmentEList<OsItemCriteria>(OsItemCriteria.class, this, OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA);
    }
    return criteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA:
        return ((InternalEList<?>)getCriteria()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA:
        return getCriteria();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA:
        getCriteria().clear();
        getCriteria().addAll((Collection<? extends OsItemCriteria>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA:
        getCriteria().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA:
        return criteria != null && !criteria.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //OsArtifactQueryByPredicateImpl
