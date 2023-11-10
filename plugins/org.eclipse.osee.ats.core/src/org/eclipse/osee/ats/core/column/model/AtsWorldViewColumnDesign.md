# ATS Column Documentation/Design

* <a href="../../workflow/AtsWorkflowLinks.md">ATS Workflow Links</a>

### ATS World Column Goals
1. Define as many World columns in core so values are available through IDE/Web clients and server without duplication
2. Define any IDE specific columns that are necessary, especially to provide custom edit capabilities
3. Add AtsDisplayHint on every attr type that is defined for a workflow artifact that specifies if it is Edit/Read/Config column.  These will be turned into default IDE columns providing default editing of attributes based on type
4. Remove any column that could be specified generically, as in #3 above
5. Make it super easy to determine and mark what type of column to create when creating new attr types.  Integration test will enforce that all attr types added to a workflow will HAVE to specify one of the appropriate AtsDisplayHints.  This ensures that new types will get their appropriate columns and we reduce user support of "how come my column doesn't exist"
6. Add UI response if column is either invalid for artifact type (eg: Review Defects for Goal) or that column is not editable

### AtsColumnService

These classes all provide column information from the AtsColumnService.  for the web or ide, they can be used as-is to format different columns. These columns can be accessed through AtsColumnService().getColumn(id) or their values thorugh AtsColumnService().getText(id).

Columns can also be specified in the OSEE IDE and need extend XViewerColumn.  The core tokens are turned into their respective XViewerColumns that provide extra alt-left-click-edit/multi-edit/inherit/rollup capabilities.

- AtsCoreColumn - Base class for columns defined and provided through ats.core bundle and AtsColumnService
    - AtsCoreAttrTokenColumn
        - Specifies a column token that handles the display of a column based on the criteria specified in the AtsColumn token. Tokens can either come from
            - Code in classes like AtsColumnTokensDefault
            - Through the AtsConfig database artifact as part of "views=<json>" 
        - These tokens are converted into XViewerAtsAttrTokenXColumn in the OSEE IDE
        - These columns will respect the DisplayHints in the defined AttributeTypeToken
    - AtsCoreCodeColumn
        - Specifies a column that provides its values through a java class.  This is for those columns where calculations or loading of different data needs to be done to provide the value.
        - Code is provided through classes (eg: AssigneeColumn) that extends AtsCoreCodeColumn
        - These tokens are converted into XViewerAtsCoreCodeXColumn in the OSEE IDE
        - These columns can also declare an attr type which will be used instead of a AtsCoreAttrTokenColumn.  
        - These columns will respect the DisplayHints in the defined AttributeTypeToken


Once the core column tokens are converted into OSEE IDE XViewerColumns, they will be registered with the AtsColumnService.  This will be done in the following order to columns do not collide with each other.

- WorldXViewerFactory.registerColumnsAndSetDefaults()
    - loadColumns
        - Load IDE columns first so they override attr type columns
        - Load Core columns from AtsColumnService, minus IDE columns
           - loadCoreAtsConfigViewsColumns() - AtsConfig.views= db config columns 
           - loadCoreAtsColumns() - Ats Core Default Columns
           - loadCoreAtsProviderColumns() - Columns provided through OSGI IAtsColumnProvider (different bundles)
           - loadCoreAtsCodedColumns() - Ats Core Columns that require Code to resolve (eg: AssigneeColumn) where assignees come from attr that contain user art id and need to be resolve to user names
           - loadRemainingAtsWorkflowAttributes() - Create/load all remaining attr type columns that haven't been loaded already.  These use the AtsDisplayHint.Edit/Read/Config to determine what the clients should provide.  Edit allows Alt-Left-Click and Multi-Edit of columns.  Read/Config does not (eg: Created Date or Team Definition attrs)
    - getDefaultVisibleColumns() - Register all columns defined as default and visible (show=true) for this XViewer.
    - Register remaining defined columns as not-visible (show=false)
    
- All WorldXViewer
    - World-type XViewers now extend off WorldXViewerFactory and use the same loadColumns including: WorldView, Backlog, Sprint, Goal, Task, Task Estimating, Sibling 
    - Each viewer defines its own default columns/widths depending on use case
           
- Other changes
    - Removed as many IDE columns as possible and turned them into Core columns  
    - Added AtsDisplayHint to all ATS workflow attr types - Added test failure if not
    - Refactored to get a consistent naming convention
    
- Example: Case where have a column token, a UI class cause it is not a simple attribute and a core class for column value and web.
    - Related To State - When task gets related to a workflow, it can be tied to a certain state where it needs to be completed
    - Token: AtsColumnTokensDefault.RelatedToStateColumn
    - UI: RelatedToStateColumnUI - This is necessary cause user should be given a list of states ONLY valid for the parent team wf and dialog is UI related. This column will be registered first and thus an attrType using DisplayHints will not be created for the IDE.
    - Core: This is just a read-only column of the current value, so don't need AtsCoreCodeColumn




