/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Layout Copy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy#getState <em>State</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getLayoutCopy()
 * @model
 * @generated
 */
public interface LayoutCopy extends LayoutType
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getLayoutCopy_State()
   * @model
   * @generated
   */
  StateDef getState();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy#getState <em>State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State</em>' reference.
   * @see #getState()
   * @generated
   */
  void setState(StateDef value);

} // LayoutCopy
