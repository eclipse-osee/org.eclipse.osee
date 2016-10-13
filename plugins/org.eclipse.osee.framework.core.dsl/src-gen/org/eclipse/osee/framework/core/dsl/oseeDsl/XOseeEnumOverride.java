/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XOsee Enum Override</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverridenEnumType <em>Overriden Enum Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#isInheritAll <em>Inherit All</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverrideOptions <em>Override Options</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeEnumOverride()
 * @model
 * @generated
 */
public interface XOseeEnumOverride extends OseeElement
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
   * @see #setOverridenEnumType(XOseeEnumType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeEnumOverride_OverridenEnumType()
   * @model
   * @generated
   */
  XOseeEnumType getOverridenEnumType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#getOverridenEnumType <em>Overriden Enum Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Overriden Enum Type</em>' reference.
   * @see #getOverridenEnumType()
   * @generated
   */
  void setOverridenEnumType(XOseeEnumType value);

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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeEnumOverride_InheritAll()
   * @model
   * @generated
   */
  boolean isInheritAll();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride#isInheritAll <em>Inherit All</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Inherit All</em>' attribute.
   * @see #isInheritAll()
   * @generated
   */
  void setInheritAll(boolean value);

  /**
   * Returns the value of the '<em><b>Override Options</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Override Options</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Override Options</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXOseeEnumOverride_OverrideOptions()
   * @model containment="true"
   * @generated
   */
  EList<OverrideOption> getOverrideOptions();

} // XOseeEnumOverride
