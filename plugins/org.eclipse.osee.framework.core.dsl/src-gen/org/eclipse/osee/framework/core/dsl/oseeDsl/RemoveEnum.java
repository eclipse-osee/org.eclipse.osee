/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Remove Enum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum#getEnumEntry <em>Enum Entry</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRemoveEnum()
 * @model
 * @generated
 */
public interface RemoveEnum extends OverrideOption
{
  /**
   * Returns the value of the '<em><b>Enum Entry</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Entry</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Entry</em>' reference.
   * @see #setEnumEntry(XOseeEnumEntry)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRemoveEnum_EnumEntry()
   * @model
   * @generated
   */
  XOseeEnumEntry getEnumEntry();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum#getEnumEntry <em>Enum Entry</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enum Entry</em>' reference.
   * @see #getEnumEntry()
   * @generated
   */
  void setEnumEntry(XOseeEnumEntry value);

} // RemoveEnum
