/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Widget Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetRef#getWidget <em>Widget</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetRef()
 * @model
 * @generated
 */
public interface WidgetRef extends LayoutItem
{
  /**
   * Returns the value of the '<em><b>Widget</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Widget</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Widget</em>' reference.
   * @see #setWidget(WidgetDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWidgetRef_Widget()
   * @model
   * @generated
   */
  WidgetDef getWidget();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.WidgetRef#getWidget <em>Widget</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Widget</em>' reference.
   * @see #getWidget()
   * @generated
   */
  void setWidget(WidgetDef value);

} // WidgetRef
