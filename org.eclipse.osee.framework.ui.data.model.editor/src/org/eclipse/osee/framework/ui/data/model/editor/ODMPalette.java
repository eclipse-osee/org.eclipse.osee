/*
 * Created on Jan 26, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.data.model.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;

/**
 * @author Roberto E. Escobar
 */
public class ODMPalette {

   private ODMEditor editor;
   private PaletteRoot paletteRoot;

   private enum DrawerEnum {
      Artifact_Types, Attribute_Types, Relation_Types;
      //      , Attribute_Base_Types, Attribute_Data_Providers, Artifact_Factory;

      public String asLabel() {
         return this.name().replaceAll("_", " ");
      }
   }

   private Map<DrawerEnum, PaletteContainer> containers;

   public ODMPalette(ODMEditor editor) {
      this.containers = new HashMap<DrawerEnum, PaletteContainer>();
      this.editor = editor;
   }

   private void updateDrawers() {
      for (DrawerEnum drawerType : DrawerEnum.values()) {
         PaletteContainer container = containers.get(drawerType);
         if (container == null) {
            container = new PaletteDrawer(drawerType.asLabel());
            containers.put(drawerType, container);
         } else {
            for (Object child : container.getChildren()) {
               container.remove((PaletteEntry) child);
            }
         }
         fillContainer(drawerType, container);
      }
   }

   private void fillContainer(DrawerEnum drawerType, PaletteContainer container) {
      if (editor.getEditorInput() == null) return;

      ImageDescriptor defaultSmallImage = ODMConstants.getImageDescriptor("rectangle16.gif");
      ImageDescriptor defaultLargeImage = ODMConstants.getImageDescriptor("rectangle24.gif");

      DataTypeCache dataTypeCache = editor.getEditorInput().getDataTypeCache();
      for (String sourceId : dataTypeCache.getDataTypeSourceIds()) {
         DataTypeSource dataTypeSource = dataTypeCache.getDataTypeSourceById(sourceId);
         List<? extends DataType> typeData = null;

         Class<?> clazz = null;
         switch (drawerType) {
            case Artifact_Types:
               typeData = dataTypeSource.getArtifactTypeManager().getAll();
               clazz = ArtifactDataType.class;
               break;
            case Attribute_Types:
               typeData = dataTypeSource.getAttributeTypeManager().getAll();
               clazz = AttributeDataType.class;
               break;
            case Relation_Types:
               typeData = dataTypeSource.getRelationTypeManager().getAll();
               clazz = RelationDataType.class;
               break;
            default:
               break;
         }

         if (typeData != null) {
            for (DataType dataType : typeData) {
               String id = dataType.getName();
               ImageDescriptor smallImage = defaultSmallImage;
               ImageDescriptor largeImage = defaultLargeImage;
               if (drawerType == DrawerEnum.Artifact_Types) {
                  smallImage = ImageDescriptor.createFromImage(((ArtifactDataType) dataType).getImage());
               }
               container.add(createComponent(id, id, clazz, smallImage, largeImage));
            }
         }
      }
   }

   private PaletteContainer createToolsGroup(PaletteRoot palette) {
      PaletteToolbar toolbar = new PaletteToolbar("Tools");

      ToolEntry tool = new PanningSelectionToolEntry();
      toolbar.add(tool);
      palette.setDefaultEntry(tool);

      toolbar.add(new MarqueeToolEntry());

      return toolbar;
   }

   private CombinedTemplateCreationEntry createComponent(String label, String description, Class<?> clazz, ImageDescriptor smallImage, ImageDescriptor largeImage) {
      CombinedTemplateCreationEntry component =
            new CombinedTemplateCreationEntry(label, description, clazz, new SimpleFactory(clazz), smallImage,
                  largeImage);
      return component;
   }

   public void updatePaletteRoot() {
      updateDrawers();
   }

   public PaletteRoot getPaletteRoot() {
      if (paletteRoot == null) {
         paletteRoot = new PaletteRoot();
         paletteRoot.add(createToolsGroup(paletteRoot));
         updateDrawers();
         for (PaletteContainer container : containers.values()) {
            paletteRoot.add(container);
         }
      }
      return paletteRoot;
   }
}
