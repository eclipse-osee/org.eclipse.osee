/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Os Query Option</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsQueryOption()
 * @model
 * @generated
 */
public enum OsQueryOption implements Enumerator
{
  /**
   * The '<em><b>CONTAINS</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #CONTAINS_VALUE
   * @generated
   * @ordered
   */
  CONTAINS(0, "CONTAINS", "contains"),

  /**
   * The '<em><b>CASE MATCH</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #CASE_MATCH_VALUE
   * @generated
   * @ordered
   */
  CASE_MATCH(1, "CASE__MATCH", "match-case"),

  /**
   * The '<em><b>CASE IGNORE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #CASE_IGNORE_VALUE
   * @generated
   * @ordered
   */
  CASE_IGNORE(2, "CASE__IGNORE", "ignore-case"),

  /**
   * The '<em><b>EXISTANCE EXISTS</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #EXISTANCE_EXISTS_VALUE
   * @generated
   * @ordered
   */
  EXISTANCE_EXISTS(3, "EXISTANCE__EXISTS", "not-exists"),

  /**
   * The '<em><b>EXISTANCE NOT EXISTS</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #EXISTANCE_NOT_EXISTS_VALUE
   * @generated
   * @ordered
   */
  EXISTANCE_NOT_EXISTS(4, "EXISTANCE__NOT_EXISTS", "exists"),

  /**
   * The '<em><b>TOKEN COUNT MATCH</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_COUNT_MATCH_VALUE
   * @generated
   * @ordered
   */
  TOKEN_COUNT_MATCH(5, "TOKEN_COUNT__MATCH", "match-token-count"),

  /**
   * The '<em><b>TOKEN COUNT IGNORE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_COUNT_IGNORE_VALUE
   * @generated
   * @ordered
   */
  TOKEN_COUNT_IGNORE(6, "TOKEN_COUNT__IGNORE", "ignore-token-count"),

  /**
   * The '<em><b>TOKEN DELIMITER EXACT</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_DELIMITER_EXACT_VALUE
   * @generated
   * @ordered
   */
  TOKEN_DELIMITER_EXACT(7, "TOKEN_DELIMITER__EXACT", "exact-delim"),

  /**
   * The '<em><b>TOKEN DELIMITER WHITESPACE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_DELIMITER_WHITESPACE_VALUE
   * @generated
   * @ordered
   */
  TOKEN_DELIMITER_WHITESPACE(8, "TOKEN_DELIMITER__WHITESPACE", "whitespace-delim"),

  /**
   * The '<em><b>TOKEN DELIMITER ANY</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_DELIMITER_ANY_VALUE
   * @generated
   * @ordered
   */
  TOKEN_DELIMITER_ANY(9, "TOKEN_DELIMITER__ANY", "any-delim"),

  /**
   * The '<em><b>TOKEN MATCH ORDER ANY</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_MATCH_ORDER_ANY_VALUE
   * @generated
   * @ordered
   */
  TOKEN_MATCH_ORDER_ANY(10, "TOKEN_MATCH_ORDER__ANY", "any-order"),

  /**
   * The '<em><b>TOKEN MATCH ORDER MATCH</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TOKEN_MATCH_ORDER_MATCH_VALUE
   * @generated
   * @ordered
   */
  TOKEN_MATCH_ORDER_MATCH(11, "TOKEN_MATCH_ORDER__MATCH", "match-order");

  /**
   * The '<em><b>CONTAINS</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CONTAINS</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #CONTAINS
   * @model literal="contains"
   * @generated
   * @ordered
   */
  public static final int CONTAINS_VALUE = 0;

  /**
   * The '<em><b>CASE MATCH</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CASE MATCH</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #CASE_MATCH
   * @model name="CASE__MATCH" literal="match-case"
   * @generated
   * @ordered
   */
  public static final int CASE_MATCH_VALUE = 1;

  /**
   * The '<em><b>CASE IGNORE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CASE IGNORE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #CASE_IGNORE
   * @model name="CASE__IGNORE" literal="ignore-case"
   * @generated
   * @ordered
   */
  public static final int CASE_IGNORE_VALUE = 2;

  /**
   * The '<em><b>EXISTANCE EXISTS</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>EXISTANCE EXISTS</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #EXISTANCE_EXISTS
   * @model name="EXISTANCE__EXISTS" literal="not-exists"
   * @generated
   * @ordered
   */
  public static final int EXISTANCE_EXISTS_VALUE = 3;

  /**
   * The '<em><b>EXISTANCE NOT EXISTS</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>EXISTANCE NOT EXISTS</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #EXISTANCE_NOT_EXISTS
   * @model name="EXISTANCE__NOT_EXISTS" literal="exists"
   * @generated
   * @ordered
   */
  public static final int EXISTANCE_NOT_EXISTS_VALUE = 4;

  /**
   * The '<em><b>TOKEN COUNT MATCH</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN COUNT MATCH</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_COUNT_MATCH
   * @model name="TOKEN_COUNT__MATCH" literal="match-token-count"
   * @generated
   * @ordered
   */
  public static final int TOKEN_COUNT_MATCH_VALUE = 5;

  /**
   * The '<em><b>TOKEN COUNT IGNORE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN COUNT IGNORE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_COUNT_IGNORE
   * @model name="TOKEN_COUNT__IGNORE" literal="ignore-token-count"
   * @generated
   * @ordered
   */
  public static final int TOKEN_COUNT_IGNORE_VALUE = 6;

  /**
   * The '<em><b>TOKEN DELIMITER EXACT</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN DELIMITER EXACT</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_DELIMITER_EXACT
   * @model name="TOKEN_DELIMITER__EXACT" literal="exact-delim"
   * @generated
   * @ordered
   */
  public static final int TOKEN_DELIMITER_EXACT_VALUE = 7;

  /**
   * The '<em><b>TOKEN DELIMITER WHITESPACE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN DELIMITER WHITESPACE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_DELIMITER_WHITESPACE
   * @model name="TOKEN_DELIMITER__WHITESPACE" literal="whitespace-delim"
   * @generated
   * @ordered
   */
  public static final int TOKEN_DELIMITER_WHITESPACE_VALUE = 8;

  /**
   * The '<em><b>TOKEN DELIMITER ANY</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN DELIMITER ANY</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_DELIMITER_ANY
   * @model name="TOKEN_DELIMITER__ANY" literal="any-delim"
   * @generated
   * @ordered
   */
  public static final int TOKEN_DELIMITER_ANY_VALUE = 9;

  /**
   * The '<em><b>TOKEN MATCH ORDER ANY</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN MATCH ORDER ANY</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_MATCH_ORDER_ANY
   * @model name="TOKEN_MATCH_ORDER__ANY" literal="any-order"
   * @generated
   * @ordered
   */
  public static final int TOKEN_MATCH_ORDER_ANY_VALUE = 10;

  /**
   * The '<em><b>TOKEN MATCH ORDER MATCH</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TOKEN MATCH ORDER MATCH</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TOKEN_MATCH_ORDER_MATCH
   * @model name="TOKEN_MATCH_ORDER__MATCH" literal="match-order"
   * @generated
   * @ordered
   */
  public static final int TOKEN_MATCH_ORDER_MATCH_VALUE = 11;

  /**
   * An array of all the '<em><b>Os Query Option</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final OsQueryOption[] VALUES_ARRAY =
    new OsQueryOption[]
    {
      CONTAINS,
      CASE_MATCH,
      CASE_IGNORE,
      EXISTANCE_EXISTS,
      EXISTANCE_NOT_EXISTS,
      TOKEN_COUNT_MATCH,
      TOKEN_COUNT_IGNORE,
      TOKEN_DELIMITER_EXACT,
      TOKEN_DELIMITER_WHITESPACE,
      TOKEN_DELIMITER_ANY,
      TOKEN_MATCH_ORDER_ANY,
      TOKEN_MATCH_ORDER_MATCH,
    };

  /**
   * A public read-only list of all the '<em><b>Os Query Option</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<OsQueryOption> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Os Query Option</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OsQueryOption get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OsQueryOption result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Os Query Option</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OsQueryOption getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OsQueryOption result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Os Query Option</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OsQueryOption get(int value)
  {
    switch (value)
    {
      case CONTAINS_VALUE: return CONTAINS;
      case CASE_MATCH_VALUE: return CASE_MATCH;
      case CASE_IGNORE_VALUE: return CASE_IGNORE;
      case EXISTANCE_EXISTS_VALUE: return EXISTANCE_EXISTS;
      case EXISTANCE_NOT_EXISTS_VALUE: return EXISTANCE_NOT_EXISTS;
      case TOKEN_COUNT_MATCH_VALUE: return TOKEN_COUNT_MATCH;
      case TOKEN_COUNT_IGNORE_VALUE: return TOKEN_COUNT_IGNORE;
      case TOKEN_DELIMITER_EXACT_VALUE: return TOKEN_DELIMITER_EXACT;
      case TOKEN_DELIMITER_WHITESPACE_VALUE: return TOKEN_DELIMITER_WHITESPACE;
      case TOKEN_DELIMITER_ANY_VALUE: return TOKEN_DELIMITER_ANY;
      case TOKEN_MATCH_ORDER_ANY_VALUE: return TOKEN_MATCH_ORDER_ANY;
      case TOKEN_MATCH_ORDER_MATCH_VALUE: return TOKEN_MATCH_ORDER_MATCH;
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
  private OsQueryOption(int value, String name, String literal)
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
  
} //OsQueryOption
