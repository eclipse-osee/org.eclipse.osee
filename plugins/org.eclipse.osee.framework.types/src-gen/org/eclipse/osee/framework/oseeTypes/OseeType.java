/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeType#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeType#getTypeGuid <em>Type Guid</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeType()
 * @model
 * @generated
 */
public interface OseeType extends OseeElement
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
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeType_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeType#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Type Guid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type Guid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type Guid</em>' attribute.
   * @see #setTypeGuid(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeType_TypeGuid()
   * @model
   * @generated
   */
  String getTypeGuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeType#getTypeGuid <em>Type Guid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type Guid</em>' attribute.
   * @see #getTypeGuid()
   * @generated
   */
  void setTypeGuid(String value);

} // OseeType
