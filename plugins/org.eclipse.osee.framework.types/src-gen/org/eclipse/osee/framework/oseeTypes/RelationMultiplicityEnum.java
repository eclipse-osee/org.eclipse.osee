/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Relation Multiplicity Enum</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationMultiplicityEnum()
 * @model
 * @generated
 */
public enum RelationMultiplicityEnum implements Enumerator
{
  /**
   * The '<em><b>ONE TO ONE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #ONE_TO_ONE_VALUE
   * @generated
   * @ordered
   */
  ONE_TO_ONE(0, "ONE_TO_ONE", "ONE_TO_ONE"),

  /**
   * The '<em><b>ONE TO MANY</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #ONE_TO_MANY_VALUE
   * @generated
   * @ordered
   */
  ONE_TO_MANY(1, "ONE_TO_MANY", "ONE_TO_MANY"),

  /**
   * The '<em><b>MANY TO ONE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MANY_TO_ONE_VALUE
   * @generated
   * @ordered
   */
  MANY_TO_ONE(2, "MANY_TO_ONE", "MANY_TO_ONE"),

  /**
   * The '<em><b>MANY TO MANY</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MANY_TO_MANY_VALUE
   * @generated
   * @ordered
   */
  MANY_TO_MANY(3, "MANY_TO_MANY", "MANY_TO_MANY");

  /**
   * The '<em><b>ONE TO ONE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>ONE TO ONE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ONE_TO_ONE
   * @model
   * @generated
   * @ordered
   */
  public static final int ONE_TO_ONE_VALUE = 0;

  /**
   * The '<em><b>ONE TO MANY</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>ONE TO MANY</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ONE_TO_MANY
   * @model
   * @generated
   * @ordered
   */
  public static final int ONE_TO_MANY_VALUE = 1;

  /**
   * The '<em><b>MANY TO ONE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>MANY TO ONE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MANY_TO_ONE
   * @model
   * @generated
   * @ordered
   */
  public static final int MANY_TO_ONE_VALUE = 2;

  /**
   * The '<em><b>MANY TO MANY</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>MANY TO MANY</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MANY_TO_MANY
   * @model
   * @generated
   * @ordered
   */
  public static final int MANY_TO_MANY_VALUE = 3;

  /**
   * An array of all the '<em><b>Relation Multiplicity Enum</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final RelationMultiplicityEnum[] VALUES_ARRAY =
    new RelationMultiplicityEnum[]
    {
      ONE_TO_ONE,
      ONE_TO_MANY,
      MANY_TO_ONE,
      MANY_TO_MANY,
    };

  /**
   * A public read-only list of all the '<em><b>Relation Multiplicity Enum</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<RelationMultiplicityEnum> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Relation Multiplicity Enum</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static RelationMultiplicityEnum get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      RelationMultiplicityEnum result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Relation Multiplicity Enum</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static RelationMultiplicityEnum getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      RelationMultiplicityEnum result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Relation Multiplicity Enum</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static RelationMultiplicityEnum get(int value)
  {
    switch (value)
    {
      case ONE_TO_ONE_VALUE: return ONE_TO_ONE;
      case ONE_TO_MANY_VALUE: return ONE_TO_MANY;
      case MANY_TO_ONE_VALUE: return MANY_TO_ONE;
      case MANY_TO_MANY_VALUE: return MANY_TO_MANY;
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
  private RelationMultiplicityEnum(int value, String name, String literal)
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
  
} //RelationMultiplicityEnum
