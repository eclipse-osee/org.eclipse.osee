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
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.InheritanceLinkModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;

/**
 * @author Roberto E. Escobar
 */
public class ODMPaletteFactory {

   private final static String DATA_TYPE_TIP_FORMAT = "Add [%s] %s type to %s";
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
      ImageDescriptor image = null;
      String message = null;
      for (String sourceId : dataTypeCache.getDataTypeSourceIds()) {
         DataTypeSource dataTypeSource = dataTypeCache.getDataTypeSourceById(sourceId);
         switch (drawerType) {
            case Artifact_Types:
               for (ArtifactDataType dataType : dataTypeSource.getArtifactTypeManager().getAllSorted()) {
                  image = ImageDescriptor.createFromImage(dataType.getImage());
                  message = String.format(DATA_TYPE_TIP_FORMAT, dataType.getName(), "artifact", "the diagram");
                  toReturn.add(createDataTypeToolEntry(dataType, image, message));
               }
               break;
            case Attribute_Types:
               image = ODMImages.getImageDescriptor(ODMImages.LOCAL_ATTRIBUTE);
               for (AttributeDataType dataType : dataTypeSource.getAttributeTypeManager().getAllSorted()) {
                  message = String.format(DATA_TYPE_TIP_FORMAT, dataType.getName(), "attribute", "an artifact type");
                  toReturn.add(createDataTypeToolEntry(dataType, image, message));
               }
               break;
            case Relation_Types:
               image = ODMImages.getImageDescriptor(ODMImages.LOCAL_RELATION);
               for (RelationDataType dataType : dataTypeSource.getRelationTypeManager().getAllSorted()) {
                  message = String.format(DATA_TYPE_TIP_FORMAT, dataType.getName(), "relation", "an artifact type");
                  toReturn.add(createDataTypeToolEntry(dataType, image, message));
               }
               break;
            default:
               break;
         }
      }
      return toReturn;
   }

   private CombinedTemplateCreationEntry createDataTypeToolEntry(final DataType dataType, ImageDescriptor imageDescriptor, String message) {
      CreationFactory factory = new CreationFactory() {

         @Override
         public Object getNewObject() {
            return dataType;
         }

         @Override
         public Object getObjectType() {
            return null;
         }

      };
      return new CombinedTemplateCreationEntry(dataType.getName(), message, factory, factory, imageDescriptor,
            imageDescriptor);
   }

   private PaletteContainer createToolsGroup(PaletteRoot palette) {
      PaletteToolbar toolbar = new PaletteToolbar("Tools");

      ToolEntry tool = new PanningSelectionToolEntry();
      toolbar.add(tool);
      palette.setDefaultEntry(tool);

      toolbar.add(new MarqueeToolEntry());
      toolbar.add(new PaletteSeparator());

      ImageDescriptor img = ODMImages.getImageDescriptor(ODMImages.INHERITANCE);
      toolbar.add(new ConnectionCreationToolEntry("Inheritance", "Inherit from an artifact", new SimpleFactory(
            InheritanceLinkModel.class), img, img));

      //      final Action action =
      //            OseeAts.createBugAction(ODMEditorActivator.getInstance(), editor, "OSEE Data Model Editor",
      //                  ODMEditor.EDITOR_ID);
      //      img = action.getImageDescriptor();
      //
      //      toolbar.add(new ToolEntry("", action.getText(), img, img, null) {
      //
      //         /* (non-Javadoc)
      //          * @see org.eclipse.gef.palette.ToolEntry#createTool()
      //          */
      //         @Override
      //         public Tool createTool() {
      //            return new AbstractTool() {
      //
      //               @Override
      //               protected String getCommandName() {
      //                  return action.getText();
      //               }
      //
      //               /* (non-Javadoc)
      //                * @see org.eclipse.gef.tools.AbstractTool#activate()
      //                */
      //               @Override
      //               public void activate() {
      //                  super.activate();
      //                  Display.getDefault().asyncExec(new Runnable() {
      //                     public void run() {
      //                        deactivate();
      //                        action.run();
      //                     }
      //                  });
      //
      //               }
      //            };
      //         }
      //
      //      });
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
