/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.eclipse.osee.framework.oseeTypes.Import;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Import</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ImportTest extends TestCase {

   /**
    * The fixture for this Import test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected Import fixture = null;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static void main(String[] args) {
      TestRunner.run(ImportTest.class);
   }

   /**
    * Constructs a new Import test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public ImportTest(String name) {
      super(name);
   }

   /**
    * Sets the fixture for this Import test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void setFixture(Import fixture) {
      this.fixture = fixture;
   }

   /**
    * Returns the fixture for this Import test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected Import getFixture() {
      return fixture;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see junit.framework.TestCase#setUp()
    * @generated
    */
   @Override
   protected void setUp() throws Exception {
      setFixture(OseeTypesFactory.eINSTANCE.createImport());
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see junit.framework.TestCase#tearDown()
    * @generated
    */
   @Override
   protected void tearDown() throws Exception {
      setFixture(null);
   }

} //ImportTest
