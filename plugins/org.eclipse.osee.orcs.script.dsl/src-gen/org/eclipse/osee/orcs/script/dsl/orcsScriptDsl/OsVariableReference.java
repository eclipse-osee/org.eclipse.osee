/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Variable Reference</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference#getRef <em>Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsVariableReference()
 * @model
 * @generated
 */
public interface OsVariableReference extends OsExpression {
   /**
    * Returns the value of the '<em><b>Ref</b></em>' reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Ref</em>' reference isn't clear, there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Ref</em>' reference.
    * @see #setRef(OsVariable)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsVariableReference_Ref()
    * @model
    * @generated
    */
   OsVariable getRef();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference#getRef
    * <em>Ref</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Ref</em>' reference.
    * @see #getRef()
    * @generated
    */
   void setRef(OsVariable value);

} // OsVariableReference
