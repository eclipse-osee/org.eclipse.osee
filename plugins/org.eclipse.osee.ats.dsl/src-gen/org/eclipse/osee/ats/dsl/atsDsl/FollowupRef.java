/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Followup Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.FollowupRef#getAssigneeRefs <em>Assignee Refs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getFollowupRef()
 * @model
 * @generated
 */
public interface FollowupRef extends EObject
{
  /**
   * Returns the value of the '<em><b>Assignee Refs</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.UserRef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Assignee Refs</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Assignee Refs</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getFollowupRef_AssigneeRefs()
   * @model containment="true"
   * @generated
   */
  EList<UserRef> getAssigneeRefs();

} // FollowupRef
