# Block Applicability Tool (BAT)

## Overview

BAT takes a zip of source files, processes the Feature/Configuration tags using the specific view.  
Results in source code that is specific to the view selected without any of the Feature/Configuration tags and irrelevant content (unless user specifies to comment non-applicable blocks).

## Definitions:

>     - server: Osee server
>     - branch_id: Branch_id of branch that contains the Product Line Definition to be used
>     - document_location: Directory that contains the source to be processed.
>       -This directly must also contain the .fileApplicability which is used to identify which files/directories to skip processing (see details below)
>     - account_id: User's OSEE account_id
>     - bat_file_id: Unique string generated after first curl command to identify osee location for processed files
>     - commentNonApplicableBlocks: Boolean to determine whether the non applicable source should be commented or removed altogether
>     - viewId: The artifact id of the Configuration or ConfigurationGroup to be used in processing the source
>         - curl -X GET 'http://{server}/orcs/branch/{branch_id}/applic/views'
>         - returns the available views for the branch to be used in processing source
>     - output_zip_file: Name of zip file to be used in storing result

## .fileApplicability

>     Using feature tagging similar to what it used in the source, specify which directory names (do not specify the path just the directory name) and/or filenames should NOT be processed.
>     Example contents of .fileApplicability:
>
>     		Configuration[Product_A]
>     		csvfiles
>     		End Configuration
>
>     		Feature[JHU_CONTROLLER]
>     		CppTest_Exclude.cpp
>     		End Feature
>
> >     How this reads:
> >     - If the source is being processed for Product A, DO NOT INCLUDE any directories at any level named "csvfiles".
> >     - If the JHU_CONTROLLER is set to Included for the view being processed, do not include the file "CppTest_Exclude.cpp"
> >
> > -   NOTE: Configuration names may not have spaces or special characters. Replace with underscores. E.g. Product A = Product_A

## One rest call to rule them all:

> Will upload a zip, process it, and return a zip.
>
> -   Generic:
>     curl --location --request POST 'http://localhost:8089/orcs/branch/{branch_id}/applic/uploadRunBlockApplicability?view={view_id}' --data-binary '@{document_location}' -o '{output_zip_file}' -H "Content-Type: application/zip" -H "osee.account.id: {account_id}"
>
> -   Example:
>     curl --location --request POST http://localhost:8089/orcs/branch/8/applic/uploadRunBlockApplicability?view=200045' --data-binary '@/c/tools/applicability/example.zip' -o '/c/tools/applicability/output.zip' -H "Content-Type: application/zip" -H "osee.account.id: 3333"
>
> > Will download a response that will be empty if the process has yet to finish, will provide zip file otherwise. If file is still running and you get an empty zip, wait a few minutes then run step 3 below.

## Individual rest calls:

> 1.  Transfer zip file to the directory where OSEE can see it:
>     > -   Generic: curl --location --request POST 'http://{server}/orcs/branch/{branch_id}/applic/uploadBlockApplicability' --data-binary '@{document_location}' -H 'Content-Type: application/zip' -H "osee.account.id: {account_id}"
>     >
>     > -   Example: curl --location --request POST 'http://localhost:8089/orcs/branch/8/applic/uploadBlockApplicability' --data-binary '@/c/tools/bat/Example.zip' -H 'Content-Type: application/zip' -H "osee.account.id: 3333"
>     >
>     > -   Will return an id {bat_file_id}
> 2.  Run the BAT tool on the specified file when inputting the {bat_file_id} produced from the first command & will zip the file:
>
> > -   Generic:
> >     curl -X POST http://localhost:8089/orcs/branch/{branch_id}/applic/blockVisibility/{bat_file_id} -d '{"views": {"{view_id}":""}, "commentNonApplicableBlocks": false}' -H "Content-Type: application/json" -H "osee.account.id: {account_id}"
> >
> > -   Example:
> >     curl -X POST http://localhost:8089/orcs/branch/8/applic/blockVisibility/5531eae7-19d5-4bd6-8244-b0db297e6405 -d '{"views": {"200045":""}, "commentNonApplicableBlocks": false}' -H "Content-Type: application/json" -H "osee.account.id: 3333"
> >
> > -   Returns XResultData
>
> 3.  Check to see if the file is processed yet, and if it is it will download.
>
> > -   Generic:
> >     curl -X GET 'http://localhost:8089/orcs/branch/{branch_id}/applic/downloadBlockApplicability/{bat_file_id}' -o {output_zip_file} -H 'Content-Type: application/zip' -H "osee.account.id: {account_id}"
> >
> > -   Example:
> >     curl -X GET 'http://localhost:8089/orcs/branch/8/applic/downloadBlockApplicability/5531eae7-19d5-4bd6-8244-b0db297e6405' -o /c/tools/bat/output.zip -H 'Content-Type: application/zip' -H "osee.account.id: 3333"
> > -   Will download a response that will be empty if the process has yet to finish, will provide zip file otherwise.
>
> 4.  Delete all directories associated with the process. (Check your resulting zip for expected results first)
>
> > -   Generic:
> >     curl -X DELETE 'http://localhost:8089/orcs/branch/{branch_id}/applic/deleteBlockApplicability/{bat_file_id}' -H "osee.account.id: {account_id}"
> >
> > -   Example:
> >     curl -X DELETE 'http://localhost:8089/orcs/branch/8/applic/deleteBlockApplicability/3714ac6b-f21c-4f44-beca-7da4d9079c00' -H "osee.account.id: 3333"
> >
> > -   Returns XResultData
