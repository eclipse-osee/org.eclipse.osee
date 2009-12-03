/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.tests;

import junit.textui.TestRunner;
import org.eclipse.osee.framework.oseeTypes.OseeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Osee Type</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class OseeTypeTest extends OseeElementTest {

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static void main(String[] args) {
      TestRunner.run(OseeTypeTest.class);
   }

   /**
    * Constructs a new Osee Type test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public OseeTypeTest(String name) {
      super(name);
   }

   /**
    * Returns the fixture for this Osee Type test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected OseeType getFixture() {
      return (OseeType) fixture;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see junit.framework.TestCase#setUp()
    * @generated
    */
   @Override
   protected void setUp() throws Exception {
      setFixture(OseeTypesFactory.eINSTANCE.createOseeType());
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see junit.framework.TestCase#tearDown()
    * @generated
    */
   @Override
   protected void tearDown() throws Exception {
      setFixture(null);
   }

} //OseeTypeTest
