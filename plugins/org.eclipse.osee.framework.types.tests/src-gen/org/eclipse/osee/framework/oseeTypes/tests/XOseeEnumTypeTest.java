/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.tests;

import junit.textui.TestRunner;

import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.XOseeEnumType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>XOsee Enum Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class XOseeEnumTypeTest extends OseeTypeTest {

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static void main(String[] args) {
      TestRunner.run(XOseeEnumTypeTest.class);
   }

   /**
    * Constructs a new XOsee Enum Type test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public XOseeEnumTypeTest(String name) {
      super(name);
   }

   /**
    * Returns the fixture for this XOsee Enum Type test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected XOseeEnumType getFixture() {
      return (XOseeEnumType)fixture;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see junit.framework.TestCase#setUp()
    * @generated
    */
   @Override
   protected void setUp() throws Exception {
      setFixture(OseeTypesFactory.eINSTANCE.createXOseeEnumType());
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

} //XOseeEnumTypeTest
