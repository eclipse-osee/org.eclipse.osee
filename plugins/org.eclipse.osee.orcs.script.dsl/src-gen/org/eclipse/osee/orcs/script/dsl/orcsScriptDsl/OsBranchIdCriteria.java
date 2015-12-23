/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Branch Id Criteria</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria#getIds <em>Ids</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchIdCriteria()
 * @model
 * @generated
 */
public interface OsBranchIdCriteria extends OsBranchCriteria {
   /**
    * Returns the value of the '<em><b>Ids</b></em>' containment reference list. The list contents are of type
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Ids</em>' containment reference list isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Ids</em>' containment reference list.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchIdCriteria_Ids()
    * @model containment="true"
    * @generated
    */
   EList<OsExpression> getIds();

} // OsBranchIdCriteria
