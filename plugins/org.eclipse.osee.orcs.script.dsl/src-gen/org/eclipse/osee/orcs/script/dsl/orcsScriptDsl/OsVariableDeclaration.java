/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Variable Declaration</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsVariableDeclaration()
 * @model
 * @generated
 */
public interface OsVariableDeclaration extends OsExpression {
   /**
    * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Elements</em>' containment reference list.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsVariableDeclaration_Elements()
    * @model containment="true"
    * @generated
    */
   EList<OsExpression> getElements();

} // OsVariableDeclaration
