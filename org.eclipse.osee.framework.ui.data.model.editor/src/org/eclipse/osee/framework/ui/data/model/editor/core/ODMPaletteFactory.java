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
package org.eclipse.osee.framework.ui.data.model.editor.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;

/**
 * @author Roberto E. Escobar
 */
public class ODMPaletteFactory {

   private ODMEditor editor;
   private PaletteRoot paletteRoot;

   private enum DrawerEnum {
      Artifact_Types, Attribute_Types, Relation_Types;

      public String asLabel() {
         return this.name().replaceAll("_", " ");
      }
   }

   private Map<DrawerEnum, PaletteContainer> containers;

   public ODMPaletteFactory(ODMEditor editor) {
      this.containers = new LinkedHashMap<DrawerEnum, PaletteContainer>();
      this.editor = editor;
   }

   private void updateDrawers() {
      if (editor.getEditorInput() == null) {
         return;
      }
      DataTypeCache dataTypeCache = editor.getEditorInput().getDataTypeCache();

      for (DrawerEnum drawerType : DrawerEnum.values()) {
         PaletteContainer container = containers.get(drawerType);
         if (container == null) {
            container = new PaletteDrawer(drawerType.asLabel());
            containers.put(drawerType, container);
            getPaletteRoot().add(container);
         } else {
            for (Object child : container.getChildren()) {
               container.remove((PaletteEntry) child);
            }
         }
         container.addAll(getToolEntries(drawerType, dataTypeCache));
      }
   }

   private List<CombinedTemplateCreationEntry> getToolEntries(DrawerEnum drawerType, DataTypeCache dataTypeCache) {
      List<CombinedTemplateCreationEntry> toReturn = new ArrayList<CombinedTemplateCreationEntry>();
      for (String sourceId : dataTypeCache.getDataTypeSourceIds()) {
         DataTypeSource dataTypeSource = dataTypeCache.getDataTypeSourceById(sourceId);
         switch (drawerType) {
            case Artifact_Types:
               for (ArtifactDataType dataType : dataTypeSource.getArtifactTypeManager().getAllSorted()) {
                  toReturn.add(createArtifactTypeToolEntry(dataType));
               }
               break;
            case Attribute_Types:
               for (AttributeDataType dataType : dataTypeSource.getAttributeTypeManager().getAllSorted()) {
                  toReturn.add(createAttributeDataTypeToolEntry(dataType));
               }
               break;
            case Relation_Types:
               for (RelationDataType dataType : dataTypeSource.getRelationTypeManager().getAllSorted()) {
                  toReturn.add(createRelationDataTypeToolEntry(dataType));
               }
               break;
            default:
               break;
         }
      }
      return toReturn;
   }

   private CombinedTemplateCreationEntry createArtifactTypeToolEntry(final ArtifactDataType dataType) {
      ImageDescriptor imageDescriptor = ImageDescriptor.createFromImage(dataType.getImage());
      CreationFactory factory = new CreationFactory() {

         @Override
         public Object getNewObject() {
            EditPart editPart = editor.getViewer().getContents();
            if (editPart != null) {
               Object object = editPart.getModel();
               if (object instanceof ODMDiagram) {
                  ((ODMDiagram) object).add(dataType);
               }
            }
            return dataType;
         }

         @Override
         public Object getObjectType() {
            return null;
         }

      };
      return new CombinedTemplateCreationEntry(dataType.getName(), String.format(
            "Create a new artifact data type [%s]", dataType.getName()), factory, factory, imageDescriptor,
            imageDescriptor);
   }

   private CombinedTemplateCreationEntry createAttributeDataTypeToolEntry(AttributeDataType dataType) {
      ImageDescriptor image = ODMImages.getImageDescriptor(ODMImages.ATTRIBUTE_ENTRY);
      CreationFactory factory = new SimpleFactory(AttributeDataType.class);
      return new CombinedTemplateCreationEntry(dataType.getName(),
            "Add attribute data type to an existing artifact type", factory, factory, image, image);
   }

   private CombinedTemplateCreationEntry createRelationDataTypeToolEntry(RelationDataType dataType) {
      ImageDescriptor image = ODMImages.getImageDescriptor(ODMImages.RELATION_ENTRY);
      CreationFactory factory = new SimpleFactory(RelationDataType.class);
      return new CombinedTemplateCreationEntry(dataType.getName(),
            "Add a new relation type to an existing artifact type", factory, factory, image, image);
   }

   private PaletteContainer createToolsGroup(PaletteRoot palette) {
      PaletteToolbar toolbar = new PaletteToolbar("Tools");

      ToolEntry tool = new PanningSelectionToolEntry();
      toolbar.add(tool);
      palette.setDefaultEntry(tool);

      toolbar.add(new MarqueeToolEntry());
      toolbar.add(new PaletteSeparator());

      ImageDescriptor img = ODMImages.getImageDescriptor(ODMImages.INHERITANCE);
      toolbar.add(new ConnectionCreationToolEntry("Inheritance", "Create an artifact hierarchy", new CreationFactory() {
         public Object getNewObject() {
            //            return ModelFactory.eINSTANCE.createInheritanceView();
            return null;
         }

         public Object getObjectType() {
            return null;
         }
      }, img, img));
      return toolbar;
   }

   public void updatePaletteRoot() {
      updateDrawers();
   }

   public PaletteRoot getPaletteRoot() {
      if (paletteRoot == null) {
         paletteRoot = new PaletteRoot();
         paletteRoot.add(createToolsGroup(paletteRoot));
         updateDrawers();
      }
      return paletteRoot;
   }
}
