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
package org.eclipse.osee.ote.message.tool.rec;

import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.EmptyEnum_Element;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.FixedPointElement;
import org.eclipse.osee.ote.message.elements.Float32Element;
import org.eclipse.osee.ote.message.elements.Float64Element;
import org.eclipse.osee.ote.message.elements.IElementVisitor;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;
import org.eclipse.osee.ote.message.elements.RealElement;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.osee.ote.message.elements.SignedInteger16Element;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.tool.rec.entry.ArrayElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.BooleanElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.CharElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.EmptyEnumEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.EnumeratedElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.IElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.IntegerElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.LongIntegerElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.RealElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.RecordElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.RecordMapEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.SignedInteger16ElementEntry;
import org.eclipse.osee.ote.message.tool.rec.entry.StringElementEntry;

public class ElementEntryFactory {

	private static final class ElementVisitor implements IElementVisitor {

		private IElementEntry entry = null;
		
		public IElementEntry getEntry() {
			return entry;
		}
		
		public void asCharElement(CharElement element) {
           entry = new CharElementEntry(element);
		}

		public void asEnumeratedElement(EnumeratedElement<?> element) {
			entry = new EnumeratedElementEntry(element);
		}

		public void asFixedPointElement(FixedPointElement element) {
			entry = new RealElementEntry(element);
		}

		public void asFloat32(Float32Element element) {
			entry = new RealElementEntry(element);		
		}

		public void asFloat64(Float64Element element) {
			entry = new RealElementEntry(element);
		}

		public void asGenericElement(Element element) {
		}

		public void asIntegerElement(IntegerElement element) {
			entry = new IntegerElementEntry(element);
		}

		public void asRealElement(RealElement element) {
			entry = new RealElementEntry(element);
		}

		public void asRecordElement(RecordElement element) {
		   entry = new RecordElementEntry(element);
		}

		public void asStringElement(StringElement element) {
			entry = new StringElementEntry(element);
		}


		public void asBooleanElement(BooleanElement element) {
		   entry = new BooleanElementEntry(element);
		}

      public void asRecordMap(RecordMap<? extends RecordElement> element) {
           entry = new RecordMapEntry(element);
		}

		public void asEmptyEnumElement(EmptyEnum_Element element) {
           entry = new EmptyEnumEntry(element);
		}

      @Override
      public void asArrayElement(ArrayElement element) {
         entry = new ArrayElementEntry(element);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ote.message.elements.IElementVisitor#asUnsignedIntegerElement(org.eclipse.osee.ote.message.elements.UnsignedIntegerElement)
       */
		public void asLongIntegerElement(LongIntegerElement element) {
			entry = new LongIntegerElementEntry(element);
		}

		public void asSignedInteger16Element(SignedInteger16Element element) {
		   entry = new SignedInteger16ElementEntry(element);
		}

        
	}
	
	public static IElementEntry createEntry(Element element) {
		
		ElementVisitor visitor = new ElementVisitor();
		element.visit(visitor);
		IElementEntry entry = visitor.getEntry();
		if (entry != null) {
			return entry;
		}
		throw new IllegalArgumentException("factory cannot generate an entry for class " + element.getClass().getName());
	}
}
