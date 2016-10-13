/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Simple Condition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getField <em>Field</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getOp <em>Op</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getSimpleCondition()
 * @model
 * @generated
 */
public interface SimpleCondition extends Condition
{
  /**
   * Returns the value of the '<em><b>Field</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Field</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Field</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField
   * @see #setField(MatchField)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getSimpleCondition_Field()
   * @model
   * @generated
   */
  MatchField getField();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getField <em>Field</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Field</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField
   * @see #getField()
   * @generated
   */
  void setField(MatchField value);

  /**
   * Returns the value of the '<em><b>Op</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Op</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Op</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp
   * @see #setOp(CompareOp)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getSimpleCondition_Op()
   * @model
   * @generated
   */
  CompareOp getOp();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getOp <em>Op</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Op</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp
   * @see #getOp()
   * @generated
   */
  void setOp(CompareOp value);

  /**
   * Returns the value of the '<em><b>Expression</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression</em>' attribute.
   * @see #setExpression(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getSimpleCondition_Expression()
   * @model
   * @generated
   */
  String getExpression();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition#getExpression <em>Expression</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression</em>' attribute.
   * @see #getExpression()
   * @generated
   */
  void setExpression(String value);

} // SimpleCondition
