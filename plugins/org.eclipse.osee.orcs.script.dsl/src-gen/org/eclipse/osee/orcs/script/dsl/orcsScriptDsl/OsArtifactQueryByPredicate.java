/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Artifact Query By Predicate</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate#getCriteria <em>Criteria</em>}
 * </li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsArtifactQueryByPredicate()
 * @model
 * @generated
 */
public interface OsArtifactQueryByPredicate extends OsArtifactQuery {
   /**
    * Returns the value of the '<em><b>Criteria</b></em>' containment reference list. The list contents are of type
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Criteria</em>' containment reference list isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Criteria</em>' containment reference list.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsArtifactQueryByPredicate_Criteria()
    * @model containment="true"
    * @generated
    */
   EList<OsItemCriteria> getCriteria();

} // OsArtifactQueryByPredicate
