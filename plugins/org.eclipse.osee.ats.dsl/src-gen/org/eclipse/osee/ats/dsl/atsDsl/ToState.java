/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>To State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ToState#getState <em>State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ToState#getOptions <em>Options</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getToState()
 * @model
 * @generated
 */
public interface ToState extends EObject
{
  /**
   * Returns the value of the '<em><b>State</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>State</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>State</em>' reference.
   * @see #setState(StateDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getToState_State()
   * @model
   * @generated
   */
  StateDef getState();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ToState#getState <em>State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State</em>' reference.
   * @see #getState()
   * @generated
   */
  void setState(StateDef value);

  /**
   * Returns the value of the '<em><b>Options</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Options</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Options</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getToState_Options()
   * @model unique="false"
   * @generated
   */
  EList<String> getOptions();

} // ToState
