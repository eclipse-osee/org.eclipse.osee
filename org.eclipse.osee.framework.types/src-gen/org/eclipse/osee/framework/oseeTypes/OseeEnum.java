/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Enum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnum#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnum#getOrdinal <em>Ordinal</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnum()
 * @model
 * @generated
 */
public interface OseeEnum extends EObject
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
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnum_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnum#getName <em>Name</em>}' attribute.
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
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnum_Ordinal()
   * @model
   * @generated
   */
  String getOrdinal();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnum#getOrdinal <em>Ordinal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ordinal</em>' attribute.
   * @see #getOrdinal()
   * @generated
   */
  void setOrdinal(String value);

} // OseeEnum
