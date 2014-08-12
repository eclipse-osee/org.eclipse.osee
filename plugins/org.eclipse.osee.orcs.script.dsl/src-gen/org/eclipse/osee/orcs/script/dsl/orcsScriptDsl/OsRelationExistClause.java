/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Os Relation Exist Clause</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause#getOp <em>Op</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsRelationExistClause()
 * @model
 * @generated
 */
public interface OsRelationExistClause extends OsRelationClause
{
  /**
   * Returns the value of the '<em><b>Op</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Op</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Op</em>' attribute.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator
   * @see #setOp(OsExistenceOperator)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsRelationExistClause_Op()
   * @model
   * @generated
   */
  OsExistenceOperator getOp();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause#getOp <em>Op</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Op</em>' attribute.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator
   * @see #getOp()
   * @generated
   */
  void setOp(OsExistenceOperator value);

} // OsRelationExistClause
