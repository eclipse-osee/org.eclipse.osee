/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Compound Condition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition#getConditions <em>Conditions</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition#getOperators <em>Operators</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getCompoundCondition()
 * @model
 * @generated
 */
public interface CompoundCondition extends Condition
{
  /**
   * Returns the value of the '<em><b>Conditions</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Conditions</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Conditions</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getCompoundCondition_Conditions()
   * @model containment="true"
   * @generated
   */
  EList<SimpleCondition> getConditions();

  /**
   * Returns the value of the '<em><b>Operators</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator}.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Operators</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Operators</em>' attribute list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getCompoundCondition_Operators()
   * @model unique="false"
   * @generated
   */
  EList<XLogicOperator> getOperators();

} // CompoundCondition
