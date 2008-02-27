/*
 * Created on Feb 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.INCOMING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.CHANGE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;

import java.io.InputStream;
import java.sql.SQLException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 *
 */
public class AttributeChanged extends Change {
    private static final SkynetActivator plugin = SkynetActivator.getInstance();
    private static final String BASE_IMAGE_STRING = "molecule";
    private static boolean imagesInitialized;
	private String sourceValue;
	private InputStream sourceContent;
	private int attrId;
	private int attrTypeId;
	private DynamicAttributeDescriptor dynamicAttributeDescriptor;

	/**
	 * @param sourceGamma
	 * @param artId
	 * @param toTransactionId
	 * @param fromTransactionId
	 * @param transactionType
	 * @param changeType
	 * @param sourceValue
	 * @param sourceContent
	 * @param attrId
	 * @param attrTypeId
	 */
	public AttributeChanged(int artTypeId, String artName, int sourceGamma, int artId,
			TransactionId toTransactionId, TransactionId fromTransactionId,
			TransactionType transactionType, ChangeType changeType,
			String sourceValue, InputStream sourceContent, int attrId,
			int attrTypeId) {
		super(artTypeId, artName, sourceGamma, artId, toTransactionId, fromTransactionId,
				transactionType, changeType);
		this.sourceValue = sourceValue;
		this.sourceContent = sourceContent;
		this.attrId = attrId;
		this.attrTypeId = attrTypeId;
	}

	/**
	 * @return the sourceValue
	 */
	public String getSourceValue() {
		return sourceValue;
	}

	/**
	 * @return the sourceContent
	 */
	public InputStream getSourceContent() {
		return sourceContent;
	}

	/**
	 * @return the attrId
	 */
	public int getAttrId() {
		return attrId;
	}

	/**
	 * @return the attrTypeId
	 */
	public int getAttrTypeId() {
		return attrTypeId;
	}

	/**
	 * @return the dynamicAttributeDescriptor
	 */
	   public DynamicAttributeDescriptor getDynamicAttributeDescriptor() throws SQLException {
		      if (dynamicAttributeDescriptor == null) {
		         dynamicAttributeDescriptor = ConfigurationPersistenceManager.getInstance().getDynamicAttributeType(attrTypeId);
		      }
		      return dynamicAttributeDescriptor;
		   }


	 public Image getImage() {
	      return getImage(getChangeType(), TransactionType.convertTransactionTypeToModificationType (getTransactionType()));
	   }

	   protected static Image getImage(ChangeType changeType, ModificationType modType) {
	      checkImageRegistry();
	      return plugin.getImage(BASE_IMAGE_STRING + changeType + modType);
	   }

	   private static void checkImageRegistry() {
	      if (!imagesInitialized) {
	         imagesInitialized = true;

	         ImageDescriptor outNew = SkynetActivator.getInstance().getImageDescriptor("out_new.gif");
	         ImageDescriptor outChange = SkynetActivator.getInstance().getImageDescriptor("out_change.gif");
	         ImageDescriptor outDeleted = SkynetActivator.getInstance().getImageDescriptor("out_delete.gif");
	         ImageDescriptor incNew = SkynetActivator.getInstance().getImageDescriptor("inc_new.gif");
	         ImageDescriptor incChange = SkynetActivator.getInstance().getImageDescriptor("inc_change.gif");
	         ImageDescriptor incDeleted = SkynetActivator.getInstance().getImageDescriptor("inc_delete.gif");
	         ImageDescriptor conChange = SkynetActivator.getInstance().getImageDescriptor("con_change.gif");
	         ImageDescriptor conDeleted = SkynetActivator.getInstance().getImageDescriptor("con_delete.gif");
	         ImageDescriptor conNew = SkynetActivator.getInstance().getImageDescriptor("con_new.gif");

	         Image baseImage = plugin.getImage(BASE_IMAGE_STRING + ".gif");

	         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + DELETE, new OverlayImage(baseImage, outDeleted));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + CHANGE, new OverlayImage(baseImage, outChange));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + NEW, new OverlayImage(baseImage, outNew));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + DELETE, new OverlayImage(baseImage, incDeleted));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + CHANGE, new OverlayImage(baseImage, incChange));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + NEW, new OverlayImage(baseImage, incNew));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + DELETE, new OverlayImage(baseImage, conDeleted));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + CHANGE, new OverlayImage(baseImage, conChange));
	         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + NEW, new OverlayImage(baseImage, conNew));
	      }
	   }

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.skynet.core.change.Change#getSourceDisplayData()
	 */
	@Override
	public String getSourceDisplayData() {
		return getSourceValue() != null? getSourceValue():"Stream data";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}
}
