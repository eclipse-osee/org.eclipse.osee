/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ose Attribute Op Clause</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause#getOptions <em>Options</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause#getValues <em>Values</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOseAttributeOpClause()
 * @model
 * @generated
 */
public interface OseAttributeOpClause extends OsAttributeClause
{
  /**
   * Returns the value of the '<em><b>Options</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption}.
   * The literals are from the enumeration {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Options</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Options</em>' attribute list.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOseAttributeOpClause_Options()
   * @model unique="false"
   * @generated
   */
  EList<OsQueryOption> getOptions();

  /**
   * Returns the value of the '<em><b>Values</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Values</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Values</em>' containment reference list.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOseAttributeOpClause_Values()
   * @model containment="true"
   * @generated
   */
  EList<OsExpression> getValues();

} // OseAttributeOpClause
