/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Os Existence Operator</b></em>',
 * and utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsExistenceOperator()
 * @model
 * @generated
 */
public enum OsExistenceOperator implements Enumerator {
   /**
    * The '<em><b>EXISTS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #EXISTS_VALUE
    * @generated
    * @ordered
    */
   EXISTS(0, "EXISTS", "exists"),

   /**
    * The '<em><b>NOT EXISTS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #NOT_EXISTS_VALUE
    * @generated
    * @ordered
    */
   NOT_EXISTS(1, "NOT_EXISTS", "not-exists");

   /**
    * The '<em><b>EXISTS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>EXISTS</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #EXISTS
    * @model literal="exists"
    * @generated
    * @ordered
    */
   public static final int EXISTS_VALUE = 0;

   /**
    * The '<em><b>NOT EXISTS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>NOT EXISTS</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #NOT_EXISTS
    * @model literal="not-exists"
    * @generated
    * @ordered
    */
   public static final int NOT_EXISTS_VALUE = 1;

   /**
    * An array of all the '<em><b>Os Existence Operator</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc
    * -->
    * 
    * @generated
    */
   private static final OsExistenceOperator[] VALUES_ARRAY = new OsExistenceOperator[] {EXISTS, NOT_EXISTS,};

   /**
    * A public read-only list of all the '<em><b>Os Existence Operator</b></em>' enumerators. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static final List<OsExistenceOperator> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Os Existence Operator</b></em>' literal with the specified literal value. <!-- begin-user-doc
    * --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsExistenceOperator get(String literal) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsExistenceOperator result = VALUES_ARRAY[i];
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Existence Operator</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static OsExistenceOperator getByName(String name) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsExistenceOperator result = VALUES_ARRAY[i];
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Existence Operator</b></em>' literal with the specified integer value. <!-- begin-user-doc
    * --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsExistenceOperator get(int value) {
      switch (value) {
         case EXISTS_VALUE:
            return EXISTS;
         case NOT_EXISTS_VALUE:
            return NOT_EXISTS;
      }
      return null;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private final int value;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private final String name;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private final String literal;

   /**
    * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private OsExistenceOperator(int value, String name, String literal) {
      this.value = value;
      this.name = name;
      this.literal = literal;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public int getValue() {
      return value;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String getLiteral() {
      return literal;
   }

   /**
    * Returns the literal value of the enumerator, which is its string representation. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String toString() {
      return literal;
   }

} //OsExistenceOperator
