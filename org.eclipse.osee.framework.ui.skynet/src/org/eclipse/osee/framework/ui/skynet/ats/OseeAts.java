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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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

public class OseeAts {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeAts.class);
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
         if (getAtsLib() == null) return true;
         return getAtsLib().isAtsAdmin();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      return false;
   }

   public static void addButtonToEditorToolBar(final MultiPageEditorPart editorPart, IActionable actionableObject, final OseeUiActivator oseePlugin, IToolBarManager toolBar, final String editorId, final String actionableItem) {

      Action bugAction = new Action(BUG_TITLE, Action.AS_PUSH_BUTTON) {
         public void run() {
            String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
            String desc = String.format("Found in \"%s\" version %s.", editorId, version);
            if (editorPart instanceof IActionable) {
               String moreDesc = ((IActionable) editorPart).getActionDescription();
               if (!moreDesc.equals("")) desc += "\n" + moreDesc;
            }
            createActionViaBug(desc, actionableItem);
         }
      };
      bugAction.setToolTipText(BUG_TITLE);
      bugAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("bug.gif"));
      toolBar.add(bugAction);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final OseeUiActivator oseePlugin, ToolBar toolBar, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, oseePlugin, toolBar, null, editorId, actionableItem);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final OseeUiActivator oseePlugin, Composite comp, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, oseePlugin, null, comp, editorId, actionableItem);
   }

   private static void addButtonToEditorToolBar(final IActionable actionableObject, final OseeUiActivator oseePlugin, ToolBar toolBar, Composite comp, final String editorId, final String aspect) {
      if (actionableObject == null) throw new IllegalArgumentException(
            String.format("actionableObject can not be null"));
      if (editorId == null || editorId.equals("")) throw new IllegalArgumentException(
            String.format("editorId can not be null or empty"));
      if (aspect == null) throw new IllegalArgumentException(String.format("aspect can not be null"));

      if (toolBar != null) {
         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(SkynetGuiPlugin.getInstance().getImage("bug.gif"));
         item.setToolTipText(BUG_TITLE);
         item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
               String desc = String.format("\n\nItem: %s\nVersion: %s", editorId, version);
               if (actionableObject != null) {
                  String moreDesc = actionableObject.getActionDescription();
                  if (moreDesc != null && !moreDesc.equals("")) desc += "\n" + moreDesc;
               }
               createActionViaBug(desc, aspect);
            }
         });
      } else if (comp != null) {
         Button bugButton = new Button(comp, SWT.PUSH);
         bugButton.setToolTipText(BUG_TITLE);
         bugButton.setImage(SkynetGuiPlugin.getInstance().getImage("bug.gif"));
         bugButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
               String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
               String desc = String.format("\n\nItem: %s\nVersion: %s", editorId, version);
               if (actionableObject != null) {
                  String moreDesc = actionableObject.getActionDescription();
                  if (moreDesc != null && !moreDesc.equals("")) desc += "\n" + moreDesc;
               }
               createActionViaBug(desc, aspect);
            }

         });
      } else
         throw new IllegalArgumentException("Can't determine bug target.");
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
         logger.log(Level.SEVERE, ex.toString(), ex);
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
      if (viewId == null || viewId.equals("")) throw new IllegalArgumentException(
            String.format("viewId can not be null or empty"));
      if (actionableItem == null) throw new IllegalArgumentException("Aspect can not be null.");
      Action bugAction = new Action("Generate Action Against This View") {
         public void run() {
            String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
            String desc = String.format("\n\nItem: %s\nVersion: %s", viewId, version);
            String moreDesc = actionableObject.getActionDescription();
            if (!moreDesc.equals("")) desc += "\n" + moreDesc;
            createActionViaBug(desc, actionableItem);
         }
      };
      bugAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("bug.gif"));
      bugAction.setToolTipText("Generate Action Against This View");

      IToolBarManager toolbarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(bugAction);
   }

   public static void openATSArtifact(String guid) {
      try {
         Artifact art = ArtifactQuery.getArtifactFromId(guid, BranchPersistenceManager.getCommonBranch());
         if (art.getArtifactTypeName().equals("Action"))
            atsLib.openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         else
            AWorkbench.popup("ERROR", "Trying to open " + art.getArtifactTypeName() + " with SMAEditor");
      } catch (Exception ex) {
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public static void openATSArtifact(Artifact art) {
      if (art instanceof IATSArtifact) {
         try {
            getAtsLib().openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         }
      } else {
         AWorkbench.popup("ERROR", "Trying to open " + art.getArtifactTypeName() + " with SMAEditor");
      }
   }

   public static IAtsLib getAtsLib() throws ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      if (Platform.getExtensionRegistry() == null) return null;
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
   }

}
