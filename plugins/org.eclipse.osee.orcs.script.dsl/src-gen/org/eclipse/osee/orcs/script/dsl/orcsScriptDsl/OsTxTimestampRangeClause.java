/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Os Tx Timestamp Range Clause</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getFrom <em>From</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getTo <em>To</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampRangeClause()
 * @model
 * @generated
 */
public interface OsTxTimestampRangeClause extends OsTxTimestampClause
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampRangeClause_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>From</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>From</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>From</em>' containment reference.
   * @see #setFrom(OsExpression)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampRangeClause_From()
   * @model containment="true"
   * @generated
   */
  OsExpression getFrom();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getFrom <em>From</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>From</em>' containment reference.
   * @see #getFrom()
   * @generated
   */
  void setFrom(OsExpression value);

  /**
   * Returns the value of the '<em><b>To</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>To</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>To</em>' containment reference.
   * @see #setTo(OsExpression)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampRangeClause_To()
   * @model containment="true"
   * @generated
   */
  OsExpression getTo();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause#getTo <em>To</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>To</em>' containment reference.
   * @see #getTo()
   * @generated
   */
  void setTo(OsExpression value);

} // OsTxTimestampRangeClause
