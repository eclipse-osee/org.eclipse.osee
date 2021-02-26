import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { action, TransitionActionDialogData } from '../../types/pl-config-actions';

@Component({
  selector: 'plconfig-transition-action-to-review-dialog',
  templateUrl: './transition-action-to-review-dialog.component.html',
  styleUrls: ['./transition-action-to-review-dialog.component.sass']
})
export class TransitionActionToReviewDialogComponent implements OnInit {
  actionInfo: action;
  users = this.actionService.users;
  constructor(public dialogRef: MatDialogRef<TransitionActionToReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransitionActionDialogData, private actionService: PlConfigActionService) {
    this.actionInfo = this.data.actions[0];
   }

  ngOnInit(): void {
  }
  onNoClick() {
    this.dialogRef.close();
  }

}
