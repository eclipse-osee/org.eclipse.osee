/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Collect Field Expression</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression#getAlias <em>Alias</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsCollectFieldExpression()
 * @model
 * @generated
 */
public interface OsCollectFieldExpression extends OsCollectExpression {
   /**
    * Returns the value of the '<em><b>Alias</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Alias</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Alias</em>' containment reference.
    * @see #setAlias(OsExpression)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsCollectFieldExpression_Alias()
    * @model containment="true"
    * @generated
    */
   OsExpression getAlias();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression#getAlias
    * <em>Alias</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Alias</em>' containment reference.
    * @see #getAlias()
    * @generated
    */
   void setAlias(OsExpression value);

} // OsCollectFieldExpression
