/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hierarchy Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getArtifact <em>Artifact</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getAccessRules <em>Access Rules</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getHierarchyRestriction()
 * @model
 * @generated
 */
public interface HierarchyRestriction extends EObject
{
  /**
   * Returns the value of the '<em><b>Artifact</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact</em>' reference.
   * @see #setArtifact(XArtifactRef)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getHierarchyRestriction_Artifact()
   * @model
   * @generated
   */
  XArtifactRef getArtifact();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction#getArtifact <em>Artifact</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact</em>' reference.
   * @see #getArtifact()
   * @generated
   */
  void setArtifact(XArtifactRef value);

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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getHierarchyRestriction_AccessRules()
   * @model containment="true"
   * @generated
   */
  EList<ObjectRestriction> getAccessRules();

} // HierarchyRestriction
