/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.swt.graphics.Image;

/**
 * Single test that can cover multiple Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class TestUnit implements ICoverageEditorItem {

   private String name;
   private String namespace;
   private String guid = GUID.create();
   private String text;
   private final List<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
   private String location;
   private Artifact artifact;

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

   public String getGuid() {
      return guid;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public Result isEditable() {
      return Result.FalseResult;
   }

   @Override
   public boolean isCompleted() {
      return true;
   }

   @Override
   public String getCoverageEditorValue(XViewerColumn xCol) {
      return "";
   }

   @Override
   public Object[] getChildren() {
      return coverageItems.toArray(new Object[coverageItems.size()]);
   }

   @Override
   public OseeImage getOseeImage() {
      if (isCovered()) {
         return CoverageImage.TEST_UNIT_GREEN;
      }
      return CoverageImage.TEST_UNIT_RED;
   }

   @Override
   public boolean isCovered() {
      if (getCoverageItems().size() > 0) return true;
      return false;
   }

   @Override
   public Image getCoverageEditorImage(XViewerColumn xCol) {
      return null;
   }

   @Override
   public ICoverageEditorItem getParent() {
      return null;
   }

   public Artifact getArtifact(boolean create) throws OseeCoreException {
      if (artifact == null && create) {
         artifact =
               ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch(), guid, null);
      }
      return artifact;
   }

   public void load() throws OseeCoreException {
      coverageItems.clear();
      getArtifact(false);
      if (artifact != null) {
         setName(artifact.getName());
         setGuid(artifact.getGuid());
         KeyValueArtifact keyValueArtifact =
               new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
         if (Strings.isValid(keyValueArtifact.getValue("location"))) {
            setLocation(keyValueArtifact.getValue("location"));
         }
         if (Strings.isValid(keyValueArtifact.getValue("previewHtml"))) {
            setText(keyValueArtifact.getValue("previewHtml"));
         }
         if (Strings.isValid(keyValueArtifact.getValue("text"))) {
            setText(keyValueArtifact.getValue("text"));
         }
      }
   }

   public void save(SkynetTransaction transaction) throws OseeCoreException {
      getArtifact(true);
      artifact.setName(getName());
      KeyValueArtifact keyValueArtifact =
            new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
      if (Strings.isValid(getText())) {
         keyValueArtifact.setValue("text", getText());
      }
      if (Strings.isValid(getLocation())) {
         keyValueArtifact.setValue("location", getLocation());
      }
      if (Strings.isValid(getNamespace())) {
         keyValueArtifact.setValue("namespace", getNamespace());
      }
      keyValueArtifact.save();
      // TODO Need to relate TestUnit to CoverageItem
      System.err.println("Need to relate TestUnit to CoverageItem");
      artifact.persist(transaction);
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
   }

   @Override
   public String getText() {
      return text;
   }

   @Override
   public String getNamespace() {
      return namespace;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public void setText(String text) {
      this.text = text;
   }

   @Override
   public boolean isAssignable() {
      return false;
   }

   @Override
   public String getNotes() {
      return null;
   }
}
