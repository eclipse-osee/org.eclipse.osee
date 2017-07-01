/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Layout Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.LayoutDef#getLayoutItems <em>Layout Items</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getLayoutDef()
 * @model
 * @generated
 */
public interface LayoutDef extends LayoutType
{
  /**
   * Returns the value of the '<em><b>Layout Items</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.LayoutItem}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Layout Items</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Layout Items</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getLayoutDef_LayoutItems()
   * @model containment="true"
   * @generated
   */
  EList<LayoutItem> getLayoutItems();

} // LayoutDef
