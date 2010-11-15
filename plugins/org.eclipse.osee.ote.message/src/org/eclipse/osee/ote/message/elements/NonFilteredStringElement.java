package org.eclipse.osee.ote.message.elements;


public class NonFilteredStringElement extends StringElement{

	public NonFilteredStringElement(StringElement element) {
		super(element.getMessage(), element.getName(), element.getMsgData(), element.getByteOffset(), element.getMsb(), element.getLsb());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.message.elements.StringElement#getValue()
	 */
	@Override
	public String getValue() {
		return getMsgData().getMem().getUnfilteredASCIIString(byteOffset, msb, lsb);
	}

}
