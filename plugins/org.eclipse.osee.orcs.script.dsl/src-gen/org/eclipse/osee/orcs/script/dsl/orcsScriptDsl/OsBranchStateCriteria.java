/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Branch State Criteria</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria#getStates <em>States</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchStateCriteria()
 * @model
 * @generated
 */
public interface OsBranchStateCriteria extends OsBranchCriteria {
   /**
    * Returns the value of the '<em><b>States</b></em>' attribute list. The list contents are of type
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState}. The literals are from the enumeration
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>States</em>' attribute list isn't clear, there really should be more of a description
    * here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>States</em>' attribute list.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchStateCriteria_States()
    * @model unique="false"
    * @generated
    */
   EList<OsBranchState> getStates();

} // OsBranchStateCriteria
