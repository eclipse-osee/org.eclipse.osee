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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Charles Shaw
 * @author Robert A. Fisher
 */
public class MethodFormatter implements Xmlizable, XmlizableStream {
    private final Collection<Argument> arguments = new ArrayList<Argument>();

    public final class Argument {
        public final String Class;
        public final String Value;

        public Argument(final String Class, final String Value) {
            this.Class = Class;
            this.Value = Value;
        }

        @Override
        public String toString() {
            return "<" + Class + ">" + Value;
        }
    }

    public MethodFormatter add(float value) {
        arguments.add(new Argument(float.class.getName(), Double.toString(value)));
        return this;
    }

    public MethodFormatter add(double value) {
        arguments.add(new Argument(double.class.getName(), Double.toString(value)));
        return this;
    }

    public MethodFormatter add(byte value) {
        arguments.add(new Argument(byte.class.getName(), Double.toString(value)));
        return this;
    }

    public MethodFormatter add(short value) {
        arguments.add(new Argument(short.class.getName(), Double.toString(value)));
        return this;
    }

    public MethodFormatter add(int value) {
        arguments.add(new Argument(int.class.getName(), Integer.toString(value)));
        return this;
    }

    public MethodFormatter add(long value) {
        arguments.add(new Argument(long.class.getName(), Double.toString(value)));
        return this;
    }

    public MethodFormatter add(char value) {
        arguments.add(new Argument(char.class.getName(), Integer.toString(value)));
        return this;
    }

    public MethodFormatter add(boolean value) {
        arguments.add(new Argument(boolean.class.getName(), Boolean.toString(value)));
        return this;
    }

    public MethodFormatter add(EnumBase value) {
        arguments.add(new Argument(EnumBase.class.getName(), value.getName()));
        return this;
    }

    public MethodFormatter add(EnumBase[] value) {
        final String sep = ", ";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i].getName()).append(sep);
        }
        if (sb.length() > sep.length()) {
            sb.setLength(sb.length() - sep.length());
        }
        sb.append("]");
        arguments.add(new Argument(EnumBase[].class.getName(), sb.toString()));
        return this;
    }

    public MethodFormatter add(Object value) {
        arguments.add(new Argument(value.getClass().getName(), value.toString()));
        return this;
    }

    @Override
    public String toString() {
        final String sep = ", ";
        StringBuilder sb = new StringBuilder();
        for (Argument argument : arguments) {
            sb.append(argument.toString()).append(sep);
        }
        if (sb.length() > sep.length()) {
            sb.setLength(sb.length() - sep.length());
        }
        return sb.toString();
    }

    @Override
    public Element toXml(Document doc) {
        Element toReturn = doc.createElement("MethodArguments");
        for (Argument argument : arguments) {
            Element element = doc.createElement("Argument");
            element.appendChild(Jaxp.createElement(doc, "Type", argument.Class));
            String toLog = argument.Value != null ? XmlSupport.format(argument.Value) : "null";
            element.appendChild(Jaxp.createElement(doc, "Value", toLog));
            toReturn.appendChild(element);
        }
        return toReturn;
    }

    @Override
    public void toXml(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("MethodArguments");
        for (Argument argument : arguments) {
            writer.writeStartElement("Argument");
            XMLStreamWriterUtil.writeElement(writer, "Type", argument.Class);
            String toLog = argument.Value != null ? XmlSupport.format(argument.Value) : "null";
            XMLStreamWriterUtil.writeElement(writer, "Value", toLog);
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }
    
    @JsonProperty
    public Collection<Argument> getMethodArguments() {
        return arguments;
    }
}
