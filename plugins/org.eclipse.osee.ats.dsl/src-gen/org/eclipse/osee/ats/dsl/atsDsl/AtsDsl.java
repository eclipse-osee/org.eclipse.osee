/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ats Dsl</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getUserDef <em>User Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getTeamDef <em>Team Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getActionableItemDef <em>Actionable Item Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.AtsDsl#getWorkDef <em>Work Def</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAtsDsl()
 * @model
 * @generated
 */
public interface AtsDsl extends EObject
{
  /**
   * Returns the value of the '<em><b>User Def</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.UserDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User Def</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User Def</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAtsDsl_UserDef()
   * @model containment="true"
   * @generated
   */
  EList<UserDef> getUserDef();

  /**
   * Returns the value of the '<em><b>Team Def</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.TeamDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Team Def</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Team Def</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAtsDsl_TeamDef()
   * @model containment="true"
   * @generated
   */
  EList<TeamDef> getTeamDef();

  /**
   * Returns the value of the '<em><b>Actionable Item Def</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Actionable Item Def</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Actionable Item Def</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAtsDsl_ActionableItemDef()
   * @model containment="true"
   * @generated
   */
  EList<ActionableItemDef> getActionableItemDef();

  /**
   * Returns the value of the '<em><b>Work Def</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.WorkDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Work Def</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Work Def</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getAtsDsl_WorkDef()
   * @model containment="true"
   * @generated
   */
  EList<WorkDef> getWorkDef();

} // AtsDsl
