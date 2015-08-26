/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attr Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getOption <em>Option</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAttrDef()
 * @model
 * @generated
 */
public interface AttrDef extends EObject
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAttrDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Option</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Option</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Option</em>' containment reference.
   * @see #setOption(AttrDefOptions)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAttrDef_Option()
   * @model containment="true"
   * @generated
   */
  AttrDefOptions getOption();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef#getOption <em>Option</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Option</em>' containment reference.
   * @see #getOption()
   * @generated
   */
  void setOption(AttrDefOptions value);

} // AttrDef
