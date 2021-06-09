import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PlConfigUserService } from '../../services/pl-config-user.service';
import { action, TransitionActionDialogData } from '../../types/pl-config-actions';

@Component({
  selector: 'app-commit-branch-dialog',
  templateUrl: './commit-branch-dialog.component.html',
  styleUrls: ['./commit-branch-dialog.component.sass']
})
export class CommitBranchDialogComponent implements OnInit {
  actionInfo: action;
  users = this.userService.usersSorted;
  constructor(public dialogRef: MatDialogRef<CommitBranchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransitionActionDialogData, private userService: PlConfigUserService) {
    this.actionInfo = this.data.actions[0];
   }

  ngOnInit(): void {
  }
  onNoClick() {
    this.dialogRef.close();
  }

}
