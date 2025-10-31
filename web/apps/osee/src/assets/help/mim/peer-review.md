# Peer Review Workflow

Peer reviews in MIM are used to combine changes from multiple branches into a single "Peer Review" branch. This is useful when a team's workflow requires changes to be made on individual branches, but need all of the changes on a single branch to review the complete ICD.

## Create a Peer Review Branch

To create the peer review branch, click the `Peer Review` button in the control cluster in the upper right side of the page to open the Peer Review Manager.

![Peer Review Button](../../images/mim/peer-review/peer-review-button.png)

In the Peer Review Manager dialog, click the `Create Peer Review` button, populate the fields (this is very similar to [creating a normal working branch](messaging/help/create-icd#create-an-action)), and click `Create Action`.

## Populate the Peer Review Branch

Now you will see a list of working branches that can be applied to the Peer Review branch. "Applying" in this case means taking all of the changes from the selected branches and putting them on the peer review branch. In this example, we are adding `TW2` and `TW3` to our peer review branch.

![Peer Review dialog with two branches selected to add](../../images/mim/peer-review/peer-review-added-selections.png)

Once those branches are applied, the "Branches to add" list will clear meaning the application is complete.

![Peer Review dialog after applying two branches](../../images/mim/peer-review/peer-review-applied.png)

Now, if we uncheck `TW3` to remove it from the PR, and check `TW4` to add it, we will see that reflected in the "Branches to add" and "Branches to remove" lists.

![Peer Review dialog after unchecking TW3 and checking TW4](../../images/mim/peer-review/peer-review-add-remove.png)

Applying these new selections will remove the changes from `TW3` and apply the changes from `TW4`. Now the peer review branch contains the changes from `TW2` and `TW4`.

## Commit Applied Branches

After the peer review is complete and all changes have been reviewed, the individual working branches should be committed to the baseline branch.

Next to each branch in the list is a `Transition` button. Clicking that will load the action state button, allowing the action to be transitioned and the branch to be committed.

Once an applied branch has been committed, working branches can no longer be added or removed from the peer review branch.

![Peer Review dialog after committing TW3](../../images/mim/peer-review/peer-review-committed.png)

## Closing the Peer Review

Once all of the working branches have been committed, the `Close Peer Review` button will become enabled. Clicking this and confirming will archive the peer review branch, and it will no longer appear in the branch list.

## Additional Notes

-   If additional changes are made to branches that have already been applied, those changes will not automatically apply to the peer review branch. You will need to go to the Peer Review Manager, uncheck the branches with the new changes, apply, re-check the branches with changes, and re-apply.
