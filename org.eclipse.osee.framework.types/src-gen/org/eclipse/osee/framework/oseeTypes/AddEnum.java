/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Add Enum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getEnumEntry <em>Enum Entry</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getOrdinal <em>Ordinal</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getEntryGuid <em>Entry Guid</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getAddEnum()
 * @model
 * @generated
 */
public interface AddEnum extends OverrideOption
{
  /**
   * Returns the value of the '<em><b>Enum Entry</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Entry</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Entry</em>' attribute.
   * @see #setEnumEntry(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getAddEnum_EnumEntry()
   * @model
   * @generated
   */
  String getEnumEntry();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getEnumEntry <em>Enum Entry</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enum Entry</em>' attribute.
   * @see #getEnumEntry()
   * @generated
   */
  void setEnumEntry(String value);

  /**
   * Returns the value of the '<em><b>Ordinal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ordinal</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ordinal</em>' attribute.
   * @see #setOrdinal(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getAddEnum_Ordinal()
   * @model
   * @generated
   */
  String getOrdinal();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getOrdinal <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ordinal</em>' attribute.
   * @see #getOrdinal()
   * @generated
   */
  void setOrdinal(String value);

  /**
   * Returns the value of the '<em><b>Entry Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Entry Guid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Entry Guid</em>' attribute.
   * @see #setEntryGuid(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getAddEnum_EntryGuid()
   * @model
   * @generated
   */
  String getEntryGuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.AddEnum#getEntryGuid <em>Entry Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Entry Guid</em>' attribute.
   * @see #getEntryGuid()
   * @generated
   */
  void setEntryGuid(String value);

} // AddEnum
