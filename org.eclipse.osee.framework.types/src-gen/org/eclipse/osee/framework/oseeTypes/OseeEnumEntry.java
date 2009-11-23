/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Enum Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getOrdinal <em>Ordinal</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getEntryGuid <em>Entry Guid</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumEntry()
 * @model
 * @generated
 */
public interface OseeEnumEntry extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumEntry_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

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
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumEntry_Ordinal()
   * @model
   * @generated
   */
  String getOrdinal();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getOrdinal <em>Ordinal</em>}' attribute.
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
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumEntry_EntryGuid()
   * @model
   * @generated
   */
  String getEntryGuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry#getEntryGuid <em>Entry Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Entry Guid</em>' attribute.
   * @see #getEntryGuid()
   * @generated
   */
  void setEntryGuid(String value);

} // OseeEnumEntry
