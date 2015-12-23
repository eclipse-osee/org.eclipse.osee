/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Dot Expression</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getRef <em>Ref</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getTail <em>Tail</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsDotExpression()
 * @model
 * @generated
 */
public interface OsDotExpression extends OsExpression {
   /**
    * Returns the value of the '<em><b>Ref</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Ref</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Ref</em>' containment reference.
    * @see #setRef(OsExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsDotExpression_Ref()
    * @model containment="true"
    * @generated
    */
   OsExpression getRef();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getRef <em>Ref</em>}'
    * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Ref</em>' containment reference.
    * @see #getRef()
    * @generated
    */
   void setRef(OsExpression value);

   /**
    * Returns the value of the '<em><b>Tail</b></em>' reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Tail</em>' reference isn't clear, there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Tail</em>' reference.
    * @see #setTail(OsExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsDotExpression_Tail()
    * @model
    * @generated
    */
   OsExpression getTail();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression#getTail <em>Tail</em>
    * }' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Tail</em>' reference.
    * @see #getTail()
    * @generated
    */
   void setTail(OsExpression value);

} // OsDotExpression
