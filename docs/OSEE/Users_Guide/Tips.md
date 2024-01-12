# Frequently Asked Questions

## Do I have to create a new database table for every type of Artifact that I want to use?

Quick Answer: No

The OSEE Architecture Framework uses a common table structure to store
all types of artifacts, attributes and relations. This allows the users
to dynamically create and start using new objects without major database
modification.

## Do I have to use the Artifact types that are built into OSEE?

Quick Answer: No

All Artifact Types, Attribute Types and Relation Types (with a few
exceptions) are fully customizable. Although OSEE was initially built
for capturing all data throughout the lifecycle of a large avionics
systems engineering project, it was architected to store any data by
simply defining a different data model to use.

In addition, these data models can be dynamically modified. New
Artifacts, Attributes and Relations can be specified at anytime (and
even by end users if allowed). Modification and deletion of these can be
performed with some administrative back-end tools with the understanding
that you would need to determine what to do with the removed data.

## How do I delete artifact types?

There is a BLAM called "Purge Artifact Type" that is used to permanently
remove all traces of an artifact type from the OSEE database. The BLAM
allows the user to select one or more artifact types to purge. If any
artifact instances of the doomed artifact type exist, a different
artifact type can be specified; these artifacts will be converted to the
new type. Otherwise, the BLAM will abort upon discovering an artifact
that is of a type to be purged; that is, the BLAM will never delete any
artifacts.

## Can OSEE be used offline and later sync'd up?

Although OSEE does not currently provide the capability to download
database artifacts, work offline and then sync up when re-connected, it
was certainly a consideration in the design. The most complex part would
be resolving conflicts that may occur to artifacts edited in both
disconnected OSEE's. Since OSEE already has the capability to run on
multiple databases and the capability to export and import artifacts, it
would just take some effort to provide the first versions of this
feature. This would be a great area for collaboration.

## Can I import existing traceability into OSEE?

Quick Answer: Yes

Since traceability is usually defined in a way that is specific to it's
use and the objects that are being traced, there is currently no
"generic" traceability importing in OSEE, however OSEE provides BLAM
that would enable traceability to be imported from existing
applications, documents, excel spreadsheets. This capability has already
been used a number of times to successfully import traceability from
legacy documents and systems on projects OSEE has been deployed to.

## Can I import from Microsoft Excel©?

Quick Answer: Yes

Through OSEE's BLAM tool, users can import from other applications,
documents and spreadsheets.

## How do I publish documents from OSEE?

The majority of the work in publishing out of OSEE has been in the
format of Microsoft Word(c) documents. The user can create a Word
template that "describes" the format, headers, footers and what data
maps into the specified areas in the template. A BLAM is then run where
the user can specify the branch and what artifacts are to be published.
Upon execution, the documents are created. This method has successfully
generated documents in excess of 10,000 pages that were delivered to the
customer.

The OSEE architecture also supports other methods of publishing
including web and open document formats. Depending on the level of
complexity, some work would be done to provide further export
capabilities.

Another form of publishing that OSEE was architected for is delivery of
OSEE and the database. Although OSEE can publish documents and reports,
it was decided early on that the vendors and customers would want and
need the same navigation and exportation capabilities that OSEE
provides. Instead of delivering generated documents, one of the projects
that OSEE is deployed on delivers a copy of OSEE with a sanitized
database (confidential data removed). This has been very successful from
both the program and customer's point of view.

## What databases can be used with OSEE?

OSEE was architected to be database independent and should run on any
SQL-99 compliant DB that has a JDBC driver. It is currently runs on
Oracle 10g and 11g and PostgreSQL 8.2. In the past, OSEE has also been
run on Derby and MySQL. The current release of OSEE would need to be
tested for compatibility with Derby, MySQL, or any other database. If
desired, the OSEE Team could work with the user/developers to help
perform this task.

## Is OSEE Compatible with Office 2007?

Yes, OSEE is compatible. When storing word content in XML format make
sure to use **Word 2003 XML Document (\*.xml)** format.

# Branch Manager

## How do I quickly find branches I care about

In branch manager, you can select any branch, right-click and add as
favorite. You can then select, via toolbar pull-down menu, to "Show
Favorites First". This will sort your favorite branches to the top of
the Branch Manager and any branch selection dialog.

## How do I quickly select a branch

Selecting "Select Branch", you can type in the filter to filter out the
branch you wish. Once your filter filters out all but a single branch,
it will be automatically selected for you and you can press enter.

# Artifact Explorer

## How do I create another Artifact Explorer

Select the artifact explorer menu bar icon to create another view. Each
Artifact Explorer can be set to it's own branch.

## How do I quickly search from Artifact Explorer

Select the Quick Search menu bar icon to open the Quick Search view.
This will also set the Quick Search view "Selected Branch" to the branch
from the Artifact Explorer you choose.

# Search Tips

## Searching for all artifacts of a given type

1.  From the pulldown menu, open **Search \> Search...**
2.  Select the **Artifact Search** tab
3.  Select the branch you wish to search on
4.  Select **Artifact Type** in the first pulldown under **Create a
    Filter**
5.  Select the artifact type you wish to search for
6.  Select "Add Filter" (make sure no other filters exist from your last
    search)
7.  Press **Search**

# MS Word Tips

## Where is the Tools Menu in Office 2007?

In Office 2007, the Options command which was located on the **Tools**
menu has been moved under the **Office Button**
![image:officebutton.jpg](/docs/images/officebutton.jpg "image:officebutton.jpg").
Click the **Office Button** ![image:officebutton.jpg](/docs/images/officebutton.jpg "image:officebutton.jpg") to open the **Options Dialog**. This dialog is
similar to the sub-menu located under the **File** menu in Office 2003.
To open the user preference dialog, you have to click on the **Word
Options** button located on the lower left corner of the **Office Button
Dialog**.

![image:officedialog.jpg](/docs/images/officedialog.jpg "image:officedialog.jpg")

![wordoptions.jpg](/docs/images/wordoptions.jpg "wordoptions.jpg")

## What format should Word Documents be stored

OSEE does not support Word 2007 XML format. Documents should be saved
using **Word 2003 XML Document** format.

## What user preferences should I set when using Word 2007?

1.  [**Disable Word from raising a "Convert File"
    prompt**](#Stop_Word_from_raising_a_"Convert_File"_prompt "wikilink")
2.  [**Always Show Smart Tags**](#Make_XML_Tags_Visible "wikilink")
3.  [**Disable Smart Tag Document
    Embedding**](#Stop_Word_from_adding_Smart_Tags "wikilink")

## Where can I find support for Word 2007 features?

[**Office 2007 How-To
Demos**](http://office.microsoft.com/en-us/training/CR100654561033.aspx)

## Do I have to use Microsoft Word to enter my requirements?

Quick Answer: No

This question comes up when users find out that OSEE is integrated with
Microsoft Word as one method to enter artifact data such as
requirements.

OSEE uses Artifacts, Attributes and Relations to store information in
its data store. The Attributes associated with a certain Artifact can be
boolean, text, date, float or any newly created attribute type. One such
attribute type is a "Word Content" attribute. This allows word content
to be added as an attribute to an Artifact. This attribute, however, is
not required to be used. One could define a "Software Requirement" to be
any set of Attribute types that must/can be entered. Although, Word 2003
XML is more tightly integrated into OSEE, the architecture provides for
other editing applications to be plugged in. We are actively working on
adding tight integration with Open Office to edit requirements.

In addition to this specific type of Attribute, OSEE does allows any
operating system file to be dragged in and created as an Artifact. This
artifact, when opened, will extract its data and present it to the
operating system to allow viewing and editing. This allows things like
requirements to be specified by other modeling, diagraming or even
mathematical applications that OSEE doesn't know about. These artifacts
can have their own metadata associated and also be related to other
artifacts in the system.

## Importing Microsoft Word(c) documents into OSEE

Yes. OSEE will import existing Word 2003 XML documents and atomize them
by their paragraph sections. The Word styles "Heading (1...9)" are used
to determine the breakout into individual artifacts and their position
in the document hierarchy.

## How are MS Word styles and formatting handled?

Whole MS Word documents can be stored and edited as an artifact like any
other native file type without any formatting restrictions of any kind.
However, to use the more tightly integrated support such as editing many
artifacts in a single Word document or the document generation
capabilities, then the file format needs to be MS Word 2003 XML. Since
OSEE supports user defined Word XML templates for editing, previewing,
comparing, and publishing, the user has complete control over the Word
styles (and all other formatting) through the rendering templates. The
only formmatting requirements for Word content are that when importing
the Word styles "Heading (1...9)" are used to determine the breakout
into individual artifacts and their position in the document hierarchy.

## Stop Word from raising a "Convert File" prompt

### Office 2013 to Recent

1.  Click the **FILE** menu
2.  Click the **Options** menu option
3.  Select **Advanced** from the topics panel on the left
4.  Scroll to the **General** settings section
5.  Uncheck **Confirm file format on open**

### Office 2007

1.  Click the **Office Button** located on the upper left corner
2.  Click the **Word Options** button to open the options dialog
3.  Select **Advanced** from the topics panel on the left
4.  Scroll to the **General** settings section
5.  Uncheck **Confirm file format on open**

### Office 2003

1.  Open **Tools \> Options... \> General**
    1.  Uncheck **Confirm conversion at Open**
    2.  Click **OK**

## Stop Word from adding Smart Tags

### Office 2003

1.  Open **Tools \> AutoCorrect Options… \> Smart Tags**
    1.  Uncheck **Label text with smart tags**
    2.  Click **Save Options...**
        1.  Uncheck **Embed Smart Tags**
        2.  Click **OK**
    3.  Click **Remove Smart Tags** if the button is not grayed out
    4.  Click **OK**

### Office 2007

1.  Click the **Office Button** located on the upper left corner
2.  Click the **Word Options** button to open the options dialog
3.  Select **Advanced** from the topics panel on the left
4.  Scroll to the **Preserve fidelity when sharing this document**
    section
5.  Select **All New Documents** from the drop-down located next to the
    section title
6.  Uncheck **Embed Smart tags**
7.  Uncheck **Embed Linguistic data** There appears to be a bug in MS
    Word 2007: [1](http://support.microsoft.com/kb/925174)

## Make XML Tags Visible

To toggle viewing of XML tags, press Ctrl+Shift+X. You should enable
these unless you have a good reason not to.

### Office 2007 Preference Settings

1.  Click the **Office Button** located on the upper left corner
2.  Click the **Word Options** button to open the options dialog
3.  Select **Advanced** from the topics panel on the left
4.  Scroll to the **Show Document** settings section
5.  Check **Show Smart Tags**

## Make tracked changes in Word appear inline instead of in balloons

1.  Open the **Show** dropdown menu within the Reviewing Toolbar and
    select **Options...**.
2.  Within the **Track Changes** window that just appeared change the
    **Insertions** and **Deletions** colors to Blue and Red,
    respectively.
3.  Change the **Use Balloons** dropdown menu from **Always** to **Only
    for comments/formatting.**
4.  Click OK.

## Keep MS Word from prompting to save Normal.doc

1.  Upgrade to Office 2007 (required by OSEE)
2.  On the Tools menu, click Options.
3.  Click the Save tab.
4.  Clear the Prompt to save Normal template check box.

\== What do I do when I am seeing some characters (such as smart quotes
and \!= signs) show up as a little black rectangle instead? ==

1.  In Word, goto Tools-\>AutoCorrect Options
    1.  In the "Replace text as you type" table , delete the "straight
        quotes with smart quotes" option
    2.  In the "AutoFormat as you Type" Tab deselect the Replace as you
        type "straight quotes with smart quotes" selection
    3.  In the "AutoFormat" Tab deselect the Replace "straight quotes
        with smart quotes" selection

### Office 2007 Preference Settings

1.  \[Windows Button\]--\> Proofing --\> in the AutoCorrect options
    block --\> AutoCorrect Options...
2.  In the "AutoFormat as you Type" Tab deselect the Replace as you type
    "straight quotes with smart quotes" selection
3.  In the "AutoFormat" Tab deselect the Replace "straight quotes with
    smart quotes" selection

## Configure MS Word (2003/2007) so it does NOT auto-format bulleted and numbered lists

As you may have already seen, Word will take the following: (A) -\> XXX
And turn it into some type of numbered list, such as: -\> XXX, or A. -\>
XXX, or 1 -\> XXX, etc.

This will cause incorrect formatting, because Word will automatically
create styles that are not part of the templates used for rendering and
publishing in OSEE. Ultimately this auto-numbering leads to the need for
fix RPCRs. Additionally when introduced using Word 2003, it can cause
compatibility issues.

1.  Close all instances of Word
2.  Open either MS Word 2007 or 2003 (whichever you prefer to work with)
      - Word 2007:
        1.  Select *Office* (top-left corner)
        2.  Go to *Word Options* =\> *Proofing* =\> *AutoCorrect
            Options*
      - Word 2003:
        1.  Go to *Tools* =\> *AutoCorrect Options...*
3.  Select *AutoFormat As You Type*
4.  Under *Apply as you type* clear the checkboxes: *Automatic bulleted
    lists* and *Automatic numbered lists*
5.  Restart MS Word

## Restarting a numbered list in an OSEE Artifact

1.  Menu Item in Word: Insert-\>Quick Parts-\>Field-\>ListNum
2.  Enable Level in list: 1
3.  Enable Start-at value: 0
4.  Click Ok
5.  To not show the 'dot' character in the document
      - Highlight the dot, right click -\> Font
      - Enable Hidden
      - Click Ok
6.  If you need to see the field code simply click on the Show/Hide icon

## Creating Cross-References between Multiple Word Artifacts having OLE Data

**Issue:** OSEE is unable to open Word Artifacts containing OLE Data for
multi-edit. Therefore, I am unable to create cross-references between
sections of artifacts.

### Method1: Use single edit

1.  Open each artifact for edit separately.
2.  In the artifact with the figure, ensure the figure has an associated
    auto-reference number (i.e., the figure has a title/caption
    paragraph with a field code for the auto-reference). Create one if
    it does not already have one using normal MSWord procedure
    (right-click on the figure and select Insert Caption, etc.).
3.  In the artifact with the figure, create a temporary paragraph with a
    temporary sentence that says “As shown in .”
4.  At the end of the temporary sentence, insert an auto-reference to
    the figure using normal MSWord procedure (use the References tab and
    select Cross-reference, etc.). The temporary sentence should now
    say, “As shown in Figure x” where x is the figure number.
5.  Copy the field code from the temporary sentence (i.e., the part that
    says “Figure x” and which will turn gray when selected) from the
    artifact with the figure in it to the other artifact that you want
    to reference the figure from. This copy/paste will result in copying
    the field code for the figure over to the other artifact. Copy it to
    the text location where it is needed in the other artifact.
6.  Once edits are complete, delete the temporary paragraph that was put
    into the artifact with the figure in it.
7.  Save both artifacts (there is no need to save the artifact with the
    figure in it unless you made substantive changes, such as adding the
    title/caption paragraph to the figure per Step 2 above).

### Method2: Separate contents and reference with hyperlinks

1.  Create a separate artifact for the section that needs to be
    referenced.
2.  Create hyperlinks to the new artifact from the referencing artifact.

## Word XML opening in XML editor instead of Word

Sometimes, the registry settings for what to open XML documents can get
set incorrectly. A common symptom is that links to Word documents in
OSEE open in an XML editor or IE. To fix this, modify the registry
entry: **Computer\\HKEY_CLASSES_ROOT\\xmlfile\\shell\\open\\command**
to the correct version of Office. For Office 2013, the value would be:
**"C:\\Program Files (x86)\\Common Files\\Microsoft
Shared\\OFFICE15\\MSOXMLED.EXE" /verb open "%1"** (with quotes)

