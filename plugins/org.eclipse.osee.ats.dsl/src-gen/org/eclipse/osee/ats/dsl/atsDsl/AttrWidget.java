/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attr Widget</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getAttributeName <em>Attribute Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getOption <em>Option</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAttrWidget()
 * @model
 * @generated
 */
public interface AttrWidget extends LayoutItem
{
  /**
   * Returns the value of the '<em><b>Attribute Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute Name</em>' attribute.
   * @see #setAttributeName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAttrWidget_AttributeName()
   * @model
   * @generated
   */
  String getAttributeName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.AttrWidget#getAttributeName <em>Attribute Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute Name</em>' attribute.
   * @see #getAttributeName()
   * @generated
   */
  void setAttributeName(String value);

  /**
   * Returns the value of the '<em><b>Option</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Option</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Option</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAttrWidget_Option()
   * @model unique="false"
   * @generated
   */
  EList<String> getOption();

} // AttrWidget
