/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Os Branch Archive Filter</b></em>
 * ', and utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchArchiveFilter()
 * @model
 * @generated
 */
public enum OsBranchArchiveFilter implements Enumerator {
   /**
    * The '<em><b>ARCHIVED EXCLUDED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #ARCHIVED_EXCLUDED_VALUE
    * @generated
    * @ordered
    */
   ARCHIVED_EXCLUDED(0, "ARCHIVED_EXCLUDED", "excluded"),

   /**
    * The '<em><b>ARCHIVED INCLUDED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #ARCHIVED_INCLUDED_VALUE
    * @generated
    * @ordered
    */
   ARCHIVED_INCLUDED(1, "ARCHIVED_INCLUDED", "included");

   /**
    * The '<em><b>ARCHIVED EXCLUDED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>ARCHIVED EXCLUDED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #ARCHIVED_EXCLUDED
    * @model literal="excluded"
    * @generated
    * @ordered
    */
   public static final int ARCHIVED_EXCLUDED_VALUE = 0;

   /**
    * The '<em><b>ARCHIVED INCLUDED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>ARCHIVED INCLUDED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #ARCHIVED_INCLUDED
    * @model literal="included"
    * @generated
    * @ordered
    */
   public static final int ARCHIVED_INCLUDED_VALUE = 1;

   /**
    * An array of all the '<em><b>Os Branch Archive Filter</b></em>' enumerators. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   private static final OsBranchArchiveFilter[] VALUES_ARRAY =
      new OsBranchArchiveFilter[] {ARCHIVED_EXCLUDED, ARCHIVED_INCLUDED,};

   /**
    * A public read-only list of all the '<em><b>Os Branch Archive Filter</b></em>' enumerators. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static final List<OsBranchArchiveFilter> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Os Branch Archive Filter</b></em>' literal with the specified literal value. <!--
    * begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsBranchArchiveFilter get(String literal) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsBranchArchiveFilter result = VALUES_ARRAY[i];
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Branch Archive Filter</b></em>' literal with the specified name. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsBranchArchiveFilter getByName(String name) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsBranchArchiveFilter result = VALUES_ARRAY[i];
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Branch Archive Filter</b></em>' literal with the specified integer value. <!--
    * begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsBranchArchiveFilter get(int value) {
      switch (value) {
         case ARCHIVED_EXCLUDED_VALUE:
            return ARCHIVED_EXCLUDED;
         case ARCHIVED_INCLUDED_VALUE:
            return ARCHIVED_INCLUDED;
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
   private OsBranchArchiveFilter(int value, String name, String literal) {
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

} //OsBranchArchiveFilter
