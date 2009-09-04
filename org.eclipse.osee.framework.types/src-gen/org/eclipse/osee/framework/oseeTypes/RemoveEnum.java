/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Remove Enum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RemoveEnum#getEnumEntry <em>Enum Entry</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRemoveEnum()
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
   * @see #setEnumEntry(OseeEnumEntry)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRemoveEnum_EnumEntry()
   * @model
   * @generated
   */
  OseeEnumEntry getEnumEntry();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RemoveEnum#getEnumEntry <em>Enum Entry</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enum Entry</em>' reference.
   * @see #getEnumEntry()
   * @generated
   */
  void setEnumEntry(OseeEnumEntry value);

} // RemoveEnum
