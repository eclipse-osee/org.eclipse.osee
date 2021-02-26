import { Injectable } from '@angular/core';
import { from, iif, zip,Observable, of } from 'rxjs';
import { switchMap, repeatWhen, share, mergeMap,filter, tap, finalize, take } from 'rxjs/operators';
import { action, actionImpl, teamWorkflowImpl } from '../types/pl-config-actions';
import { PlConfigApplicUIBranchMapping, PlConfigApplicUIBranchMappingImpl } from '../types/pl-config-applicui-branch-mapping';
import { PlConfigBranchListingBranchImpl } from '../types/pl-config-branch';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import { configuration, configurationGroup, editConfiguration } from '../types/pl-config-configurations';
import { modifyFeature, writeFeature } from '../types/pl-config-features';
import { PlConfigActionService } from './pl-config-action.service';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';

@Injectable({
  providedIn: 'root'
})
export class PlConfigCurrentBranchService {
  private _branchApplicability: Observable<PlConfigApplicUIBranchMapping>=this.uiStateService.branchId.pipe(
    switchMap(val => iif(() => val !== '',
    this.branchService.getBranchApplicability(val).pipe(
      repeatWhen(_ => this.uiStateService.updateReq),
      share(),
    ),of(new PlConfigApplicUIBranchMappingImpl())
    )
    ),
    share(),
  );
  private _branchAction: Observable<action[]> = this.branchApplicability.pipe(
    switchMap(val => iif(() => val.associatedArtifactId != '-1' && typeof val !== 'undefined' && val.associatedArtifactId !== '',
    this.actionService.getAction(val.associatedArtifactId).pipe(
      repeatWhen(_ => this.uiStateService.updateReq),
      share(),
      ),
      of([new actionImpl()])
    )
    ),
    share(),
  )
  private _branchWorkflow = this.branchAction.pipe(
    switchMap(val => iif(() => (val[0]?.TeamWfAtsId != '' && typeof (val[0]?.id) !== 'undefined'),
    this.actionService.getWorkFlow(val[0].id).pipe(
      repeatWhen(_ => this.uiStateService.updateReq),
      share(),
    ), of(new teamWorkflowImpl())
    )
    ),
    share(),
  )
  private _branchState =this.uiStateService.branchId.pipe(
    filter(val => val !== '0'),
    switchMap(branchId => iif(() => branchId !== '0' && branchId !== '',
    this.branchService.getBranchState(branchId).pipe(
      repeatWhen(_ => this.uiStateService.updateReq),
      share(),
    ),of(new PlConfigBranchListingBranchImpl())
    )

    ),
    share()
  )
  private _cfgGroups =this.uiStateService.branchId.pipe(
    filter(val => val !== ''),
    switchMap(branchId => 
        this.branchService.getCfgGroups(branchId).pipe(
        repeatWhen(_ => this.uiStateService.updateReq),
        share(),
      )
      ),
    share()
  )
  private _branchListing = this.uiStateService.viewBranchType.pipe(
    switchMap(viewBranchType => iif(() => viewBranchType === 'all' || viewBranchType === 'working' || viewBranchType === 'baseline',
    this.branchService.getBranches(viewBranchType)
    )),
    share()
  )
  constructor(private uiStateService: PlConfigUIStateService, private branchService: PlConfigBranchService, private actionService: PlConfigActionService) {
   }
  public get branchApplicability() {
    return this._branchApplicability;
  }
  public get branchAction() {
    return this._branchAction;
  }
  public get branchWorkFlow() {
    return this._branchWorkflow;
  }
  public get branchState() {
    return this._branchState;
  }
  public get cfgGroups() {
    return this.uiStateService.branchId.pipe(
    filter(val => val !== ''),
    switchMap(branchId => 
        this.branchService.getCfgGroups(branchId).pipe(
        repeatWhen(_ => this.uiStateService.updateReq),
        share(),
      )
      ),
    share()
  )
  }
  public get branchListing() {
    return this._branchListing;
  }
  public getCfgGroupDetail(cfgGroup: string) {
    return this.uiStateService.branchId.pipe(
      filter(val => val !== ''),
      switchMap(branchId => this.branchService.getCfgGroupDetail(branchId, cfgGroup).pipe(
        tap(ev => this.setLoadingIndicator()),
        finalize(() => this.turnOffLoadingIndicator()),
        repeatWhen(_ => this.uiStateService.updateReq),
        share(),
      ))
    )
  }
  public editConfiguration(featureId: string, body: string) {
    return this.uiStateService.branchId.pipe(
      filter(val => val !== ''),
      switchMap(value => this.branchService.modifyConfiguration(value, featureId, body).pipe(
        tap((response) => {
          if (response.results.length === 0) {
            this.uiStateService.updateReqConfig = true;
            this.uiStateService.error = "";
          }
        })
      )
      ),
    )
  }
  // Modifies feature value for configuration
  public modifyConfiguration(featureId: string, body: string, groups:configurationGroup[]) {
    return this.editConfiguration(featureId, body).pipe(
      switchMap((val) =>
        iif(() => val.success,
          from(groups).pipe(
            mergeMap((elem) => zip(this.synchronizeGroup(elem.id).pipe(
              tap((value) => {
                this.uiStateService.updateReqConfig = true;
                if (value.results.length > 0) {
                  this.uiStateService.error=value.results[0]
                }
              })
              )
            ).pipe(tap((responses) => {
              let error = false;
              responses.forEach((syncResponse) => {
                if (syncResponse.results.length > 0) {
                  error = true;
                }
              })
              if (!error) {
                this.uiStateService.updateReqConfig = true;
                this.uiStateService.error = "";
              }
            })
            )
            )
          )
        ),
      ),
    );
  }
  public editConfigurationDetailsBase(body: editConfiguration) {
    return this.uiStateService.branchId.pipe(
      filter(val => val !== ''),
      switchMap((val) => this.branchService.editConfiguration(val, body).pipe(
        tap((response) => {
          if (response.success) {
            this.uiStateService.updateReqConfig = true;
            this.uiStateService.error = "";
          }
        })
      ))
    )
  }
  //modifies configuration
  public editConfigurationDetails(body: editConfiguration) {
    return this.editConfigurationDetailsBase(body).pipe(
      switchMap((val) => iif(() => val.success && (typeof body?.configurationGroup !== 'undefined') && (body?.configurationGroup !== ''),
        this.synchronizeGroup(body && body.configurationGroup || '').pipe(
          tap((response) => {
            if (response.success) {
              this.uiStateService.updateReqConfig = true;
              this.uiStateService.error = "";
            } else {
              this.uiStateService.error = response.results[0];
          }
        })
      )
      ))
    )
  }
  public addConfigurationBase(body:configuration) {
    return this.uiStateService.branchId.pipe(
      filter(val => val !== ''),
      switchMap(branchId => this.branchService.addConfiguration(branchId, body).pipe(
        tap((response) => {
          if (response.results.length === 0) {
            this.uiStateService.updateReqConfig = true;
            this.uiStateService.error = "";
          }
        })
      ))
    )
  }

  //adds configuration
  public addConfiguration(body: configuration) {
    return this.addConfigurationBase(body).pipe(
      tap((b) => {
        if (b.success) {
          this.uiStateService.updateReqConfig = true;
          this.uiStateService.error = "";
        }
      }),
      switchMap((val) => iif(() => val.success&&(typeof body?.configurationGroup!=='undefined')&&(body?.configurationGroup!==''),
        this.synchronizeGroup(body && body.configurationGroup || '').pipe(
          tap((response) => {
            if (response.success) {
              this.uiStateService.updateReqConfig = true;
              this.uiStateService.error = "";
            } else {
              this.uiStateService.error = response.results[0];
          }
        })
      )
      ))
    )
  }
  public deleteConfigurationBase(configId:string) {
    return this.uiStateService.branchId.pipe(
      filter(val => val !== ''),
      switchMap((val) => this.branchService.deleteConfiguration(configId, val).pipe(
        tap((results) => {
          if (results.success) {
            this.uiStateService.updateReqConfig = true;
            this.uiStateService.error = "";
          } else {
            this.uiStateService.error = results.results[0];
          }
        })
      ))
    )
  }
  public deleteConfiguration(configId: string) {
    return this.deleteConfigurationBase(configId).pipe(
      switchMap((val) => iif(() => val.success,
        this.uiStateService.groups.pipe(
          take(1),
          mergeMap((x) => from(x).pipe(
            mergeMap((y) => zip(this.synchronizeGroup(y).pipe(
              tap((a) => {
                if (a.success) {
                  this.uiStateService.updateReqConfig = true;
                  this.uiStateService.error = "";
                } else {
                  this.uiStateService.error=a.results[0];
                }
              })
            ))
              .pipe(
                tap((responses) => {
                  let error: string = "";
                  responses.forEach((response) => {
                    if (response.success) {
                      this.uiStateService.updateReqConfig = true;
                      this.uiStateService.error = "";
                    } else {
                      response.results.forEach((result) => {
                        error += result;
                      })
                    }
                  })
                  this.uiStateService.error = error;
                })
            ))
          ))
      )
      ))
    )
  }
  public synchronizeGroup(configId: string) {
    return this.uiStateService.branchId.pipe(
      filter(val => val !== ''),
      switchMap(branchId => this.branchService.synchronizeGroup(branchId, configId).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          }
        })
      ))
    )
  }
  public addFeature(feature:writeFeature) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.addFeature(branchId, feature).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          } else {
            this.uiStateService.updateReqConfig = true;
            this.uiStateService.error = "";
          }
        })
      ))
    )
  }

  public modifyFeature(feature:modifyFeature) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.modifyFeature(branchId, feature).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          } else {
            this.uiStateService.updateReqConfig = true;
            this.uiStateService.error = "";
          }
        })
      ))
    )
  }

  public deleteFeature(feature: string) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.deleteFeature(branchId, feature).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          }
        })
      ))
    )
  }
  public commitBranch(parentBranchId: string,body:{committer:string, archive:string}) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.commitBranch(branchId, parentBranchId, body).pipe(
        tap((val) => {
          if (val.results.results.length > 0) {
            this.uiStateService.error = val.results.results[0];
          }
        })
      ))
    )
  }
  public setLoadingIndicator() {
    if (!(this.uiStateService.loading.getValue()==='true')) {
      this.uiStateService.loadingBool = true;
    }
  }
  public turnOffLoadingIndicator() {
    if ((this.uiStateService.loading.getValue()==='true')) {
      this.uiStateService.loadingBool = false;
    }
  }
  public addConfigurationGroup(cfgGroup:ConfigurationGroupDefinition) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.addConfigurationGroup(branchId, cfgGroup).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          } else {
            this.uiStateService.updateReqConfig = true;
          }
        })
      ))
    )
  }
  public deleteConfigurationGroup(id:string) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.deleteConfigurationGroup(branchId, id).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          } else {
            this.uiStateService.updateReqConfig = true;
          }
        })
      ))
    )
  }
  public updateConfigurationGroup(cfgGroup: ConfigurationGroupDefinition) {
    return this.uiStateService.branchId.pipe(
      filter(val => val != ''),
      switchMap(branchId => this.branchService.updateConfigurationGroup(branchId, cfgGroup).pipe(
        tap((val) => {
          if (val.results.length > 0) {
            this.uiStateService.error = val.results[0];
          } else {
            this.uiStateService.updateReqConfig = true;
          }
        })
      ))
    )
  }
}
