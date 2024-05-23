## Add an element to structure

1. Right click on any row on the structure to see the options for inserting an element
    > > ![OSEE Navigator](assets/images/mim/addelement1.jpg)
2. After choosing the appropriate option you will see the following dialog:
    > > ![editelement1](assets/images/mim/addelement2.jpg)
3. If the element to be inserted exists on another structure, choose the element from the drop-down menu on the right.
4. Otherwise, click on the “Create New Element” button to open up a dialog which will allow you to define a new element. Fields with an \* are required.
    > > ![editelement1](assets/images/mim/addelement3.jpg)
5. If a type already exists that can be used, simply choose it in the Platform Type drop down. However, if a new type is needed, click on the green + sign on the right of the Platform Type field. Scroll down in the window to see the fields required to begin creating a Platform type.
    > > ![editelement1](assets/images/mim/addelement4.jpg)
6. If a multi-element array is needed, you can create an "Array Header" element.
    > > ![editelement1](assets/images/mim/arrayheader.jpg)
    - When adding the header element, you can use the "+" button below the "Array Elements" header to add child elements.
    - These child elements are created using the same element creation form as described on this page. Note that there is no option to create another array header as a child element.
    - There are some fields here that affect how the elements are displayed in exports
        - The demiliters describe how the array indices are separated from the element name. Examples are shown as these values are edited. They default to a space.
        - The "Use Array Header Name in Reports" toggle determines if the array header name is used for each element name in the exports. For example, if the header name is "Array" and the child element is called "Character", the exports would list each instance of that child element as "Array 1 Character", "Array 2 Character", etc.
    - If child element descriptions need to include the current parent array index, or its own index, the following tokens can be used and will be replaced when exporting:
        - `$parentindex`: This will be replaced by the current index of the array header.
        - `$index`: This will be replaced by the current index of the element. Note that this can be used in any array, not just multi-element arrays.
7. After selecting the appropriate logical type, click on the Next button and fill out the required fields. Then click next again.
    > > ![editelement1](assets/images/mim/addelement5.jpg)
8. A summary of the new type will be shown. If it looks correct, click on the “OK” button.
9. The new Type will appear in the platform type field of the New Element form
    > > ![editelement1](assets/images/mim/addelement6.jpg)
10. Verify all the fields look correct, then click “Next”.
11. A summary of the element to be created will appear:
    > > ![editelement1](assets/images/mim/addelement7.jpg)
12. If it looks correct, click on “Ok”
13. The element will be added to the structure and the structure page will re-query.
    > > ![editelement1](assets/images/mim/addelement8.jpg)
