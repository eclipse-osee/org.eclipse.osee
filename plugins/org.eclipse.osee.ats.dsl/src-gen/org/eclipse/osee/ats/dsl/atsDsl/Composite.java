/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Composite</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getNumColumns <em>Num Columns</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getLayoutItems <em>Layout Items</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getOptions <em>Options</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getComposite()
 * @model
 * @generated
 */
public interface Composite extends LayoutItem
{
  /**
   * Returns the value of the '<em><b>Num Columns</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Num Columns</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Num Columns</em>' attribute.
   * @see #setNumColumns(int)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getComposite_NumColumns()
   * @model
   * @generated
   */
  int getNumColumns();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.Composite#getNumColumns <em>Num Columns</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Num Columns</em>' attribute.
   * @see #getNumColumns()
   * @generated
   */
  void setNumColumns(int value);

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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getComposite_LayoutItems()
   * @model containment="true"
   * @generated
   */
  EList<LayoutItem> getLayoutItems();

  /**
   * Returns the value of the '<em><b>Options</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Options</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Options</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getComposite_Options()
   * @model unique="false"
   * @generated
   */
  EList<String> getOptions();

} // Composite
