/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Os Tx Type</b></em>', and utility
 * methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxType()
 * @model
 * @generated
 */
public enum OsTxType implements Enumerator {
   /**
    * The '<em><b>BASELINE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #BASELINE_VALUE
    * @generated
    * @ordered
    */
   BASELINE(0, "BASELINE", "baseline"),

   /**
    * The '<em><b>NON BASELINE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #NON_BASELINE_VALUE
    * @generated
    * @ordered
    */
   NON_BASELINE(1, "NON_BASELINE", "non-baseline");

   /**
    * The '<em><b>BASELINE</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>BASELINE</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #BASELINE
    * @model literal="baseline"
    * @generated
    * @ordered
    */
   public static final int BASELINE_VALUE = 0;

   /**
    * The '<em><b>NON BASELINE</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>NON BASELINE</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #NON_BASELINE
    * @model literal="non-baseline"
    * @generated
    * @ordered
    */
   public static final int NON_BASELINE_VALUE = 1;

   /**
    * An array of all the '<em><b>Os Tx Type</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private static final OsTxType[] VALUES_ARRAY = new OsTxType[] {BASELINE, NON_BASELINE,};

   /**
    * A public read-only list of all the '<em><b>Os Tx Type</b></em>' enumerators. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static final List<OsTxType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Os Tx Type</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static OsTxType get(String literal) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsTxType result = VALUES_ARRAY[i];
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Tx Type</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static OsTxType getByName(String name) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsTxType result = VALUES_ARRAY[i];
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Tx Type</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static OsTxType get(int value) {
      switch (value) {
         case BASELINE_VALUE:
            return BASELINE;
         case NON_BASELINE_VALUE:
            return NON_BASELINE;
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
   private OsTxType(int value, String name, String literal) {
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

} //OsTxType
