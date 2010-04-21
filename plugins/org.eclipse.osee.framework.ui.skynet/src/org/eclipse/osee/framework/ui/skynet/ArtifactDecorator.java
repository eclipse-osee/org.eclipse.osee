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
package org.eclipse.osee.framework.ui.skynet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.AttributeTypeCheckTreeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDecorator implements IArtifactDecoratorPreferences {

   private static final Collection<WeakReference<ArtifactDecorator>> DECORATOR_INSTANCES =
         new CopyOnWriteArrayList<WeakReference<ArtifactDecorator>>();

   private DecoratorAction showArtIds;
   private DecoratorAction showArtType;
   private DecoratorAction showArtVersion;
   private DecoratorAction showArtBranch;
   private ShowAttributeAction attributesAction;
   private SetSettingsAsDefault saveSettingsAction;
   private StructuredViewer viewer;

   private final String storageKey;

   public ArtifactDecorator(String storageKey) {
      this.viewer = null;
      this.storageKey = storageKey;
      addDecoratorInstance(this);
   }

   public void setViewer(StructuredViewer viewer) {
      this.viewer = viewer;
   }

   private String asKey(String prefix, String name) {
      return String.format("%s.%s", prefix, name);
   }

   private void storeState() {
      try {
         User store = UserManager.getUser();
         if (store != null) {
            saveAction(store, showArtIds, "artifact.decorator.show.artId");
            saveAction(store, showArtType, "artifact.decorator.show.artType");
            saveAction(store, showArtBranch, "artifact.decorator.show.artBranch");
            saveAction(store, showArtVersion, "artifact.decorator.show.artVersion");
            if (attributesAction != null) {
               Collection<String> items = attributesAction.getSelected();
               store.setSetting(asKey(storageKey, "artifact.decorator.attrTypes"), Collections.toString(items, ","));
               saveAction(store, attributesAction, "artifact.decorator.show.attrTypes");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private void loadState() {
      try {
         User store = UserManager.getUser();
         if (store != null) {
            loadAction(store, showArtIds, "artifact.decorator.show.artId");
            loadAction(store, showArtType, "artifact.decorator.show.artType");
            loadAction(store, showArtBranch, "artifact.decorator.show.artBranch");
            loadAction(store, showArtVersion, "artifact.decorator.show.artVersion");
            if (attributesAction != null) {
               String value = store.getSetting("artifact.decorator.attrTypes");
               if (Strings.isValid(value)) {
                  String[] entries = value.split(",");
                  attributesAction.setSelected(Arrays.asList(entries));
               }
               loadAction(store, attributesAction, "artifact.decorator.show.attrTypes");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private void loadAction(User store, Action action, String key) throws OseeCoreException {
      if (action != null) {
         boolean isChecked = store.getBooleanSetting(asKey(storageKey, key));
         action.setChecked(isChecked);
      }
   }

   private void saveAction(User store, Action action, String key) throws OseeCoreException {
      boolean isChecked = action != null && action.isChecked();
      store.setSetting(asKey(storageKey, key), String.valueOf(isChecked));
   }

   private void checkActionsCreated(IBranchProvider branchProvider) {
      if (showArtType == null) {
         showArtType = new DecoratorAction("Artifact Type", FrameworkImage.FILTERS);
      }
      if (showArtBranch == null) {
         showArtBranch = new DecoratorAction("Artifact Branch", FrameworkImage.FILTERS);
      }
      if (showArtVersion == null) {
         showArtVersion = new DecoratorAction("Artifact Version", FrameworkImage.FILTERS);
      }

      if (attributesAction == null && branchProvider != null) {
         attributesAction = new ShowAttributeAction(branchProvider, FrameworkImage.FILTERS);
      }

      if (showArtIds == null && isAdmin()) {
         showArtIds = new DecoratorAction("Artifact Ids", FrameworkImage.FILTERS);
      }
      if (saveSettingsAction == null) {
         saveSettingsAction = new SetSettingsAsDefault();
      }
   }

   private boolean isAdmin() {
      boolean result = false;
      try {
         if (AccessControlManager.isOseeAdmin()) {
            result = true;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         result = false;
      }
      return result;
   }

   public void addActions(IMenuManager manager, IBranchProvider provider) {
      checkActionsCreated(provider);

      if (showArtIds != null && isAdmin()) {
         if (manager != null) {
            manager.add(showArtIds);
         }
         showArtIds.updateText();
      }

      if (manager != null) {
         manager.add(showArtVersion);
         manager.add(showArtType);
         manager.add(showArtBranch);
      }
      showArtType.updateText();
      showArtVersion.updateText();
      showArtBranch.updateText();

      if (manager != null) {
         manager.add(attributesAction);
      }
      manager.add(new Separator());
      manager.add(saveSettingsAction);

      loadState();
   }

   public String getSelectedAttributeData(Artifact artifact) throws OseeCoreException {
      String toReturn = null;
      if (attributesAction != null) {
         Conditions.checkNotNull(artifact, "artifact");
         Collection<String> selectedItems = attributesAction.getSelected();

         List<String> info = new ArrayList<String>();
         for (IAttributeType attributeType : artifact.getAttributeTypes()) {
            if (selectedItems.contains(attributeType.getGuid())) {
               String value = artifact.getAttributesToString(attributeType.getName());
               if (Strings.isValid(value)) {
                  info.add(value);
               } else {
                  info.add("?");
               }
            }
         }
         if (!info.isEmpty()) {
            toReturn = "[" + Collections.toString(info, " | ") + "]";
         }
      }
      return toReturn != null ? toReturn : "";
   }

   public boolean showArtIds() {
      return showArtIds != null && showArtIds.isChecked();
   }

   public boolean showArtType() {
      return showArtType != null && showArtType.isChecked();
   }

   public boolean showArtBranch() {
      return showArtBranch != null && showArtBranch.isChecked();
   }

   public boolean showArtVersion() {
      return showArtVersion != null && showArtVersion.isChecked();
   }

   public void setShowArtType(boolean set) {
      if (showArtType != null) {
         showArtType.setChecked(set);
      }
   }

   public void setShowArtBranch(boolean set) {
      if (showArtBranch != null) {
         showArtBranch.setChecked(set);
      }
   }

   private void refreshView() {
      if (viewer != null) {
         viewer.refresh();
      }
   }

   private final class DecoratorAction extends Action {
      private final String name;
      private boolean isSelected;

      public DecoratorAction(String name, KeyedImage image) {
         super(name, IAction.AS_PUSH_BUTTON);
         this.name = name;
         this.isSelected = false;
         if (image != null) {
            setImageDescriptor(ImageManager.getImageDescriptor(image));
         }
         updateText();
      }

      @Override
      public void run() {
         setChecked(!isChecked());
         refreshView();
      }

      @Override
      public boolean isChecked() {
         return isSelected;
      }

      @Override
      public void setChecked(boolean checked) {
         isSelected = checked;
         updateText();
      }

      public void updateText() {
         String message = String.format("%s %s", isChecked() ? "Hide" : "Show", name);
         setText(message);
      }
   }

   private final class ShowAttributeAction extends Action {

      private final Set<String> selectedTypes;
      private final IBranchProvider branchProvider;

      public ShowAttributeAction(IBranchProvider branchProvider, KeyedImage image) {
         super("Show Attributes", IAction.AS_PUSH_BUTTON);
         this.branchProvider = branchProvider;
         this.selectedTypes = new HashSet<String>();
         if (image != null) {
            setImageDescriptor(ImageManager.getImageDescriptor(image));
         }
      }

      @Override
      public void run() {
         if (branchProvider != null) {
            try {
               Branch branch = branchProvider.getBranch();
               Collection<AttributeType> selectableTypes = AttributeTypeManager.getValidAttributeTypes(branch);
               AttributeTypeCheckTreeDialog dialog = new AttributeTypeCheckTreeDialog(selectableTypes);
               dialog.setTitle("Select Attribute Types");
               dialog.setMessage("Select attribute types to display.");

               List<IAttributeType> initSelection = new ArrayList<IAttributeType>();
               for (String entry : selectedTypes) {
                  for (AttributeType type : selectableTypes) {
                     if (type.getGuid().equals(entry)) {
                        initSelection.add(type);
                     }
                  }
               }
               dialog.setInitialElementSelections(initSelection);

               int result = dialog.open();
               if (result == Window.OK) {
                  selectedTypes.clear();
                  for (Object object : dialog.getResult()) {
                     if (object instanceof IAttributeType) {
                        selectedTypes.add(((IAttributeType) object).getGuid());
                     }
                  }
                  refreshView();
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      }

      public Collection<String> getSelected() {
         return selectedTypes;
      }

      public void setSelected(Collection<String> selected) {
         selectedTypes.clear();
         selectedTypes.addAll(selected);
      }
   }

   private final class SetSettingsAsDefault extends Action {

      public SetSettingsAsDefault() {
         super("Store Label Settings", IAction.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.SAVE));
      }

      @Override
      public void run() {
         storeState();
         notifySettingsChanged(ArtifactDecorator.this);
      }
   }

   private static void addDecoratorInstance(ArtifactDecorator source) {
      DECORATOR_INSTANCES.add(new WeakReference<ArtifactDecorator>(source));
   }

   private static void notifySettingsChanged(ArtifactDecorator source) {
      List<Object> toRemove = new ArrayList<Object>();

      for (WeakReference<ArtifactDecorator> ref : DECORATOR_INSTANCES) {
         ArtifactDecorator decorator = ref.get();
         if (decorator != null) {
            if (!source.equals(decorator)) {
               decorator.loadState();
               decorator.refreshView();
            }
         } else {
            toRemove.add(ref);
         }
      }
      DECORATOR_INSTANCES.removeAll(toRemove);
   }
}
