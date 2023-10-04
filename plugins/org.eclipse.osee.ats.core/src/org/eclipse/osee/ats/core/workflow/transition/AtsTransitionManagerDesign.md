# ATS Workflow Documentation/Design

* <a href="../AtsWorkflowLinks.md">ATS Workflow Links</a>

### Transition Assignees
- Validation
    - Check to see if Work Definition specifies assignee required.  If so, fail if user isn't assigned already

- Transition
    - If transition-to-assignees has valid users, use them
    - Else use the current user as the to-assignees

### Things to consider before implementing a transition hook:

Most transition validation can/should be done in the Workflow Definitions. Anything "generic" should be added to
the normal checks so all workflows can take advantage without having to add hooks. Always consider implementing
generically, if possible. See WorkDefOption and RuleDefinitionOption for generic checks that are already possible for states.

- **Examples where transition hooks are necessary:**
    - Where multiple related attributes must be checked. eg: If SubSystem == Navigation then Reviewed By is set
    - Where only specific users can select/enter a widget. eg: Reviewed By can only be selected by certain users


- **Use the correct method in IAtsTransitionHook** depending on what effect you desire and how the hook will interact/delay the user.


- **Checks should be quick** especially when determining if this hook is valid. Consider:
    - Check ATS object. Use workItem.isTeamWorkflow(), workItem.isTask, etc.
    - Check Artifact Type. Use workItem.isOftype(xxx.My_Artifact_Type)
    - NOTE: You ***usually don't need both*** of the above since artifact types and ATS object types are similar


- If task can be performed in background and not stop/delay the transition, use isBackgroundTask and transitionPersistedBackground which runs in the background.
