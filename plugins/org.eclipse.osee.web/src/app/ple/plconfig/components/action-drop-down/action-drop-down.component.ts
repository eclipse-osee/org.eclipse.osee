import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { action, CreateAction, PLConfigCreateAction, transitionAction, TransitionActionDialogData } from '../../types/pl-config-actions';
import { commitResponse } from '../../types/pl-config-responses';
import { CommitBranchDialogComponent } from '../commit-branch-dialog/commit-branch-dialog.component';
import { CreateActionDialogComponent } from '../create-action-dialog/create-action-dialog.component';
import { TransitionActionToReviewDialogComponent } from '../transition-action-to-review-dialog/transition-action-to-review-dialog.component';

@Component({
  selector: 'plconfig-action-dropdown',
  templateUrl: './action-drop-down.component.html',
  styleUrls: ['./action-drop-down.component.sass']
})
export class ActionDropDownComponent implements OnInit {
  branchInfo = this.currentBranchService.branchState;
  branchAction = this.currentBranchService.branchAction;
  branchWorkflow = this.currentBranchService.branchWorkFlow;
  actions: action[] =[];
  branchType: string = "";
  branchName: Observable<string> = new Observable<string>();
  parentBranch: string = "";
  workflowState: string = "";
  constructor(public dialog: MatDialog,
    private currentBranchService: PlConfigCurrentBranchService,
    private actionService: PlConfigActionService,
    private uiStateService: PlConfigUIStateService,
    private route: ActivatedRoute,
    private router: Router) { 
    this.branchAction.subscribe((value) => {
      this.actions = value;
    })
    this.branchInfo.subscribe((response) => {
      this.branchType = response.branchType;
      this.branchName = of(response.name);
      this.parentBranch = response && response.parentBranch && response.parentBranch.id || '-1';
    });
    this.branchWorkflow.subscribe((response) => {
      this.workflowState = response.State;
    })
  }

  ngOnInit(): void {
  }
  isBranchTransitionable() {
    if (this.workflowState==='InWork') {
      return true;
    }
    return false;
  }
  isBranchCommittable() {
    if (this.workflowState==='Review') {
      return true;
    }
    return false;
  }
  addAction(): void{
    let dialogData: PLConfigCreateAction = new PLConfigCreateAction(); 
    const dialogRef = this.dialog.open(CreateActionDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    });
    dialogRef.afterClosed().subscribe((value: PLConfigCreateAction) => {
      if (typeof(value?.description)!='undefined') {
        let formData: CreateAction = new CreateAction(value);
        this.actionService.createBranch(formData).subscribe((response) => {
          if (response.results.success) {
            let _branchType = '';
            if (response.workingBranchId.branchType === '2') {
              _branchType='baseline'
            } else {
              _branchType='working'
            }
            this.router.navigate([_branchType,response.workingBranchId.id], {
              relativeTo: this.route.parent,
              queryParamsHandling: 'merge',
            })
          }
        }) 
      }
    })
  }
  transitionToReview(): void{
    let dialogData = {
      actions: this.actions,
    };
    const dialogRef = this.dialog.open(TransitionActionToReviewDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    })
    dialogRef.afterClosed().subscribe((result: TransitionActionDialogData) => {
      if (result?.selectedUser?.artifactId != null &&
        result?.selectedUser?.artifactId != undefined &&
        result?.actions[0]?.id != null &&
        result?.actions[0]?.id != undefined) {
        this.actionService.validateTransitionAction(
          new transitionAction("Review", "Transition To Review", result))
          .subscribe((response) => {
            if (response.results.length === 0) {
              this.actionService.transitionAction(
                new transitionAction("Review", "Transition To Review", result))
                .subscribe((response) => {
                  if (response.results.length === 0) {
                    this.uiStateService.updateReqConfig = true;
                  }
              })
            }
          })
        }
      
    })
  }
  commitBranch(): void {
    let dialogData = {
      actions: this.actions,
    };
    const dialogRef = this.dialog.open(CommitBranchDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    })
    dialogRef.afterClosed().subscribe((result: TransitionActionDialogData) => {
      if (result?.selectedUser?.artifactId != null && result?.selectedUser?.artifactId!=undefined) {
        let body = {
          committer: result.selectedUser.artifactId,
          archive:"false"
        }
        this.currentBranchService.commitBranch(this.parentBranch,body).subscribe((commitResponse: commitResponse) => {
          if (commitResponse.success) {
            this.actionService.validateTransitionAction(new transitionAction("Completed", "Transition to Completed", result)).subscribe((validateResponse) => {
              if (validateResponse.results.length === 0) {
                this.actionService.transitionAction(new transitionAction("Completed", "Transition to Completed", result)).subscribe((response) => {
                  if (response.results.length > 0) {
                    this.uiStateService.error = response.results[0];
                  }
                  this.uiStateService.updateReqConfig = true;
                })  
              } else {
                this.uiStateService.error = validateResponse.results[0];
              }
            })
          }
        }) 
      }
    })
}

}
