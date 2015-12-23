/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Tx Id Op Clause</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getOp <em>Op</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxIdOpClause()
 * @model
 * @generated
 */
public interface OsTxIdOpClause extends OsTxIdClause {
   /**
    * Returns the value of the '<em><b>Op</b></em>' attribute. The literals are from the enumeration
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Op</em>' attribute isn't clear, there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Op</em>' attribute.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator
    * @see #setOp(OsNonEqualOperator)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxIdOpClause_Op()
    * @model
    * @generated
    */
   OsNonEqualOperator getOp();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getOp <em>Op</em>}'
    * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Op</em>' attribute.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator
    * @see #getOp()
    * @generated
    */
   void setOp(OsNonEqualOperator value);

   /**
    * Returns the value of the '<em><b>Id</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Id</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Id</em>' containment reference.
    * @see #setId(OsExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxIdOpClause_Id()
    * @model containment="true"
    * @generated
    */
   OsExpression getId();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause#getId <em>Id</em>}'
    * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Id</em>' containment reference.
    * @see #getId()
    * @generated
    */
   void setId(OsExpression value);

} // OsTxIdOpClause
