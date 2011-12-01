/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.jvnet.mimepull;

import java.nio.ByteBuffer;

/**
 * @author Jitendra Kotamraju
 */
abstract class MIMEEvent {

    enum EVENT_TYPE {START_MESSAGE, START_PART, HEADERS, CONTENT, END_PART, END_MESSAGE}

    /**
     * Returns a event for parser's current cursor location in the MIME message.
     *
     * <p>
     * {@link EVENT_TYPE#START_MESSAGE} and {@link EVENT_TYPE#START_MESSAGE} events
     * are generated only once.
     *
     * <p>
     * {@link EVENT_TYPE#START_PART}, {@link EVENT_TYPE#END_PART}, {@link EVENT_TYPE#HEADERS}
     * events are generated only once for each attachment part.
     *
     * <p>
     * {@link EVENT_TYPE#CONTENT} event may be generated more than once for an attachment
     * part.
     *
     * @return event type
     */
    abstract EVENT_TYPE getEventType();

    static final StartMessage START_MESSAGE = new StartMessage();
    static final StartPart START_PART = new StartPart();
    static final EndPart END_PART = new EndPart();
    static final EndMessage END_MESSAGE = new EndMessage();

    static final class StartMessage extends MIMEEvent {
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.START_MESSAGE;
        }
    }

    static final class StartPart extends MIMEEvent {
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.START_PART;
        }
    }

    static final class EndPart extends MIMEEvent {
        EVENT_TYPE getEventType () {
            return EVENT_TYPE.END_PART;
        }
    }

    static final class Headers extends MIMEEvent {
        InternetHeaders ih;

        Headers(InternetHeaders ih) {
            this.ih = ih;
        }

        EVENT_TYPE getEventType() {
            return EVENT_TYPE.HEADERS;
        }

        InternetHeaders getHeaders() {
            return ih;
        }
    }

    static final class Content extends MIMEEvent {
        private final ByteBuffer buf;

        Content(ByteBuffer buf) {
            this.buf = buf;
        }

        EVENT_TYPE getEventType() {
            return EVENT_TYPE.CONTENT;
        }

        ByteBuffer getData() {
            return buf;
        }
    }

    static final class EndMessage extends MIMEEvent {
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.END_MESSAGE;
        }
    }

}
