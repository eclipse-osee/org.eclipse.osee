/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ose Attribute Op Clause</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OseAttributeOpClauseImpl#getOptions <em>Options</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OseAttributeOpClauseImpl#getValues <em>Values</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OseAttributeOpClauseImpl extends OsAttributeClauseImpl implements OseAttributeOpClause
{
  /**
   * The cached value of the '{@link #getOptions() <em>Options</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOptions()
   * @generated
   * @ordered
   */
  protected EList<OsQueryOption> options;

  /**
   * The cached value of the '{@link #getValues() <em>Values</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValues()
   * @generated
   * @ordered
   */
  protected EList<OsExpression> values;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseAttributeOpClauseImpl()
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
    return OrcsScriptDslPackage.Literals.OSE_ATTRIBUTE_OP_CLAUSE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OsQueryOption> getOptions()
  {
    if (options == null)
    {
      options = new EDataTypeEList<>(OsQueryOption.class, this, OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS);
    }
    return options;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OsExpression> getValues()
  {
    if (values == null)
    {
      values = new EObjectContainmentEList<>(OsExpression.class, this, OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__VALUES);
    }
    return values;
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
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__VALUES:
        return ((InternalEList<?>)getValues()).basicRemove(otherEnd, msgs);
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
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS:
        return getOptions();
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__VALUES:
        return getValues();
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
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS:
        getOptions().clear();
        getOptions().addAll((Collection<? extends OsQueryOption>)newValue);
        return;
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__VALUES:
        getValues().clear();
        getValues().addAll((Collection<? extends OsExpression>)newValue);
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
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS:
        getOptions().clear();
        return;
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__VALUES:
        getValues().clear();
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
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS:
        return options != null && !options.isEmpty();
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE__VALUES:
        return values != null && !values.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (options: ");
    result.append(options);
    result.append(')');
    return result.toString();
  }

} //OseAttributeOpClauseImpl
