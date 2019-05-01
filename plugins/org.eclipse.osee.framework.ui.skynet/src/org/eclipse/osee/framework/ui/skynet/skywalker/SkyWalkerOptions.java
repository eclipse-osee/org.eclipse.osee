/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.skywalker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.skywalker.ISkyWalkerOptionsChangeListener.ModType;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;

/**
 * @author Donald G. Dunne
 */
public final class SkyWalkerOptions {

   private Artifact artifact;
   private int levels = 1;
   private static Map<AbstractLayoutAlgorithm, String> layouts;
   private AbstractLayoutAlgorithm layout;
   protected static AbstractLayoutAlgorithm defaultLayout;
   private Map<ArtifactTypeToken, Boolean> artTypes;
   private Map<AttributeTypeToken, Boolean> showAttributes;
   // RelationLinkDescriptor and RelationLinkDescriptorSide
   private Map<Object, Boolean> relTypes;
   private boolean filterEnabled = true;
   private final Set<ISkyWalkerOptionsChangeListener> listeners = new HashSet<>();
   public static final String RADIAL_DOWN_LAYOUT = "Radial - Down";
   public static final String RADIAL_RIGHT_LAYOUT = "Radial - Right";
   public static final String SPRING_LAYOUT = "Spring";
   public static enum LinkName {
      None,
      Link_Name,
      Full_Link_Name,
      Phrasing_A_to_B,
      Phrasing_B_to_A,
      Other_Side_Name
   };
   private LinkName linkName = LinkName.Link_Name;

   public SkyWalkerOptions() {
      loadLayouts();
      layout = defaultLayout;
   }

   static {
      layouts = new HashMap<>();

      RadialLayoutAlgorithm radLayout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
      radLayout.setRangeToLayout(-90 * Math.PI / 360, 90 * Math.PI / 360);
      defaultLayout = radLayout;
      layouts.put(radLayout, "Radial - Right (default)");

      radLayout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
      radLayout.setRangeToLayout(0, 180 * Math.PI / 360);
      layouts.put(radLayout, RADIAL_DOWN_LAYOUT);

      layouts.put(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), "Radial - Full");
      layouts.put(new SpringLayoutAlgorithm(), SPRING_LAYOUT);
      layouts.put(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), "Tree");
      layouts.put(new VerticalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), "Vertical");
      layouts.put(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), "Grid");
   }

   public void addSkyWalkerOptionsChangeListener(ISkyWalkerOptionsChangeListener skyWalkerOptionsChangeListener) {
      listeners.add(skyWalkerOptionsChangeListener);
   }

   public String getExtendedName(Artifact artifact) {
      if (getSelectedShowAttributeTypes().isEmpty()) {
         return "";
      } else {
         StringBuffer sb = new StringBuffer();
         for (AttributeTypeId attributeType : getSelectedShowAttributeTypes()) {
            if (artifact.getAttributeCount(attributeType) > 0) {
               sb.append("\n");
               sb.append(artifact.getAttributesToString(attributeType));
            }
         }
         return sb.toString();
      }
   }

   private void loadArtTypes() {
      if (artTypes == null) {
         artTypes = new HashMap<>();
         try {
            for (ArtifactTypeToken descriptor : ArtifactTypeManager.getValidArtifactTypes(artifact.getBranch())) {
               artTypes.put(descriptor, true);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void loadAttributeTypes() {
      if (showAttributes == null) {
         showAttributes = new HashMap<>();
         try {
            for (AttributeTypeToken descriptor : AttributeTypeManager.getValidAttributeTypes(artifact.getBranch())) {
               showAttributes.put(descriptor, false);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void loadRelTypes() {
      if (relTypes == null) {
         relTypes = new HashMap<>();
         try {
            for (RelationType relationType : RelationTypeManager.getValidTypes(artifact.getBranch())) {
               relTypes.put(relationType, true);
               relTypes.put(new RelationTypeSide(relationType, RelationSide.SIDE_A), true);
               relTypes.put(new RelationTypeSide(relationType, RelationSide.SIDE_B), true);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("guid", artifact.getGuid()));
      sb.append(AXml.addTagData("branchUuid", artifact.getBranch().getIdString() + ""));
      sb.append(AXml.addTagData("artTypes",
         org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", getSelectedArtTypes())));
      sb.append(AXml.addTagData("relTypes",
         org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", getSelectedRelTypes())));
      sb.append(AXml.addTagData("showAttributes",
         org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", getSelectedShowAttributeTypes())));
      sb.append(AXml.addTagData("layout", getLayoutName(getLayout())));
      sb.append(AXml.addTagData("levels", getLevels() + ""));
      sb.append(AXml.addTagData("linkName", getLinkName() + ""));
      return sb.toString();
   }

   public void fromXml(String xml) {
      try {
         String guid = AXml.getTagData(xml, "guid");
         if (Strings.isValid(guid)) {
            String branchUuid = AXml.getTagData(xml, "branchUuid");
            BranchId branch = BranchId.valueOf(branchUuid);
            Artifact art = ArtifactQuery.getArtifactFromId(guid, branch);
            if (art != null) {
               setArtifact(art);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.WARNING, "SkyWalker couldn't find stored artifact via guid", ex);
      }
      String artTypeStr = AXml.getTagData(xml, "artTypes");
      if (Strings.isValid(artTypeStr)) {
         for (Entry<ArtifactTypeToken, Boolean> desc : artTypes.entrySet()) {
            desc.setValue(false);
         }
         for (String name : artTypeStr.split(",")) {
            for (Entry<ArtifactTypeToken, Boolean> desc : artTypes.entrySet()) {
               if (desc.getKey().getName().equals(name)) {
                  desc.setValue(true);
                  break;
               }
            }
         }
      }
      String relTypeStr = AXml.getTagData(xml, "relTypes");
      if (Strings.isValid(relTypeStr)) {
         for (Entry<Object, Boolean> desc : relTypes.entrySet()) {
            desc.setValue(false);
         }
         for (String name : relTypeStr.split(",")) {
            for (Entry<Object, Boolean> desc : relTypes.entrySet()) {
               if (desc.getKey().toString().equals(name)) {
                  desc.setValue(true);
                  break;
               }
            }
         }
      }
      String showAttrString = AXml.getTagData(xml, "showAttributes");
      if (Strings.isValid(showAttrString)) {
         for (Entry<AttributeTypeToken, Boolean> desc : showAttributes.entrySet()) {
            desc.setValue(false);
         }
         for (String name : showAttrString.split(",")) {
            for (Entry<AttributeTypeToken, Boolean> desc : showAttributes.entrySet()) {
               if (desc.getKey().getName().equals(name)) {
                  desc.setValue(true);
                  break;
               }
            }
         }
      }
      String layoutStr = AXml.getTagData(xml, "layout");
      if (Strings.isValid(layoutStr)) {
         for (AbstractLayoutAlgorithm layout : getLayouts()) {
            if (getLayoutName(layout).equals(layoutStr)) {
               setLayout(layout);
               break;
            }
         }
      }
      String levelStr = AXml.getTagData(xml, "levels");
      if (Strings.isValid(levelStr)) {
         setLevels(Integer.parseInt(levelStr));
      }

      String linkNameStr = AXml.getTagData(xml, "linkName");
      if (Strings.isValid(linkNameStr)) {
         setLinkName(LinkName.valueOf(linkNameStr));
      }

      notifyListeners(ModType.ArtType, ModType.RelType, ModType.Level, ModType.Layout, ModType.Link_Name,
         ModType.Show_Attribute);
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      return artifact;
   }

   private synchronized Map<AbstractLayoutAlgorithm, String> loadLayouts() {
      return layouts;
   }

   /**
    * @return the defaultLayout
    */
   public AbstractLayoutAlgorithm getLayout() {
      return layout;
   }

   public AbstractLayoutAlgorithm getLayout(String layoutName) {
      for (Entry<AbstractLayoutAlgorithm, String> entry : layouts.entrySet()) {
         if (entry.getValue().equals(layoutName)) {
            return entry.getKey();
         }
      }
      return defaultLayout;
   }

   public Set<AbstractLayoutAlgorithm> getLayouts() {
      return layouts.keySet();
   }

   public String getLayoutName(AbstractLayoutAlgorithm layout) {
      return layouts.get(layout);
   }

   /**
    * @param artifact the artifact to set
    */
   public void setArtifact(Artifact artifact) {
      if (artifact.equals(this.artifact)) {
         return;
      }
      this.artifact = artifact;
      loadArtTypes();
      loadRelTypes();
      loadAttributeTypes();
      notifyListeners(ModType.Artifact);
   }

   public boolean isValidArtifactType(ArtifactTypeToken type) {
      if (!isFilterEnabled()) {
         return true;
      }
      return getSelectedArtTypes().contains(type);
   }

   public boolean isValidRelationType(RelationType type) {
      if (!isFilterEnabled()) {
         return true;
      }
      return getSelectedRelTypes().contains(type);
   }

   public boolean isValidRelationLinkDescriptorSide(RelationTypeSide side) {
      if (!isFilterEnabled()) {
         return true;
      }
      return getSelectedRelTypes().contains(side);
   }

   /**
    * @return the levels
    */
   public int getLevels() {
      return levels;
   }

   /**
    * @param levels the levels to set
    */
   public void setLevels(int levels) {
      if (this.levels == levels) {
         return;
      }
      this.levels = levels;
      notifyListeners(ModType.Level);
   }

   /**
    * @return the filterByArtType
    */
   public boolean isFilterEnabled() {
      return filterEnabled;
   }

   /**
    * @param filterByArtType the filterByArtType to set
    */
   public void setFilterEnabled(boolean enable) {
      if (this.filterEnabled == enable) {
         return;
      }
      this.filterEnabled = enable;
      notifyListeners(ModType.FilterEnabled);
   }

   private void notifyListeners(ModType... modType) {
      for (ISkyWalkerOptionsChangeListener listener : listeners) {
         listener.modified(modType);
      }
   }

   public void setSelectedRelTypes(Object[] selected) {
      if (relTypes == null) {
         loadRelTypes();
      }
      List<Object> selList = new ArrayList<>();
      for (Object obj : selected) {
         selList.add(obj);
      }
      for (Entry<Object, Boolean> entry : relTypes.entrySet()) {
         entry.setValue(selList.contains(entry.getKey()));
      }
      notifyListeners(ModType.RelType);
   }

   public void setSelectedShowAttributes(Object[] selected) {
      List<Object> selList = new ArrayList<>();
      for (Object obj : selected) {
         selList.add(obj);
      }
      for (Entry<AttributeTypeToken, Boolean> entry : showAttributes.entrySet()) {
         entry.setValue(selList.contains(entry.getKey()));
      }
      notifyListeners(ModType.Show_Attribute);
   }

   public void setSelectedArtTypes(Collection<ArtifactTypeToken> selected) {
      if (artTypes == null) {
         loadArtTypes();
      }
      for (Entry<ArtifactTypeToken, Boolean> entry : artTypes.entrySet()) {
         entry.setValue(selected.contains(entry.getKey()));
      }
      notifyListeners(ModType.ArtType);
   }

   public Set<ArtifactTypeToken> getSelectedArtTypes() {
      Set<ArtifactTypeToken> selected = new HashSet<>();
      if (artTypes == null) {
         return selected;
      }
      for (ArtifactTypeToken desc : artTypes.keySet()) {
         if (artTypes.get(desc)) {
            selected.add(desc);
         }
      }
      return selected;
   }

   public Set<Object> getSelectedRelTypes() {
      Set<Object> selected = new HashSet<>();
      if (relTypes == null) {
         return selected;
      }
      for (Object desc : relTypes.keySet()) {
         if (relTypes.get(desc)) {
            selected.add(desc);
         }
      }
      return selected;
   }

   public Set<AttributeTypeToken> getSelectedShowAttributeTypes() {
      Set<AttributeTypeToken> selected = new HashSet<>();
      if (showAttributes == null) {
         return selected;
      }
      for (AttributeTypeToken desc : showAttributes.keySet()) {
         if (showAttributes.get(desc)) {
            selected.add(desc);
         }
      }
      return selected;
   }

   public Set<ArtifactTypeToken> getAllArtTypes() {
      if (artTypes == null) {
         return new HashSet<>();
      }
      return artTypes.keySet();
   }

   public Set<Object> getAllRelTypes() {
      if (relTypes == null) {
         return new HashSet<>();
      }
      return relTypes.keySet();
   }

   public Set<AttributeTypeToken> getAllShowAttributes() {
      if (showAttributes == null) {
         return new HashSet<>();
      }
      return showAttributes.keySet();
   }

   public Set<RelationType> getAllRelationLinkDescriptorTypes() {
      if (relTypes == null) {
         return new HashSet<>();
      }
      Set<RelationType> descs = new HashSet<>();
      for (Object obj : relTypes.keySet()) {
         if (obj instanceof RelationType) {
            descs.add((RelationType) obj);
         }
      }
      return descs;
   }

   /**
    * @param layout the layout to set
    */
   public void setLayout(AbstractLayoutAlgorithm layout) {
      if (this.layout == layout) {
         return;
      }
      this.layout = layout;
      notifyListeners(ModType.Layout);
   }

   /**
    * @return the linkName
    */
   public LinkName getLinkName() {
      return linkName;
   }

   /**
    * @param linkName the linkName to set
    */
   public void setLinkName(LinkName linkName) {
      if (this.linkName == linkName) {
         return;
      }
      this.linkName = linkName;
      notifyListeners(ModType.Link_Name);
   }

   /**
    * @return the defaultLayout
    */
   public AbstractLayoutAlgorithm getDefaultLayout() {
      return defaultLayout;
   }

   /**
    * @param defaultLayout the defaultLayout to set
    */
   public void setDefaultLayout(AbstractLayoutAlgorithm defaultLayout) {
      SkyWalkerOptions.defaultLayout = defaultLayout;
   }

}
