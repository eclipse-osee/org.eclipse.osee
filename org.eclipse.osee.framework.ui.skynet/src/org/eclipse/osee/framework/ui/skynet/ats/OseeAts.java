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
package org.eclipse.osee.framework.ui.skynet.ats;

import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class OseeAts {
   public static enum OpenView {
      ActionEditor, ArtifactEditor, ArtifactHyperViewer
   };
   private static IAtsLib atsLib;
   private static String BUG_TITLE = "Generate Action Against This Tool";

   public OseeAts() {
      super();
   }

   public static boolean isAtsAdmin() {
      try {
         if (getAtsLib() != null) {
            return getAtsLib().isAdmin();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   public static void addButtonToEditorToolBar(final MultiPageEditorPart editorPart, IActionable actionableObject, final OseeUiActivator oseePlugin, IToolBarManager toolBar, final String editorId, final String actionableItem) {

      Action bugAction = new Action(BUG_TITLE, Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
            String desc = String.format("Found in \"%s\" version %s.", editorId, version);
            if (editorPart instanceof IActionable) {
               String moreDesc = ((IActionable) editorPart).getActionDescription();
               if (moreDesc != null && !moreDesc.equals("")) {
                  desc += "\n" + moreDesc;
               }
            }
            createActionViaBug(desc, actionableItem);
         }
      };
      bugAction.setToolTipText(BUG_TITLE);
      bugAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BUG));
      toolBar.add(bugAction);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final OseeUiActivator oseePlugin, ToolBar toolBar, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, oseePlugin, toolBar, null, editorId, actionableItem);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final OseeUiActivator oseePlugin, Composite comp, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, oseePlugin, null, comp, editorId, actionableItem);
   }

   private static void addButtonToEditorToolBar(final IActionable actionableObject, final OseeUiActivator oseePlugin, ToolBar toolBar, Composite comp, final String editorId, final String aspect) {
      if (actionableObject == null) {
         throw new IllegalArgumentException(String.format("actionableObject can not be null"));
      }
      if (editorId == null || editorId.equals("")) {
         throw new IllegalArgumentException(String.format("editorId can not be null or empty"));
      }
      if (aspect == null) {
         throw new IllegalArgumentException(String.format("aspect can not be null"));
      }

      if (toolBar != null) {
         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(FrameworkImage.BUG));
         item.setToolTipText(BUG_TITLE);
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
               String desc = String.format("\n\nItem: %s\nVersion: %s", editorId, version);
               if (actionableObject != null) {
                  String moreDesc = actionableObject.getActionDescription();
                  if (moreDesc != null && !moreDesc.equals("")) {
                     desc += "\n" + moreDesc;
                  }
               }
               createActionViaBug(desc, aspect);
            }
         });
      } else if (comp != null) {
         Button bugButton = new Button(comp, SWT.PUSH);
         bugButton.setToolTipText(BUG_TITLE);
         bugButton.setImage(ImageManager.getImage(FrameworkImage.BUG));
         bugButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
               String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
               String desc = String.format("\n\nItem: %s\nVersion: %s", editorId, version);
               if (actionableObject != null) {
                  String moreDesc = actionableObject.getActionDescription();
                  if (moreDesc != null && !moreDesc.equals("")) {
                     desc += "\n" + moreDesc;
                  }
               }
               createActionViaBug(desc, aspect);
            }

         });
      } else {
         throw new IllegalArgumentException("Can't determine bug target.");
      }
   }

   /**
    * Uses the ActionJob extension point to kickoff ATS code to create and open the action
    * 
    * @param version
    * @param desc
    */
   private static void createActionViaBug(String desc, String actionableItem) {
      try {
         getAtsLib().createATSAction(desc, actionableItem);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /**
    * @param viewPart
    * @param actionableObject
    * @param oseePlugin
    * @param viewId
    * @param actionableItem match the name of one of the configured Actionable Items in ATS
    */
   public static void addBugToViewToolbar(final ViewPart viewPart, final IActionable actionableObject, final OseeUiActivator oseePlugin, final String viewId, final String actionableItem) {
      if (viewId == null || viewId.equals("")) {
         throw new IllegalArgumentException(String.format("viewId can not be null or empty"));
      }
      if (actionableItem == null) {
         throw new IllegalArgumentException("Aspect can not be null.");
      }
      Action bugAction = new Action("Generate Action Against This View") {
         @Override
         public void run() {
            String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
            String desc = String.format("\n\nItem: %s\nVersion: %s", viewId, version);
            String moreDesc = actionableObject.getActionDescription();
            if (!moreDesc.equals("")) {
               desc += "\n" + moreDesc;
            }
            createActionViaBug(desc, actionableItem);
         }
      };
      bugAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BUG));
      bugAction.setToolTipText("Generate Action Against This View");

      IToolBarManager toolbarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(bugAction);
   }

   public static void openATSArtifact(String guid) {
      try {
         Artifact art = ArtifactQuery.getArtifactFromId(guid, BranchManager.getCommonBranch());
         if (art.isOfType("Action")) {
            atsLib.openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         } else {
            AWorkbench.popup("ERROR", "Trying to open " + art.getArtifactTypeName() + " with SMAEditor");
         }
      } catch (Exception ex) {
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public static void openATSArtifact(Artifact art) {
      if (art instanceof IATSArtifact) {
         try {
            getAtsLib().openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         }
      } else {
         AWorkbench.popup("ERROR", "Trying to open " + art.getArtifactTypeName() + " with SMAEditor");
      }
   }

   public static IAtsLib getAtsLib() throws OseeWrappedException {
      try {
         if (Platform.getExtensionRegistry() == null) {
            return null;
         }
         if (atsLib == null) {
            IExtensionPoint point =
                  Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.skynet.core.AtsLib");
            IExtension[] extensions = point.getExtensions();
            for (IExtension extension : extensions) {
               IConfigurationElement[] elements = extension.getConfigurationElements();
               for (IConfigurationElement el : elements) {
                  if (el.getName().equals("AtsLib")) {
                     String className = el.getAttribute("classname");
                     String bundleName = el.getContributor().getName();
                     if (className != null && bundleName != null) {
                        Bundle bundle = Platform.getBundle(bundleName);
                        Class<?> interfaceClass = bundle.loadClass(className);
                        atsLib = (IAtsLib) interfaceClass.getConstructor().newInstance();
                     }
                  }
               }
            }
         }
         return atsLib;
      } catch (Exception e) {
         throw new OseeWrappedException(e);
      }
   }

   public static Action createBugAction(OseeUiActivator oseePlugin, IAdaptable target, String itemId, String actionableItem) {
      return new BugAction(oseePlugin, target, itemId, actionableItem);
   }

   private static final class BugAction extends Action {
      private static String BUG_TITLE = "Generate Action Against This Tool";
      private final OseeUiActivator oseePlugin;
      private final String itemId;
      private final IAdaptable target;
      private final String actionableItem;

      public BugAction(OseeUiActivator oseePlugin, IAdaptable target, String itemId, String actionableItem) {
         super(BUG_TITLE, Action.AS_PUSH_BUTTON);
         this.oseePlugin = oseePlugin;
         this.itemId = itemId;
         this.target = target;
         this.actionableItem = actionableItem;
         setToolTipText(BUG_TITLE);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BUG));
      }

      @Override
      public void run() {
         Version version =
               new Version(
                     (String) oseePlugin.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION));
         String desc = String.format("Found in \"%s\" version %s.", itemId, version);
         IActionable actionable = (IActionable) target.getAdapter(IActionable.class);
         if (actionable != null) {
            String moreDesc = actionable.getActionDescription();
            if (moreDesc != null && !moreDesc.equals("")) {
               desc += "\n" + moreDesc;
            }
         }
         createActionViaBug(desc, actionableItem);
      }
   }
}
