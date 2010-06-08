package org.eclipse.osee.framework.ui.skynet.change.presenter;

import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public interface IChangeReportUiHandler {

   public KeyedImage getActionImage();

   public String getActionName();

   public KeyedImage getScenarioImage(ChangeUiData changeUiData);

   public String getScenarioDescriptionHtml(ChangeUiData changeUiData) throws OseeCoreException;

   public String getActionDescription();

   public String getName(TransactionDelta txDelta);

   public void appendTransactionInfoHtml(StringBuilder sb, ChangeUiData changeUiData) throws OseeCoreException;

}
