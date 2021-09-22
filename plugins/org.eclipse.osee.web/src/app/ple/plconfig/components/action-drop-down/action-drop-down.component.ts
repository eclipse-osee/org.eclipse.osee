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
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { CreateAction, PLConfigCreateAction, transitionAction } from '../../types/pl-config-actions';
import { CreateActionDialogComponent } from '../create-action-dialog/create-action-dialog.component';

@Component({
  selector: 'plconfig-action-dropdown',
  templateUrl: './action-drop-down.component.html',
  styleUrls: ['./action-drop-down.component.sass']
})
export class ActionDropDownComponent implements OnInit {
  branchInfo = this.currentBranchService.branchState.pipe(share(), shareReplay(1));
  branchAction = this.currentBranchService.branchAction.pipe(share(), shareReplay(1));
  branchWorkflow = this.currentBranchService.branchWorkFlow.pipe(share(), shareReplay(1));
  userDisplayable = this.accountService.getUser();

  branchApproved = this.branchAction.pipe(switchMap((action) => iif(() => action.length > 0 && action[0].TeamWfAtsId.length > 0, this.actionService.getBranchApproved(action[0].TeamWfAtsId).pipe(
    switchMap((approval) => iif(() => approval.errorCount > 0, of('false'), of('true')))
  ))));

  teamsLeads = this.branchWorkflow.pipe(
    switchMap((workflow) => iif(() => workflow['ats.Team Definition Reference'].length > 0, this.actionService.getTeamLeads(workflow['ats.Team Definition Reference']), of([]))));

  branchTransitionable = this.branchWorkflow.pipe(
    switchMap((workflow) => iif(() => workflow.State === "InWork", of('true'), of('false'))));


  branchApprovable = combineLatest([this.branchApproved, this.teamsLeads, this.branchWorkflow, this.userDisplayable]).pipe
    (switchMap(([approved, leads, workflow, currentUser]) => iif(() => leads.filter(e => e.id === currentUser.id).length > 0 && approved === 'false' && workflow.State === 'Review', of('true'), of('false'))));

  branchCommitable = combineLatest([this.branchApproved, this.branchWorkflow]).pipe
    (switchMap(([approved, workflow]) => iif(() => approved === 'true' && workflow.State === 'Review', of('true'), of('false'))));

  doAddAction = this.userDisplayable.pipe(take(1),switchMap((thisUser) =>
    this.dialog.open(CreateActionDialogComponent, { data: new PLConfigCreateAction(thisUser), minWidth: '60%' }).afterClosed()
      .pipe(switchMap((value: PLConfigCreateAction) => iif(() => typeof (value?.description) != 'undefined',
        this.actionService.createBranch(new CreateAction(value), thisUser)
          .pipe(tap((createResponse) => {
            if (createResponse.results.success) {
              let _branchType = '';
              if (createResponse.workingBranchId.branchType === '2') {
                _branchType = 'baseline'
              } else {
                _branchType = 'working'
              }
              this.router.navigate([_branchType, createResponse.workingBranchId.id], {
                relativeTo: this.route.parent,
                queryParamsHandling: 'merge',
              })
            }
          }))
      )))));

  doApproveBranch = combineLatest([this.branchAction, this.userDisplayable]).pipe(take(1),
    switchMap((initialObs) => iif(() => initialObs[0].length > 0, this.actionService.approveBranch(initialObs[0][0].TeamWfAtsId, initialObs[1]).pipe(
      tap((response) => {
        if (response.results.length > 0) {
          this.uiStateService.error = response.results[0]
        } else {
          this.uiStateService.updateReqConfig = true;
        }
      })))));

  doTransition = combineLatest([this.branchAction, this.userDisplayable])
    .pipe(take(1), switchMap((initialObservable) =>
      this.actionService.validateTransitionAction(new transitionAction("Review", "Transition to Review", initialObservable[0], initialObservable[1]))
        .pipe(switchMap((secondObservable) => iif(() => secondObservable.results.length === 0,
          this.actionService.transitionAction(new transitionAction("Review", "Transition To Review", initialObservable[0], initialObservable[1]))
            .pipe(tap((response) => {
              if (response.results.length > 0) {
                this.uiStateService.error = response.results[0]
              } else {
                this.uiStateService.updateReqConfig = true;
              }
            })))))));
  doCommitBranch = combineLatest([this.branchInfo, this.branchAction, this.userDisplayable])
    .pipe(take(1),
      switchMap((initialObs) =>
        iif(() => initialObs[0].parentBranch.id.length > 0 && initialObs[1].length > 0 && initialObs[2].name.length > 0,
          this.currentBranchService.commitBranch(initialObs[0].parentBranch.id, { committer: initialObs[2].id, archive: 'false' })
            .pipe(switchMap((commitObs) =>
              iif(() => commitObs.success,
                this.actionService.validateTransitionAction(new transitionAction("Completed", "Transition to Completed", initialObs[1], initialObs[2]))
                  .pipe(switchMap((validateObs) =>
                    iif(() => validateObs.results.length === 0,
                      this.actionService.transitionAction(new transitionAction("Completed", "Transition To Completed", initialObs[1], initialObs[2]))
                        .pipe(tap((transitionResponse) => {
                          if (transitionResponse.results.length > 0) {
                            this.uiStateService.error = transitionResponse.results[0]
                          } else { this.uiStateService.updateReqConfig = true; }
                        })))))))))));

  constructor(public dialog: MatDialog,
    private currentBranchService: PlConfigCurrentBranchService,
    private actionService: PlConfigActionService,
    private uiStateService: PlConfigUIStateService,
    private accountService: UserDataAccountService,
    private route: ActivatedRoute,
    private router: Router) {

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
