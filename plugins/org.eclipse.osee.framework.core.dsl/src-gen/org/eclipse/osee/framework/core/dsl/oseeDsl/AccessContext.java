/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Access Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getSuperAccessContexts <em>Super Access Contexts</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getAccessRules <em>Access Rules</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getHierarchyRestrictions <em>Hierarchy Restrictions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAccessContext()
 * @model
 * @generated
 */
public interface AccessContext extends EObject
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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAccessContext_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Super Access Contexts</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Access Contexts</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Access Contexts</em>' reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAccessContext_SuperAccessContexts()
   * @model
   * @generated
   */
  EList<AccessContext> getSuperAccessContexts();

  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(String)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAccessContext_Id()
   * @model
   * @generated
   */
  String getId();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(String value);

  /**
   * Returns the value of the '<em><b>Access Rules</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Access Rules</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Access Rules</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAccessContext_AccessRules()
   * @model containment="true"
   * @generated
   */
  EList<ObjectRestriction> getAccessRules();

  /**
   * Returns the value of the '<em><b>Hierarchy Restrictions</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Hierarchy Restrictions</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Hierarchy Restrictions</em>' containment reference list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getAccessContext_HierarchyRestrictions()
   * @model containment="true"
   * @generated
   */
  EList<HierarchyRestriction> getHierarchyRestrictions();

} // AccessContext
