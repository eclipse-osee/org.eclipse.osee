/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Os Branch State</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchState()
 * @model
 * @generated
 */
public enum OsBranchState implements Enumerator {
   /**
    * The '<em><b>CREATED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #CREATED_VALUE
    * @generated
    * @ordered
    */
   CREATED(0, "CREATED", "created"),

   /**
    * The '<em><b>MODIFIED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #MODIFIED_VALUE
    * @generated
    * @ordered
    */
   MODIFIED(1, "MODIFIED", "modified"),

   /**
    * The '<em><b>COMMITTED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #COMMITTED_VALUE
    * @generated
    * @ordered
    */
   COMMITTED(2, "COMMITTED", "committed"),

   /**
    * The '<em><b>REBASELINED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #REBASELINED_VALUE
    * @generated
    * @ordered
    */
   REBASELINED(3, "REBASELINED", "rebaselined"),

   /**
    * The '<em><b>DELETED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #DELETED_VALUE
    * @generated
    * @ordered
    */
   DELETED(4, "DELETED", "deleted"),

   /**
    * The '<em><b>REBASELINE IN PROGRESS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #REBASELINE_IN_PROGRESS_VALUE
    * @generated
    * @ordered
    */
   REBASELINE_IN_PROGRESS(5, "REBASELINE_IN_PROGRESS", "rebaseline_in_progress"),

   /**
    * The '<em><b>COMMIT IN PROGRESS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #COMMIT_IN_PROGRESS_VALUE
    * @generated
    * @ordered
    */
   COMMIT_IN_PROGRESS(6, "COMMIT_IN_PROGRESS", "commit_in_progress"),

   /**
    * The '<em><b>CREATION IN PROGRESS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #CREATION_IN_PROGRESS_VALUE
    * @generated
    * @ordered
    */
   CREATION_IN_PROGRESS(7, "CREATION_IN_PROGRESS", "creation_in_progress"),

   /**
    * The '<em><b>DELETE IN PROGRESS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #DELETE_IN_PROGRESS_VALUE
    * @generated
    * @ordered
    */
   DELETE_IN_PROGRESS(8, "DELETE_IN_PROGRESS", "delete_in_progress"),

   /**
    * The '<em><b>PURGE IN PROGRESS</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #PURGE_IN_PROGRESS_VALUE
    * @generated
    * @ordered
    */
   PURGE_IN_PROGRESS(9, "PURGE_IN_PROGRESS", "purge_in_progress"),

   /**
    * The '<em><b>PURGED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #PURGED_VALUE
    * @generated
    * @ordered
    */
   PURGED(10, "PURGED", "purged");

   /**
    * The '<em><b>CREATED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>CREATED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #CREATED
    * @model literal="created"
    * @generated
    * @ordered
    */
   public static final int CREATED_VALUE = 0;

   /**
    * The '<em><b>MODIFIED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>MODIFIED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #MODIFIED
    * @model literal="modified"
    * @generated
    * @ordered
    */
   public static final int MODIFIED_VALUE = 1;

   /**
    * The '<em><b>COMMITTED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>COMMITTED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #COMMITTED
    * @model literal="committed"
    * @generated
    * @ordered
    */
   public static final int COMMITTED_VALUE = 2;

   /**
    * The '<em><b>REBASELINED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>REBASELINED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #REBASELINED
    * @model literal="rebaselined"
    * @generated
    * @ordered
    */
   public static final int REBASELINED_VALUE = 3;

   /**
    * The '<em><b>DELETED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>DELETED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #DELETED
    * @model literal="deleted"
    * @generated
    * @ordered
    */
   public static final int DELETED_VALUE = 4;

   /**
    * The '<em><b>REBASELINE IN PROGRESS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>REBASELINE IN PROGRESS</b></em>' literal object isn't clear, there really should be more
    * of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #REBASELINE_IN_PROGRESS
    * @model literal="rebaseline_in_progress"
    * @generated
    * @ordered
    */
   public static final int REBASELINE_IN_PROGRESS_VALUE = 5;

   /**
    * The '<em><b>COMMIT IN PROGRESS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>COMMIT IN PROGRESS</b></em>' literal object isn't clear, there really should be more of
    * a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #COMMIT_IN_PROGRESS
    * @model literal="commit_in_progress"
    * @generated
    * @ordered
    */
   public static final int COMMIT_IN_PROGRESS_VALUE = 6;

   /**
    * The '<em><b>CREATION IN PROGRESS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>CREATION IN PROGRESS</b></em>' literal object isn't clear, there really should be more
    * of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #CREATION_IN_PROGRESS
    * @model literal="creation_in_progress"
    * @generated
    * @ordered
    */
   public static final int CREATION_IN_PROGRESS_VALUE = 7;

   /**
    * The '<em><b>DELETE IN PROGRESS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>DELETE IN PROGRESS</b></em>' literal object isn't clear, there really should be more of
    * a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #DELETE_IN_PROGRESS
    * @model literal="delete_in_progress"
    * @generated
    * @ordered
    */
   public static final int DELETE_IN_PROGRESS_VALUE = 8;

   /**
    * The '<em><b>PURGE IN PROGRESS</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>PURGE IN PROGRESS</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #PURGE_IN_PROGRESS
    * @model literal="purge_in_progress"
    * @generated
    * @ordered
    */
   public static final int PURGE_IN_PROGRESS_VALUE = 9;

   /**
    * The '<em><b>PURGED</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>PURGED</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #PURGED
    * @model literal="purged"
    * @generated
    * @ordered
    */
   public static final int PURGED_VALUE = 10;

   /**
    * An array of all the '<em><b>Os Branch State</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private static final OsBranchState[] VALUES_ARRAY = new OsBranchState[] {
      CREATED,
      MODIFIED,
      COMMITTED,
      REBASELINED,
      DELETED,
      REBASELINE_IN_PROGRESS,
      COMMIT_IN_PROGRESS,
      CREATION_IN_PROGRESS,
      DELETE_IN_PROGRESS,
      PURGE_IN_PROGRESS,
      PURGED,};

   /**
    * A public read-only list of all the '<em><b>Os Branch State</b></em>' enumerators. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static final List<OsBranchState> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Os Branch State</b></em>' literal with the specified literal value. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsBranchState get(String literal) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsBranchState result = VALUES_ARRAY[i];
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Branch State</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static OsBranchState getByName(String name) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsBranchState result = VALUES_ARRAY[i];
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Branch State</b></em>' literal with the specified integer value. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsBranchState get(int value) {
      switch (value) {
         case CREATED_VALUE:
            return CREATED;
         case MODIFIED_VALUE:
            return MODIFIED;
         case COMMITTED_VALUE:
            return COMMITTED;
         case REBASELINED_VALUE:
            return REBASELINED;
         case DELETED_VALUE:
            return DELETED;
         case REBASELINE_IN_PROGRESS_VALUE:
            return REBASELINE_IN_PROGRESS;
         case COMMIT_IN_PROGRESS_VALUE:
            return COMMIT_IN_PROGRESS;
         case CREATION_IN_PROGRESS_VALUE:
            return CREATION_IN_PROGRESS;
         case DELETE_IN_PROGRESS_VALUE:
            return DELETE_IN_PROGRESS;
         case PURGE_IN_PROGRESS_VALUE:
            return PURGE_IN_PROGRESS;
         case PURGED_VALUE:
            return PURGED;
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
   private OsBranchState(int value, String name, String literal) {
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

} //OsBranchState
