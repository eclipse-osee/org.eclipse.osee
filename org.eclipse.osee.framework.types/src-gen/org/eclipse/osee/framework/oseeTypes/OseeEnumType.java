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
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumType#getEnums <em>Enums</em>}</li>
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
   * Returns the value of the '<em><b>Enums</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.OseeEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enums</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enums</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumType_Enums()
   * @model containment="true"
   * @generated
   */
  EList<OseeEnum> getEnums();

} // OseeEnumType
