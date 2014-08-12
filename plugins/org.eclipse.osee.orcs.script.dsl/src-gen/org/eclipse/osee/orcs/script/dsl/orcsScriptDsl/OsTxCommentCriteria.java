/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Os Tx Comment Criteria</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria#getClause <em>Clause</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxCommentCriteria()
 * @model
 * @generated
 */
public interface OsTxCommentCriteria extends OsTxCriteria
{
  /**
   * Returns the value of the '<em><b>Clause</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Clause</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Clause</em>' containment reference.
   * @see #setClause(OsTxCommentClause)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxCommentCriteria_Clause()
   * @model containment="true"
   * @generated
   */
  OsTxCommentClause getClause();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria#getClause <em>Clause</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Clause</em>' containment reference.
   * @see #getClause()
   * @generated
   */
  void setClause(OsTxCommentClause value);

} // OsTxCommentCriteria
