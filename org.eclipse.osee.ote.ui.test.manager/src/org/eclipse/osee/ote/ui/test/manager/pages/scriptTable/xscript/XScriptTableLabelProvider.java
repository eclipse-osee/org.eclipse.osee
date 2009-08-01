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
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask.ScriptStatusEnum;
import org.eclipse.swt.graphics.Image;

public class XScriptTableLabelProvider extends XViewerLabelProvider {
   public static final OseeUiActivator plugin = TestManagerPlugin.getInstance();
   private static Image checkedImage = ImageManager.getImage(OteTestManagerImage.CHECKBOX_ENABLED);
   private static Image outputImage = ImageManager.getImage(OteTestManagerImage.CHECK);
   private static Map<ScriptStatusEnum, Image> statusImage = new HashMap<ScriptStatusEnum, Image>();
   private static Image uncheckedImage = ImageManager.getImage(OteTestManagerImage.CHECKBOX_DISABLED);

   public XScriptTableLabelProvider(XScriptTable viewer) {
      super(viewer);
      if (statusImage.size() == 0) {
         statusImage.put(ScriptStatusEnum.NOT_CONNECTED, ImageManager.getImage(OteTestManagerImage.ALERT_OBJ));
         statusImage.put(ScriptStatusEnum.READY, ImageManager.getImage(OteTestManagerImage.SCRIPT_READY_SM));
         statusImage.put(ScriptStatusEnum.IN_QUEUE, ImageManager.getImage(OteTestManagerImage.SCRIPT_IN_QUEUE_SM));
         statusImage.put(ScriptStatusEnum.RUNNING, ImageManager.getImage(OteTestManagerImage.SCRIPT_RUNNING));
         statusImage.put(ScriptStatusEnum.COMPLETE, ImageManager.getImage(OteTestManagerImage.SCRIPT_COMPLETE_SM));
         statusImage.put(ScriptStatusEnum.CANCELLED, ImageManager.getImage(OteTestManagerImage.SCRIPT_CANCELLED_SM));
         statusImage.put(ScriptStatusEnum.CANCELLING, ImageManager.getImage(OteTestManagerImage.SCRIPT_CANCELLING_SM));
         statusImage.put(ScriptStatusEnum.INVALID, ImageManager.getImage(OteTestManagerImage.ERROR_STACK));
         statusImage.put(ScriptStatusEnum.INCOMPATIBLE, ImageManager.getImage(OteTestManagerImage.ERROR_STACK));
      }
   };

   private Image getOutputImage(ScriptTask task) {
      if (task.isOutputExists()) {
         return outputImage;
      }
      return null;
   }

   private Image getPassFailImage(ScriptTask task) {
      Matcher m = Pattern.compile("(FAIL|ABORTED)").matcher(task.getPassFail());
      if (m.find()) {
         return (Image) statusImage.get(ScriptStatusEnum.INVALID);
      }
      return null;
   }

   /**
    * Returns the image with the given key, or <code>null</code> if not found.
    */
   private Image getRunImage(boolean isSelected) {
      return isSelected ? checkedImage : uncheckedImage;
   }

   private Image getStatusImage(ScriptStatusEnum status) {
      return (Image) statusImage.get(status);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
      if (XScriptTableFactory.OUPUT_FILE.equals(col)) {
         return getOutputImage(((ScriptTask) element));
      } else if (XScriptTableFactory.RUN.equals(col)) {
         return getRunImage(((ScriptTask) element).isRun());
      } else if (XScriptTableFactory.STATUS.equals(col)) {
         return getStatusImage(((ScriptTask) element).getStatus());
      } else if (XScriptTableFactory.RESULT.equals(col)) {
         return getPassFailImage(((ScriptTask) element));
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws Exception {
      ScriptTask task = (ScriptTask) element;
      if (XScriptTableFactory.STATUS.equals(col)) {
         return task.getStatus().toString();
      } else if (XScriptTableFactory.RESULT.equals(col)) {
         return task.getPassFail();
      } else if (XScriptTableFactory.TEST.equals(col)) {
         return task.getName();
      } else if (XScriptTableFactory.TEST_LOCATION.equals(col)) {
         return task.getPath();
      } else if (XScriptTableFactory.RUN.equals(col)) return task.getRunStatus().toString();

      return "";
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
   public void dispose() {
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
   }

}
