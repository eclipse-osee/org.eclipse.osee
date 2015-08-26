/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Create Task Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getAssignees <em>Assignees</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getRelatedState <em>Related State</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getTaskWorkDef <em>Task Work Def</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getOnEvent <em>On Event</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateTaskRule()
 * @model
 * @generated
 */
public interface CreateTaskRule extends Rule
{
  /**
   * Returns the value of the '<em><b>Assignees</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.UserDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Assignees</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Assignees</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateTaskRule_Assignees()
   * @model containment="true"
   * @generated
   */
  EList<UserDef> getAssignees();

  /**
   * Returns the value of the '<em><b>Related State</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Related State</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Related State</em>' attribute.
   * @see #setRelatedState(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateTaskRule_RelatedState()
   * @model
   * @generated
   */
  String getRelatedState();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getRelatedState <em>Related State</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Related State</em>' attribute.
   * @see #getRelatedState()
   * @generated
   */
  void setRelatedState(String value);

  /**
   * Returns the value of the '<em><b>Task Work Def</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Task Work Def</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Task Work Def</em>' attribute.
   * @see #setTaskWorkDef(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateTaskRule_TaskWorkDef()
   * @model
   * @generated
   */
  String getTaskWorkDef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule#getTaskWorkDef <em>Task Work Def</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Task Work Def</em>' attribute.
   * @see #getTaskWorkDef()
   * @generated
   */
  void setTaskWorkDef(String value);

  /**
   * Returns the value of the '<em><b>On Event</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.OnEventType}.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.OnEventType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>On Event</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>On Event</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.OnEventType
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateTaskRule_OnEvent()
   * @model unique="false"
   * @generated
   */
  EList<OnEventType> getOnEvent();

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getCreateTaskRule_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<AttrDef> getAttributes();

} // CreateTaskRule
