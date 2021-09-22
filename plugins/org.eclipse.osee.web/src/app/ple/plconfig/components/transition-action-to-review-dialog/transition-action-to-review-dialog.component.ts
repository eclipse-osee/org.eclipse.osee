/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PlConfigUserService } from '../../services/pl-config-user.service';
import { action, TransitionActionDialogData } from '../../types/pl-config-actions';

@Component({
  selector: 'plconfig-transition-action-to-review-dialog',
  templateUrl: './transition-action-to-review-dialog.component.html',
  styleUrls: ['./transition-action-to-review-dialog.component.sass']
})
export class TransitionActionToReviewDialogComponent implements OnInit {
  actionInfo: action;
  users = this.userService.usersSorted;
  constructor(public dialogRef: MatDialogRef<TransitionActionToReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransitionActionDialogData, private userService: PlConfigUserService) {
    this.actionInfo = this.data.actions[0];
   }

  ngOnInit(): void {
  }
  onNoClick() {
    this.dialogRef.close();
  }

}
