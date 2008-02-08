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

package org.eclipse.osee.framework.ui.skynet.branch;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.IAttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.IRelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.IRevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.ShowAttributeAction;
import org.eclipse.osee.framework.ui.swt.ITreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class BranchLabelProvider implements ITableLabelProvider, ITableColorProvider, ILabelProvider {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchLabelProvider.class);
   private static final SkynetAuthentication authentication = SkynetAuthentication.getInstance();
   private static Image branchImage = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static Image changeManagedBranchImage = SkynetGuiPlugin.getInstance().getImage("change_managed_branch.gif");

   private static Image favoriteBranchImage = null;
   private static Image defaultBranchImage = null;
   private static Image favoriteDefaultBranchImage = null;
   private static Image oldSnapshotImage = null;

   private static Image favoriteDefaultChangedManagedBranchImage = null;
   private static Image defaultChangeManagedBranchImage = null;
   private static Image favoriteChangeManagedBranchImage = null;
   private boolean showChangeType = false;

   private final ShowAttributeAction attributeAction;
   private Collection<Integer> attributeModifiedArtifactIds;

   public BranchLabelProvider() {
      this(null);
   }

   public BranchLabelProvider(ShowAttributeAction attributeAction) {
      this.attributeAction = attributeAction;

      // Make sure all of the users are mapped so we don't incur many single hits for users
      authentication.getUsers();
   }

   @SuppressWarnings("unchecked")
   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof ITreeNode) {
         element = ((ITreeNode) element).getBackingData();
      }

      // Seek down through aggregation lists to the lowest level to get an actual element
      while (element instanceof List && !((List) element).isEmpty()) {
         element = ((List) element).get(0);
      }

      if (element instanceof Branch && columnIndex == 0) {
         try {
            checkImages();
            Branch branch = (Branch) element;
            boolean favorite = authentication.getAuthenticatedUser().isFavoriteBranch(branch);
            boolean action = branch.isChangeManaged();
            boolean isDefault = element.equals(BranchPersistenceManager.getInstance().getDefaultBranch());

            if (favorite && action) {
               return isDefault ? favoriteDefaultChangedManagedBranchImage : favoriteChangeManagedBranchImage;
            } else if (favorite) {
               return isDefault ? favoriteDefaultBranchImage : favoriteBranchImage;
            } else if (action) {
               return isDefault ? defaultChangeManagedBranchImage : changeManagedBranchImage;
            } else {
               return isDefault ? defaultBranchImage : branchImage;
            }
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }

      } else if (element instanceof SnapshotDescription && columnIndex == 0) {
         if (((SnapshotDescription) element).isOutOfDate()) {
            checkImages();

            return oldSnapshotImage;
         }
      } else if (element instanceof TransactionData && columnIndex == 0) {
         return SkynetGuiPlugin.getInstance().getImage("transaction.gif");

      } else if (element instanceof RelationLinkChange && columnIndex == 2) {
         ArtifactSubtypeDescriptor descriptor = ((RelationLinkChange) element).getOtherArtifactDescriptor();
         if (descriptor == null)
            return null;
         else
            return descriptor.getImage();

      } else if (element instanceof IRevisionChange && columnIndex == 0) {
         return ((IRevisionChange) element).getImage();
      }

      return null;
   }

   private static synchronized void checkImages() {
      if (defaultBranchImage == null) {
         favoriteBranchImage =
               new OverlayImage(branchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("star_9_9.gif"), 0, 7).createImage();
         defaultBranchImage =
               new OverlayImage(branchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("black_check.gif"), 8, 0).createImage();
         favoriteDefaultBranchImage =
               new OverlayImage(defaultBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("star_9_9.gif"),
                     0, 7).createImage();

         defaultChangeManagedBranchImage =
               new OverlayImage(changeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "black_check.gif"), 8, 0).createImage();
         favoriteChangeManagedBranchImage =
               new OverlayImage(changeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "star_9_9.gif"), 0, 7).createImage();
         favoriteDefaultChangedManagedBranchImage =
               new OverlayImage(defaultChangeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "star_9_9.gif"), 0, 7).createImage();

         oldSnapshotImage = SkynetGuiPlugin.getInstance().getImage("old.gif");
      }
   }

   /**
    * @return Returns the text for a specific column
    */
   public String getColumnText(Object element, int columnIndex) {
      return getColumnTextLabel(element, columnIndex);
   }

   @SuppressWarnings("unchecked")
   public String getColumnTextLabel(Object element, int columnIndex) {
      if (element instanceof ITreeNode) element = ((ITreeNode) element).getBackingData();

      if (element instanceof Branch) {
         Branch branch = (Branch) element;

         if (columnIndex == 0) {
            return (OseeProperties.getInstance().isDeveloper() ? "(" + branch.getBranchId() + ") " : "") + branch.getBranchName();
         } else if (columnIndex == 1) {
            return String.valueOf(branch.getDisplayName());
         } else if (columnIndex == 2) {
            return String.valueOf(branch.getCreationDate());
         } else if (columnIndex == 3) {
            User user = authentication.getUserByArtId(branch.getAuthorId());
            return user == null ? "" : user.getDescriptiveName();
         } else if (columnIndex == 4) {
            return branch.getCreationComment();
         }
      } else if (element instanceof TransactionData) {
         TransactionData transactionData = (TransactionData) element;

         if (columnIndex == 0) {
            return String.valueOf(transactionData.getTransactionNumber());
         } else if (columnIndex == 2) {
            return String.valueOf((Timestamp) transactionData.getTimeStamp());
         } else if (columnIndex == 3) {
            return transactionData.getName();
         } else if (columnIndex == 4) {
            return transactionData.getComment();
         }
      } else if (element instanceof Collection) {
         Object headCursor = element;
         Object tailCursor = element;

         while (headCursor instanceof List && !((List) headCursor).isEmpty()) {
            headCursor = ((List) headCursor).get(0);
         }
         while (tailCursor instanceof List && !((List) tailCursor).isEmpty()) {
            List list = (List) tailCursor;
            tailCursor = list.get(list.size() - 1);
         }

         if (headCursor instanceof TransactionData && tailCursor instanceof TransactionData) {
            TransactionData headTransactionData = (TransactionData) headCursor;
            TransactionData tailTransactionData = (TransactionData) tailCursor;

            if (columnIndex == 0) {
               return String.valueOf(headTransactionData.getTransactionNumber() + "..." + tailTransactionData.getTransactionNumber());
            } else if (columnIndex == 2) {
               return String.valueOf((Timestamp) headTransactionData.getTimeStamp());
            }
         } else {
            return "Unexpected aggregation of " + headCursor.getClass().getSimpleName() + " and " + tailCursor.getClass().getSimpleName();
         }

      } else if (element instanceof ArtifactChange) {
         if (columnIndex == 0) {
            ArtifactChange artifactChange = (ArtifactChange) element;
            try {
               String txt =
                     artifactChange.getName() + (showChangeType ? " (" + getChangeType(artifactChange) + ")" : "");
               if (artifactChange.getModType() == ModificationType.DELETE && artifactChange.getChangeType() == ChangeType.INCOMING) {
                  txt = "Artifact Deleted";
               } else {
                  if (attributeAction != null && !attributeAction.noneSelected()) {
                     String attributeText = "";
                     try {
                        attributeText = attributeAction.getSelectedAttributeData(artifactChange.getArtifact());
                        if (attributeText != null) {
                           return txt + attributeText;
                        }
                     } catch (Exception ex) {
                        OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                        return txt + ex.getLocalizedMessage();
                     }
                  }
               }
               return txt;
            } catch (SQLException ex) {
               OSEELog.logException(getClass(), ex, false);
               return ex.getLocalizedMessage();
            }
         }
      } else if (element instanceof IAttributeChange) {
         IAttributeChange change = (IAttributeChange) element;

         if (columnIndex == 0) {
            return String.valueOf(change.getGammaId());
         } else if (columnIndex == 1) {
            return change.getName();
         } else if (columnIndex == 2) {
            return "was:" + change.getWasValue();
         } else if (columnIndex == 3) {
            return "is:" + change.getChange();
         }
      } else if (element instanceof IRelationLinkChange) {
         IRelationLinkChange change = (IRelationLinkChange) element;

         if (columnIndex == 0) {
            return String.valueOf(change.getGammaId());
         } else if (columnIndex == 1) {
            return change.getRelTypeName();
         } else if (columnIndex == 2) {
            return change.getOtherArtifactName();
         } else if (columnIndex == 3) {
            return change.getRationale();
         }
      } else if (columnIndex == 0) {
         return element.toString();
      }
      return "";
   }

   private String getChangeType(ArtifactChange artifactChange) throws SQLException {
      // Compare using artids cause a historical artifact is NOT equal to a current artifact
      if ((artifactChange.getModType() == SkynetDatabase.ModificationType.CHANGE) && attributeModifiedArtifactIds != null && !attributeModifiedArtifactIds.contains(artifactChange.getArtifact().getArtId())) return artifactChange.getModType().getDisplayName() + " Relation Only";
      return artifactChange.getModType().getDisplayName();
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public Image getImage(Object element) {
      return getColumnImage(element, 0);
   }

   /**
    * @return returns the text for the first column as default
    */
   public String getText(Object element) {
      return getColumnText(element, 0);
   }

   public Color getForeground(Object element, int columnIndex) {
      if (element instanceof ITreeNode) element = ((ITreeNode) element).getBackingData();

      if (element instanceof SnapshotDescription && columnIndex == 0) {
         if (((SnapshotDescription) element).isOutOfDate()) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
         }
      }
      return null;
   }

   public Color getBackground(Object element, int columnIndex) {
      return null;
   }

   /**
    * @param branch
    * @return Returns the image for a branch.
    */
   public static Image getBranchImage(Branch branch) {
      if (branch == null) {
         throw new IllegalArgumentException("The branch can not be null.");
      }

      return branch.isChangeManaged() ? changeManagedBranchImage : branchImage;
   }

   /**
    * @return the showChangeType
    */
   public boolean isShowChangeType() {
      return showChangeType;
   }

   /**
    * @param showChangeType the showChangeType to set
    */
   public void setShowChangeType(boolean showChangeType, Collection<Integer> attributeModifiedArtifactIds) {
      this.showChangeType = showChangeType;
      this.attributeModifiedArtifactIds = attributeModifiedArtifactIds;
   }
}
