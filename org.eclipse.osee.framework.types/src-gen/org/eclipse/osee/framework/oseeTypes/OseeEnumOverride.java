/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Osee Enum Override</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverridenEnumType <em>Overriden Enum Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#isInheritAll <em>Inherit All</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverrideOptions <em>Override Options</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumOverride()
 * @model
 * @generated
 */
public interface OseeEnumOverride extends OseeElement
{
  /**
   * Returns the value of the '<em><b>Overriden Enum Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Overriden Enum Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Overriden Enum Type</em>' reference.
   * @see #setOverridenEnumType(OseeEnumType)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumOverride_OverridenEnumType()
   * @model
   * @generated
   */
  OseeEnumType getOverridenEnumType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#getOverridenEnumType <em>Overriden Enum Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Overriden Enum Type</em>' reference.
   * @see #getOverridenEnumType()
   * @generated
   */
  void setOverridenEnumType(OseeEnumType value);

  /**
   * Returns the value of the '<em><b>Inherit All</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Inherit All</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Inherit All</em>' attribute.
   * @see #setInheritAll(boolean)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumOverride_InheritAll()
   * @model
   * @generated
   */
  boolean isInheritAll();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride#isInheritAll <em>Inherit All</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Inherit All</em>' attribute.
   * @see #isInheritAll()
   * @generated
   */
  void setInheritAll(boolean value);

  /**
   * Returns the value of the '<em><b>Override Options</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.oseeTypes.OverrideOption}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Override Options</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Override Options</em>' containment reference list.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getOseeEnumOverride_OverrideOptions()
   * @model containment="true"
   * @generated
   */
  EList<OverrideOption> getOverrideOptions();

} // OseeEnumOverride
