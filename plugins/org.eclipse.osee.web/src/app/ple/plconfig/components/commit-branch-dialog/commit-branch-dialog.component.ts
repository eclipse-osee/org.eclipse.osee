import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { action, TransitionActionDialogData } from '../../types/pl-config-actions';

@Component({
  selector: 'app-commit-branch-dialog',
  templateUrl: './commit-branch-dialog.component.html',
  styleUrls: ['./commit-branch-dialog.component.sass']
})
export class CommitBranchDialogComponent implements OnInit {
  actionInfo: action;
  users = this.actionService.users;
  constructor(public dialogRef: MatDialogRef<CommitBranchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransitionActionDialogData, private actionService: PlConfigActionService) {
    this.actionInfo = this.data.actions[0];
   }

  ngOnInit(): void {
  }
  onNoClick() {
    this.dialogRef.close();
  }

}
