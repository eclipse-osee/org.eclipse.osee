# Message Interface Modeler (MIM) Overview

The purpose of the tool is to manage the software interfaces between
subsystems for a Product Line. MIM models the messages moving across the
network in detail, including their source, destination and composition. MIM
allows users to mark data artifacts with an applicability which is the
vessel by which Product Line is implemented.

The MIM tool can be used to export data in various formats. For example, an
ICD can be exported in an Excel Workbook format, or the data can be exported
into a zip of text files to be used in downstream tools. The
[Reports Page](../reports/) will list available reports for
your installation.

## Below is sequence of screenshots of the tool to show how the data flows:

![Connections Page](../../../osee/assets/images/mim/connection_page.jpg)

-   `node1` and `node2` can represent hardware or software components
-   `conn` represents the set of data broken into `messages/submessages/structures/elements` that represents the traffic on the connection
-   Generally the information stored is exported to an Interface Control Document (ICD)
-   `transport1` represents the type of connection - example might be MUX, Fibre...etc
-   Clicking on `Connection1` would open up a page showing associated messages such as below
    &nbsp;
    ![Messages](../../../osee/assets/images/mim/messages.jpg)
-   The above screenshot shows the list of messages which comprise `Connection1`
-   When user selects the drop down to the left of the message name, the message is expanded to show the submessages of the message
    &nbsp;
    ![SubMessages]../../../osee/assets/mim/messages_submsgs.jpg)
-   The above screenshot shows the first message expanded to show the SubMessages contained in message `Control Message`
-   Clicking on the `Go To Message Details` button will open up a new page which shows details of the submessages
    &nbsp;
    ![Structures](../../../osee/assets/images/mim/structures.jpg)
-   Initially after click on `Go To Message Details` will display the list of Structures that make up the SubMessages.
-   In this case `Temp Control SubMessage` is made up of two structures.
-   When user selects the drop down to the left of the structure name, the structure is expanded to show the elements as seen in the screenshot below.
    &nbsp;
    ![Structures](../../../osee/assets/images/mim/structure_with_elements.jpg)
