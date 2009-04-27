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
package org.eclipse.osee.framework.ui.skynet;

import java.util.Date;
import java.util.GregorianCalendar;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BinaryAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.DateValue;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.EnumeratedValue;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.StringValue;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Item;

/**
 * @author Ryan D. Brooks
 */
public class AttributeCellModifier implements ICellModifier {
   private final TableViewer tableViewer;
   private final DateValue dateValue;
   private final EnumeratedValue enumeratedValue;
   private final StringValue stringValue;
   private final IDirtiableEditor editor;

   private final AttributesComposite attrComp;

   public AttributeCellModifier(IDirtiableEditor editor, TableViewer tableViewer, AttributesComposite attrComp) {
      super();
      this.tableViewer = tableViewer;
      this.attrComp = attrComp;
      this.dateValue = new DateValue();
      this.enumeratedValue = new EnumeratedValue();
      this.stringValue = new StringValue();
      this.editor = editor;

      // this.pList = new PermissionList();
      // pList.addPermission(Permission.PermissionEnum.EDITREQUIREMENT);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   public boolean canModify(Object element, String property) {
      attrComp.updateLabel("");
      if (element != null) {
         if (element instanceof Item) {
            element = ((Item) element).getData();
         }
         try {
            Attribute<?> attribute = (Attribute<?>) element;

            if (attribute instanceof WordAttribute) {
               return false;
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return property.equals("value");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   public Object getValue(Object element, String property) {
      try {
         Attribute<?> attribute = (Attribute<?>) element;
         Object object = attribute.getValue();
         if (attribute instanceof EnumeratedAttribute) {
            enumeratedValue.setValue(attribute.getDisplayableString());

            String[] choices =
                  AttributeTypeManager.getEnumerationValues(attribute.getAttributeType()).toArray(
                        new String[0]);
            enumeratedValue.setChoices(choices);
            return enumeratedValue;
         } else if (attribute instanceof BooleanAttribute) {
            enumeratedValue.setValue(attribute.getDisplayableString());
            enumeratedValue.setChoices(BooleanAttribute.booleanChoices);
            return enumeratedValue;
         } else if (attribute instanceof DateAttribute) {
            dateValue.setValue((Date) object);
            return dateValue;
         } else {
            stringValue.setValue(attribute.getDisplayableString() != null ? attribute.getDisplayableString() : "");
            return stringValue;
         }
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String,
    *      java.lang.Object)
    */
   public void modify(Object element, String property, Object value) {
      if (element != null) {
         // Note that it is possible for an SWT Item to be passed instead of the model element.
         if (element instanceof Item) {
            element = ((Item) element).getData();
         }
         try {
            Attribute<?> attribute = (Attribute<?>) element;

            if (attribute instanceof DateAttribute) {
               if (value instanceof GregorianCalendar) {
                  ((DateAttribute) attribute).setValue(new Date(((GregorianCalendar) value).getTimeInMillis()));
               } else {
                  ((DateAttribute) attribute).setValue((Date) value);
               }
            } else if (!(attribute instanceof BinaryAttribute)) {
               //binary attributes should not be changed.
               attribute.setFromString((String) value);
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
         tableViewer.update(element, null);
         editor.onDirtied();
         attrComp.notifyModifyAttribuesListeners();
      }
   }
}
