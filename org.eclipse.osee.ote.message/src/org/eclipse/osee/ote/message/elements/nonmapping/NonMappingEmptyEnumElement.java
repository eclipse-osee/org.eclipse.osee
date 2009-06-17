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
package org.eclipse.osee.ote.message.elements.nonmapping;

import java.lang.ref.WeakReference;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.condition.IDiscreteElementCondition;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.EmptyEnum_Element;
import org.eclipse.osee.ote.message.elements.IElementVisitor;
import org.eclipse.osee.ote.message.enums.EmptyEnum;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

public class NonMappingEmptyEnumElement extends EmptyEnum_Element {

	/**
	 * Copy constructor.
	 * 
	 * @param element
	 */
	public NonMappingEmptyEnumElement(EmptyEnum_Element element) {
		super(null, element.getElementName(), element.getMsgData(), element
				.getByteOffset(), element.getMsb(), element.getLsb());
		// This is being done so it doesn't get added to the element list hash
		// map.
		this.msg = new WeakReference<Message<?, ?, ?>>(element.getMessage());
		for (Object obj : element.getElementPath()) {
			this.getElementPath().add(obj);
		}
	}

	public NonMappingEmptyEnumElement(Message<?, ?, ?> message,
			String elementName, MessageData messageData, int byteOffset,
			int msb, int lsb) {
		super(message, elementName, messageData, byteOffset, msb, lsb);
	}

	public NonMappingEmptyEnumElement(Message<?, ?, ?> message,
			String elementName, MessageData messageData, int byteOffset,
			int msb, int lsb, int originalLsb, int originalMsb) {
		super(message, elementName, messageData, byteOffset, msb, lsb,
				originalLsb, originalMsb);
	}

	public NonMappingEmptyEnumElement(Message<?, ?, ?> message,
			String elementName, MessageData messageData, int bitOffset,
			int bitLength) {
		super(message, elementName, messageData, bitOffset, bitLength);
	}

	@Override
	public void checkForwarding(ITestAccessor accessor,
			EmptyEnum_Element cause, EmptyEnum value)
			throws InterruptedException {
		throwNoMappingElementException();
	}

	@Override
	public EmptyEnum elementMask(EmptyEnum value) {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum get(ITestEnvironmentAccessor accessor) {
		throwNoMappingElementException();
		return null;
	}

	@Override
	protected Element getNonMappingElement() {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum getValue() {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public void parseAndSet(ITestEnvironmentAccessor accessor, String value)
			throws IllegalArgumentException {
		throwNoMappingElementException();
	}

	@Override
	public void setAndSend(ITestEnvironmentAccessor accessor,
			EmptyEnum enumeration) {
		throwNoMappingElementException();
	}

	@Override
	public void setValue(EmptyEnum obj) {
		throwNoMappingElementException();
	}

	@Override
	public String toString(EmptyEnum obj) {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum valueOf(MemoryResource mem) {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public void visit(IElementVisitor visitor) {
		throwNoMappingElementException();
	}

	@Override
	public boolean check(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum value, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean check(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum value) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup,
			boolean isInList, EmptyEnum[] list, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup,
			boolean wantInList, EmptyEnum[] list) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public EmptyEnum checkMaintain(ITestAccessor accessor,
			CheckGroup checkGroup, EmptyEnum value, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainList(ITestAccessor accessor,
			CheckGroup checkGroup, EmptyEnum[] list, boolean isInList,
			int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainNot(ITestAccessor accessor,
			CheckGroup checkGroup, EmptyEnum value, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainNotNT(ITestAccessor accessor,
			EmptyEnum value, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainNotRange(ITestAccessor accessor,
			CheckGroup checkGroup, EmptyEnum minValue, boolean minInclusive,
			EmptyEnum maxValue, boolean maxInclusive, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainNotRangeNT(ITestAccessor accessor,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainNT(ITestAccessor accessor, EmptyEnum value,
			int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainRange(ITestAccessor accessor,
			CheckGroup checkGroup, EmptyEnum minValue, boolean minInclusive,
			EmptyEnum maxValue, boolean maxInclusive, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum checkMaintainRangeNT(ITestAccessor accessor,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum value, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum value) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkNotNT(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum value) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkNotRangeNT(ITestAccessor accessor, EmptyEnum minValue,
			boolean minInclusive, EmptyEnum maxValue, boolean maxInclusive,
			int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum pulsedValue, EmptyEnum nonPulsedValue, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkRangeNT(ITestAccessor accessor, EmptyEnum minValue,
			boolean minInclusive, EmptyEnum maxValue, boolean maxInclusive,
			int millis) throws InterruptedException {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkRangeNT(ITestAccessor accessor, EmptyEnum minValue,
			boolean minInclusive, EmptyEnum maxValue, boolean maxInclusive) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public boolean checkNT(ITestAccessor accessor, CheckGroup checkGroup,
			EmptyEnum value) {
		throwNoMappingElementException();
		return false;
	}

	@Override
	public int compareTo(DiscreteElement<EmptyEnum> o) {
		throwNoMappingElementException();
		return 0;
	}

	@Override
	public EmptyEnum get() {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum getNoLog() {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public void set(EmptyEnum value) {
		throwNoMappingElementException();
	}

	@Override
	public void set(ITestEnvironmentAccessor accessor, EmptyEnum value) {
		throwNoMappingElementException();
	}

	@Override
	public void setNoLog(EmptyEnum value) {
		throwNoMappingElementException();
	}

	@Override
	public void setNoLog(ITestEnvironmentAccessor accessor, EmptyEnum value) {
		throwNoMappingElementException();
	}

	@Override
	public synchronized void toggle(ITestEnvironmentAccessor accessor,
			EmptyEnum value1, EmptyEnum value2, int milliseconds)
			throws InterruptedException {
		throwNoMappingElementException();
	}

	@Override
	public String toString() {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public String valueOf() {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum waitForList(ITestAccessor accessor, EmptyEnum[] list,
			boolean isInList, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum waitForNotRange(ITestEnvironmentAccessor accessor,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum waitForNotValue(ITestEnvironmentAccessor accessor,
			EmptyEnum value, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum waitForRange(ITestEnvironmentAccessor accessor,
			EmptyEnum minValue, boolean minInclusive, EmptyEnum maxValue,
			boolean maxInclusive, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	public EmptyEnum waitForValue(ITestEnvironmentAccessor accessor,
			EmptyEnum value, int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

	@Override
	protected CheckPoint waitWithCheckPoint(ITestAccessor accessor,
			CheckGroup checkGroup, String expected,
			IDiscreteElementCondition<EmptyEnum> condition, boolean maintain,
			int milliseconds) throws InterruptedException {
		throwNoMappingElementException();
		return null;
	}

}
