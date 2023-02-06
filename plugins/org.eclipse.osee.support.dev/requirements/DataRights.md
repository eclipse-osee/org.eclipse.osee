---

# Data Rights

Copyright (c) 2023 Boeing

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0

Contributors: Boeing - initial API and implementation

## Indicating

### CUI Artifact Indicating

>`GUID`: {ee03e0d9-dd2b-41de-ab71-f79e6d40fcbf}

>`SOURCE`: derived

*The primary unit of data storage in OSEE is the Artifact. Each artifact has a name and a collection of attributes to hold the artifact’s data.*

OSEE shall allow the user to indicate that an OSEE Artifact contains CUI.

### CUI Category

>`GUID`: {efb853d7-9beb-4d1b-91fe-57bdc2de1def}
    
>`SOURCE`: [{042ea84c-5e52-4de3-87e1-53f23a7fa070}](#042ea84c-5e52-4de3-87e1-53f23a7fa070)
    
*A CUI Category or Subcategory are the exclusive designations for identifying CUI that a law, regulation, or Government-wide policy requires or permits agencies to apply safegauarding or dissemination controls to.*

OSEE shall allow the user to indicate the CUI Category or Subcategory for OSEE Artifacts containing CUI.

### CUI Type

>`GUID`: {e8205b4b-bc32-4cad-bef7-74b82abd627c}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5) page 8
    
*CUI Categories and Subcategories are either “Basic” or “Specified”. “Specified” categories and subcategories have handling requirements that go beyond the “Basic” CUI handling requirements. Only some data for a CUI Category or Subcategory may be "Specified".*

OSEE shall allow the user to indicate the CUI type as “Basic” or “Specified” for OSEE Artifacts containing CUI.

### CUI Dissemination Controls

>`GUID`: {340cc122-4a13-491e-9083-5224e8f2d38a}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5) page 12
    
*In addition to a Required Indicator, the dissemination of data stored in an OSEE Artifact may be controlled.*

OSEE shall allow the user to indicate the dissemination controls for OSEE Artifacts containing CUI.

### Rational

>`GUID`:	{f026e884-4a3b-4e79-ba09-2cf1211071bd}

>`SOURCE`: derived
    
*In addition to the CUI indicators and Required Indicators, it is also important to record the rationale for the assigned indicators.*

OSEE shall allow the user to record the rationale for the OSEE Artifact’s assigned CUI and Required indicators.

### Required Indicators per Authorities

>`GUID`: {b62edc79-ed37-4ed0-b2a3-df2130d60597}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5) page 20
        
*The Authority specifying the CUI category may also require additional data indicators. Additional indicators may also be required by corporate policy and or contract. The following are examples of Required Indicators:*
-	*Non-Deliverable*
-	*Program Documentation Requirements*
-	*Government Rights*
-	*Export Control*

OSEE shall allow the user to enumerate all Required Indicators for an OSEE Artifact.

### Required Indicator Frequency

>`GUID`: {f8584f30-ff79-43d4-8577-d8fd64d2ab78}

>`SOURCE`: derived
    
OSEE shall allow the user to indicate if a Required Indicator must be present on every page or only on containing pages.

### Required Indicator Title Statement

>`GUID`: {7c014747-ac9e-4f99-b2aa-d3b9fc67191d}

>`SOURCE`: derived
    
OSEE shall allow the user to provide a title page Required Indicator statement for each Required Indicator type on a per document basis.

### Required Indicator Header Statement

>`GUID`: {062bd520-8a08-4534-98ae-8ac5362fc7fb}

>`SOURCE`: derived
    
OSEE shall allow the user to provide a document header statement for each Required Indicator type on a per document basis.

### Required Indicator Footer Statement

>`GUID`: {3b100711-c9d8-4d0d-8dbc-d70f0c91374b}

>`SOURCE`: derived
    
OSEE shall allow the user to provide a document footer statement for each Required Indicator type on a per document basis.

#	Access

##	User Access

>`GUID`: {8a84a735-370b-40f8-a27c-e8ebf41cc911}

>`SOURCE`: [{d082460b-df40-450d-95ed-66bd06afee17}](#d082460b-df40-450d-95ed-66bd06afee17)

*OSEE is a database which may contain data for multiple programs with different data rights and different user access controls. The unit of access control in OSEE is the branch.*

OSEE shall only allow authorized users access to a branch and all of it’s sub-branches.

##	API User Access

>`GUID`: {93c530e0-efe3-471c-b224-ae907179672f}

>`SOURCE`: [{d082460b-df40-450d-95ed-66bd06afee17}](#d082460b-df40-450d-95ed-66bd06afee17)

*OSEE provides HTTP(S) API that allow external tools/browsers to access OSEE Data.*

OSEE shall not allow access to controlled data via an HTTP(S) API that a user does not have access to.

##	E-mail

### E-mail Encryption

>`GUID`:	{3e91b1ad-89db-4dc7-b439-7803e82952e0}

>`SOURCE`: [{0b9a9ced-ecb2-4518-b174-913e26ea4843}](#0b9a9ced-ecb2-4518-b174-913e26ea4843), page 7

*OSEE maybe configured to send E-mail notifications to users.*

All CUI data e-mailed by OSEE shall be contained in encrypted attachments.

### E-mail Markings

>`GUID`: {c8394f89-34e7-413b-aa96-86abb7cb2ce5}

>`SOURCE`: [{0b9a9ced-ecb2-4518-b174-913e26ea4843}](#0b9a9ced-ecb2-4518-b174-913e26ea4843), page 7

OSEE shall include CUI Banner markings on any e-mails containing CUI data.

#	Login Banner

### Login Banner

>`GUID`:	{1af9e199-c2c3-4215-909f-0d79866f697e}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 27

>`SOURCE`: [{d33a2e6a-3e09-4736-b2c0-771038777d55}](#d33a2e6a-3e09-4736-b2c0-771038777d55), a.8
    
*Users must be notified of authorized use restrictions and requirements for access to the OSEE database.*

OSEE shall display the GSSFL Banner to users upon login.

#	Destruction

##	Data Destruction

>`GUID`: {615e3e78-c33a-4135-aeee-83a44900d103}

>`SOURCE`: [{f82c6533-ff04-40a3-9a38-0f5e348ec506}](#f82c6533-ff04-40a3-9a38-0f5e348ec506), page 7

*At some juncture it may become necessary to destroy CUI data stored within OSEE.*

OSEE shall provide a data destruction capability compliant with the electronic media data destruction requirements for CUI data.

#	Publishing

##	CUI Banner Marking

### Presence

>`GUID`: {0e59788b-5652-4ddc-8d79-89e505e4f012}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 6, 7

*The CUI Banner must be included on all pages, this includes the title page.*

OSEE shall publish a CUI Banner at the top of each page for all documents containing CUI.

### Best Practices

>`GUID`: {b349e3d0-37b0-467b-82ff-63e47fd458ae}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 7

OSEE publishing shall provide an option to include the CUI Banner Marking at the bottom of each page.

### Consistent

>`GUID`: {54883874-3924-4c4e-ad99-54d440117cfe}
    
>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 6    

OSEE shall publish the same CUI Banner on all pages of a publish.

### Inclusive

>`GUID`: {2f9d6287-4e7c-453a-a979-9d687512f4f2}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 6

OSEE shall publish a CUI Banner that is inclusive of all data within the publish.

### Presentation

>`GUID`: {0c18405e-651d-4f12-bb80-4461718c2de1}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 6

OSEE shall publish the CUI Banner with the following formatting:
-	Bold
-	Capitalized
-	Black Text
-	Centered

### Format

>`GUID`: {41f04e8e-5b88-4127-bc14-6f4398a4b371}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 6, 7, 11, 12

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

>`GUID`:	{56eb07e0-6ad8-409e-975c-9be39dba0cec}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 6

OSEE shall include the \<category-indicators\> for all OSEE Artifacts in the publish.

### Specified Category Indicators

>`GUID`: {d3487f0b-532a-4e1e-bd26-cd33f1b7b63e}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 10

OSEE shall prefix \<category-indicators\> for “Specified” CUI categories and subcategories with “SP-“ in the CUI Banner Marking.

### Category List Contents Specified And Basic Order

>`GUID`: {0d5534a4-49f3-49c5-a277-a85ea572cb72}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 11

OSEE shall place all the \<specified-category\> indicators ahead of the \<basic-category\> indicators in the \<category-list-contents\>.

### Category List Contents Basic Indicators Order

>`GUID`: {fb5176cc-00c7-40cb-8b35-13a9b310f396}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 11

OSEE shall sort the \<basic-category\> indicators in the \<category-list-contents\> in alphabetical order.

### Category List Contents Basic Indicators Order

>`GUID`:	{e5c27303-87b0-4041-8913-5e97aa89ec3b}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 11
    
OSEE shall sort the \<specified-category\> indicators in the \<category-list-contents\> in alphabetical order.

### Dissemination List

>`GUID`: {7d2aab7b-2b9b-4f69-98a1-4d8f96e1785a}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 12

OSEE shall include the \<dissemination\> indicators for all OSEE Artifacts in the publish.

### Dissemination List Order

>`GUID`: {37b7db74-d174-4588-a894-af6f65f0a5ad}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 12

OSEE shall sort the \<dissemination\> indicators in the \<dissemination-list\> in alphabetical order.

### Foreign Release List USA First

>`GUID`: {5a735e77-0feb-4faa-9e24-0fe3d353dfea}

>`SOURCE`: [{54d395d5-a8d0-4dd5-bae5-52ca4b610fc5}](#54d395d5-a8d0-4dd5-bae5-52ca4b610fc5)

OSEE shall include "USA" as the first \<foreign-indicator\> in the \<foreign-release-list\>.

### Foreign Release List Order

>`GUID`: {61cce808-7ffe-4d10-a00f-764b688127da}

>`SOURCE`: [{54d395d5-a8d0-4dd5-bae5-52ca4b610fc5}](#54d395d5-a8d0-4dd5-bae5-52ca4b610fc5)

OSEE shall sort the \<foreign-indicator\> indicators for foreign nations in the \<foreign-release-list\> in alphabetical order.

##	CUI Designation Indicator Block

### CUI Designation Indicator Block

>`GUID`: {bdbc9c17-9eb3-4e4e-81e4-cfc4c97bf4d0}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 13

OSEE shall include a CUI Designation Indicator Block in each document containing CUI data.

##	CUI Designation Indicator Block Contents

>`GUID`: {7c014747-ac9e-4f99-b2aa-d3b9fc67191d}

>`SOURCE`: [{d3ae6f7d-a85f-475b-9087-5ef26a810cd5}](#d3ae6f7d-a85f-475b-9087-5ef26a810cd5), page 13

OSEE shall include the following in the “CUI Designation Indicator Block”:

-	Designating Agency Identification
-	Branch or division
-	Point of contact
-	Contact information

##	Required Indicators

### Title Page Required Indicators

>`GUID`: {f4384574-db42-4298-8154-d4c65bcfef00}

>`SOURCE`: derived

OSEE shall include a Required Indicator Title Page Statement for each type of Required Indication with a Title Page Statement contained within the document.

###	Every Page Header Required Indicators

>`GUID`: {51825598-5310-46ab-90aa-70f21836770d}

>`SOURCE`: derived

OSEE shall include a Required Indicator Header Statement for each type of Every Page Required Indication contained within the document.

###	Only Page Header Required Indicators

>`GUID`: {e38fa7f8-767f-4912-b7d3-124d905acd49}

>`SOURCE`: derived

OSEE shall include a Required Indicator Header Statement for each type of Only Page Required Indication contained on that page of the document.

###	Every Page Footer Required Indicators

>`GUID`: {d99598f0-aafa-40d7-b7d3-4036fffcf1c9}

>`SOURCE`: derived
    
OSEE shall include a Required Indicator Footer Statement for each type of Every Page Required Indication contained within the document.

### Only Page Footer Required Indicators

>`GUID`: {5238d3e2-220f-4caa-b4ff-dcf5857a252b}

>`SOURCE`: derived

OSEE shall include a Required Indicator Footer Statement for each type of Only Page Required Indication contained on that page of the document.

## Temporary Files

### Temporary File Destruction

>`GUID`: {92c2e52a-109f-40bb-ba2c-fdf7954b9f37}

>`SOURCE`: [{d082460b-df40-450d-95ed-66bd06afee17}](#d082460b-df40-450d-95ed-66bd06afee17)

*Temporary files are files that are created by either the OSEE client or the OSEE server, not delivered to the user, and no longer needed at the completion of the publish.*

OSEE shall destroy all temporary files containing CUI at the completion of a publish in a manner compliant with the electronic media data destruction requirements for CUI data.

### Temporary File Access

>`GUID`: {105bc5a6-dac1-4718-863c-b37d8735c295}

>`SOURCE`: [{d082460b-df40-450d-95ed-66bd06afee17}](#d082460b-df40-450d-95ed-66bd06afee17)

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

