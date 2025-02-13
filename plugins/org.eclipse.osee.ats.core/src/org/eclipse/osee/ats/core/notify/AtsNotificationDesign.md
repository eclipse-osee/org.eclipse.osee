# ATS Notification Design

- <a href="../../workflow/AtsWorkflowLinks.md">ATS Workflow Links</a>

ATS Notification Service provides notifications for

- Originated (if not current user)
- Assigned (if not current user)
- Completed to Originator
- Cancelled to Originator
- Subscribed for state changes if user subscribes
- Subscribed Team if user subscribes to actions against Team
- Subscribed AI if user subscribes to actions against Actionable Item
- Journal updates - user is auto-subscribed upon edit and can manually subscribe; emails sent on Journal updates
- Email from WorkflowEditor allows selecting users and entering body with hyperlink to email
- Email Actions allows emailing Originators/Assignees of list of Actions selected. Use Case: Email all originators to see if Action is still valid/desired with cancel link if not

ATS rolls up multiple email notifications per transaction into a single emails. eg: 50 tasks created and 20 are assigned to Joe Smith. Joe receives 1 email with 20 items listed.

Abridged Email: ATS uses the framework Abridged Email service to notify users with Abridged Email attribute set on their User artifact to another email (possibly external to corporate/business). This provides a simplified email with generic text and no proprietary data. eg: Email body would be simply that team workflow TWxxxx transitioned to Implement.

ATS also provides notification through hooks like emailing a User Group when a user selects the Subsystem impacted. This is done through AtsApi.AtsNotifyIAtsNotificationService
