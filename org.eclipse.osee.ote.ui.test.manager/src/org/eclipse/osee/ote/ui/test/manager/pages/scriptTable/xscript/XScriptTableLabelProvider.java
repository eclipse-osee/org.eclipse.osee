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
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask.ScriptStatusEnum;
import org.eclipse.swt.graphics.Image;

public class XScriptTableLabelProvider extends XViewerLabelProvider {
   public static final OseeUiActivator plugin = TestManagerPlugin.getInstance();
   private static Image checkedImage = plugin.getImage("chkbox_enabled.gif");
   private static Image outputImage = plugin.getImage("check.gif");
   private static Map<ScriptStatusEnum, Image> statusImage = new HashMap<ScriptStatusEnum, Image>();
   private static Image uncheckedImage = plugin.getImage("chkbox_disabled.gif");

   public XScriptTableLabelProvider(XScriptTable viewer) {
      super(viewer);
      if (statusImage.size() == 0) {
         statusImage.put(ScriptStatusEnum.NOT_CONNECTED, plugin.getImage("alert_obj.gif"));
         statusImage.put(ScriptStatusEnum.READY, plugin.getImage("scriptReady_sm.gif"));
         statusImage.put(ScriptStatusEnum.IN_QUEUE, plugin.getImage("scriptInQueue_sm.gif"));
         statusImage.put(ScriptStatusEnum.RUNNING, plugin.getImage("scriptRunning.gif"));
         statusImage.put(ScriptStatusEnum.COMPLETE, plugin.getImage("scriptComplete_sm.gif"));
         statusImage.put(ScriptStatusEnum.CANCELLED, plugin.getImage("scriptCancelled_sm.gif"));
         statusImage.put(ScriptStatusEnum.CANCELLING, plugin.getImage("scriptCancelling_sm.gif"));
         statusImage.put(ScriptStatusEnum.INVALID, plugin.getImage("error_stack.gif"));
			statusImage.put(ScriptStatusEnum.INCOMPATIBLE, plugin.getImage("error_stack.gif"));
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
      if (m.find()){
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

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getColumnImage(java.lang.Object, org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
    */
   @Override
   public Image getColumnImage(Object element, XViewerColumn col,
			int columnIndex) throws Exception {
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

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getColumnText(java.lang.Object, org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn col,
			int columnIndex) throws Exception {
		ScriptTask task = (ScriptTask) element;
		if (XScriptTableFactory.STATUS.equals(col)) {
			return task.getStatus().toString();
		} else if (XScriptTableFactory.RESULT.equals(col)) {
			return task.getPassFail();
		} else if (XScriptTableFactory.TEST.equals(col)) {
			return task.getName(); 
		} else if (XScriptTableFactory.TEST_LOCATION.equals(col)) {
			return task.getPath();
		} else if (XScriptTableFactory.RUN.equals(col))
			return task.getRunStatus().toString();

      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
    */
   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   @Override
   public void removeListener(ILabelProviderListener listener) {
   }


}
