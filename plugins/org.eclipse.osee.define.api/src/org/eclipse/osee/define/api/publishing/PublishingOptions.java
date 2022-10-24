/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.api.publishing;

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * This class is used as a Data Transfer Object for transferring options from the client to the server.
 *
 * @author Branden W. Phillips
 */
public class PublishingOptions implements ToMessage {

   //boolean
   public boolean publishDiff = false;
   public boolean originPublishAsDiff = false;
   public boolean addMergeTag = false;
   public boolean includeUuids = false;
   public boolean updateParagraphNumbers = false;
   public boolean useParagraphNumbers = false;
   public boolean skipErrors = false;
   public boolean excludeFolders = false;
   public boolean useTemplateOnce = false;
   public boolean recurse = false;
   public boolean recurseOnLoad = false;
   public boolean useArtifactNames = false;
   public boolean allAttributes = false;
   public boolean maintainOrder = false;
   public boolean noDisplay = false;
   public boolean skipDialogs = false;
   public boolean firstTime = false;
   public boolean secondTime = false;
   public boolean templateOnly = false;
   public boolean inPublishMode = false;

   //BranchId
   public BranchId branch = BranchId.SENTINEL;
   public BranchId compareBranch = BranchId.SENTINEL;
   public BranchId wasBranch = BranchId.SENTINEL;

   //ArtifactId
   public ArtifactId view = ArtifactId.SENTINEL;
   public ArtifactId templateArtifact = ArtifactId.SENTINEL;

   //Link Type
   public LinkType linkType = null;

   //ArtifactType
   public List<ArtifactTypeToken> excludeArtifactTypes = null;

   //String
   public String orcsQuery = null;
   public String overrideDataRights = null;
   public String attributeName = null;
   public String paragraphNumber = null;
   public String outlineType = null;
   public String resultPathReturn = null;
   public String openOption = null;
   public String executeVBScript = null;
   public String templateOption = null;
   public String previewRecurseValue = null;
   public String previewRecurseNoAttrValue = null;
   public String diffValue = null;
   public String diffNoAttrValue = null;
   public String threeWayMerge = null;
   public String id = null;
   public String name = null;

   //Int
   public int msWordHeadingDepth = 9;

   public void setAllAttributes(boolean allAttributes) {
      this.allAttributes = allAttributes;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "PublishingOptions" )
         .indentInc()
         .segment( "publishDiff",               this.publishDiff               )
         .segment( "originPublishAsDiff",       this.originPublishAsDiff       )
         .segment( "addMergeTag",               this.addMergeTag               )
         .segment( "includeUuids",              this.includeUuids              )
         .segment( "updateParagraphNumbers",    this.updateParagraphNumbers    )
         .segment( "useParagraphNumbers",       this.useParagraphNumbers       )
         .segment( "skipErrors",                this.skipErrors                )
         .segment( "excludeFolders",            this.excludeFolders            )
         .segment( "useTemplateOnce",           this.useTemplateOnce           )
         .segment( "recurse",                   this.recurse                   )
         .segment( "recurseOnLoad",             this.recurseOnLoad             )
         .segment( "useArtifactNames",          this.useArtifactNames          )
         .segment( "allAttributes",             this.allAttributes             )
         .segment( "maintainOrder",             this.maintainOrder             )
         .segment( "noDisplay",                 this.noDisplay                 )
         .segment( "skipDialogs",               this.skipDialogs               )
         .segment( "firstTime",                 this.firstTime                 )
         .segment( "secondTime",                this.secondTime                )
         .segment( "templateOnly",              this.templateOnly              )
         .segment( "inPublishMode",             this.inPublishMode             )
         .segment( "branch",                    this.branch                    )
         .segment( "compareBranch",             this.compareBranch             )
         .segment( "wasBranch",                 this.wasBranch                 )
         .segment( "view",                      this.view                      )
         .segment( "templateArtifact",          this.templateArtifact          )
         .segment( "linkType",                  this.linkType                  )
         .segment( "excludeArtifactTypes",      this.excludeArtifactTypes      )
         .segment( "orcsQuery",                 this.orcsQuery                 )
         .segment( "overrideDataRights",        this.overrideDataRights        )
         .segment( "attributeName",             this.attributeName             )
         .segment( "paragraphNumber",           this.paragraphNumber           )
         .segment( "outlineType",               this.outlineType               )
         .segment( "resultPathReturn",          this.resultPathReturn          )
         .segment( "openOption",                this.openOption                )
         .segment( "executeVBScript",           this.executeVBScript           )
         .segment( "templateOption",            this.templateOption            )
         .segment( "previewRecurseValue",       this.previewRecurseValue       )
         .segment( "previewRecurseNoAttrValue", this.previewRecurseNoAttrValue )
         .segment( "diffValue",                 this.diffValue                 )
         .segment( "diffNoAttrValue",           this.diffNoAttrValue           )
         .segment( "threeWayMerge",             this.threeWayMerge             )
         .segment( "id",                        this.id                        )
         .segment( "name",                      this.name                      )
         .segment( "msWordHeadingDepth",        this.msWordHeadingDepth        )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}
