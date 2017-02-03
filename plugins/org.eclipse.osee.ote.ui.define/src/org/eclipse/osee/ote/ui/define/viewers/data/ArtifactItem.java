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
package org.eclipse.osee.ote.ui.define.viewers.data;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactItem extends DataItem implements IXViewerItem, IArtifactEventListener {
   private static Image FROM_LOCAL_WS_COMMIT_ALLOWED_IMAGE = null;
   private static Image FROM_DATABASE_IMAGE = null;
   private static Image FROM_LOCAL_WS_COMMIT_NOT_ALLOWED_IMAGE = null;
   private static Image INVALID_SCRIPT_IMAGE = null;
   private static boolean isFullDescriptionMode = true;

   private final XViewer xViewer;
   private Artifact artifact;
   private ArtifactTestRunOperator operator;
   private String key;

   public ArtifactItem(XViewer xViewer, Artifact artifact, DataItem parent) throws OseeArgumentException {
      super(parent);
      this.xViewer = xViewer;
      setArtifact(artifact);
      OseeEventManager.addListener(this);
   }

   @Override
   public Artifact getData() {
      return artifact;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   private void setArtifact(Artifact artifact) throws OseeArgumentException {
      this.artifact = artifact;
      this.operator = new ArtifactTestRunOperator(artifact);
      try {
         this.key = String.format("%s:%s:%s", getOperator().getChecksum(), getOperator().isFromLocalWorkspace(),
            getOperator().hasNotBeenCommitted());
      } catch (Exception ex) {
         this.key = "";
      }
   }

   public XViewer getXViewer() {
      return xViewer;
   }

   public ArtifactTestRunOperator getOperator() {
      return operator;
   }

   @Override
   public String getLabel(int index) {
      String toReturn = "";
      Artifact artifact = getData();
      try {
         if (artifact != null && artifact.isDeleted() != true) {

            if (index <= xViewer.getTree().getColumns().length - 1) {

               TreeColumn treeCol = xViewer.getTree().getColumns()[index];
               String colName = treeCol.getText();
               if (colName.equals("GUID")) {
                  toReturn = artifact.getGuid();
               } else if (colName.equals("Artifact_Type")) {
                  toReturn = artifact.getArtifactTypeName();
                  if (getOperator().isFromLocalWorkspace()) {
                     toReturn += " (Local)";
                  }
               } else if (colName.equals("Status")) {
                  try {
                     toReturn = getOperator().getTestResultStatus();
                  } catch (Exception ex) {
                     OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
                     toReturn = ex.getLocalizedMessage();
                  }
               } else if (colName.equals("Disposition")) {
                  //Special case for the Disposition Artifact
                  String name = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.Name, "");
                  if (name != "") {
                     try {
                        Artifact dispoArtifact = ArtifactQuery.getArtifactFromTypeAndName(
                           CoreArtifactTypes.TestRunDisposition, name, artifact.getBranch());
                        if (dispoArtifact != null) {
                           return dispoArtifact.getSoleAttributeValueAsString(OteAttributeTypes.TestDisposition, "");
                        }
                     } catch (ArtifactDoesNotExist ex) {
                        //ignore if not defined
                     }
                  }
                  return "";
               } else {
                  AttributeTypeId attributeType = AttributeTypeManager.getType(colName);
                  if (artifact.isAttributeTypeValid(attributeType)) {
                     if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
                        Date date = null;
                        try {
                           date = artifact.getSoleAttributeValue(attributeType);
                        } catch (Exception ex) {
                           // Do Nothing;
                        }
                        if (date != null) {
                           toReturn = new DateAttribute().MMDDYYHHMM.format(date);
                        } else {
                           toReturn = "NOT SET";
                        }
                     } else {
                        toReturn = artifact.getAttributesToString(attributeType);
                        if (colName.equals("Name")) {
                           toReturn = getArtifactName(toReturn);
                        }
                     }
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
         toReturn = ex.getLocalizedMessage();
      }
      return toReturn;
   }

   private String getArtifactName(String rawName) throws OseeCoreException {
      String name = rawName;
      if (isFullDescriptionModeEnabled() != true) {
         String[] qualifiers = rawName.split("\\.");
         name = qualifiers[qualifiers.length - 1];
      }
      return String.format("%s%s [%s]", getOperator().isFromLocalWorkspace() ? "> " : "", name,
         getOperator().getChecksum());
   }

   @Override
   public Image getImage() {
      Image toReturn = null;
      try {
         Artifact artifact = getData();
         if (artifact != null && artifact.isDeleted() != true) {
            if (areImagesInitialized() != true) {
               initializeImages();
            }
            ArtifactTestRunOperator operator = getOperator();
            if (operator.isFromLocalWorkspace() == true) {
               if (operator.isCommitAllowed() == true) {
                  toReturn = FROM_LOCAL_WS_COMMIT_ALLOWED_IMAGE;
               } else {
                  if (operator.isScriptRevisionValid() != true) {
                     toReturn = INVALID_SCRIPT_IMAGE;
                  } else {
                     toReturn = FROM_LOCAL_WS_COMMIT_NOT_ALLOWED_IMAGE;
                  }
               }
            } else {
               toReturn = FROM_DATABASE_IMAGE;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   private boolean areImagesInitialized() {
      return FROM_LOCAL_WS_COMMIT_ALLOWED_IMAGE != null && FROM_DATABASE_IMAGE != null && FROM_LOCAL_WS_COMMIT_NOT_ALLOWED_IMAGE != null;
   }

   private void initializeImages() {
      Artifact artifact = getData();
      Image defaultImage = ArtifactImageManager.getImage(artifact);
      DecorationOverlayIcon overlay = null;
      if (FROM_LOCAL_WS_COMMIT_ALLOWED_IMAGE == null) {
         OverlayImage overlayImage = new OverlayImage(defaultImage,
            ImageManager.getImageDescriptor(OteDefineImage.ADDITION), Location.BOT_RIGHT);
         FROM_LOCAL_WS_COMMIT_ALLOWED_IMAGE = overlayImage.createImage();
      }
      if (FROM_LOCAL_WS_COMMIT_NOT_ALLOWED_IMAGE == null) {
         overlay = new DecorationOverlayIcon(defaultImage, ImageManager.getImageDescriptor(OteDefineImage.CONFAUTO_OV),
            IDecoration.BOTTOM_RIGHT);
         FROM_LOCAL_WS_COMMIT_NOT_ALLOWED_IMAGE = overlay.createImage();
      }
      if (FROM_DATABASE_IMAGE == null) {
         overlay = new DecorationOverlayIcon(defaultImage,
            ImageManager.getImageDescriptor(OteDefineImage.VERSION_CONTROLLED), IDecoration.BOTTOM_RIGHT);
         FROM_DATABASE_IMAGE = overlay.createImage();
      }
      if (INVALID_SCRIPT_IMAGE == null) {
         overlay = new DecorationOverlayIcon(defaultImage, ImageManager.getImageDescriptor(OteDefineImage.OBSTRUCTED),
            IDecoration.BOTTOM_RIGHT);
         INVALID_SCRIPT_IMAGE = overlay.createImage();
      }
   }

   @Override
   public Object getKey() {
      return key;
   }

   public static void setFullDescriptionModeEnabled(boolean isEnabled) {
      ArtifactItem.isFullDescriptionMode = isEnabled;
   }

   public static boolean isFullDescriptionModeEnabled() {
      return ArtifactItem.isFullDescriptionMode;
   }

   //   @Override
   //   public void handleTransactionEvent(Sender sender, TransactionEvent transData) {
   //      if (artifact.isDeleted()) {
   //         return;
   //      }
   //
   ////      transData.getTransactions()
   //      TransactionChange change = null;
   //      change.
   //      if (transData.isDeleted(artifact)) {
   //         Displays.ensureInDisplayThread(new Runnable() {
   //            @Override
   //            public void run() {
   //               if (!xViewer.getTree().isDisposed()) {
   //                  xViewer.remove(this);
   //               }
   //               dispose();
   //            }
   //         });
   //         return;
   //      }
   //      if (transData.isRelAddedChangedDeleted(artifact) || transData.isChanged(artifact)) {
   //         Displays.ensureInDisplayThread(new Runnable() {
   //            @Override
   //            public void run() {
   //               if (!xViewer.getTree().isDisposed()) {
   //                  xViewer.remove(this);
   //               } else {
   //                  xViewer.update(this, null);
   //               }
   //            }
   //         });
   //      }
   //   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (artifact.isDeleted()) {
         return;
      }
      if (artifactEvent.isDeletedPurged(artifact)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (!xViewer.getTree().isDisposed()) {
                  xViewer.remove(this);
               }
               dispose();
            }
         });
         return;
      }
      if (artifactEvent.isRelAddedChangedDeleted(artifact) || artifactEvent.isModified(artifact)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (!xViewer.getTree().isDisposed()) {
                  xViewer.remove(this);
               } else {
                  xViewer.update(this, null);
               }
            }
         });
      }
   }

}
