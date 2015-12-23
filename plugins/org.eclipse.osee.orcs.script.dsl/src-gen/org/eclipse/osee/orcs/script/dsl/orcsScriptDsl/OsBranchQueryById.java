/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Branch Query By Id</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchQueryById()
 * @model
 * @generated
 */
public interface OsBranchQueryById extends OsBranchQuery {
   /**
    * Returns the value of the '<em><b>Name</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Name</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Name</em>' containment reference.
    * @see #setName(OsExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchQueryById_Name()
    * @model containment="true"
    * @generated
    */
   OsExpression getName();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById#getName
    * <em>Name</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Name</em>' containment reference.
    * @see #getName()
    * @generated
    */
   void setName(OsExpression value);

} // OsBranchQueryById
