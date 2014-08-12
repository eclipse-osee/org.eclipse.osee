/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Os Branch Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchType()
 * @model
 * @generated
 */
public enum OsBranchType implements Enumerator
{
  /**
   * The '<em><b>WORKING</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #WORKING_VALUE
   * @generated
   * @ordered
   */
  WORKING(0, "WORKING", "working"),

  /**
   * The '<em><b>BASELINE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #BASELINE_VALUE
   * @generated
   * @ordered
   */
  BASELINE(1, "BASELINE", "baseline"),

  /**
   * The '<em><b>MERGE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MERGE_VALUE
   * @generated
   * @ordered
   */
  MERGE(2, "MERGE", "merge"),

  /**
   * The '<em><b>SYSTEM ROOT</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #SYSTEM_ROOT_VALUE
   * @generated
   * @ordered
   */
  SYSTEM_ROOT(3, "SYSTEM_ROOT", "system-root"),

  /**
   * The '<em><b>PORT</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #PORT_VALUE
   * @generated
   * @ordered
   */
  PORT(4, "PORT", "port");

  /**
   * The '<em><b>WORKING</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>WORKING</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #WORKING
   * @model literal="working"
   * @generated
   * @ordered
   */
  public static final int WORKING_VALUE = 0;

  /**
   * The '<em><b>BASELINE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>BASELINE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #BASELINE
   * @model literal="baseline"
   * @generated
   * @ordered
   */
  public static final int BASELINE_VALUE = 1;

  /**
   * The '<em><b>MERGE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>MERGE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MERGE
   * @model literal="merge"
   * @generated
   * @ordered
   */
  public static final int MERGE_VALUE = 2;

  /**
   * The '<em><b>SYSTEM ROOT</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>SYSTEM ROOT</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #SYSTEM_ROOT
   * @model literal="system-root"
   * @generated
   * @ordered
   */
  public static final int SYSTEM_ROOT_VALUE = 3;

  /**
   * The '<em><b>PORT</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>PORT</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #PORT
   * @model literal="port"
   * @generated
   * @ordered
   */
  public static final int PORT_VALUE = 4;

  /**
   * An array of all the '<em><b>Os Branch Type</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final OsBranchType[] VALUES_ARRAY =
    new OsBranchType[]
    {
      WORKING,
      BASELINE,
      MERGE,
      SYSTEM_ROOT,
      PORT,
    };

  /**
   * A public read-only list of all the '<em><b>Os Branch Type</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<OsBranchType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Os Branch Type</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OsBranchType get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OsBranchType result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Os Branch Type</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OsBranchType getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OsBranchType result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Os Branch Type</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OsBranchType get(int value)
  {
    switch (value)
    {
      case WORKING_VALUE: return WORKING;
      case BASELINE_VALUE: return BASELINE;
      case MERGE_VALUE: return MERGE;
      case SYSTEM_ROOT_VALUE: return SYSTEM_ROOT;
      case PORT_VALUE: return PORT;
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
  private OsBranchType(int value, String name, String literal)
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
  
} //OsBranchType
