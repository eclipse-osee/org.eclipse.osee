/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Tx Timestamp Op Clause</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getOp <em>Op</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getTimestamp <em>Timestamp</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampOpClause()
 * @model
 * @generated
 */
public interface OsTxTimestampOpClause extends OsTxTimestampClause {
   /**
    * Returns the value of the '<em><b>Op</b></em>' attribute. The literals are from the enumeration
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Op</em>' attribute isn't clear, there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Op</em>' attribute.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator
    * @see #setOp(OsOperator)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampOpClause_Op()
    * @model
    * @generated
    */
   OsOperator getOp();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getOp
    * <em>Op</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Op</em>' attribute.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator
    * @see #getOp()
    * @generated
    */
   void setOp(OsOperator value);

   /**
    * Returns the value of the '<em><b>Timestamp</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Timestamp</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Timestamp</em>' containment reference.
    * @see #setTimestamp(OsExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTimestampOpClause_Timestamp()
    * @model containment="true"
    * @generated
    */
   OsExpression getTimestamp();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause#getTimestamp
    * <em>Timestamp</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Timestamp</em>' containment reference.
    * @see #getTimestamp()
    * @generated
    */
   void setTimestamp(OsExpression value);

} // OsTxTimestampOpClause
