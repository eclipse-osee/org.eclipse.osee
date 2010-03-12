/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.tests;

import junit.textui.TestRunner;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.XAttributeType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Attribute Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class AttributeTypeTest extends OseeTypeTest {

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static void main(String[] args) {
      TestRunner.run(AttributeTypeTest.class);
   }

   /**
    * Constructs a new Attribute Type test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public AttributeTypeTest(String name) {
      super(name);
   }

   /**
    * Returns the fixture for this Attribute Type test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected XAttributeType getFixture() {
      return (XAttributeType) fixture;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see junit.framework.TestCase#setUp()
    * @generated
    */
   @Override
   protected void setUp() throws Exception {
      setFixture(OseeTypesFactory.eINSTANCE.createXAttributeType());
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

} //AttributeTypeTest
