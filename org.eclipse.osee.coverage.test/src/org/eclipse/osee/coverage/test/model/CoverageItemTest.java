/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import junit.framework.Assert;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageTestUnit;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageItemTest {

   public static CoverageUnit parent = null;
   public static CoverageItem ci1 = null, ci2 = null, ci3 = null, ci4 = null;

   @BeforeClass
   public static void testSetup() {
      parent = new CoverageUnit(null, "Top", "C:/UserData/");
      ci1 = new CoverageItem(parent, CoverageMethodEnum.Deactivated_Code, "1");
      ci1.setText("this is text");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#CoverageItem(org.eclipse.osee.coverage.model.CoverageUnit, org.eclipse.osee.coverage.model.CoverageMethodEnum, java.lang.String)}
    * .
    */
   @Test
   public void testCoverageItemCoverageUnitCoverageMethodEnumString() {
      Assert.assertNotNull(ci1);
      ci2 = new CoverageItem(parent, CoverageMethodEnum.Exception_Handling, "2");
      Assert.assertNotNull(ci2);
      ci3 = new CoverageItem(parent, CoverageMethodEnum.Test_Unit, "3");
      Assert.assertNotNull(ci3);
      ci4 = new CoverageItem(parent, CoverageMethodEnum.Not_Covered, "4");
      Assert.assertNotNull(ci4);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#CoverageItem(org.eclipse.osee.coverage.model.CoverageUnit, java.lang.String)}
    * .
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testCoverageItemCoverageUnitString() throws OseeCoreException {
      String xml = ci1.toXml();
      CoverageItem ci = new CoverageItem(parent, xml);
      Assert.assertEquals(ci1.getParent(), ci.getParent());
      Assert.assertEquals(ci1.getCoverageMethod(), ci.getCoverageMethod());
      Assert.assertEquals(ci1.getExecuteNum(), ci.getExecuteNum());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#addTestUnit(org.eclipse.osee.coverage.model.CoverageTestUnit)}
    * .
    */
   @Test
   public void testAddGetTestUnit() {
      for (int x = 0; x < 10; x++) {
         ci1.addTestUnit(new CoverageTestUnit("Test Unit " + x));
      }
      Assert.assertEquals(10, ci1.getTestUnits().size());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#setCoverageMethod(org.eclipse.osee.coverage.model.CoverageMethodEnum)}
    * .
    */
   @Test
   public void testSetGetCoverageMethod() {
      ci1.setCoverageMethod(CoverageMethodEnum.Exception_Handling);
      Assert.assertEquals(CoverageMethodEnum.Exception_Handling, ci1.getCoverageMethod());
      ci1.setCoverageMethod(CoverageMethodEnum.Deactivated_Code);
      Assert.assertEquals(CoverageMethodEnum.Deactivated_Code, ci1.getCoverageMethod());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getExecuteNum()}.
    */
   @Test
   public void testGetExecuteNum() {
      Assert.assertEquals("1", ci1.getExecuteNum());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#setLineNum(java.lang.String)}.
    */
   @Test
   public void testSetGetLineNum() {
      ci1.setLineNum("55");
      Assert.assertEquals("55", ci1.getLineNum());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getMethodNum()}.
    */
   @Test
   public void testSetGetMethodNum() {
      ci1.setMethodNum("33");
      Assert.assertEquals("33", ci1.getMethodNum());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getNotes()}.
    */
   @Test
   public void testGetNotes() {
      Assert.assertEquals(ci1.getNotes(), null);
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#isEditable()}.
    */
   @Test
   public void testIsEditable() {
      Assert.assertTrue(ci1.isEditable().isFalse());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getName()}.
    */
   @Test
   public void testGetName() {
      Assert.assertEquals(ci1.getName(), "33:1 [this is text]");
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#isCovered()}.
    */
   @Test
   public void testIsCovered() {
      Assert.assertTrue(ci1.isCovered());
      Assert.assertTrue(ci2.isCovered());
      Assert.assertTrue(ci3.isCovered());
      Assert.assertFalse(ci4.isCovered());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#equals(java.lang.Object)}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testEqualsObject() throws OseeCoreException {
      CoverageItem ci = new CoverageItem(parent, ci1.toXml());
      Assert.assertEquals(ci, ci1);
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getGuid()}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testGetGuid() throws OseeCoreException {
      CoverageItem ci = new CoverageItem(parent, ci1.toXml());
      Assert.assertEquals(ci1.getGuid(), ci.getGuid());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getParent()}.
    */
   @Test
   public void testGetParent() {
      Assert.assertTrue(ci1.getParent().equals(parent));
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#setGuid(java.lang.String)}.
    */
   @Test
   public void testSetGuid() throws OseeCoreException {
      CoverageItem ci = new CoverageItem(parent, ci1.toXml());
      ci.setGuid("asdf");
      Assert.assertEquals("asdf", ci.getGuid());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getCoverageRationale()}.
    */
   @Test
   public void testSetGetCoverageRationale() {
      ci1.setCoverageRationale("this is rationale");
      Assert.assertEquals("this is rationale", ci1.getCoverageRationale());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#setText(java.lang.String)}.
    */
   @Test
   public void testSetGetText() {
      ci1.setText("this is text2");
      Assert.assertEquals("this is text2", ci1.getText());
      ci1.setText("this is text");
      Assert.assertEquals("this is text", ci1.getText());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getLocation()}.
    */
   @Test
   public void testSetGetLocation() {
      Assert.assertEquals("", ci1.getLocation());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getNamespace()}.
    */
   @Test
   public void testGetNamespace() {
      Assert.assertEquals("", ci1.getNamespace());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#toXml()}.
    */
   @Test
   public void testToXml() throws OseeCoreException {
      CoverageItem ci = new CoverageItem(parent, ci1.toXml());
      Assert.assertEquals(ci1.getGuid(), ci.getGuid());
      Assert.assertEquals(ci1.getMethodNum(), ci.getMethodNum());
      Assert.assertEquals(ci1.getExecuteNum(), ci.getExecuteNum());
      Assert.assertEquals(ci1.getLineNum(), ci.getLineNum());
      Assert.assertEquals(ci1.getCoverageMethod(), ci.getCoverageMethod());
      Assert.assertEquals(ci1.getText(), ci.getText());
      Assert.assertEquals(ci1.getCoverageRationale(), ci.getCoverageRationale());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#isAssignable()}.
    */
   @Test
   public void testIsAssignable() {
      Assert.assertFalse(ci1.isAssignable());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getAssignees()}.
    */
   @Test
   public void testGetAssignees() throws OseeCoreException {
      Assert.assertEquals("", ci1.getAssignees());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getCoveragePercent()}.
    */
   @Test
   public void testGetCoveragePercent() {
      Assert.assertEquals(100, ci1.getCoveragePercent());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getChildrenItems()}.
    */
   @Test
   public void testGetChildrenItems() {
      Assert.assertEquals(10, ci1.getChildrenItems().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getCoverageEditorItems(boolean)}.
    */
   @Test
   public void testGetCoverageEditorItems() {
      Assert.assertEquals(0, ci1.getCoverageEditorItems(false).size());
      Assert.assertEquals(0, ci1.getCoverageEditorItems(true).size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getCoveragePercentStr()}.
    */
   @Test
   public void testGetCoveragePercentStr() {
      Assert.assertEquals("100", ci1.getCoveragePercentStr());
      Assert.assertEquals("0", ci4.getCoveragePercentStr());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#isFolder()}.
    */
   @Test
   public void testIsFolder() {
      Assert.assertEquals(false, ci1.isFolder());
   }

}
