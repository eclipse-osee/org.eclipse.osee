/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Permission Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getObjectRestriction <em>Object Restriction</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getPermissionRule()
 * @model
 * @generated
 */
public interface PermissionRule extends EObject
{
  /**
   * Returns the value of the '<em><b>Permission</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Permission</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Permission</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @see #setPermission(AccessPermissionEnum)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getPermissionRule_Permission()
   * @model
   * @generated
   */
  AccessPermissionEnum getPermission();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getPermission <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Permission</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @see #getPermission()
   * @generated
   */
  void setPermission(AccessPermissionEnum value);

  /**
   * Returns the value of the '<em><b>Object Restriction</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Object Restriction</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Object Restriction</em>' containment reference.
   * @see #setObjectRestriction(ObjectRestriction)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getPermissionRule_ObjectRestriction()
   * @model containment="true"
   * @generated
   */
  ObjectRestriction getObjectRestriction();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule#getObjectRestriction <em>Object Restriction</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Object Restriction</em>' containment reference.
   * @see #getObjectRestriction()
   * @generated
   */
  void setObjectRestriction(ObjectRestriction value);

} // PermissionRule
