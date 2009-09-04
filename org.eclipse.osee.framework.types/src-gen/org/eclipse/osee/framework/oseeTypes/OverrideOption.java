/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Override Option</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OverrideOption#isOverrideOperation <em>Override Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOverrideOption()
 * @model
 * @generated
 */
public interface OverrideOption extends EObject
{
  /**
   * Returns the value of the '<em><b>Override Operation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Override Operation</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Override Operation</em>' attribute.
   * @see #setOverrideOperation(boolean)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOverrideOption_OverrideOperation()
   * @model
   * @generated
   */
  boolean isOverrideOperation();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OverrideOption#isOverrideOperation <em>Override Operation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Override Operation</em>' attribute.
   * @see #isOverrideOperation()
   * @generated
   */
  void setOverrideOperation(boolean value);

} // OverrideOption
