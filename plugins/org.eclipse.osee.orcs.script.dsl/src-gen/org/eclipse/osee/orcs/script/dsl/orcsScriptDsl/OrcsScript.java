/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Orcs Script</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getVersion <em>Version</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getStatements <em>Statements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOrcsScript()
 * @model
 * @generated
 */
public interface OrcsScript extends EObject {
   /**
    * Returns the value of the '<em><b>Version</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Version</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Version</em>' containment reference.
    * @see #setVersion(ScriptVersion)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOrcsScript_Version()
    * @model containment="true"
    * @generated
    */
   ScriptVersion getVersion();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript#getVersion
    * <em>Version</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Version</em>' containment reference.
    * @see #getVersion()
    * @generated
    */
   void setVersion(ScriptVersion value);

   /**
    * Returns the value of the '<em><b>Statements</b></em>' containment reference list. The list contents are of type
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptStatement}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Statements</em>' containment reference list isn't clear, there really should be more of
    * a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Statements</em>' containment reference list.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOrcsScript_Statements()
    * @model containment="true"
    * @generated
    */
   EList<ScriptStatement> getStatements();

} // OrcsScript
