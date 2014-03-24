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
package org.eclipse.osee.ote.core;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Charles Shaw
 * @author Robert A. Fisher
 */
public class ReturnFormatter implements Xmlizable, XmlizableStream {

    private String returnValue;

    public void set(float value) {
        returnValue = Double.toString(value);
    }

    public void add(double value) {
        returnValue = Double.toString(value);
    }

    public void add(byte value) {
        returnValue = Double.toString(value);
    }

    public void add(short value) {
        returnValue = Double.toString(value);
    }

    public void add(int value) {
        returnValue = Integer.toString(value);
    }

    public void add(long value) {
        returnValue = Double.toString(value);
    }

    public void add(char value) {
        returnValue = Integer.toString(value);
    }

    public void add(boolean value) {
        returnValue = Boolean.toString(value);
    }

    public void add(Object value) {
        returnValue = value.toString();
    }

    @Override
    public String toString() {
        return returnValue;
    }

    @Override
    public Element toXml(Document doc) {
        return Jaxp.createElement(doc, "ReturnValue", returnValue);
    }

    @Override
    public void toXml(XMLStreamWriter writer) throws XMLStreamException {
        String toLog = returnValue != null ? XmlSupport.format(returnValue) : "null";
        XMLStreamWriterUtil.writeElement(writer, "ReturnValue", toLog);
    }

    public String getValue() {
        return returnValue;
    }
    
    @JsonProperty
    public String getReturnValue() {
        return returnValue;
    }
}
