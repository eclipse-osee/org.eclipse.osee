/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.tests;

import junit.textui.TestRunner;

import org.eclipse.osee.framework.oseeTypes.AddEnum;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Add Enum</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class AddEnumTest extends OverrideOptionTest {

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static void main(String[] args) {
      TestRunner.run(AddEnumTest.class);
   }

   /**
    * Constructs a new Add Enum test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public AddEnumTest(String name) {
      super(name);
   }

   /**
    * Returns the fixture for this Add Enum test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected AddEnum getFixture() {
      return (AddEnum)fixture;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see junit.framework.TestCase#setUp()
    * @generated
    */
   @Override
   protected void setUp() throws Exception {
      setFixture(OseeTypesFactory.eINSTANCE.createAddEnum());
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

} //AddEnumTest
