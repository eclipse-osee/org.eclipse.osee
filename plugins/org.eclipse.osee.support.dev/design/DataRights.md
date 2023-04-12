---

# Data Rights

Copyright (c) 2023 Boeing

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0

Contributors: Boeing - initial API and implementation

## Data Content Artifacts And Attributes

*Data rights will be specified for each OSEE Artifact using the set of attributes described in this section.*

### Attribute: Data Rights Classification -> Required Indicators

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: Data Rights Classification</dt>
<dd>
<ul>
<li>Change the display name of the attribute to "Required Indicators".</li>
<li>Add additional Required Indicators as specified by projects, corporate, or by CUI Authorities.</li>
</ul>
</dd>
<dt>Artifacts:</dt>
<ul>
<li>Add the "DataRightsClassification" attribute as ".any" instead of ".zeroOrOne" to all artifacts that currently have it.
</ul>
</dd>
</dl>
</td></tr></table>

*The CUI Authority may specify additional title page, header, and or footer statements to accompany the CUI data. Corporate policy and contract may also require additional statements. These title page, header, and footer statements are called "Required Indicators".*

<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>An enumerated member from the "Required Indicators" attribute is referred to as a &lt;required-indicator&gt;.</li>
<li>The set of &lt;required-indicator&gt; enumeration members that are selected is referred to as the &lt;required-indicator-set&gt;.</li>
<li>A map of &lt;required-indicator-set&gt;s keyed with a unique integer identifier is referred to as a &lt;required-indicator-set-map&gt;</li>
</ul>
</td></tr></table>

### Attribute: Data Rights Basis -> Required Indicators Rationale

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: Data Rights Basis</dt>
<dd>
<ul>
<li>Change the display name of the attribute "DataRightsBasis" to "Required Indicator Rational".</li>
</ul>
</dd>
</dl>
</td></tr></table>

*This text attribute is used to indicate the rational for the &lt;required-indicator&gt; selections in the "Required Indicators" attribute. This attribute is not used for publishing.*

### CUI

*CUI is Controlled Unclassified Information that requires special markings and handeling.*

### Attribute: Data Classification -> CUI Category And CUI Type

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: CUI Category And CUI Type</dt>
<dd>
<ul>
<li>Change the display name of the attribute "DataClassification" to “CUI Category And CUI Type”.</li>
</ul>
</dd>
<dt>Artifacts:</dt>
<dd>
<ul>
<li>Add the "DataClassification" attribute as ".any" instead of ".zeroOrOne" to all artifacts that currently have it.</li>
</ul>
</dd>
</dl>
</td></tr></table>

*The members of this enumeration must be listed in the CUI Registry. Each “CUI Category” has an enumeration member for “Basic” and one for “Specified”. A separate attribute cannot be used for the “CUI Type” because the “CUI Type” must be specified independently for each “CUI Category” selected in the “CUI Category” attribute.*

<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>The "CUI Category" is referred to as a &lt;cui-category-indicator&gt;.</li>
<li>The "CUI Type" is referred to as a &lt;cui-type-indicator&gt;.</li>
<li>The "CUI Category" and "CUI Type" that is selected by a member of the enumeration is referred to as a &lt;cui-category-indicator-cui-type-indicator-pair&gt;.</li>
<li>The set of &lt;cui-category-indicator-cui-type-indicator-pair&gt; that are selected is referred to as the &lt;cui-category-indicator-cui-type-indicator-pair-set&gt;.</li>
<li>A map of &lt;cui-category-indicator-cui-type-indicator-pair-set&gt;s keyed with a unique integer identifier is referred to as a &lt;cui-category-indicator-cui-type-indicator-pair-set-map&gt;.</li>
</ul>
</td></tr></table>

### Attribute: Data Classification Rationale -> CUI Category Rationale

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: CUI Category Rationale</dt>
<dd>
<ul>
<li>Change the display name of the attribute “DataClassificationRationale” to "CUI Category Rationale".</li>
</ul>
</dd>
</dl>
</td></tr></table>

*This attribute is used to indicate the rational for the selected &lt;cui-category-indicator-cui-type-indicator-pair-set&gt; in the "CUI Category and CUI Type" attribute. This attribute is not used for publishing.*


### Attribute: CUI Limited Dissemination Control

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: CUI Limited Dissemination Control</dt>
<dd>
<ul>
<dd>Create a new multi-value enumeration with the values from &lt;cui-limited-dissemination-control-indicator&gt; with the name "DataRightsCuiLimitedDisseminationControl" and the display name "CUI Limited Dissemination Control".</dd>
</ul>
</dd>
<dt>Artifacts:</dt>
<dd>
<ul>
<li>Add the attribute ".any(DataRightsCuiLimitedDisseminationControls)" on to all artifacts with the attribute "CUI Category And CUI Type".</li>
</ul>
</dd>
</dl>
</td></tr></table>

*The members of this enumeration must be listed in the CUI Registry.*


<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>The "CUI Dissemination" that is selected by a member of the enumerations is referred to as a &lt;cui-limited-dissemination-control-indicator&gt;.</li>
<li>The set of &lt;cui-limited-dissemination-control-indicator&gt;s that are selected is referred to as the &lt;cui-limited-dissemination-control-indicator-set&gt;.</li>
<li>When either of the &lt;cui-limited-dissemination-control-indicator&gt;s "REL TO" or "DISPLAY ONLY" are selected the &lt;cui-limited-dissemination-control-indicator-set&gt; must be associated with a &lt;trigraph-country-code-indicator-set&gt;. This pair is referred to as the &lt;cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair&gt;
<li>A map of &lt;cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair&gt;s keyed with a unique integer identifier is referred to as a &lt;cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair-map&gt;.</li>
</ul>
</td></tr></table>


### Artifact: CUI Release List

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: CUI Release List</dt>
<dd>
<ul>
<li>Create a new multi-value enumeration with the values from &lt;trigraph-country-code-indicator&gt; with the name "DataRightsTrigraphCountryCode" and the display name “CUI Release List”.</li>
</ul>
</dd>
<dt>Artifacts:</dt>
<dd>
<ul>
<li>Add the attribute ".any(DataRightsTrigraphCountryCodes)" on to all artifacts with the attribute "CUI Category And CUI Type".</li>
</ul>
</dd>
</dl>
</td></tr></table>

*This attribute should only be specified when the "REL TO" or "DISPLAY ONLY" are selected in the &lt;cui-limited-dissemination-control-indicator-set&gt;. The countries that the Artifact’s data may be released to are selected with this enumeration. This enumeration does NOT indicate the countries that a published document may be released to.*

<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>The "Trigraph Country Code" that is selected by a member of the enumerations is referred to as a &lt;trigraph-country-code-indicator&gt;.</li>
<li>The set of &lt;trigraph-country-code-indicator&gt; that are selected is referred to as the &lt;trigraph-country-code-indicator-set&gt;.</li>
</ul>
</td></tr></table>

## Publishing Template Data Rights Configuration Artifacts And Attributes

### Artifact: RendererTemplateWholeWord

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: Data Rights Configuration</dt>
<dd>
<ul>
<li>Create a new "AttributeTypeString" Attribute with the name "PublishingTemplateDataRightsConfigurationNameReference" and the display name "Data Rights Configuration".</li>
<li>Create a new ".exactlyOne(PublishingTemplateDataRightsConfigurationNameReference)" attribute on the artifact.</li>
</ul>
</dd>
</dl>
</td></tr></table>

*The new "Data Rights Configuration" attribute will contain the name of an &lt;DataRightsConfiguration&gt; artifact that will applicable for publishes with the containing &lt;RendererTemplateWholeWord&gt; publishing template.*

<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>The "Data Rights Configuration" name that is specified is referred to as a &lt;data-rights-configuration-indicator&gt;.</li>
</ul>
</td></tr></table>

### Artifact: Data Rights Configuration

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: CUI Category CUI Type</dt>
<dd>Create a new multi-value enumeration with the values from &lt;cui-category-indicator&gt; and &lt;cui-type-indicator&gt; with the name "DataRightsCuiCategoryCuiType" and the display name "CUI Category And CUI Type".</dd>
<dt>Attribute: CUI Limited Dissemination Control</dt>
<dd>Create a new multi-value enumeration with the values from &lt;cui-limited-dissemination-control-indicator&gt; with the name "DataRightsCuiLimitedDisseminationControl" and the display name "CUI Limited Dissemination Control".</dd>
<dt>Attribute: Trigraph Country Codes</dt>
<dd>Create a new multi-value enumeration with the values from &lt;trigraph-country-code-indicator&gt; with the name "DataRightsTrigraphCountryCodes" and the display name "Trigraph Country Codes".</dd>
<dt>Attribute: Required Indicators</dt>
<dd>Create a new "AttributeTypeString" attribute with the name "DataRightsRequiredIndicator" and the display name "Required Indicator".</dd>
<dt>Artifact: Data Rights Configuration</dt>
<dd>
<ul>
<li>Create a new "ArtifactTypeToken" with the name "DataRightsConfiguration" and the display name "Data Rights Configuration".</li>
<li>Add the attribute ".any(DataRightsCuiCategoryCuiType)".</li>
<li>Add the attribute ".any(DataRightsCuiLimitedDisseminationControl)".</li>
<li>Add the attribute ".any(DataRightsTrigraphCountryCodes)."</li>
<li>Add the attribute ".any(DataRightsRequiredIndicator)."</li>
</ul>
</dd>
</dl>
</td></tr></table>

*The Publishing Template artifact references the &lt;DataRightsConfiguration&gt; artifact by name.. Having the data rights configuration in a separate artifact would allow multiple publishing templates to share the same configuration without having to duplicate the data. It also allows for a single point of maintenance.*

<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>The selected "CUI Category" and "CUI Type" indicator pairs are referred to as &lt;cui-category-indicator-cui-type-indicator-pair-set&gt;.</li>
<li>The selected "CUI Limited Dissemination Control" indicators are referred to as &lt;cui-limited-dissemination-control-indicator-set&gt;.</li>
<li>The selected "Trigraph Country Code" indicators are referred to as &lt;trigraph-country-code-indicator-set&gt;.</li>
<li>The values in the "Required Indicators" attribute are referred to as &lt;required-indicator-definition-indicator-set&gt;.</li>
</ul>
</td></tr></table>

### Artifact: Required Indicator Configuration

<table style="background-color:#ffddbb; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<dl>
<dt>Attribute: Title Statement</dt>
<dd>Create a new "AttributeTypeString" attribute with the name "DataRightsRequiredIndicatorTitleStatement" and the display name "Title Statement".</dd>
<dt>Attribute: Header Statement</dt>
<dd>Create a new "AttributeTypeString" attribute with the name "DataRightsRequiredIndicatorHeaderStatement" and the display name "Header Statement".</dd>
<dt>Attribute: Footer Statement</dt>
<dd>Create a new "AttributeTypeString" attribute with the name "DataRightsRequiredIndicatorFooterStatement" and the display name "Footer Statement".</dd>
<dt>Attribute: Required Indicator Frequency</dt>
<dd>Create a new multi-value enumeration with the values from &lt;&gt; with the name "DataRightsRequiredIndicatorFrequency" and the display name "Required Indicator Frequency".</dd>
<dt>Artifact: Required Indicator Definition</dt>
<dd>
<ul>
<li>Create a new "ArtifactTypeToken" with the name "DataRightsRequiredIndicatorConfiguration" and the display name "Required Indicator Configuration".</li>
<li>Add the attribute ".exactlyOne(DataRightsRequiredIndicatorTitleStatement)".</li>
<li>Add the attribute ".exactlyOne(DataRightsRequiredIndicatorHeaderStatement)".</li>
<li>Add the attribute ".exactlyOne(DataRightsRequiredIndicatorFooterStatement)".</li>
<li>Add the attribute ".any(DataRightsRequiredIndicatorFrequency)".</li>
</ul>
</dd>
</dl>
</td></tr></table>

*Separating the Required Indicator definition from the statements will allow the statements to be reused for other data rights configurations where the location or frequency of the statement is different.*

<table style="background-color:#eeffee; border-collapse: collapse; border:solid 1px; margin-left: 2em; margin-right: 2em;"><tr><td>
<ul>
<li>The "Title Statement" name that is specified is referred to as &lt;statement-indicator&gt;.</li>
<li>The "Header Statement" name that is specified is referred to as &lt;statement-indicator&gt;.</li>
<li>The "Footer Statement" name that is specified is referred to as &lt;statement-indicator&gt;.</li>
<li>The values in the "Required Indicator Frequency" attribute are referred to as &lt;required-indicator-frequency-indicator-set&gt;.
</ul>
</td></tr></table>

## Data Rights Configuration Folders

*The default data rights are stored under the folder "OSEE Configuration/Data Rights/Default". The general preview and editing publishing templates will link to the data rights configuration artifacts under this folder.*

*Publishing Templates are OSEE Artifacts the contain a MS Word document the publishing content is inserted into. The publishing templates also contain a JSON data structure with additional parameters for the publish.*

### Data Rights Configuration Artifacts

*The configuration folder for Data Rights will be located on the "Common" branch under the "OSEE Configuration" folder. The folder tree below shows the hierarchical structure of the configuration artifacts.*


<ul>
<li>"OSEE Configuration" (Artifact Type Name: Folder)</li>
<ul>
<li>"Data Rights" (Artifact Type Name: Folder)</li>
<ul>
<li>"Data Rights Configuration" (Artifact Type Name: Folder)</li>
<ul>
<li>{ "Default" | &lt;document&gt; (Artifact Type Name: Data Rights Configuration) }</li>
</ul>
<li>"Required Indicators Configuration" (Artifact Type Name: Folder)</li>
<ul>
<li>{ &lt;required-indicator-indicator&gt; } (Artifact Type Name: Required Indicator Configuration)</li>
</ul>
<li>"Statements" (Artifact Type Name: Folder)</li>
<ul>
<li>{ &lt;statement&gt; } (Artifact Type Name: General Data)</li>
</ul>
</ul>
<li>"Document Templates" (Artifact Type Name: Folder)</li>
<ul>
<li>{ &lt;presentation-type&gt; | &lt;document&gt; } (Artifact Type Name: Renderer Template - Whole Word)</li>
</ul>
</ul>
</ul>






### OSEE Configuration Artifact Relationships

*The text diagram below show the relationships between the Publishing Template Artifact and the Data Rights Artifacts. The relationships are made by Artifact name, so the user has to be careful to maintain unique names.*



```                    
|   Name Reference Relationships
|  
+---->  
```

```
( &lt;document&gt; | &lt;presentation-type&gt; ) <Artifact Type: RendererTemplateWholeWord>
   |   ((Under "OSEE Configuration/Document Templates" Folder))
   |
   |
   +--->( "Default" | &lt;document&gt; ) <Artifact Type: DataRightsConfiguration>
           |   ((Under "OSEE Configuration/Data Rights/Data Rights Configuration" Folder))
           |
           |
           +---> { &lt;required-indicator-indicator&gt; } <Artifact Type: DataRightsRequiredIndicatorConfiguration>
                    |   ((Under "OSEE Configuration/Data Rights/Required Indicators Configuration Folder))
                    |
                    |     
                    +---> { &lt;statement-indicator&gt; } <Artifact Type: General Data>
                             ((Under "OSEE Configuration/Data Rights/Statements" Folder))
```


### Publishing Template Manager Data Rights Cache

#### Data Rights Configuration Cache

The Publishing Template Manager shall build a data structure for each Publishing Template that is properly linked to Data Rights Configuration Artifacts according to the JSON schema "file://eclipse.org/json-schemas/publishing-templates-manager/publishing-template-data-rights".

The Publishing Template Manager shall cache the &lt;publishing-template-data-rights&gt; structures with the associated publishing templates.


#### Required Indicator Definition Cache

The Publishing Template Manager shall build a data structure for each Required Indicator Definition according to the JSON Schema "file://eclipse.org/json-schemas/publishing-template-manager/required-indicator-definition".

The Publishing Template Manager shall cache the &lt;required-indicator-definition&gt; structures in a map keyed by &lt;required-indicator-definition-indicator&gt;.


#### Statement Cache

The Publishing Template Manager shall build a data structure for each Required Indicator Statement according to the JSON Schema "file://eclipse.org/json-schemas/publishing-template-manager/statement".

The Publishing Template Manager shall cache the &lt;statement&gt; structures in a map keyed by &lt;statement-indicator&gt;.



# Publishing

## CUI Limited Dissemination Control Indicator Compatibility


*The table below shows the compatibility of the CUI Limited Dissemination Controls Indicators. For a &lt;cui-limited-dissemination-control-indicator-set&gt; to be valid, all of the &lt;cui-limited-dissemination-control-indicator&gt;s in the set must be compatible according to the table below.*


<table border="1">
<tr>
<th>Primary</th>
<th>Allowed With</th>
</tr>
<tr>
<td>
<table border="1" style="border-collapse:collapse;">
<tr style="height:2em;"><th></th></tr>
<tr style="height:2em;"><th>NOFORN</th></tr>
<tr style="height:2em;"><th>FED ONLY</th></tr>
<tr style="height:2em;"><th>FEDCON</th></tr>
<tr style="height:2em;"><th>NOCON</th></tr>
<tr style="height:2em;"><th>DL ONLY</th></tr>
<tr style="height:2em;"><th>RELIDO</th></tr>
<tr style="height:2em;"><th>REL TO</th></tr>
<tr style="height:2em;"><th>DISPLAY ONLY</th></tr>
</table>
</td> 
<td>
<table border="1" style="border-collapse:collapse;">
<tr style="height:2em;"><th>NOFORN</th>                                 <th>FED ONLY</th>                                 <th>FEDCON</th>                                 <th>NOCON</th>                                 <th>DL ONLY</th>                                 <th>RELIDO</th>                                 <th>REL TO</th>                                 <th>DISPLAY ONLY</th></tr>
<tr style="height:2em;"><td style="background-color: gray"; >NA    </td><td style="background-color: green;">YES     </td><td style="background-color: green;">YES   </td><td style="background-color: green;">YES  </td><td style="background-color: red;"  >NO     </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO          </td></tr>
<tr style="height:2em;"><td style="background-color: green;">YES   </td><td style="background-color: gray"; >NA      </td><td style="background-color: red;"  >NO    </td><td style="background-color: green;">YES  </td><td style="background-color: red;"  >NO     </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO          </td></tr>
<tr style="height:2em;"><td style="background-color: green;">YES   </td><td style="background-color: red;"  >NO      </td><td style="background-color: gray"; >NA    </td><td style="background-color: red;"  >NO   </td><td style="background-color: red;"  >NO     </td><td style="background-color: red;"  >NO    </td><td style="background-color: green;">YES   </td><td style="background-color: green;">YES         </td></tr>
<tr style="height:2em;"><td style="background-color: green;">YES   </td><td style="background-color: green;">YES     </td><td style="background-color: red;"  >NO    </td><td style="background-color: gray"; >NA   </td><td style="background-color: red;"  >NO     </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO          </td></tr>
<tr style="height:2em;"><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO      </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO   </td><td style="background-color: gray"; >NA     </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO          </td></tr>
<tr style="height:2em;"><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO      </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO   </td><td style="background-color: red;"  >NO     </td><td style="background-color: gray"; >NA    </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO          </td></tr>
<tr style="height:2em;"><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO      </td><td style="background-color: green;">YES   </td><td style="background-color: red;"  >NO   </td><td style="background-color: red;"  >NO     </td><td style="background-color: red;"  >NO    </td><td style="background-color: gray"; >NA    </td><td style="background-color: red;"  >NO          </td></tr>
<tr style="height:2em;"><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO      </td><td style="background-color: green;">YES   </td><td style="background-color: red;"  >NO   </td><td style="background-color: red;"  >NO     </td><td style="background-color: red;"  >NO    </td><td style="background-color: red;"  >NO    </td><td style="background-color: gray"; >NA          </td></tr>
</table>
</td>  
</tr>
</table>

## Publishing

*When publishing for a preview, it is assumed the user has authorized access to artifacts the preview is being generated for. The recommended settings for a preview function are to have the CUI Dissemination Controls of "NOFORN" and "FEDCON" along with a Required Indicator with statements indicating the document is a preview and not for release.*

*When publishing a document for release, the document publish function will exclude the artifacts whose CUI Limited Dissemination Control Indicator is not compatible with the CUI Limited Dissemination Control Indicator Set specified for the publish.*

### Document Publish CUI Limited Dissemination Control Indicator Compatible Set

When the &lt;cui-limited-dissemination-control-indicator-set&gt; specified in the &lt;cui-limited-dissemination-control-specification&gt; of the publishing template are compatible, OSEE shall use that &lt;cui-limited-dissemination-control-indicator-set&gt; for the document publish.

### Document Publish CUI Limited Dissemination Control Indicator Incompatible Set

When the &lt;cui-limited-dissemination-control-indicator-set&gt; specified in the &lt;cui-limited-dissemination-control-specification&gt; of the publishing template are incompatible, OSEE shall report the error and not publish the document.

### Document Publish "REL TO" Or "DISPLAY ONLY" Trigraph Country Code Indicator Set

When the &lt;cui-limited-dissemination-control-indicator-set&gt; specified in the &lt;cui-limited-dissemination-control-specification&gt; of the publishing template contain "REL TO" or "DISPLAY ONLY", OSEE shall use the &lt;trigraph-country-code-indicator-set&gt; from the &lt;cui-limited-dissemination-control-specification&gt; for the publish.

### Artifact Exclusion For Incompatible CUI Limited Dissemination Control

OSEE shall exclude artifacts from the publish that have a &lt;cui-limited-dissemination-control-indicator&gt; that is not compatible with the &lt;cui-limited-dissemination-control-indicator-set&gt; for the publish.

### Artifact Exclusion With "REL TO" Or "DISPLAY ONLY"

When the &lt;cui-limited-dissemination-control-indicator-set&gt; for the publish contains "REL TO" or "DISPLAY ONLY", OSEE shall exclude artifacts from the publish when all members of the artifact's &lt;trigraph-country-code-indicator-set&gt; are not contained in the &lt;trigraph-country-code-indicator-set&gt; for the publish.

### Excluded Artifacts

OSEE shall include a place holder for excluded artifacts in the published document with the OSEE artifact identifier and the exclusion reason.

# Data Rights Manager Operations and Endpoint

*The server side publisher will load all of the ArtifactReadable objects for the publish and can pass them to the Data Rights Manager via the operations interface. This will save the Data Rights Manager from having to reload the artifact from the database. The client side publisher will load all of the Artifact objects for the publish. The client side publisher will then send a list of the artifacts identifiers to the Data Rights Manager which will then have to reload all of the artifacts.*

### Data Rights Manager Operations


#### deleteCache()

Empties all caches held by the Data Rights Manager.

#### getDataRights(DataRightsRequest dataRightsRequest)

The DataRightsRequest object contains the following:

* format-indicator
* document-indicator
* foreign-dissemination
* A list of the ArtifactReadable objects for the publish in the publishing order

Returns an object with the following:

* General Data
* List of Sections

The General Data contains the following:

* CUI Header
* Hash Table of all statements (title,header,footer) by unique arbitrary identifier
* Unique arbitrary identifiers of the required title page statements.

Each Section on the Section List contains the following:

* The unique arbitrary identifiers of the required headers for the section.
* The unique arbitrary identifiers of the required footers for the section.
* List of artifacts in the section.

### Data Rights Manager Endpoint

Only members of the publishing group will be allowed to make calls to the Data Rights Manager Endpoint.

#### deleteCache()

An HTTP DELETE at the URL ../datarights/deleteCache.

Calls the operations deleteChache() method.

### getDataRights(DataRightsRequest dataRightsRequest)

An HTTP POST at the URL ../datarights/load

The DataRightsRequest object contains the following:

* format-indicator
* document-indicator
* foreign-dissemination
* A list of the ArtifactId objects for the artifact to be published in the publishing order


Loads all of the artifacts that were specified by identifier from the specified branch and then calls the operations getDataRights() method returning it's result.





# JSON Schemas

## Common

### Document Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/document-indicator",
   "title"       : "Document Indicator",
   "description" : "A unique user assigned name or identifier for a document to be published. The document indicator is used to associate Data Rights Configurations with Publishing Templates for a document. The document indicator, \"*\" is used to indicate the Publishing Template or Data Rights Configuration is for the default case.",
   "type"        : "string"
}
```

### Format Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/format-indicator",
   "title"       : "Format Indicator",
   "description" : "An enumeration of the supported format types.",
   "type"        : "string",
   "enum"        :
   [
      "markdown",
      "text",
      "xhtml",
      "word-ml"
   ]
}
```

### Statement Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/statement-indicator",
   "title"       : "Statement Indicator",
   "description" : "A unique user assigned name or identifier for a Required Indicator statement.",
   "type"        : "string"
}
```

### Unique Integer List

```
{
   "$id"         : "file://eclipse.org/json-schemas/unique-integer-list",
   "title"       : "Unique Integer List",
   "description" : "This is an array of unique integers.",
   "type"        : "array",
   "items"       :
   {
      "type"     : "integer"
   },
   "uniqueItems" : true
}
```

## CUI

### CUI Category Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-category-indicator",
   "title"       : "CUI - CUI Category Indicator",
   "description" : "An enumeration of the allowed CUI Category Indicators.",
   "type"        : "string",
   "enum"        :
   [
      "CTI",
      "DCNI",
      "DCRIT",
      "EXPT",
      "EXPTR",
      "MFC",
      "NNPI",
      "PROPIN",
      "PROCURE"
   ]
}
```

#### CUI Category Indicator CUI Type Indicator Pair

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-category-indicator-cui-type-indicator-pair",
   "title"       : "CUI - CUI Category Indicator CUI Type Indicator Pair",
   "description" : "A CUI Category Indicator paired with a CUI Type Indicator",
   "type"        : "object",
   "properties"  :
   {
      "cui-category-indicator":
      {
         "$ref" : "../cui-category-indicator"
      }
      
      "cui-type-indicator":
      {
         "$ref" : "../cui-type-indicator"
      }
   }
}
```

#### CUI Category Indicator CUI Type Indicator Pair Set

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-category-indicator-cui-type-indicator-pair-set",
   "title"       : "CUI - CUI Category Indicator CUI Type Indicator Pair Set",
   "description" : "This is an array of CUI Category Indicator CUI Type Indicator Pairs.
   "type"        : "array",
   "items"       :
   {
       "$ref"    : "../cui-category-indicator-cui-type-indicator-pair"
   },
   "uniqueItems" : true
}
```


### CUI Limited Dissemination Control Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-limited-dissemination-control-indicator",
   "title"       : "CUI - CUI Limited Dissemination Control Indicator",
   "description" : "An enumeration of the allowed CUI Limited Dissemination Control Indicators.",
   "type"        : "string",
   "enum"        :
   [
      "NOFORN",
      "FED ONLY",
      "FEDCON",
      "NOCON",
      "DL ONLY",
      "RELIDO",
      "REL TO",
      "DISPLAY ONLY"
   ]
}
```

### CUI Limited Dissemination Control Indicator Set

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-limited-dissemination-control-indicator-set",
   "title"       : "CUI - CUI Limited Dissemination Control Indicator Set",
   "description" : "This is an array of CUI Limited Dissemination Control Indicators.",
   "type"        : "array",
   "items"       :
   {
      "$ref"     : "../cui-limited-dissemination-control-indicator"
   },
   "uniqueItems" : true
}

```


### CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair,
   "title"       : "CUI - CUI Limited Dissemination Control Indicator Set and Trigraph Country Code Indicator Set Pair.",
   "description" : "CUI Limited Dissemination Control Indicator Set paired with an optional Trigraph Country Code Indicator Set.",
   "type"        : "object",
   "properties":
   {
      "cui-limited-dissemination-control-indicator-set" :
      {
         "$ref" : "../cui-limited-dissemination-control-indicator-set"
      },
                  
      "trigraph-country-code-indicator-set":
      {
         "$ref" : "../trigraph-country-code-indicator-set"
      }
   },
   "required"   : [ "cui-limited-dissemination-control-indicator-set" ]
   "additionalProperties" : false
},

```



### CUI Type Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/cui-type-indicator",
   "title"       : "CUI - CUI Type Indicator",
   "description" : "An enumeration of the allowed CUI Category Type Indicators.",
   "type"        : "string",
   "enum"        :
   [
      "BASIC",
      "SPECIFIED"
   ]
}
```


### Trigraph Country Code Indicator


```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/trigraph-country-code-indicator",
   "title"       : "CUI - Trigraph Country Code Indicator",
   "description" : "An enumeration of the allowed Trigraph Country Code Indicators.",
   "type"        : "string",
   "enum"        :
   [
      "ABW",  "AFG",  "AGO",  "AIA",  "ALB",  "AND",  "ARE",  "ARG",  "ARM",  "ASM",
      "ATA",  "ATF",  "ATG",  "AUS",  "AUT",  "AX1",  "AX2",  "AX3",  "AZE",  "BDI",
      "BEL",  "BEN",  "BES",  "BFA",  "BGD",  "BGR",  "BHR",  "BHS",  "BIH",  "BLM",
      "BLR",  "BLZ",  "BMU",  "BOL",  "BRA",  "BRB",  "BRN",  "BTN",  "BVT",  "BWA",
      "CAF",  "CAN",  "CCK",  "CHE",  "CHL",  "CHN",  "CIV",  "CMR",  "COD",  "COG",
      "COK",  "COL",  "COM",  "CPT",  "CPV",  "CRI",  "CUB",  "CUW",  "CXR",  "CYM",
      "CYP",  "CZE",  "DEU",  "DGA",  "DJI",  "DMA",  "DNK",  "DOM",  "DZA",  "ECU",
      "EGY",  "ERI",  "ESH",  "ESP",  "EST",  "ETH",  "FIN",  "FJI",  "FLK",  "FRA",
      "FRO",  "FSM",  "GAB",  "GBR",  "GEO",  "GGY",  "GHA",  "GIB",  "GIN",  "GLP",
      "GMB",  "GNB",  "GNQ",  "GRC",  "GRD",  "GRL",  "GTM",  "GUF",  "GUM",  "GUY",
      "HKG",  "HMD",  "HND",  "HRV",  "HTI",  "HUN",  "IDN",  "IMN",  "IND",  "IOT",
      "IRL",  "IRN",  "IRQ",  "ISL",  "ISR",  "ITA",  "JAM",  "JEY",  "JOR",  "JPN",
      "KAZ",  "KEN",  "KGZ",  "KHM",  "KIR",  "KNA",  "KOR",  "KWT",  "LAO",  "LBN",
      "LBR",  "LBY",  "LCA",  "LIE",  "LKA",  "LSO",  "LTU",  "LUX",  "LVA",  "MAC",
      "MAF",  "MAR",  "MCO",  "MDA",  "MDG",  "MDV",  "MEX",  "MHL",  "MKD",  "MLI",
      "MLT",  "MMR",  "MNE",  "MNG",  "MNP",  "MOZ",  "MRT",  "MSR",  "MTQ",  "MUS",
      "MWI",  "MYS",  "MYT",  "NAM",  "NCL",  "NER",  "NFK",  "NGA",  "NIC",  "NIU",
      "NLD",  "NOR",  "NPL",  "NRU",  "NZL",  "OMN",  "PAK",  "PAN",  "PCN",  "PER",
      "PHL",  "PLW",  "PNG",  "POL",  "PRI",  "PRK",  "PRT",  "PRY",  "PYF",  "QAT",
      "REU",  "ROU",  "RUS",  "RWA",  "SAU",  "SDN",  "SEN",  "SGP",  "SGS",  "SHN",
      "SLB",  "SLE",  "SLV",  "SMR",  "SOM",  "SPM",  "SRB",  "SSD",  "STP",  "SUR",
      "SVK",  "SVN",  "SWE",  "SWZ",  "SXM",  "SYC",  "SYR",  "TCA",  "TCD",  "TGO",
      "THA",  "TJK",  "TKL",  "TKM",  "TLS",  "TON",  "TTO",  "TUN",  "TUR",  "TUV",
      "TWN",  "TZA",  "U.S. VIR",     "UGA",  "UKR",  "URY",  "USA",  "UZB",  "VAT",
      "VAT",  "VCT",  "VEN",  "VGB",  "VNM",  "VUT",  "WLF",  "WSM",  "XAC",  "XAZ",
      "XBI",  "XBK",  "XCR",  "XCS",  "XCY",  "XEU",  "XGL",  "XGZ",  "XHO",  "XJA",
      "XJM",  "XJN",  "XJV",  "XKM",  "XKN",  "XKR",  "XKS",  "XMW",  "XNV",  "XPL",
      "XPR",  "XQZ",  "XSP",  "XSV",  "XTR",  "XWB",  "XWK",  "XXD",  "YEM",  "ZAF",
      "ZMB",  "ZWE"
   ]
}
```

### Trigraph Country Code Indicator Set

```
{
   "$id"         : "file://eclipse.org/json-schemas/cui/trigraph-country-code-indicator-set",
   "title"       : "CUI - Trigraph Country Code Indicator Set",
   "description" : "This is either the string \"*\" or an array of Trigraph Country Code Indicators.",
   "oneOf"       :
   [
      {
         "type"        : "string",
         "pattern"     : "^\*$"
      },
      {
         "type"        : "array",
         "items"       :
         {
            "$ref"     : "../trigraph-country-code-indicator"
         },
         "uniqueItems" : true
      }
   ]
}
```

## Required Indicators


### Required Indicator Frequency Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/required-indicator/required-indicator-frequency-indicator",
   "title"       : "Required Indicator - Required Indicator Frequency Indicator",
   "description" : "An enumeration of the frequency requirements for Required Indicators.",
   "type"        : "string",
   "enum"        : 
   [ 
      "FOOTER_CONTAINING",
      "FOOTER_EVERY",
      "HEADER_CONTIANING",
      "HEADER_EVERY",
      "TITLE"
   ]
}
```


### Required Indicator Frequency Indicator Set

```
{
   "$id" : "file://eclipse.org/json-schemas/required-indicator/required-indicator-frequency-indicator-set",
   "title" : "Required Indicator - Required Indicator Frequency Indicator Set",
   "type": "array",
   "items": 
   {
      "$ref" : "../required-indicator-frequency-indicator"
   },
   "uniqueItems": true,
   "allOf":
   [
      {
         "not":
         {
            "allOf":
            [
               {
                  "contains": { "const": "FOOTER_CONTAINING" }
               },
               {
                  "contains": { "const": "FOOTER_EVERY" }
               }
            ]
         }
      },
      {
         "not":
         {
            "allOf":
            [
               {
                  "contains": { "const": "HEADER_CONTAINING" }
               },
               {
                  "contains": { "const": "HEADER_EVERY" }
               }
            ]
         }
      }
   ]
}

```

### Required Indicator Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/required-indicator/required-indicator-indicator",
   "title"       : "Required Indicator - Required Indicator Indicator",
   "description" : "An enumeration of the supported Required Indicators.",
   "type"        : "string",
   "enum"        :
   [
      "Restricted Rights",
      "Government Purpose Rights",
      "Propriety",
      "Limited Rights",
      "Unlimited Rights",
      "Export Controlled ITAR",
   ]
}
```

### Required Indicator Indicator Set

```
{
   "$id"         : "file://eclipse.org/json-schemas/required-indicator/required-indicator-indicator-set",
   "title"       : "Required Indicator - Required Indicator Indicator Set",
   "description" : "An array of Required Indicators.",
   "type"        : "array",
   "items"       :
   {
      "$ref"     : "../required-indicator-indicator"
   },
   "uniqueItems" : true
}
```

### Statement Location Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/required-indicator/statement-location-indicator",
   "title"       : "Required Indicator - Statement Location Indicator",
   "description" : "An enumeration of the locations for Required Indicators.",
   "type"        : "string",
   "enum"        : 
   [ 
      "FOOTER",
      "HEADER",
      "TITLE"
   ]
}
```


## Data Rights Manager

#### Artifact Identifier

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/artifact-identifier",
   "title"       : "Data Rights Manager - Artifact Identifier",
   "description" : "An integer identifier used by the Data Rights Manager for OSEE Artifacts.",
   "type"        : "integer"
}
```

#### Artifact Identifier List

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/artifact-identifier-list",
   "title"       : "Data Rights Manager - Artifact Identifier List",
   "description" : "This is an ordered array of Artifact Identifiers.",
   "type"        : "array",
   "items"       :
   {
      "$ref"     : "../artifact-identifier"
   }
}
```


### Artifact Proxy

```
{
   "$id" : "file://eclipse.org/json-schemas/data-rights-manager/artifact-proxy",
   "title" : "Data Rights Manager - Artifact Proxy",
   "description" : "Represents an OSEE Artifact being published.",
   "type"        : "object",
   "properties"  :
   {
      "artifact-identifier":
      {
         "description": "The OSEE Artifact Identifier.",
         "type" : "integer"
      },
      
      "required-indicator-set-identifier":
      {
         "description" : "The unique integer identifier to look up the Required Indicator Set for the Artifact.",
         "type"        : "integer"
      },
      
      "cui-category-type-pair-indicator-set-identifier":
      {
         "description" : "The unique integer identifier to look up the CUI Category Type Pair Set for the Artifact.",
         "type" : "integer"
      },
      
      "cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair-identifier":
      {
         "description" : "The unique integer identifier to look up the CUI Limited Dissemination Control Indicator Set with optional Trigraph Country Code Indicator Set Pair.",
         "type" : "integer"
      },
   },
   "required" : [ "artifact-identifier" ],
   "additionalProperties" : false
}   
```

### Artifact Proxy List

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/artifact-proxy-list",
   "title"       : "Data Rights Manager - Artifact Proxy List",
   "description" : "This is an ordered array of Artifact Proxies.",
   "type"        : "array",
   "items"       :
   {
      "$ref"     : "../artifact-proxy"
   },
   "uniqueItems" : true
}
```






### CUI Category Indicator CUI Type Indicator Pair Set Map Entry

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/cui-category-indicator-cui-type-indicator-pair-set-map-entry",
   "title"       : "Data Rights Manager - CUI Category Indicator CUI Type Indicator Pair Set Map Entry",
   "description" : "A unique integer identifier and it's associated CUI Category Indicator CUI Type Indicator Pair Set".,
   "type"        : "object",
   "properties":
   {
      "identifier" :
      {
         "type" : "integer"
      },
                  
      "cui-category-indicator-cui-type-indicator-pair-set":
      {
         "$ref" : "../cui-category-indicator-cui-type-indicator-pair-set"
      }
   }
},
```

### CUI Category Indicator CUI Type Indicator Pair Set Map

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/cui-category-indicator-cui-type-indicator-pair-set-map",
   "title"       : "Data Rights Manager - CUI Category Indicator CUI Type Indicator Pair Set Map",
   "description" : "A map of CUI Category Indicator CUI Type Indicator Pair Sets by an unique integer identifier.",
   "type"        : "array",
   "items"       :
   {
      "$ref: : "../cui-category-indicator-cui-type-indicator-pair-set-map-entry"
   },
   "uniqueItems" : true
},
```




### CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair Map Entry

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair-map-entry",
   "title"       : "Data Rights Manager - CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair Map.",
   "description" : "A pair with a unique integer identifier and a CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair.",
   "type"        : "object",
   "properties":
   {
      "identifier" :
      {
         "type" : "integer"
      },
                  
      "cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair":
      {
         "$ref" : "../cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair"
      }
   }}
```

### CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair Map

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair-map",
   "title"       : "Data Rights Manager - CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair Map",
   "description" : "A map of CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pairs.",
   "type"        : "array",
   "items"       :
   {
      "$ref: : "../cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair-map-entry"
   },
   "uniqueItems" : true
}
```

#### CUI Limited Dissemination Control Specification

```
{
   "$id"         : "file://eclipse.org/json-schemas/template-manager/cui-limited-dissemination-control-specification",
   "title"       : "CUI Limited Dissemination Control Specification",
   "description" : "This is either the string \"*\" or a CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair.",
   "anyOf"       :
   [
      {
         "type"        : "string",
         "pattern"     : "^\*$"
      },
      {
         "$ref"        : "../cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair"
      }
   ]
}
```


#### Data Rights Request JSON Schema

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights/request",
   "title"       : "Data Rights - Request",
   "description" : "The structure of the data required to make a request for a data rights analysis from the Data Rights Manager.",
   "type"        : "object",
   "properties"  :
   {
      "format-indicator":
      {
         "description" : "The title, head, and footer statements in the response will be in the format specified by the format-indicator.",
         "$ref"        : "../format-indicator",
      },
      
      "publishing-template-request":
      {
         "description" : "",
         "$ref"        : "../../template-manager/publishing-template-request",
      },
      
      "artifact-proxy-list":
      {
         "description" : "An ordered list of artifact proxies. Each proxy has the OSEE Artifact Identifier and the unique integer identiers to lookup the data rights configuration for the Artifact from the maps in the request.",
         "$ref"        : "../artifact-proxy-list"
      }
   },
   "additionalProperties" : false
}
```

### Data Rights Response JSON Schema

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights/response",
   "title"       : "Data Rights - Response",
   "description" : "The structure of the data returned by the Data Rights Manager in response to a request.",
   "type"        : "object",
   "properties"  :
   {
      "format-indicator"
      {
         "description" : "This indicates the format of the statements that was requested.",
         "$ref"        : "../format-indicator"
      },
   
      "cui-header"
      {
         "description" : "The CUI Header statement for the publish",
         "type"        : "string"
      },
   
      "statement-map"
      {
         "description" : "An array of title, header, and footer statements all associated with a unique integer. The statements are referenced by their unique associated identifiers. Statements in the array are in the format indicated by the format-indicator.",
         "type"        : "array",
         "items"       :
         [
            {
               "description" : "Each array entry is an association pair between a unique integer identifier and a statement.",
               "type"        : "object",
               "properties"  :
               {
                  "identifier":
                  {
                     "description" : "A unique integer identifier for the associated statement.",
                     "type"        : "integer"
                  },
                  
                  "statement":
                  {
                     "description" : "A title, header, or footer statement in the format indicated by the format-indicator of the statement-for-response. The format of the statement will be in the requested format if available; otherwise, the statement will be in the default text format.",
                     "$ref"        : "../statement-for-response"
                  }
               }
            }
         ]
      }
   },
            
   "title-page-statement-list"
   {
      "description" : "A list of the title page statements for the publish by their identifiers in the statement-map.",
      "$ref"        : "../../unique-integer-list"
   },
   
   "every-page-header-statement-list"
   {
      "description" : "A list of the header statements that are required on every page for the publish by their identifiers in the statement-map.",
      "$ref"        : "../../unique-integer-list"
   },
   
   "every-page-footer-statement-list"
   {
      "description" : "A list of the footer statements that are required on every page for the publish by their identifiers in the statement-map.",
      "$ref"        : "../../unique-integer-list"
   },
   
   "section-list"
   {
      "description" : "The artifacts for the publish are organized into sequential groups where all of the artifacts in the group have identical data rights. The section enumerates the header and footer statements required for the section and a list of the artifact identifiers in publishing order for the artifacts in the section.",
      "$ref"        : "../data-rights/section-list"
   },
   
   "additionalProperties" : false
}

```












### Required Indicator Indicator Set Map Entry

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/required-indicator-indicator-set-map-entry",
   "title"       : "Data Rights Manager - Required Indicator Indicator Set Map Entry",
   "description" : "A pair with a unique integer identifier and a Required Indicator Set.",
   "type"        : "object",
   "properties":
   {
      "identifier" :
      {
         "type" : "integer"
      },
                  
      "required-indicator-set":
      {
         "$ref" : "../required-indicator-indicator-set"
      }
   }
},
```

### Required Indicator Set Map

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/required-indicator-indicator-set-map",
   "title"       : "Data Rights Manager - Required Indicator Indicator Set Map",
   "description" : "A map of Required Indicator Sets by an unique integer identifier.",
   "type"        : "array",
   "items"       :
   {
      "$ref" : "../required-indicator-indicator-set-map-entry"
   },
   "uniqueItems" : true
},
```






#### Statement For Response

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/statement-for-response",
   "title"       : "Data Rights Manager - Statement For Response",
   "description" : "An array of the statement for a title page, header, or footer in various formats.",
   "type"       : "object",
   "properties" :
   {
      "format-indicator":
      {
         "description" : "The format-indicator for the format of the statement in the \"formatted-text\" property.",
         "$ref"        : "../format-indicators"
      },
               
      "formatted-text":
      {
         "description": "The statement in the format indicated by the property \"format-indicator\".",
         "type": "string"
      }
   },
   "additionalProperties" : false
}
```

### Section

```
{
   "$id"         : "file://eclipse.org/json-schemas/data-rights-manager/section",
   "title"       : "Data Rights Manager - Section",
   "description" : "A section is a run of Artifacts for a publish that have identical data rights. The section specifies the required header and footer statements for the section and the list of Artifacts in the section.",
   "type"        : "object",
   "properties"  :
   {
      "header-statement-list"
      {
         "description" : "The header statements are stored in a map keyed with integer identifiers. The header statement list contains the identifiers of the header statements required for the section.",
         "$ref"        : "../unique-integer-list"
      },
   
      "footer-statement-list"
      {
         "description" : "The footer statements are stored in a map keyed with integer identifiers. The footer statement list contains the identifiers of the footer statements required for the section.",
         "$ref"        : "../unique-integer-list"
      },
   
      "artifacts-list"
      {
         "description" : "This is a list of the artifact-identifier objects for the Artifacts in the section. The list is arranged in the publishing order of the artifacts.",
         "$ref"        : "../artifact-identifier-list"
      }
   }
}
```





## Publishing Template Manager

### Data Rights Configuration Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-template-manager/data-rights-configuration-indicator",
   "title"       : "Publishing Template Manager - Data Rights Configuration Indicator",
   "description" : "A unique user assigned name for a Data Rights Configuration Artifact.",
   "type"        : "string"
}
```

### Publishing Template Data Rights

```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-templates-manager/publishing-template-data-rights",
   "title"       : "Publishing Template Manager - Publishing Template Data Rights",
   "description" : "Specifies the data rights configuration to use for a publish.",
   "type"        : "object",
   "properties"  :
   {
      "data-rights-configuration-indicator" :
      {
         "description" : "This field is populated from the Data Rights Configuration Attribute of the Publishing Template Artifact must match the name of the Data Rights Configuration Artifact.",
         "$ref"        : "../../data-rights-configuration-indicator"
      },
   
      "cui-category-indicator-cui-type-indicator-pair-set":
      {
         "description" : "This field is populated with the &lt;cui-category-indicator-cui-type-indicator-pair-set&gt; from the CUI Category CUI Type Attribute of the Data Rights Configuration Artifact named by the &lt;data-rights-configuration-indicator&gt;",
         "$ref"        : "../../cui/cui-category-indicator-cui-type-indicator-pair-set"
      }
      
      
      "cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair" :
      {
         "description" : "The field is populated with the $lt;cui-limited-dissemination-control-indicator-set-trigraph-country-code-indicator-set-pair&gt; from the CUI Limited Dissemination Control Attribute and the Trigraph Country Codes Attribute of the Data Rights Configuration Artifact named by the &lt;data-rights-configuration-indicator&gt;.",
         "$ref"        : "../../cui/cui-limited-dissemination-control-specification"
      },
      
      "required-indicator-definition-indicator-set" :
      {
         "description" : "The field is populated with the &lt;required-indicator-definition-indicator-set&gt; from the Required Indicators Attribute of the Data Rights Configuration Artifact named by the &lt;data-rights-configuration-indicator&gt;.",
         "$ref"        : "../../required-indicator/required-indicator-definition-indicator-set"
      }
   },
   "required"    :
   [
      "data-rights-configuration-indicator"
   ],
   "additionalProperties" : false
}
```



### Publishing Template Request

```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-template-manager/publishing-template-request",   
   "title"       : "Publishing Template Manager - Publishing Template Request",
   "description" : "This bean contains the parameters to request a publishing template.",  
   "type"        : "object",
   "properties"  :
   {
      "byOptions":
      {
         "description" : "When true, the request will include just the template identifier. When false, the request must include the option, presentationType, publishingArtifactTypeName, and rendererId.",
         "type"        : "boolean"
      },
      
      "option":
      {
         "description" : "An additional string that may be provided for template selection.",
         "type"        : "string"
      },
      
      "presentationType":
      {
         "description" : "A representation of the PresentationType enumeration that describes the type of presentation that is being made to the user.",
         "type"        : "string"
      },
      
      "publishArtifactTypeName":
      {
         "description" : "The OSEE artifact type name for the primary artifact being published.",
         "type"        : "string"
      },
      
      "rendererId":
      {
         "description" : "The identifier of the renderer making the publishing request.",
         "type"        : "string"
      },
  
               
      "templateId":
      {
         "description" : "A unique identifier for a publishing template.",
         "type"        : "string"
      }
   },
   "if" :
   {
      "properties": { "byOptions": { "const": true } }
   },
   "then":
   {
      "required" : [ "byOptions", "rendererId", "presentationType" ],
      "not" :
      {
         "anyOf" : [
                      { "required" : [ "templateId" ] }
                   ]
      }
   },
   "else" :
   {
      "required" : [ "byOptions", "templateId" ],
      "not" : 
      {
        "anyOf" :  [
                      { "required" : [ "rendererId"              ] }, 
                      { "required" : [ "option"                  ] },
                      { "required" : [ "presentationType"        ] },
                      { "required" : [ "publishArtifactTypeName" ] }
                   ] 
      }
   },
   "additionalProperties" : false
}
```

### Required Indicator Definition


```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-template-manager/required-indicator-definition",
   "title"       : "Publishing Template Manger - Required Indicator Definition",
   "description" : "The Required Indicator statements for title page and the headers and footers on every page.",
   "type"        : "object",
   "properties"  :
   {
      "required-indicator-definition-indicator":
      {
         "description" : "This field is populated from the Name attribute of the Required Indicator Configuration Artifact.",
         "$ref"        : "../required-indicator-definition-indicator"
      },
      
      "title-page-statement-indicator":
      {
         "description" : "This field is populated with the &lt;statement-indicator&gt; from the Title Statement Attribute of the Required Indicator Configuration Artifact named by &lt;required-indicator-definition-indicator&gt;",
         "$ref"        : "../../statement-indicator"
      },

      "header-statement-indicator":
      {
         "description" : "This field is populated with the &lt;statement-indicator&gt; from the Header Statement Attribute of the Required Indicator Configuration Artifact named by &lt;required-indicator-definition-indicator&gt;",
         "$ref"        : "../../statements-indicator"
      },

      "footer":
      {
         "description" : "This field is populated with the &lt;statement-indicator&gt; from the Footer Statement Attribute of the Required Indicator Configuration Artifact named by &lt;required-indicator-definition-indicator&gt;",
         "$ref"        : "../../statements-indicator"
      },
      
      "required-indicator-frequency-indicator-set":
      {
         "description" : "This field is populated with the &lt;required-frequency-indicator-set&gt; from the Required Indicator Frequency Attribute of the Required Indicator Configuration Artifact named by &lt;required-indicator-definition-indicator&gt;.",
         "$ref"        : "../../required-indicator/required-indicator-frequency-indicator-set"
      }

   },
   "required": 
   [
      "required-indicator-definition-indicator",
      "required-indicator-frequency-indicator-set"
   ],
   "additionalProperties" : false,
   "minItems"             : 3
}
```

### Required Indicator Definition Indicator

```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-template-manager/required-indicator-definition-indicator",
   "title"       : "Publishing Template Manager - Required Indicator Definition Indicator",
   "description" : "A unique user assigned name or identifier for a Required Indicator definition.",
   "type"        : "string"
}
```

### Required Indicator Definition Indicator Set

```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-template-manager/required-indicator-indicator-set",
   "title"       : "Publishing Template Manager - Required Indicator Definition Indicator Set",
   "description" : "An array of Required Indicators.",
   "type"        : "array",
   "items"       :
   {
      "$ref"     : "../required-indicator-indicator"
   },
   "uniqueItems" : true
}
```

### Statement

```
{
   "$id"        : "file://eclipse.org/json-schemas/publishing-template-manager/statement",
   "title"      : "Publishing Template Manger - Statement",
   "type"       : "object",
   "properties" :
   {
      "statement-indicator":
      {
         "$ref" : "../../statement-indicator"
      },
      
      "statement-body":
      {
         "$ref" : "../statement-body"
      }
  },
  "required": 
  [
     "statement-indicator",
     "statement-body"
  ],
  "additionalProperties" : false
}
```


### Statement Body

```
{
   "$id"         : "file://eclipse.org/json-schemas/publishing-template-manager/statement-body",
   "title"       : "Publishing Template Manager - Statement Body",
   "description" : "The statement in various formats for a required indicator. Each Configuration Statement is stored in its on Artifact. The Artifact Branch/Artifact Id are used to identify the Configuration Statement.",
   "type"        : "array",
   "items"       :
   {
      "type"       : "object",
      "properties" :
      {
         "format-indicator":
         {
            "description" : "The format-indicator for the format of the statement in the \"formatted-text\" property.",
            "$ref"        : "../../format-indicator"
         },
               
         "formatted-text":
         {
            "description": "The statement in format indicated by the property \"format-indicator\".",
            "type": "string"
         }
      },
      "required"             : [ "format-indicator", "formatted-text" ],
      "additionalProperties" : false
   },
   "contains":
   {
      "type"       : "object",
      "properties" :
      {
         "format":
         {
            "pattern" : "^text$"
         },
         "formatted-text":
         {
            "type": "string"
         }
      }
   }
}
```





