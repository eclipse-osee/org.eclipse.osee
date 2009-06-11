/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.Saveable;

/**
 * @author b1565043
 */
public class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate, ISaveablesSource , IEditableContent{
   private String contents, name;
   private long time;

   CompareItem(String name, String contents, long time) {
      this.name = name;
      this.contents = contents;
      this.time = time;
   }

   public InputStream getContents() throws CoreException {
      return new ByteArrayInputStream(contents.getBytes());
   }

   public Image getImage() {
      return null;
   }
   
   public boolean isEditable() {
      return true;
  }

   public long getModificationDate() {
      return time;
   }

   public String getName() {
      return name;
   }

   public String getString() {
      return contents;
   }

   public String getType() {
      return ITypedElement.TEXT_TYPE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.ISaveablesSource#getActiveSaveables()
    */
   @Override
   public Saveable[] getActiveSaveables() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.ISaveablesSource#getSaveables()
    */
   @Override
   public Saveable[] getSaveables() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.IEditableContent#replace(org.eclipse.compare.ITypedElement, org.eclipse.compare.ITypedElement)
    */
   @Override
   public ITypedElement replace(ITypedElement dest, ITypedElement src) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.IEditableContent#setContent(byte[])
    */
   @Override
   public void setContent(byte[] newContent) {
   }
}
