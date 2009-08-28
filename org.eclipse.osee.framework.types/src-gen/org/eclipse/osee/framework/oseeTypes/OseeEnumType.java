/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Enum Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumType#getOverride <em>Override</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumType#getEnumEntries <em>Enum Entries</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumType()
 * @model
 * @generated
 */
public interface OseeEnumType extends OseeType
{
  /**
   * Returns the value of the '<em><b>Override</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Override</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Override</em>' reference.
   * @see #setOverride(OseeEnumType)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumType_Override()
   * @model
   * @generated
   */
  OseeEnumType getOverride();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumType#getOverride <em>Override</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Override</em>' reference.
   * @see #getOverride()
   * @generated
   */
  void setOverride(OseeEnumType value);

  /**
   * Returns the value of the '<em><b>Enum Entries</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.OseeEnumEntry}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Entries</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Entries</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumType_EnumEntries()
   * @model containment="true"
   * @generated
   */
  EList<OseeEnumEntry> getEnumEntries();

} // OseeEnumType
