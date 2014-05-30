/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.log;

import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.core.internal.AtsCoreService;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Donald G. Dunne
 */
public class AtsLogWriter {

   private final ILogStorageProvider storageProvider;
   public final static String ATS_LOG_TAG = "AtsLog";
   public final static String LOG_ITEM_TAG = "Item";
   public final static Pattern LOG_ITEM_PATTERN =
      Pattern.compile("<Item date=\"(.*?)\" msg=\"(.*?)\" state=\"(.*?)\" type=\"(.*?)\" userId=\"(.*?)\"/>");
   public final static Pattern LOG_ITEM_TAG_PATTERN = Pattern.compile("<Item ");
   private final IAtsLog atsLog;

   public AtsLogWriter(IAtsLog atsLog, ILogStorageProvider storageProvider) {
      this.atsLog = atsLog;
      this.storageProvider = storageProvider;
   }

   public void save(IAtsChangeSet changes) {
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element rootElement = doc.createElement(ATS_LOG_TAG);
         doc.appendChild(rootElement);
         for (IAtsLogItem item : atsLog.getLogItems()) {
            Element element = doc.createElement(LOG_ITEM_TAG);
            element.setAttribute("type", item.getType().name());
            element.setAttribute("date", String.valueOf(item.getDate().getTime()));
            element.setAttribute("userId", item.getUserId());
            element.setAttribute("state", item.getState());
            element.setAttribute("msg", item.getMsg());
            rootElement.appendChild(element);
         }
         storageProvider.saveLogXml(Jaxp.getDocumentXml(doc), changes);
      } catch (Exception ex) {
         OseeLog.log(AtsCoreService.class, OseeLevel.SEVERE_POPUP, "Can't create ats log document", ex);
      }
      atsLog.setDirty(false);
   }

}
