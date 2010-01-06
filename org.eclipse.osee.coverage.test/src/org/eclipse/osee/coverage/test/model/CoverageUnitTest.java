/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import junit.framework.Assert;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageUnitTest {

   public static CoverageUnit cu = null, childCu = null;
   public static CoverageItem ci1 = null;
   public static SimpleCoverageUnitFileContentsProvider fileContentsProvider =
         new SimpleCoverageUnitFileContentsProvider();

   @Before
   public void testSetup() {
      cu = new CoverageUnit(null, "Top CU", "C:/UserData/", fileContentsProvider);
      cu.setOrderNumber("33");
      ci1 = new CoverageItem(cu, CoverageOptionManager.Test_Unit, "1");
      ci1.setName("this is text");
      childCu = new CoverageUnit(cu, "Child Coverage Unit", "C:\\UserData\\", fileContentsProvider);
      childCu.setOrderNumber("1");
      CoverageItem item = new CoverageItem(childCu, CoverageOptionManager.Exception_Handling, "1");
      childCu.addCoverageItem(item);
      item = new CoverageItem(childCu, CoverageOptionManager.Test_Unit, "2");
      childCu.addCoverageItem(item);
      item = new CoverageItem(childCu, CoverageOptionManager.Not_Covered, "3");
      childCu.addCoverageItem(item);
      cu.addCoverageUnit(childCu);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#CoverageUnit(org.eclipse.osee.coverage.model.ICoverage, java.lang.String, java.lang.String)}
    * .
    */
   @Test
   public void testCoverageUnitICoverageStringString() {
      Assert.assertNotNull(cu);
      Assert.assertEquals("Top CU", cu.getName());
      Assert.assertEquals("C:/UserData/", cu.getLocation());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#addCoverageItem(org.eclipse.osee.coverage.model.CoverageItem)}
    * .
    */
   @Test
   public void testAddCoverageItem() {
      Assert.assertEquals(1, cu.getCoverageItems().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getMethodNum()}.
    */
   @Test
   public void testSetGetOrderNum() {
      cu.setOrderNumber("33");
      Assert.assertEquals("33", cu.getOrderNumber());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#addCoverageUnit(org.eclipse.osee.coverage.model.CoverageUnit)}
    * .
    */
   @Test
   public void testAddCoverageUnit() {
      Assert.assertEquals(3, childCu.getChildren().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getChildren(boolean)}.
    */
   @Test
   public void testGetCoverageEditorItems() {
      Assert.assertEquals(1, cu.getCoverageItems().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoverageUnits()}.
    */
   @Test
   public void testGetCoverageUnits() {
      Assert.assertEquals(1, cu.getCoverageUnits().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoverageItems(boolean)}.
    */
   @Test
   public void testGetCoverageItemsBoolean() {
      Assert.assertEquals(1, cu.getCoverageItems(false).size());
      Assert.assertEquals(4, cu.getCoverageItems(true).size());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoverageItem(java.lang.String, java.lang.String)}.
    */
   @Test
   public void testGetCoverageItem() {
      Assert.assertNotNull(cu.getCoverageItem("1", "1"));
      Assert.assertNull(cu.getCoverageItem("1", "5"));
      Assert.assertNull(cu.getCoverageItem("2", "1"));
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getName()}.
    */
   @Test
   public void testGetName() {
      Assert.assertEquals("Child Coverage Unit", childCu.getName());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#setName(java.lang.String)}.
    */
   @Test
   public void testSetName() {
      String current = cu.getName();
      cu.setName("New Name");
      Assert.assertEquals("New Name", cu.getName());
      cu.setName(current);
      Assert.assertEquals(current, cu.getName());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getLocation()}.
    */
   @Test
   public void testSetGetLocation() {
      String current = cu.getLocation();
      cu.setLocation("New Loc");
      Assert.assertEquals("New Loc", cu.getLocation());
      cu.setLocation(current);
      Assert.assertEquals(current, cu.getLocation());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getFileContents()}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testSetGetText() throws OseeCoreException {
      String current = cu.getFileContents();
      cu.setFileContents("New Text");
      Assert.assertEquals("New Text", cu.getFileContents());
      cu.setFileContents(current);
      Assert.assertEquals(current, cu.getFileContents());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getGuid()}.
    */
   @Test
   public void testGetGuid() {
      Assert.assertTrue(GUID.isValid(cu.getGuid()));
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getParentCoverageUnit()}.
    */
   @Test
   public void testGetParentCoverageUnit() {
      Assert.assertNull(cu.getParentCoverageUnit());
      Assert.assertEquals(cu, childCu.getParentCoverageUnit());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#isEditable()}.
    */
   @Test
   public void testIsEditable() {
      Assert.assertTrue(cu.isEditable().isTrue());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoverageItemsCovered(boolean, org.eclipse.osee.coverage.model.CoverageOption[])}
    * .
    */
   @Test
   public void testGetCoverageItemsCoveredBooleanCoverageOptionArray() {
      Assert.assertEquals(1, cu.getCoverageItemsCovered(false, CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(2, cu.getCoverageItemsCovered(true, CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(1, cu.getCoverageItemsCovered(true, CoverageOptionManager.Not_Covered).size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#isCovered()}.
    */
   @Test
   public void testIsCovered() {
      Assert.assertFalse(cu.isCovered());
      CoverageTestUtil.setAllCoverageMethod(cu, CoverageOptionManager.Test_Unit, true);
      Assert.assertTrue(cu.isCovered());
      CoverageTestUtil.setAllCoverageMethod(cu, CoverageOptionManager.Not_Covered, true);
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getParent()}.
    */
   @Test
   public void testGetParent() {
      Assert.assertNull(cu.getParent());
      Assert.assertEquals(cu, childCu.getParent());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#setParentCoverageEditorItem(org.eclipse.osee.coverage.model.ICoverage)}
    * .
    */
   @Test
   public void testSetParentCoverageEditorItem() {
      Assert.assertEquals(cu, childCu.getParent());
      childCu.setParent(null);
      Assert.assertNull(childCu.getParent());
      childCu.setParent(cu);
      Assert.assertEquals(cu, childCu.getParent());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#setGuid(java.lang.String)}.
    */
   @Test
   public void testSetGuid() {
      String current = cu.getGuid();
      cu.setGuid("New GUID");
      Assert.assertEquals("New GUID", cu.getGuid());
      cu.setGuid(current);
      Assert.assertEquals(current, cu.getGuid());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getNamespace()}.
    */
   @Test
   public void testSetGetNamespace() {
      String current = cu.getNamespace();
      cu.setNamespace("org.osee.coverageUnit");
      Assert.assertEquals("org.osee.coverageUnit", cu.getNamespace());
      cu.setNamespace(current);
      Assert.assertEquals(current, cu.getNamespace());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getAssignees()}.
    */
   @Test
   public void testSetGetAssignees() {
      String current = cu.getAssignees();
      cu.setAssignees("New Assignees");
      Assert.assertEquals("New Assignees", cu.getAssignees());
      cu.setAssignees(current);
      Assert.assertEquals(current, cu.getAssignees());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#isAssignable()}.
    */
   @Test
   public void testIsAssignable() {
      Assert.assertTrue(cu.isEditable().isTrue());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getNotes()}.
    */
   @Test
   public void testSetGetNotes() {
      String current = cu.getNotes();
      cu.setNotes("New Notes");
      Assert.assertEquals("New Notes", cu.getNotes());
      cu.setNotes(current);
      Assert.assertEquals(current, cu.getNotes());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoverageItemsCovered(boolean)}.
    */
   @Test
   public void testGetCoverageItemsCoveredBoolean() {
      Assert.assertFalse(cu.isCovered());
      CoverageTestUtil.setAllCoverageMethod(cu, CoverageOptionManager.Test_Unit, true);
      Assert.assertTrue(cu.isCovered());
      CoverageTestUtil.setAllCoverageMethod(cu, CoverageOptionManager.Not_Covered, true);
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getChildren()}.
    */
   @Test
   public void testGetChildrenItems() {
      Assert.assertEquals(2, cu.getChildren().size());
      Assert.assertEquals(3, childCu.getChildren().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoverageItems()}.
    */
   @Test
   public void testGetCoverageItems() {
      Assert.assertEquals(1, cu.getCoverageItems().size());
      Assert.assertEquals(3, childCu.getCoverageItems().size());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#updateAssigneesAndNotes(org.eclipse.osee.coverage.model.CoverageUnit)}
    * .
    */
   @Test
   public void testUpdateAssigneesAndNotes() {
      CoverageUnit cu2 = new CoverageUnit(null, "New Coverage Unit", "location", null);
      cu2.setAssignees("assignees");
      cu2.setNotes("notes");
      Assert.assertNull(cu.getAssignees());
      Assert.assertNull(cu.getNotes());
      cu.updateAssigneesAndNotes(cu2);
      Assert.assertEquals("assignees", cu.getAssignees());
      Assert.assertEquals("notes", cu.getNotes());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#copy(boolean)}.
    */
   @Test
   public void testCopy() throws OseeCoreException {
      CoverageUnit oldCu = new CoverageUnit(null, "This CU", "location", fileContentsProvider);
      oldCu.setAssignees("assignees");
      oldCu.setNotes("notes");
      oldCu.setNamespace("namespace");
      oldCu.setLocation("location");
      CoverageUnit newCu = oldCu.copy(false);
      Assert.assertEquals(oldCu.getGuid(), newCu.getGuid());
      Assert.assertEquals(oldCu.getLocation(), newCu.getLocation());
      Assert.assertEquals(oldCu.getName(), newCu.getName());
      Assert.assertEquals(oldCu.getAssignees(), newCu.getAssignees());
      Assert.assertEquals(oldCu.getNotes(), newCu.getNotes());
      Assert.assertEquals(oldCu.getNamespace(), newCu.getNamespace());
      Assert.assertEquals(oldCu.getFileContents(), newCu.getFileContents());
      Assert.assertEquals(0, newCu.getCoverageItems().size());

      newCu = childCu.copy(false);
      Assert.assertEquals(0, newCu.getCoverageItems().size());

      newCu = childCu.copy(true);
      Assert.assertEquals(3, newCu.getCoverageItems().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#getCoveragePercent()}.
    */
   @Test
   public void testGetCoveragePercentAndStr() {
      CoverageTestUtil.setAllCoverageMethod(cu, CoverageOptionManager.Not_Covered, true);
      Assert.assertFalse(cu.isCovered());
      Assert.assertEquals(0, cu.getCoveragePercent());
      Assert.assertEquals("0% 0/4", cu.getCoveragePercentStr());
      CoverageTestUtil.setAllCoverageMethod(cu, CoverageOptionManager.Test_Unit, true);
      Assert.assertEquals(100, cu.getCoveragePercent());
      Assert.assertEquals("100% 4/4", cu.getCoveragePercentStr());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageUnit#isFolder()}.
    */
   @Test
   public void testSetIsFolder() {
      boolean current = cu.isFolder();
      Assert.assertFalse(cu.isFolder());
      cu.setFolder(true);
      Assert.assertTrue(cu.isFolder());
      cu.setFolder(current);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#removeCoverageItem(org.eclipse.osee.coverage.model.CoverageItem)}
    * .
    */
   @Test
   public void testRemoveCoverageItem() {
      Assert.assertEquals(1, cu.getCoverageItems().size());
      cu.removeCoverageItem(ci1);
      Assert.assertEquals(0, cu.getCoverageItems().size());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageUnit#removeCoverageUnit(org.eclipse.osee.coverage.model.CoverageUnit)}
    * .
    */
   @Test
   public void testRemoveCoverageUnit() {
      Assert.assertEquals(1, cu.getCoverageUnits().size());
      cu.removeCoverageUnit(childCu);
      Assert.assertEquals(0, cu.getCoverageUnits().size());
   }

}
