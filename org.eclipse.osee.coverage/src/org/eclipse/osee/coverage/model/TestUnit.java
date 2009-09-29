/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * Single test that can cover multiple Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class TestUnit implements ICoverageEditorItem {

   private String name;
   private final String guid = GUID.create();
   private String previewHtml;
   private final List<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
   private String location;

   public TestUnit(String name, String location) {
      super();
      this.name = name;
      this.location = location;
   }

   public void addCoverageItem(CoverageItem coverageItem) {
      coverageItems.add(coverageItem);
   }

   public List<CoverageItem> getCoverageItems() {
      return coverageItems;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getPreviewHtml() {
      return previewHtml;
   }

   public void setPreviewHtml(String previewHtml) {
      this.previewHtml = previewHtml;
   }

   public String getGuid() {
      return guid;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public User getUser() {
      return null;
   }

   @Override
   public Result isEditable() {
      return null;
   }

   @Override
   public void setUser(User user) {
   }
}
