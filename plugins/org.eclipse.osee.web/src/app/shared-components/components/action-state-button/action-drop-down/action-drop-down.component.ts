/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { switchMap } from 'rxjs/operators';
import { ActionStateButtonService } from '../../../services/action-state-button.service';
import { PLConfigCreateAction } from '../../../../ple/plconfig/types/pl-config-actions';
import { CreateActionDialogComponent } from '../create-action-dialog/create-action-dialog.component';

/**
 * Allows users to create and manage the state of a branch from within a page.
 */
@Component({
  selector: 'action-dropdown',
  templateUrl: './action-drop-down.component.html',
  styleUrls: ['./action-drop-down.component.sass']
})
export class ActionDropDownComponent implements OnInit,OnChanges {
  @Input() category: string = "0"
  @Input() workType:string=""
  branchInfo = this.actionService.branchState;

  branchTransitionable = this.actionService.branchTransitionable;


  branchApprovableOrCommittable = this.actionService.approvedState;
  doAddAction = this.actionService.addActionInitialStep.pipe(
    switchMap((thisUser) =>
      this.dialog
        .open(CreateActionDialogComponent, {
          data: new PLConfigCreateAction(thisUser),
          minWidth: '60%',
        })
        .afterClosed().pipe(
          switchMap((value) => this.actionService.doAddAction(value,this.category)),
      )
    )
  )

  doApproveBranch = this.actionService.doApproveBranch;

  doTransition = this.actionService.doTransition;
  doCommitBranch = this.actionService.doCommitBranch;

  constructor (private actionService: ActionStateButtonService,
    public dialog: MatDialog,) {
  }
  ngOnChanges(changes: SimpleChanges): void {
    this.actionService.category = this.category;
    this.actionService.workTypeValue = this.workType;
  }

  ngOnInit(): void {
  }

  addAction(): void {
    this.doAddAction.subscribe();
  }
  transitionToReview(): void {
    this.doTransition.subscribe();
  }
  approveBranch(): void {
    this.doApproveBranch.subscribe();
  }
  commitBranch(): void {
    this.doCommitBranch.subscribe();

  }

}
