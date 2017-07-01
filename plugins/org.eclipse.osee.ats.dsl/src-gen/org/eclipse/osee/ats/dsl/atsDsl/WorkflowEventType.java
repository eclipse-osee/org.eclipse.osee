/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Workflow Event Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getWorkflowEventType()
 * @model
 * @generated
 */
public enum WorkflowEventType implements Enumerator
{
  /**
   * The '<em><b>Transition To</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TRANSITION_TO_VALUE
   * @generated
   * @ordered
   */
  TRANSITION_TO(0, "TransitionTo", "TransitionTo"),

  /**
   * The '<em><b>Create Branch</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #CREATE_BRANCH_VALUE
   * @generated
   * @ordered
   */
  CREATE_BRANCH(1, "CreateBranch", "CreateBranch"),

  /**
   * The '<em><b>Commit Branch</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #COMMIT_BRANCH_VALUE
   * @generated
   * @ordered
   */
  COMMIT_BRANCH(2, "CommitBranch", "CommitBranch");

  /**
   * The '<em><b>Transition To</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Transition To</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TRANSITION_TO
   * @model name="TransitionTo"
   * @generated
   * @ordered
   */
  public static final int TRANSITION_TO_VALUE = 0;

  /**
   * The '<em><b>Create Branch</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Create Branch</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #CREATE_BRANCH
   * @model name="CreateBranch"
   * @generated
   * @ordered
   */
  public static final int CREATE_BRANCH_VALUE = 1;

  /**
   * The '<em><b>Commit Branch</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Commit Branch</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #COMMIT_BRANCH
   * @model name="CommitBranch"
   * @generated
   * @ordered
   */
  public static final int COMMIT_BRANCH_VALUE = 2;

  /**
   * An array of all the '<em><b>Workflow Event Type</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final WorkflowEventType[] VALUES_ARRAY =
    new WorkflowEventType[]
    {
      TRANSITION_TO,
      CREATE_BRANCH,
      COMMIT_BRANCH,
    };

  /**
   * A public read-only list of all the '<em><b>Workflow Event Type</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<WorkflowEventType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Workflow Event Type</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static WorkflowEventType get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      WorkflowEventType result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Workflow Event Type</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static WorkflowEventType getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      WorkflowEventType result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Workflow Event Type</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static WorkflowEventType get(int value)
  {
    switch (value)
    {
      case TRANSITION_TO_VALUE: return TRANSITION_TO;
      case CREATE_BRANCH_VALUE: return CREATE_BRANCH;
      case COMMIT_BRANCH_VALUE: return COMMIT_BRANCH;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private WorkflowEventType(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }
  
} //WorkflowEventType
