# 1 OSEE Eclipse IDE Client Artifact Markdown Content Editor

This document contains descriptions of the existing capabilities for the Eclipse IDE client artifact Markdown content editor. The Markdown Editor consists of two primary tabs called "Markdown Edit" and "Markdown Preview". These tabs can be found next to the "Attributes" tab at the bottom of the artifact editor for any artifact with the Markdown Content attribute.

## 1.1 Important Acronyms

- OME -> OSEE Markdown Editor

## 1.2 Markdown Edit Tab

The Markdown Editor Tab

### 1.2.1 How to locate Markdown Editor Tab on Markdown artifact

- Double-click on the Markdown artifact from the artifact explorer
- Select the "Markdown Edit" tab located near the bottom of the artifact editor (if the artifact does not have the "Markdown Content" attribute, this tab will not display)

### 1.2.2 Toolbar

The Toolbar located along the top of the "Markdown Edit" tab

#### 1.2.2.1 Toolbar Buttons

- Tool tip buttons (located in the top left): Provide Markdown syntax examples in the editor pane.
- Split tab button (located in the top right): Opens Markdown Preview tab to the right of the Markdown Editor tab
- Refresh button (located in the top of Preview tab when in split tab view): Refreshes Markdown Preview tab (if you are in split tab)

### 1.2.3 Editor

The Editor is the text editor containing the artifact's "Markdown Content" attribute to input Markdown syntax

#### 1.2.3.1 Editor Features

- Drag and drop any artifact (on the same branch) from the artifact explorer into the Markdown editor to link artifacts

  - This creates a clickable \<oseelink> tag
  - The \<oseelink> tag will render the link in the Markdown Preview tab
  - The \<oseelink> tag will show up-to-date artifact names if the linked artifact name has been updated
  - The \<oseelink> tag will be replaced by a 'not found' message if the linked artifact is not found (i.e. not on the branch or deleted)
    - This message will need to be removed by the user or the text will persist.

- Drag and drop any general document artifact with a png extension (on the same branch) from the artifact explorer into the Markdown editor to link image artifacts

  - This creates a clickable \<oseeimagelink> tag
  - The \<oseeimagelink> tag will render the image in the Markdown Preview tab
  - The \<oseeimagelink> tag will show up-to-date artifact names if the linked artifact name has been updated
  - The \<oseeimagelink> tag will be replaced by a 'not found' message if the linked artifact is not found (i.e. not on the branch or deleted)
    - This message will need to be removed by the user or the text will persist.

- Press Ctrl + s to save
  - This action will also refresh the Markdown Preview tab if you are in split-tab view

## 1.3 Markdown Preview Tab

The Markdown Preview Tab is composed of Toolbar and Preview sections

### 1.3.1 How to locate Markdown Preview Tab on Markdown artifact

- Double-click on the Markdown artifact from the artifact explorer
- Select the "Markdown Preview" tab located near the bottom of the artifact editor (if the artifact does not have the "Markdown Content" attribute, this tab will not display)

### 1.3.2 Toolbar

Toolbar located along the top of the "Markdown Preview" tab

#### 1.3.2.1 Toolbar Options

These accessibility options are created in OmeHtmlComposite.java

- Refresh button (located in the top): Refreshes Markdown Preview tab
- "Display Image" check box: Toggle for html rendering of image artifacts
  - Unchecked: Image artifact link is rendered in Markdown Preview as a link (href)
  - Checked: Image artifact is rendered in Markdown Preview as an image (img)
  - \*Note: This also refreshes the Markdown html preview composite

### 1.3.3 Preview

The Preview is the pane containing the html content rendered from the artifact's "Markdown Content"

## 1.4 Markdown Renderer

- **Flexmark-java** is open source dependency used to traverse md document and generate html
  - Extensions that have been added to OSEE code base
    - Tables
      - https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-tables
    - Tasklists
      - https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-gfm-tasklist
    - Table of Contents
      - https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-toc
    - Autolinks
      - https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-autolink
    - Gitlab
      - https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-gitlab
- Currently located in: OmeHtmlTab.java

## 1.5 Demo Database Initiaization of Markdown Artifacts

Performing a demo db init will generate Markdown requirements artifacts to view and test in a local runtime client.

### 1.5.1 Steps to Run Demo DB Init (and generate Markdown artifacts)

- Run the **OSEE_Application_Server[HSQLDB]** from debug configurations
- Run the **AtsIde_Integration_Test_Suite** from debug configurations
- Terminate the **AtsIde_Integration_Test_Suite** after the console displays "End Database Population"

### 1.5.2 Steps to View the Generated Markdown Artifacts

- Run the **OSEE_Application_Server[HSQLDB]** from debug configurations
- Run the **OSEE*IDE*[localhost]** from debug configurations
- Navigate to the **SAW_Bld_1** branch in the artifact explorer
- Open any folder with the " - Markdown" tag at the end of the folder's name

## 1.6 Markdown Editor Tests

There are no tests yet.

## 1.7 Markdown Editor Demo

### 1.7.1 Cases

1. Drag and drop another md artifact from artifact explorer into md editor

1. Drag and drop an image artifact (general document with 'png' as value for extension attribute) from the artifact explorer into md editor

1. Change the name of one of the linked artifacts and re-open the md editor

   - Demonstrates the name within the link changing in the md content

1. Delete one of the linked artifacts and re-open the md editor

   - Demonstrates the error message that pops up when the linked artifact no longer exists
   - User is prompted to remove/update this link

## 1.8 Developer Notes
