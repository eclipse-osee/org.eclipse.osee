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
