/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Find Clause</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause#getQuery <em>Query</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFindClause()
 * @model
 * @generated
 */
public interface OsFindClause extends OsClause {
   /**
    * Returns the value of the '<em><b>Query</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Query</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Query</em>' containment reference.
    * @see #setQuery(OsObjectQuery)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFindClause_Query()
    * @model containment="true"
    * @generated
    */
   OsObjectQuery getQuery();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause#getQuery <em>Query</em>}
    * ' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Query</em>' containment reference.
    * @see #getQuery()
    * @generated
    */
   void setQuery(OsObjectQuery value);

} // OsFindClause
