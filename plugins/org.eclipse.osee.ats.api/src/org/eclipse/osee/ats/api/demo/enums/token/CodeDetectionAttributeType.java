/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.demo.enums.token;

import org.eclipse.osee.ats.api.demo.enums.token.CodeDetectionAttributeType.CodeDetectionEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class CodeDetectionAttributeType extends AttributeTypeEnum<CodeDetectionEnum> {

   // @formatter:off
	public final CodeDetectionEnum TestScriptS = new CodeDetectionEnum(0, "Test Script(s)");
	public final CodeDetectionEnum InspectionTest = new CodeDetectionEnum(1, "Inspection (Test)");
	public final CodeDetectionEnum HotBench = new CodeDetectionEnum(2, "Hot Bench");
	public final CodeDetectionEnum Aircraft = new CodeDetectionEnum(3, "Aircraft");
	public final CodeDetectionEnum PeerReview = new CodeDetectionEnum(4, "Peer Review");
	public final CodeDetectionEnum TraxIssue = new CodeDetectionEnum(5, "TRAX Issue");
	public final CodeDetectionEnum Other = new CodeDetectionEnum(6, "Other");
	// @formatter:on

   public CodeDetectionAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847243L, namespace, "Code Detection", mediaType, "", taggerType);
   }

   public class CodeDetectionEnum extends EnumToken {
      public CodeDetectionEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
