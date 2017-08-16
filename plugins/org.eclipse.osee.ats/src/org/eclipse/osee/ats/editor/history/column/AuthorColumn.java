/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.history.column;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AuthorColumn extends XViewerValueColumn {
   private static AuthorColumn instance = new AuthorColumn();
   private final Map<ArtifactId, String> artIdToName = new HashMap<>(40);

   public static AuthorColumn getInstance() {
      return instance;
   }

   public AuthorColumn() {
      super("ats.history.Author", "Author", 100, XViewerAlign.Left, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AuthorColumn copy() {
      AuthorColumn newXCol = new AuthorColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String name = "";
      if (element instanceof Change) {
         try {
            TransactionRecord endTx = TransactionManager.getTransaction(((Change) element).getTxDelta().getEndTx());
            ArtifactId author = endTx.getAuthor();
            name = artIdToName.get(author);
            if (name == null) {
               Artifact art = ArtifactQuery.getArtifactFromId(author, AtsClientService.get().getAtsBranch(),
                  DeletionFlag.EXCLUDE_DELETED);
               if (art != null) {
                  name = art.getName();
                  artIdToName.put(author, name);
               } else {
                  name = "unknown for " + author;
               }
            }
            return name;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            name = "exception " + ex.getLocalizedMessage();
         }
      }
      return name;
   }
}
