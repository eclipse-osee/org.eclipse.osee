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
import { Observable } from 'rxjs';
import { share} from 'rxjs/operators';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { PlConfigUserService } from '../../services/pl-config-user.service';
import { actionableItem, PLConfigCreateAction, targetedVersion } from '../../types/pl-config-actions';

@Component({
  selector: 'app-create-action-dialog',
  templateUrl: './create-action-dialog.component.html',
  styleUrls: ['./create-action-dialog.component.sass']
})
export class CreateActionDialogComponent implements OnInit {
  users = this.userService.usersSorted;
  arb: Observable<actionableItem[]> = this.actionService.ARB.pipe(share());
  targetedVersions!: Observable<targetedVersion[]>;
  constructor(public dialogRef: MatDialogRef<CreateActionDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: PLConfigCreateAction, public actionService: PlConfigActionService, public userService: PlConfigUserService) { 
  }

  ngOnInit(): void {
  }
  onNoClick(): void{
    this.dialogRef.close();
  }
  selectActionableItem() {
    this.targetedVersions = this.actionService.getVersions(this.data.actionableItem.id);
  }

}
