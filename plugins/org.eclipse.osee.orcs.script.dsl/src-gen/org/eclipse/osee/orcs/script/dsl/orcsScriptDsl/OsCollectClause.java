/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Collect Clause</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getExpression <em>Expression</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getLimit <em>Limit</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsCollectClause()
 * @model
 * @generated
 */
public interface OsCollectClause extends EObject {
   /**
    * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Name</em>' attribute.
    * @see #setName(String)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsCollectClause_Name()
    * @model
    * @generated
    */
   String getName();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getName <em>Name</em>
    * }' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Name</em>' attribute.
    * @see #getName()
    * @generated
    */
   void setName(String value);

   /**
    * Returns the value of the '<em><b>Expression</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Expression</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Expression</em>' containment reference.
    * @see #setExpression(OsCollectExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsCollectClause_Expression()
    * @model containment="true"
    * @generated
    */
   OsCollectExpression getExpression();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getExpression
    * <em>Expression</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Expression</em>' containment reference.
    * @see #getExpression()
    * @generated
    */
   void setExpression(OsCollectExpression value);

   /**
    * Returns the value of the '<em><b>Limit</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Limit</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Limit</em>' containment reference.
    * @see #setLimit(OsLimitClause)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsCollectClause_Limit()
    * @model containment="true"
    * @generated
    */
   OsLimitClause getLimit();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause#getLimit
    * <em>Limit</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Limit</em>' containment reference.
    * @see #getLimit()
    * @generated
    */
   void setLimit(OsLimitClause value);

} // OsCollectClause
