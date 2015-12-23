/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Tx Type Criteria</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTypeCriteria()
 * @model
 * @generated
 */
public interface OsTxTypeCriteria extends OsTxCriteria {
   /**
    * Returns the value of the '<em><b>Types</b></em>' attribute list. The list contents are of type
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType}. The literals are from the enumeration
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Types</em>' attribute list isn't clear, there really should be more of a description
    * here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Types</em>' attribute list.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxTypeCriteria_Types()
    * @model unique="false"
    * @generated
    */
   EList<OsTxType> getTypes();

} // OsTxTypeCriteria
