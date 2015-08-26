/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Rule Location</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getRuleLocation()
 * @model
 * @generated
 */
public enum RuleLocation implements Enumerator
{
  /**
   * The '<em><b>State Definition</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #STATE_DEFINITION_VALUE
   * @generated
   * @ordered
   */
  STATE_DEFINITION(0, "StateDefinition", "StateDefinition"),

  /**
   * The '<em><b>Team Definition</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TEAM_DEFINITION_VALUE
   * @generated
   * @ordered
   */
  TEAM_DEFINITION(1, "TeamDefinition", "TeamDefinition"),

  /**
   * The '<em><b>Actionable Item</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #ACTIONABLE_ITEM_VALUE
   * @generated
   * @ordered
   */
  ACTIONABLE_ITEM(2, "ActionableItem", "ActionableItem");

  /**
   * The '<em><b>State Definition</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>State Definition</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #STATE_DEFINITION
   * @model name="StateDefinition"
   * @generated
   * @ordered
   */
  public static final int STATE_DEFINITION_VALUE = 0;

  /**
   * The '<em><b>Team Definition</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Team Definition</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TEAM_DEFINITION
   * @model name="TeamDefinition"
   * @generated
   * @ordered
   */
  public static final int TEAM_DEFINITION_VALUE = 1;

  /**
   * The '<em><b>Actionable Item</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Actionable Item</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ACTIONABLE_ITEM
   * @model name="ActionableItem"
   * @generated
   * @ordered
   */
  public static final int ACTIONABLE_ITEM_VALUE = 2;

  /**
   * An array of all the '<em><b>Rule Location</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final RuleLocation[] VALUES_ARRAY =
    new RuleLocation[]
    {
      STATE_DEFINITION,
      TEAM_DEFINITION,
      ACTIONABLE_ITEM,
    };

  /**
   * A public read-only list of all the '<em><b>Rule Location</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<RuleLocation> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Rule Location</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static RuleLocation get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      RuleLocation result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Rule Location</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static RuleLocation getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      RuleLocation result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Rule Location</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static RuleLocation get(int value)
  {
    switch (value)
    {
      case STATE_DEFINITION_VALUE: return STATE_DEFINITION;
      case TEAM_DEFINITION_VALUE: return TEAM_DEFINITION;
      case ACTIONABLE_ITEM_VALUE: return ACTIONABLE_ITEM;
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
  private RuleLocation(int value, String name, String literal)
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
  
} //RuleLocation
