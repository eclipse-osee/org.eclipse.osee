---

# Data Rights

Copyright (c) 2023 Boeing

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0

Contributors: Boeing - initial API and implementation

## Indicating

### CUI Artifact Indicating

*The primary unit of data storage in OSEE is the Artifact. Each artifact has a name and a collection of attributes to hold the artifact’s data.*

OSEE shall allow the user to indicate that an OSEE Artifact contains CUI.

### CUI Category
  
*A CUI Category or Subcategory are the exclusive designations for identifying CUI that a law, regulation, or Government-wide policy requires or permits agencies to apply safegauarding or dissemination controls to.*

OSEE shall allow the user to indicate the CUI Category or Subcategory for OSEE Artifacts containing CUI.

### CUI Type
  
*CUI Categories and Subcategories are either “Basic” or “Specified”. “Specified” categories and subcategories have handling requirements that go beyond the “Basic” CUI handling requirements. Only some data for a CUI Category or Subcategory may be "Specified".*

OSEE shall allow the user to indicate the CUI type as “Basic” or “Specified” for OSEE Artifacts containing CUI.

### CUI Dissemination Controls
    
*In addition to a Required Indicator, the dissemination of data stored in an OSEE Artifact may be controlled.*

OSEE shall allow the user to indicate the dissemination controls for OSEE Artifacts containing CUI.

### Rational
   
*In addition to the CUI indicators and Required Indicators, it is also important to record the rationale for the assigned indicators.*

OSEE shall allow the user to record the rationale for the OSEE Artifact’s assigned CUI and Required indicators.

### Required Indicators per Authorities
    
*The Authority specifying the CUI category may also require additional data indicators. Additional indicators may also be required by corporate policy and or contract. The following are examples of Required Indicators:*
-	*Non-Deliverable*
-	*Program Documentation Requirements*
-	*Government Rights*
-	*Export Control*

OSEE shall allow the user to enumerate all Required Indicators for an OSEE Artifact.

### Required Indicator Frequency
 
OSEE shall allow the user to indicate if a Required Indicator must be present on every page or only on containing pages.

### Required Indicator Title Statement
    
OSEE shall allow the user to provide a title page Required Indicator statement for each Required Indicator type on a per document basis.

### Required Indicator Header Statement
   
OSEE shall allow the user to provide a document header statement for each Required Indicator type on a per document basis.

### Required Indicator Footer Statement
 
OSEE shall allow the user to provide a document footer statement for each Required Indicator type on a per document basis.

#	Access

##	User Access

*OSEE is a database which may contain data for multiple programs with different data rights and different user access controls. The unit of access control in OSEE is the branch.*

OSEE shall only allow authorized users access to a branch and all of it’s sub-branches.

##	API User Access

*OSEE provides HTTP(S) API that allow external tools/browsers to access OSEE Data.*

OSEE shall not allow access to controlled data via an HTTP(S) API that a user does not have access to.

##	E-mail

### E-mail Encryption

*OSEE maybe configured to send E-mail notifications to users.*

All CUI data e-mailed by OSEE shall be contained in encrypted attachments.

### E-mail Markings

OSEE shall include CUI Banner markings on any e-mails containing CUI data.

#	Login Banner

### Login Banner
  
*Users must be notified of authorized use restrictions and requirements for access to the OSEE database.*

OSEE shall display the GSSFL Banner to users upon login.

#	Destruction

##	Data Destruction

*At some juncture it may become necessary to destroy CUI data stored within OSEE.*

OSEE shall provide a data destruction capability compliant with the electronic media data destruction requirements for CUI data.

#	Publishing

##	CUI Banner Marking

### Presence

*The CUI Banner must be included on all pages, this includes the title page.*

OSEE shall publish a CUI Banner at the top of each page for all documents containing CUI.

### Best Practices


OSEE publishing shall provide an option to include the CUI Banner Marking at the bottom of each page.

### Consistent
 
OSEE shall publish the same CUI Banner on all pages of a publish.

### Inclusive

OSEE shall publish a CUI Banner that is inclusive of all data within the publish.

### Presentation

OSEE shall publish the CUI Banner with the following formatting:
-	Bold
-	Capitalized
-	Black Text
-	Centered

### Format

OSEE shall formulate the CUI Banner according to the following EBNF:

    <cui-banner> ::= <control> [ <category-list> ] [ <dissemination-list> ]
    <control> ::= “CUI” | “CONTROLLED”
    <category-list> ::= “//” <category-list-contents>
    <category-list-contents> ::= <category> { “/” <category> }
    <category> ::= <basic-category> | <specified-category>
    <basic-category> ::= <category-indicator>
    <specified-category> ::= “SP-“ <category-indicator>
    <dissemination-list> ::= “//” <dissemination-list-contents>
    <dissemination-list-contents> ::= <dissemination> | <dissemination> { “/” <dissemination> }
    <dissemination> :: = “NOFORN” | “FED ONLY” | “FEDCON” | “NOCON” | “DL ONLY” | <release-to>
    <release-to> ::= “REL TO USA” { <foreign-release-list> }
    <foreign-release-list> ::= “,” <foreign-indicator> { “,” <foreign-indicator> }

### Category List Contents

OSEE shall include the &lt;category-indicators&gt; for all OSEE Artifacts in the publish.

### Specified Category Indicators

OSEE shall prefix &lt;category-indicators&gt; for “Specified” CUI categories and subcategories with “SP-“ in the CUI Banner Marking.

### Category List Contents Specified And Basic Order

OSEE shall place all the &lt;specified-category&gt; indicators ahead of the &lt;basic-category&gt; indicators in the &lt;category-list-contents&gt;.

### Category List Contents Basic Indicators Order

OSEE shall sort the &lt;basic-category&gt; indicators in the &lt;category-list-contents&gt; in alphabetical order.

### Category List Contents Basic Indicators Order

OSEE shall sort the &lt;specified-category&gt; indicators in the &lt;category-list-contents&gt; in alphabetical order.

### Dissemination List

OSEE shall include the &lt;dissemination&gt; indicators for all OSEE Artifacts in the publish.

### Dissemination List Order

OSEE shall sort the &lt;dissemination&gt; indicators in the &lt;dissemination-list&gt; in alphabetical order.

### Foreign Release List USA First

OSEE shall include "USA" as the first &lt;foreign-indicator&gt; in the &lt;foreign-release-list&gt;.

### Foreign Release List Order

OSEE shall sort the &lt;foreign-indicator&gt; indicators for foreign nations in the &lt;foreign-release-list&gt; in alphabetical order.

##	CUI Designation Indicator Block

### CUI Designation Indicator Block

OSEE shall include a CUI Designation Indicator Block in each document containing CUI data.

##	CUI Designation Indicator Block Contents

OSEE shall include the following in the “CUI Designation Indicator Block”:

-	Designating Agency Identification
-	Branch or division
-	Point of contact
-	Contact information

##	Required Indicators

### Title Page Required Indicators

OSEE shall include a Required Indicator Title Page Statement for each type of Required Indication with a Title Page Statement contained within the document.

###	Every Page Header Required Indicators

OSEE shall include a Required Indicator Header Statement for each type of Every Page Required Indication contained within the document.

###	Only Page Header Required Indicators

OSEE shall include a Required Indicator Header Statement for each type of Only Page Required Indication contained on that page of the document.

###	Every Page Footer Required Indicators

OSEE shall include a Required Indicator Footer Statement for each type of Every Page Required Indication contained within the document.

### Only Page Footer Required Indicators

OSEE shall include a Required Indicator Footer Statement for each type of Only Page Required Indication contained on that page of the document.


## Temporary Files

### Temporary File Destruction

*Temporary files are files that are created by either the OSEE client or the OSEE server, not delivered to the user, and no longer needed at the completion of the publish.*

OSEE shall destroy all temporary files containing CUI at the completion of a publish in a manner compliant with the electronic media data destruction requirements for CUI data.

### Temporary File Access

*Temporary files are files that are created by either the OSEE client or the OSEE server, not delivered to the user, and no longer needed at the completion of the publish.*

OSEE shall create temporary files containing CUI with OS level access controls that only permit access by authorized users.

---

# Acronyms

<table border="1">
   <tr>
      <th>Acronym</th>
      <th>Definition</th>
   </tr>
   <tr>
      <td>CUI</td>
      <td>Controlled Unclassified Information</td>
   </tr>
   <tr>
      <td>OS</td>
      <td>Operating System</td>
   <tr>
      <td>OSEE</td>
      <td>Open System Engineering Environment</td>
   </tr>
</table>

---

# References


<table border="1">

<tr>
   <th>GUID</th>
   <th>Author</th>
   <th>Title</th>
   <th>Date/Version</th>
   <th>Site</th>
   <th>URL</th>
</tr>

<tr>
   <td><a id="f82c6533-ff04-40a3-9a38-0f5e348ec506">{f82c6533-ff04-40a3-9a38-0f5e348ec506}</a></td>
   <td>Patrick Viscuso</td>
   <td>Controlled Unclassified Information Destruction</td>
   <td>September 7, 2017</td>
   <td>National Archives</td>
   <td>https://www.archives.gov/files/cui/documents/destruction-20170906.pdf</td>
</tr>



<tr>
   <td><a id="0b9a9ced-ecb2-4518-b174-913e26ea4843">{0b9a9ced-ecb2-4518-b174-913e26ea4843}</a></td>
   <td></td>
   <td>CUI Marking Job AID</td>
   <td>October 18, 2021</td>
   <td>Defense Counterintelligence And Security Agency</td>
   <td>https://www.dcsa.mil/Portals/91/Documents/CTP/CUI/21-10-18%20CUI%20MARKING%20JOB%20AID%20FINAL.pdf</td>
</tr>

<tr>
   <td><a id="d3ae6f7d-a85f-475b-9087-5ef26a810cd5">{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}</a></td>
   <td>National Archives and Records Administration</td>
   <td>Marking Controlled Unclassified Information</td>
   <td>Version 1.1 - December 6, 2016</td>
   <td>National Archives</td>
   <td>https://www.archives.gov/files/cui/documents/20161206-cui-marking-handbook-v1-1-20190524.pdf</td>
</tr>

<tr>
   <td>54d395d5-a8d0-4dd5-bae5-52ca4b610fc5</td>
   <td></td>
   <td>CUI Registry: Limited Dissemination Controls</td>
   <td>October 17, 2022/Version</td>
   <td>National Archives</td>
   <td>https://www.archives.gov/cui/registry/limited-dissemination</td>
</tr>

<tr>
    <td><a id="042ea84c-5e52-4de3-87e1-53f23a7fa070">{042ea84c-5e52-4de3-87e1-53f23a7fa070}</a></td>
    <td></td>
    <td>32 C.F.R. § 2002.12 (2023)</td>
    <td>January 23, 2023</td>
    <td>Code of Federal Regulations</td>
    <td>https://www.ecfr.gov/current/title-32/subtitle-B/chapter-XX/part-2002/subpart-B/section-2002.12</td>
</tr>

<tr>
    <td><a id="d082460b-df40-450d-95ed-66bd06afee17">{d082460b-df40-450d-95ed-66bd06afee17}</a></td>
    <td></td>
    <td>32 C.F.R. § 2002.14 (2023)</td>
    <td>January 23, 2023</td>
    <td>Code of Federal Regulations</td>
    <td>https://www.ecfr.gov/current/title-32/subtitle-B/chapter-XX/part-2002/subpart-B/section-2002.14</td>
</tr>

<tr>
    <td><a id="d33a2e6a-3e09-4736-b2c0-771038777d55">{d33a2e6a-3e09-4736-b2c0-771038777d55}</a></td>
    <td></td>
    <td>32 C.F.R. § 2002.20 (2023)</td>
    <td>January 23, 2023</td>
    <td>Code of Federal Regulations</td>
    <td>https://www.ecfr.gov/current/title-32/subtitle-B/chapter-XX/part-2002/subpart-B/section-2002.20</td>
</tr>

</table>

