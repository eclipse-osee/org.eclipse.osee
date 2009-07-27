/*
 * Created on Jun 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.test.manager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public enum OteTestManagerImage implements OseeImage {
   ADD("add.gif"),
   ALERT_OBJ("alert_obj.gif"),
   CHECK("check.gif"),
   CHECKBOX_ENABLED("chkbox_enabled.gif"),
   CHECKBOX_DISABLED("chkbox_disabled.gif"),
   DELETE("delete.gif"),
   ENVIRONMENT("environment.gif"),
   ERROR("error.gif"),
   ERROR_STACK("error_stack.gif"),
   FILE("file.gif"),
   FILE_DELETE("file_delete.gif"),
   FLDR_OBJ("fldr_obj.gif"),
   LOAD_CONFIG("loadConfig.gif"),
   OFP("ofp.gif"),
   PROJECT_SET_IMAGE("import_wiz.gif"),
   SAVE_EDIT("save_edit.gif"),
   SAVEAS_EDIT("saveas_edit.gif"),
   SEL_ABORT_STOP("sel_abort_stop.gif"),
   SEL_BATCH_ABORT_STOP("sel_batch_abort_stop.gif"),
   SEL_RUN_EXEC("sel_run_exec.gif"),
   SCRIPT_CANCELLED("scriptCancelled.gif"),
   SCRIPT_CANCELLED_SM("scriptCancelled_sm.gif"),
   SCRIPT_CANCELLING("scriptCancelling.gif"),
   SCRIPT_CANCELLING_SM("scriptCancelling_sm.gif"),
   SCRIPT_COMPLETE("scriptComplete.gif"),
   SCRIPT_COMPLETE_SM("scriptComplete_sm.gif"),
   SCRIPT_IN_QUEUE("scriptInQueue.gif"),
   SCRIPT_IN_QUEUE_SM("scriptInQueue_sm.gif"),
   SCRIPT_OUTPUT("scriptOutput.gif"),
   SCRIPT_OUTPUT_SM("scriptOutput_sm.gif"),
   SCRIPT_READY("scriptReady.gif"),
   SCRIPT_READY_SM("scriptReady_sm.gif"),
   SCRIPT_RUNNING("scriptRunning.gif"),
   TEST("test.gif"),
   TEST_BATCH_IMAGE("file.gif"),
   TEST_MANAGER("tm.gif"),
   UNSEL_ABORT_STOP("unsel_abort_stop.gif"),
   UNSEL_BATCH_ABORT_STOP("unsel_batch_abort_stop.gif"),
   UNSEL_RUN_EXEC("unsel_run_exec.gif");

   private final String fileName;

   private OteTestManagerImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(TestManagerPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return TestManagerPlugin.PLUGIN_ID + ".images." + fileName;
   }
}
