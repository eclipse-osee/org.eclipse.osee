/*
 * Created on Jul 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Jeff C. Phillips
 */
public class StringHandlePromptChange implements IHandlePromptChange {
   private final EntryDialog ed;
   private final String attributeName;
   private final boolean persist;
   private final boolean multiLine;
   private final Collection<? extends Artifact> artifacts;
   private final NumberFormat format;

   public StringHandlePromptChange(String attributeName, boolean persist, String displayName, Collection<? extends Artifact> artifacts, NumberFormat format, boolean multiLine) {
      super();
      this.attributeName = attributeName;
      this.persist = persist;
      this.artifacts = artifacts;
      this.multiLine = multiLine;
      this.format = format;
      this.ed = new EntryDialog("Enter " + displayName, "Enter " + displayName);
   }

   @Override
   public boolean promptOk() throws OseeCoreException {
      ed.setFillVertically(multiLine);
      setInitialText(artifacts, ed, format, attributeName);
      ed.setNumberFormat(format);
      return ed.open() == Window.OK;
   }

   @Override
   public boolean store() throws OseeCoreException {
      updateSmaAttributes(artifacts, attributeName, format, ed);
      if (persist) {
         persistSmaAttributes(artifacts);
      }
      return true;
   }

   private static void setInitialText(Collection<? extends Artifact> smas, EntryDialog ed, NumberFormat format, String attributeName) throws OseeCoreException {
      if (smas.size() == 1) {
         Object smaObj = smas.iterator().next().getSoleAttributeValue(attributeName, "");
         String initialText = smaObj.equals("") ? "" : formatObject(smaObj, format);
         ed.setEntry(initialText);
      } else {
         ed.setEntry("");
      }
   }

   private static void updateSmaAttributes(final Collection<? extends Artifact> smas, String attributeName, NumberFormat format, EntryDialog ed) throws OseeCoreException {
      for (Artifact sma : smas) {
         String value = ed.getEntry();
         String safeValue = getSafeValue(value, format);
         sma.setSoleAttributeFromString(attributeName, safeValue);
      }
   }

   private static String getSafeValue(String value, NumberFormat format) throws OseeCoreException {
      String toReturn = value;
      if (format != null) {
         try {
            toReturn = String.valueOf(format.parse(value).doubleValue()); // TODO check for dot in integers
         } catch (ParseException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return toReturn;
   }

   private static void persistSmaAttributes(final Collection<? extends Artifact> smas) throws OseeCoreException {
      SkynetTransaction transaction =
         new SkynetTransaction(smas.iterator().next().getBranch(), "Persist SMA attributes");
      for (Artifact sma : smas) {
         sma.persist(transaction);
      }
      transaction.execute();
   }

   private static String formatObject(Object src, NumberFormat format) {
      if (format == null) {
         return String.valueOf(src);
      } else {
         return format.format(src);
      }
   }
}