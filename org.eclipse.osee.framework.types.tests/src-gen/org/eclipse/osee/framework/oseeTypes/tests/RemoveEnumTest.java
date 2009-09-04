/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.tests;

import junit.textui.TestRunner;

import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.RemoveEnum;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Remove Enum</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class RemoveEnumTest extends OverrideOptionTest {

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static void main(String[] args) {
      TestRunner.run(RemoveEnumTest.class);
   }

   /**
    * Constructs a new Remove Enum test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public RemoveEnumTest(String name) {
      super(name);
   }

   /**
    * Returns the fixture for this Remove Enum test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected RemoveEnum getFixture() {
      return (RemoveEnum)fixture;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see junit.framework.TestCase#setUp()
    * @generated
    */
   @Override
   protected void setUp() throws Exception {
      setFixture(OseeTypesFactory.eINSTANCE.createRemoveEnum());
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

} //RemoveEnumTest
