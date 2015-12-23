/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Follow Clause</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause#getStmt <em>Stmt</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowClause()
 * @model
 * @generated
 */
public interface OsFollowClause extends OsClause {
   /**
    * Returns the value of the '<em><b>Stmt</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Stmt</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Stmt</em>' containment reference.
    * @see #setStmt(OsFollowStatement)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowClause_Stmt()
    * @model containment="true"
    * @generated
    */
   OsFollowStatement getStmt();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause#getStmt <em>Stmt</em>}
    * ' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Stmt</em>' containment reference.
    * @see #getStmt()
    * @generated
    */
   void setStmt(OsFollowStatement value);

} // OsFollowClause
