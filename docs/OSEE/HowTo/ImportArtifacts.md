## SpreadsheetML

## WordML

Rules:

  - Paragraph number and title cannot be in the same line as the content
    (body of the artifact).
      -
        Example:

|        |                                                                                                            |
| ------ | ---------------------------------------------------------------------------------------------------------- |
| WRONG: | ![image:paragraph_number_wrong.png](/docs/images/paragraph_number_wrong.png "image:paragraph_number_wrong.png")       |
| RIGHT: | ![image:paragraph_number_correct.png](/docs/images/paragraph_number_correct.png "image:paragraph_number_correct.png") |

  - Paragraph numbers and titles cannot be embedded into a table. Any
    content (i.e. table) has to be in a new line after paragraph number
    and title.

<!-- end list -->

  - Tables of contents and any section preceding the usual "1.0
    Paragraph Title" need to be removed

<!-- end list -->

  - Paragraph headings **must** be numbered

<!-- end list -->

  - For **large imports** having content in the same line as the
    paragraph title, we have a WordMLNewLineMaker (instance of
    org.eclipse.osee.framework.jdk.core.text.Rule), which will help
    facilitate the process.