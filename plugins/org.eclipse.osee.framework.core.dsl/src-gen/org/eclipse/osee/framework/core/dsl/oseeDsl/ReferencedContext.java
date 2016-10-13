/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Referenced Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext#getAccessContextRef <em>Access Context Ref</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getReferencedContext()
 * @model
 * @generated
 */
public interface ReferencedContext extends EObject
{
  /**
   * Returns the value of the '<em><b>Access Context Ref</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Access Context Ref</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Access Context Ref</em>' attribute.
   * @see #setAccessContextRef(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getReferencedContext_AccessContextRef()
   * @model
   * @generated
   */
  String getAccessContextRef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext#getAccessContextRef <em>Access Context Ref</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Access Context Ref</em>' attribute.
   * @see #getAccessContextRef()
   * @generated
   */
  void setAccessContextRef(String value);

} // ReferencedContext
