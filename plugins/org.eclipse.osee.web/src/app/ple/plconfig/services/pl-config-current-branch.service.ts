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
import { Injectable } from '@angular/core';
import { from, iif, zip,Observable, of, combineLatest, OperatorFunction, interval } from 'rxjs';
import { switchMap, repeatWhen, share, mergeMap,filter, tap, finalize, take, shareReplay, distinct, distinctUntilChanged, groupBy, map, reduce, scan, startWith, distinctUntilKeyChanged, delay } from 'rxjs/operators';
import { changeInstance, changeTypeNumber, difference, ignoreType, ModificationType } from 'src/app/types/change-report/change-report.d';
import { ARTIFACTTYPEID } from 'src/app/types/constants/ArtifactTypeId.enum';
import { action, actionImpl, teamWorkflowImpl } from '../types/pl-config-actions';
import { PlConfigApplicUIBranchMapping, PlConfigApplicUIBranchMappingImpl, view, viewWithChanges } from '../types/pl-config-applicui-branch-mapping';
import { PlConfigBranchListingBranchImpl } from '../types/pl-config-branch';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import { configGroup, configGroupWithChanges, configuration, editConfiguration } from '../types/pl-config-configurations';
import { modifyFeature, writeFeature } from '../types/pl-config-features';
import { PlConfigActionService } from './pl-config-action.service';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';
import { ExtendedNameValuePair, ExtendedNameValuePairWithChanges } from '../types/base-types/ExtendedNameValuePair'
import { ATTRIBUTETYPEID } from 'src/app/types/constants/AttributeTypeId.enum';
import { extendedFeature, extendedFeatureWithChanges } from '../types/features/base';
import { SideNavService } from 'src/app/shared-services/ui/side-nav.service';
import { applic } from 'src/app/types/applicability/applic';
import { transportType } from '../../messaging/shared/types/connection';
import { transactionToken } from 'src/app/types/change-report/transaction-token';
import { NameValuePair } from '../types/base-types/NameValuePair';

@Injectable({
  providedIn: 'root'
})
export class PlConfigCurrentBranchService {
  private _branchApplicabilityNoChanges: Observable<PlConfigApplicUIBranchMapping> = this.uiStateService.branchId.pipe(
    switchMap(val => iif(() => val !== '',
      this.branchService.getBranchApplicability(val).pipe(
        repeatWhen(_ => this.uiStateService.updateReq),
        share(),
      ), of(new PlConfigApplicUIBranchMappingImpl())
    )
    ),
    share(),
    shareReplay({ bufferSize: 1, refCount: true })
  );
  private _branchApplicability = combineLatest([this.uiStateService.branchId, this.uiStateService.isInDiff, this._branchApplicabilityNoChanges, this.differences]).pipe(
    switchMap(([branchId,mode, applic,differences]) => iif(() => mode && applic !== new PlConfigApplicUIBranchMappingImpl(),
      of(differences).pipe(
        filter((val) => val !== undefined && val!==[]) as OperatorFunction<changeInstance[] | undefined, changeInstance[]>,
        switchMap((differenceArray) => of(differenceArray).pipe(
          map((differenceArray) => differenceArray.sort((a, b) => ["111", "222", "333", "444"].indexOf(a.changeType.id) - ["111", "222", "333", "444"].indexOf(b.changeType.id))),
          mergeMap((differences) => from(differences).pipe(
            filter((val) => val.ignoreType !== ignoreType.DELETED_AND_DNE_ON_DESTINATION),
            switchMap((change) => iif(() => change.changeType.id === changeTypeNumber.ARTIFACT_CHANGE,
              iif(() => change.itemTypeId === ARTIFACTTYPEID.CONFIGURATION && change.currentVersion.transactionToken.id !== '-1',
                iif(() => change.deleted && !applic.views.map(a => a.id).includes(change.artId),
                  of(change).pipe(
                    tap(() => {
                      applic.features.forEach((feature) => {
                        feature.configurations.push({ id: change.artId, name: '', value: feature.defaultValue, values: [feature.defaultValue] })
                      })
                      applic.views.push({
                        id: change.artId,
                        name: '',
                        hasFeatureApplicabilities: false,
                        productApplicabilities: [],
                        added: false,
                        deleted: true,
                        changes: {}
                      })
                    })
                  ),
                  iif(() => change.currentVersion.modType === ModificationType.NEW && change.currentVersion.value === null && applic.views.some((val) => val.id === change.artId),
                    of(change).pipe(
                      tap(() => {
                        if (applic.views.find((val) => val.id === change.artId) !== undefined) {
                          (applic.views.find((val) => val.id === change.artId) as viewWithChanges).added = true;
                          (applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes = {}; 
                        }
                      })
                    ),
                    iif(() => (applic.views.find((val) => val.productApplicabilities?.includes(change.currentVersion.value as string)) !== undefined),
                      of(change).pipe(
                        tap(() => {
                          if ((applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes === undefined) {
                            (applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes = {};
                          }
                          if ((applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes.productApplicabilities === undefined) {
                            (applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes.productApplicabilities = [];
                          }
                          if (!((applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes.productApplicabilities as difference[]).some((val) => val.currentValue === change.currentVersion.value)) {
                            ((applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes.productApplicabilities as difference[]).push({ currentValue: change.currentVersion.value, previousValue: change.baselineVersion.value, transactionToken: change.currentVersion.transactionToken })
                          }
                        })
                      ),
                      iif(() => change.currentVersion.modType === ModificationType.DELETED && change.currentVersion.value === null,
                        of(change).pipe(
                          tap(() => {
                            (applic.views.find((val) => val.id === change.artId) as viewWithChanges).deleted = true
                          })
                        ),
                        of()
                      )
                    )
                  )
                ),
                iif(() => change.itemTypeId === ARTIFACTTYPEID.CONFIGURATION_GROUP && change.currentVersion.transactionToken.id !== '-1',
                  iif(() => change.deleted && !applic.groups.map(a => a.id).includes(change.artId),
                    of(change).pipe(
                      tap(() => {
                        applic.features.forEach((feature) => {
                          feature.configurations.push({id:change.artId,name:'',value:feature.defaultValue,values:[feature.defaultValue]})
                        })
                        applic.groups.push({
                          id: change.artId,
                          name: '',
                          configurations: [],
                          deleted:true
                        })
                      })
                    ),
                    iif(() => change.currentVersion.modType === ModificationType.NEW && change.currentVersion.value === null,
                      of(change).pipe(
                        tap(() => {
                          if (applic.groups.find((val) => val.id === change.artId) !== undefined) {
                            (applic.groups.find((val) => val.id === change.artId) as configGroupWithChanges).added = true;
                            (applic.groups.find((val) => val.id === change.artId) as configGroupWithChanges).changes = {}; 
                          }
                        })
                      ),
                      iif(() => change.currentVersion.modType === ModificationType.DELETED && change.currentVersion.value === null,
                        of(change).pipe(
                          tap(() => {
                            if (applic.groups.find((val) => val.id === change.artId) !== undefined) {
                              (applic.groups.find((val) => val.id === change.artId) as configGroupWithChanges).deleted = true 
                            }
                          })
                        ),
                        of()
                      )
                    )
                  ),
                  iif(() => change.itemTypeId === ARTIFACTTYPEID.FEATURE && change.currentVersion.transactionToken.id !== '-1',
                    iif(() => change.deleted && !applic.features.map(a => a.id).includes(change.artId),
                      of(change).pipe(
                        tap(() => {
                          let configurations: ExtendedNameValuePair[] = [];
                          applic.views.forEach((view) => {
                            configurations.push({id:view.id,name:view.name,value:'',values:[]})
                          })
                          applic.groups.forEach((group) => {
                            configurations.push({id:group.id,name:group.name,value:'',values:[]})
                          })
                          applic.features.push({
                            id: change.artId,
                            name: '',
                            description: '',
                            defaultValue: '',
                            multiValued: false,
                            deleted: true,
                            configurations: configurations,
                            type: null,
                            valueType: "",
                            values: [],
                            productApplicabilities:[],
                            setValueStr() { },
                            setProductAppStr(){},
                          })
                        })
                      ),
                      iif(() => change.currentVersion.modType === ModificationType.NEW && change.currentVersion.value === null,
                        of(change).pipe(
                          tap(() => {
                            if (applic.features.find((val) => val.id === change.artId)) {
                              (applic.features.find((val) => val.id === change.artId) as extendedFeatureWithChanges).added = true; 
                            }
                          })
                        ),
                        iif(() => change.currentVersion.modType === ModificationType.DELETED && change.currentVersion.value === null,
                        of(change).pipe(
                          tap(() => {
                            if (applic.features.find((val) => val.id === change.artId) !== undefined) {
                              (applic.features.find((val) => val.id === change.artId) as extendedFeatureWithChanges).deleted = true 
                            }
                          })
                        ),
                        of()
                      ))),
                    of()
                  )
                )
              ),
              iif(() => change.changeType.id === changeTypeNumber.ATTRIBUTE_CHANGE,
                iif(() => applic.features.map(a => a.id).includes(change.artId),
                  of(change).pipe(
                    tap(() => {
                      if ((applic.features.find((val) => val.id === change.artId) as extendedFeatureWithChanges).changes === undefined) {
                        (applic.features.find((val) => val.id === change.artId) as extendedFeatureWithChanges).changes = {};
                      }
                      this.updateFeatureAttributes(change, (applic.features.find((val) => val.id === change.artId) as extendedFeatureWithChanges))
                      if (change.itemTypeId === ATTRIBUTETYPEID.DEFAULTVALUE) {
                        ((applic.features.find((val) => val.id === change.artId) as extendedFeatureWithChanges).configurations.map((config) => {
                          if (config.id !== undefined) {
                            config.value = change.currentVersion.value as string;
                            config.values = [change.currentVersion.value as string]
                          }
                          return config;
                        }))
                      }
                    })
                  ),
                  iif(() => applic.views.map(a => a.id).includes(change.artId),
                    of(change).pipe(
                      tap(() => {
                        if ((applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes === undefined) {
                          (applic.views.find((val) => val.id === change.artId) as viewWithChanges).changes = {};
                        }
                        this.updateViewAttributes(change, (applic.views.find((val) => val.id === change.artId) as viewWithChanges))
                        if (change.deleted && change.itemTypeId === ATTRIBUTETYPEID.NAME) {
                          applic.features.forEach((feature) => {
                            if ((feature.configurations.find((val) => val.id === change.artId) !== undefined)){
                              (feature.configurations.find((val) => val.id === change.artId) as ExtendedNameValuePair).name = change.currentVersion.value as string; 
                            }
                          })
                        }
                      })
                    ),
                    iif(() => applic.groups.map(a => a.id).includes(change.artId),
                      of(change).pipe(
                        tap(() => {
                          if ((applic.groups.find((val) => val.id === change.artId) as configGroupWithChanges).changes === undefined) {
                            (applic.groups.find((val) => val.id === change.artId) as configGroupWithChanges).changes = {};
                          }
                          this.updateGroupAttributes(change, (applic.groups.find((val) => val.id === change.artId) as configGroupWithChanges));
                          if (change.deleted && change.itemTypeId === ATTRIBUTETYPEID.NAME) {
                            applic.features.forEach((feature) => {
                              (feature.configurations.find((val) => val.id === change.artId) as ExtendedNameValuePair).name = change.currentVersion.value as string;
                            })
                          }
                        })
                      ),
                      of()
                    )
                  )
                ),
                iif(() => change.changeType.id === changeTypeNumber.RELATION_CHANGE,
                  of(),
                  iif(() => change.changeType.id === changeTypeNumber.TUPLE_CHANGE,
                    iif(() => typeof change.currentVersion.value === 'string' && change.currentVersion.value.includes('Tuple2|'),
                      //first token is view/group
                      //second token is applicability token
                      iif(() => change.itemTypeId === '2', //filter to only be Applicability Definitions
                        of(change),
                        of() //other tuples DNC currently
                      ),
                      of()
                    ),
                    of())
                )
              )
            ))
          )),
          take(differenceArray.filter((val)=>val.ignoreType!==ignoreType.DELETED_AND_DNE_ON_DESTINATION).length),
          reduce((acc, curr) => [...acc, curr], [] as (changeInstance | any)[]),
          switchMap((changes)=>this.processTupleChanges(changes.filter((val)=>val.changeType.id === changeTypeNumber.TUPLE_CHANGE && val.ignoreType !== ignoreType.DELETED_AND_DNE_ON_DESTINATION),applic,branchId)),
          switchMap((val)=>of(applic))
        )),
      ),
      of(applic)
    )),
    share(),
    shareReplay({ bufferSize: 1, refCount: true })
  )
  private _branchAction: Observable<action[]> = this._branchApplicabilityNoChanges.pipe(
    switchMap(val => iif(() => val.associatedArtifactId != '-1' && typeof val !== 'undefined' && val.associatedArtifactId !== '',
    this.actionService.getAction(val.associatedArtifactId).pipe(
      repeatWhen(_ => this.uiStateService.updateReq),
      share(),
      ),
      of([new actionImpl()])
    )
    ),
    share(),
    shareReplay({ bufferSize: 1, refCount: true })
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
    shareReplay({ bufferSize: 1, refCount: true })
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
  private _grouping = this.branchApplicability.pipe(
    switchMap((applicability) => of({ groups: applicability.groups, views: applicability.views }).pipe(
      switchMap((combined) => of(combined).pipe(
        mergeMap((filterer) => from(filterer.views).pipe(
          groupBy(v => combined.groups.find((val) => val.configurations.includes(v.id))),
          mergeMap((group$) => group$.pipe(
            reduce((acc, cur) => { acc.views.push(cur); return { group: group$.key||{configurations:[],id:'-1',name:'No Group'}, views: acc.views } },{group:{configurations:[],id:'-1',name:'No Group'},views:[]} as {group:configGroup,views:view[]})
          ))
        ))
      )),
      scan((acc, curr) => [...acc, curr], [] as { group: configGroup, views: view[] }[]),
      map((data) => data.sort((a, b) => Number(a && a.group && a.group.id) - Number(b && b.group && b.group.id))),
      distinctUntilChanged(),
    )),
    shareReplay({bufferSize:1,refCount:true})
  );

  private _topLevelHeaders = this._grouping.pipe(
    switchMap((response) => of(response).pipe(
      mergeMap((grouping) => from(grouping).pipe(
        filter((val)=>val.group.name!=='No Group')
      ))
    )),
    scan((acc, curr) => [...acc, curr], [] as { group: configGroup, views: view[] }[]),
    map((grouping) => grouping.length),
    switchMap((length) => iif(
      () => length > 0, of([' ', 'Configurations', 'Groups']),
      of([' ', 'Configurations'])),
    ),
    shareReplay({bufferSize:1,refCount:true})
  )
  private _secondaryHeaders = this._grouping.pipe(
    switchMap((response) => of(response).pipe(
      mergeMap((grouping) => from(grouping).pipe(
        switchMap((grouping) => of(grouping.group).pipe(
          map((group)=>group.name)
        ))
      )),
      distinct(),
      scan((acc, cur) => { acc = this.makeHeaderUnique([...acc, cur]); return acc},['  '] as string[]),
    )),
    shareReplay({bufferSize:1,refCount:true})
  )
  private _secondaryHeaderLength = this._grouping.pipe(
    switchMap((response) => of(response).pipe(
      mergeMap((grouping) => from(grouping).pipe(
      )),
      distinct(),
      scan((acc, cur) => { acc.push(cur !== undefined && cur.group !== undefined && cur.group.id !== '-1' ? cur.views.length + 1 : cur.views.length); return acc }, [1] as number[]),
    )),
    shareReplay({bufferSize:1,refCount:true})
  )
  private _headers = this._grouping.pipe(
    switchMap((response) => of(response).pipe(
      mergeMap((grouping) => from(grouping).pipe(
        switchMap((grouping) => iif(() => grouping.group.name !== 'No Group', from(grouping.views).pipe(
          startWith({ id: '', name: grouping.group.name, hasFeatureApplicabilities: false, productApplicabilities: [] }),
          switchMap((view)=>of(view.name))
        ), from(grouping.views).pipe(
          switchMap((view)=>of(view.name))
        )))
      )),
      distinct(),
      scan((acc, cur) => [...acc, cur], ['feature'] as string[]),
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _viewCount = this._grouping.pipe(
    switchMap((response) => of(response).pipe(
      mergeMap((responseGrouping) => from(responseGrouping).pipe(
        filter((grouping) => grouping.group.name === 'No Group'),
        map((group)=>group.views.length)
      ))
    )),
    shareReplay({bufferSize:1,refCount:true})
  )
  private _groupCount = this._grouping.pipe(
    switchMap((grouping) => of(grouping).pipe(
      mergeMap((grouping) => from(grouping).pipe(
        map((group)=>group.views.length+1)  
      )),
      scan((acc,curr)=>acc+curr,0),
    )),
    shareReplay({bufferSize:1,refCount:true})
  )
  private _groupList = this._grouping.pipe(
    switchMap((response) => of(response).pipe(
      mergeMap((responseGrouping) => from(responseGrouping).pipe(
        filter((grouping) => grouping.group.name !== 'No Group'),
        map((group)=>group.group)
      )),
    )),
    distinctUntilChanged(),
    scan((acc, curr) => [...acc, curr], [] as configGroup[]),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _branchApplicEditable = this._branchApplicabilityNoChanges.pipe(
    map((applic)=>applic.editable)
  )
  private _branchApplicFeatures = this._branchApplicabilityNoChanges.pipe(
    map((applic)=>applic.features)
  )
  private _branchApplicViews = this._branchApplicabilityNoChanges.pipe(
    map((applic)=>applic.views)
  )
  private _branchApproved = this.branchAction.pipe(switchMap((action) => iif(() => action.length > 0 && action[0].TeamWfAtsId.length > 0, this.actionService.getBranchApproved(action[0].TeamWfAtsId).pipe(
    shareReplay({bufferSize:1,refCount:true}),
    switchMap((approval) => iif(() => approval.errorCount > 0, of('false'), of('true'))),
    shareReplay({bufferSize:1,refCount:true})
  ))));
  private _teamsLeads = this.branchWorkFlow.pipe(
    switchMap((workflow) => iif(() => workflow['ats.Team Definition Reference'].length > 0, this.actionService.getTeamLeads(workflow['ats.Team Definition Reference']), of([]))),
    shareReplay({ bufferSize: 1, refCount: true }));
   private _branchTransitionable = this.branchWorkFlow.pipe(
     switchMap((workflow) => iif(() => workflow.State === "InWork", of('true'), of('false'))),
     shareReplay({ bufferSize: 1, refCount: true })
   );
  constructor (private uiStateService: PlConfigUIStateService, private branchService: PlConfigBranchService, private actionService: PlConfigActionService, private sideNavService: SideNavService) {}
  public get branchApplicability() {
    return this._branchApplicability;
  }
  public get groupList() {
    return this._groupList;
  }

  public get groupCount() {
    return this._groupCount;
  }

  get viewCount() {
    return this._viewCount;
  }

  get headers() {
    return this._headers;
  }

  get secondaryHeaders() {
    return this._secondaryHeaders;
  }

  get secondaryHeaderLength() {
    return this._secondaryHeaderLength;
  }

  get grouping() {
    return this._grouping;
  }

  get topLevelHeaders() {
    return this._topLevelHeaders;
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
  public get branchApproved() {
    return this._branchApproved;
  }
  public get teamsLeads() {
    return this._teamsLeads;
  }
  public get branchTransitionable() {
    return this._branchTransitionable;
  }
  public get branchApplicEditable() {
    return this._branchApplicEditable;
  }

  public get branchApplicFeatures() {
    return this._branchApplicFeatures;
  }
  public get branchApplicViews() {
    return this._branchApplicViews;
  }
  get differences() {
    return this.uiStateService.differences;
  }
  set difference(value: changeInstance[]) {
    this.uiStateService.difference=value;
  }

  set sideNav(value: { opened: boolean, field: string, currentValue: string | number | applic | transportType | boolean, previousValue?: string | number | applic | transportType | boolean, transaction?: transactionToken, user?: string, date?: string }) {
    this.sideNavService.sideNav = value;
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
  public modifyConfiguration(featureId: string, body: string, groups: configGroup[]) {
    return this.editConfiguration(featureId, body).pipe(
      switchMap((val) =>
        iif(() => val.success,
          from(groups).pipe(
            mergeMap((elem) =>
              this.synchronizeGroup(elem.id).pipe(
            ),
            )
          )
        ),
      ),
      take(groups.length),
      reduce((acc, curr) => { if (curr.results.length > 0) { acc.push(curr.results[0]) } return acc; }, [] as string[]),
      switchMap((val) => iif(() => val.length > 0,
        of(val).pipe(
          tap((val) => {
            val.forEach((el) => {
              this.uiStateService.error = el;
            })
          })
        ),
        of())),
      tap(() => {
        this.uiStateService.updateReqConfig = true;
      })
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
      switchMap((val) => iif(() => val.success&&(typeof body?.configurationGroup!=='undefined')&&(body?.configurationGroup!=='')&&(body?.configurationGroup!=='0'),
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
  /**
   * @todo fix
   * @param configId 
   * @returns 
   */
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
  makeHeaderUnique(names: string[]) {
    let newArray: string[] = [];
    for (let name of names) {
      newArray.push(name+" ")
    }
    return newArray
  }

  findViewByName(viewName: string) {
    return this.grouping.pipe(
      switchMap((response) => of(response).pipe(
        take(1),
        mergeMap((responseGrouping) => from(responseGrouping).pipe(
          mergeMap((grouping) => from(grouping.views).pipe(
            switchMap((view)=>iif(()=>view.name===viewName,of(view),of(undefined)))
          ))
        ))
      )),
      scan((acc, curr) => { if (curr !== undefined) { acc = curr; } return acc; }, undefined as view | viewWithChanges | undefined),
    )
  }
  findViewById(viewId: string) {
    return this.grouping.pipe(
      take(1),
      switchMap((response) => of(response).pipe(
        take(1),
        mergeMap((responseGrouping) => from(responseGrouping).pipe(
          mergeMap((grouping) => from(grouping.views).pipe(
            switchMap((view)=>iif(()=>view.id===viewId,of(view),of(undefined)))
          ))
        ))
      )),
      scan((acc, curr) => { if (curr !== undefined) { acc = curr; } return acc; }, undefined as view | viewWithChanges | undefined),
    )
  }

  findGroup(groupName: string) {
    return this.groupList.pipe(
      switchMap((response) => of(response).pipe(
        mergeMap((responseGrouping) => from(responseGrouping).pipe(
          switchMap((grouping)=>iif(()=>grouping.name===groupName,of(grouping),of(undefined)))
        ))
      )),
      scan((acc, curr) => { if (curr !== undefined) { acc = curr; } return acc; }, undefined as configGroup| configGroupWithChanges | undefined),
    )
  }

  updateFeatureAttributes(change: changeInstance, feature: extendedFeatureWithChanges) {
    let changes:difference = {
      currentValue: change.currentVersion.value,
      previousValue: change.baselineVersion.value,
      transactionToken:change.currentVersion.transactionToken
    }
    if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
      if (feature.name === change.currentVersion.value as string && !feature.deleted) {
        feature.changes.name = changes;
      }
      if (feature.deleted) {
        feature.name = change.currentVersion.value as string;
        feature.changes.name = {
          currentValue: change.destinationVersion.value,
          previousValue: change.baselineVersion.value,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
      if (feature.description === change.currentVersion.value as string && !feature.deleted) {
        feature.changes.description = changes;
      }
      if (feature.deleted) {
        feature.description = change.currentVersion.value as string;
        feature.changes.description = {
          currentValue: change.destinationVersion.value,
          previousValue: change.baselineVersion.value,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.PRODUCT_TYPE) {
      if (feature.productApplicabilities === undefined) {
        feature.productApplicabilities = [];
      }
      if (feature.changes.productApplicabilities === undefined) {
        feature.changes.productApplicabilities = [];
      }
      if (feature.productApplicabilities?.some((val)=>val===change.currentVersion.value as string) && !feature.deleted && !feature.changes.productApplicabilities?.some((val)=>val.currentValue===change.currentVersion.value) && change.currentVersion.transactionToken.id!=='-1') {
        feature.changes.productApplicabilities?.push(changes)
      }
      if (feature.deleted && !feature.productApplicabilities?.some((val) => val === change.currentVersion.value as string) && change.currentVersion.transactionToken.id !== '-1') {
        feature.productApplicabilities?.push(change.currentVersion.value as string);
        if (!feature.changes.productApplicabilities?.some((val) => val.previousValue === change.baselineVersion.value as string)) {
          feature.changes.productApplicabilities?.push({
            currentValue: change.destinationVersion.value,
            previousValue: change.baselineVersion.value,
            transactionToken: change.currentVersion.transactionToken
          })
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.MULTIVALUED) {
      if (feature.multiValued === (change.currentVersion.value as string === 'true') && !feature.deleted) {
        feature.changes.multiValued = {
          currentValue: (change.currentVersion.value as string==='true'),
          previousValue: (change.baselineVersion.value as string === 'true'),
          transactionToken:change.currentVersion.transactionToken
        }
      }
      if (feature.deleted) {
        feature.multiValued = (change.currentVersion.value as string==='true');
        feature.changes.multiValued = {
          currentValue: (change.destinationVersion.value as string === 'true'),
          previousValue: (change.baselineVersion.value as string ==='true'),
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.FEATUREVALUE) {
      if (feature.valueType === change.currentVersion.value as string && !feature.deleted) {
        feature.changes.valueType = changes;
      }
      if (feature.deleted) {
        feature.valueType = change.currentVersion.value as string;
        feature.changes.valueType = {
          currentValue: change.destinationVersion.value,
          previousValue: change.baselineVersion.value,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.DEFAULTVALUE) {
      if (feature.defaultValue === change.currentVersion.value as string && !feature.deleted) {
        feature.changes.defaultValue = changes;
      }
      if (feature.deleted) {
        feature.defaultValue = change.currentVersion.value as string;
        feature.changes.defaultValue = {
          currentValue: change.destinationVersion.value,
          previousValue: change.baselineVersion.value,
          transactionToken: change.currentVersion.transactionToken
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.VALUE) {
      if (feature.changes.values === undefined) {
        feature.changes.values = [];
      }
      if (feature.values?.some((val)=>val===change.currentVersion.value as string) && !feature.deleted && !feature.changes.values?.some((val)=>val.currentValue===change.currentVersion.value) && change.currentVersion.transactionToken.id!=='-1') {
        feature.changes.values?.push(changes)
      }
      if (feature.deleted && !feature.values?.some((val) => val === change.currentVersion.value as string) && change.currentVersion.transactionToken.id !== '-1') {
        feature.values?.push(change.currentVersion.value as string);
        if (!feature.changes.values?.some((val) => val.previousValue === change.baselineVersion.value as string)) {
          feature.changes.values?.push({
            currentValue: change.destinationVersion.value,
            previousValue: change.baselineVersion.value,
            transactionToken: change.currentVersion.transactionToken
          })
        }
      }
    }
  }
  updateGroupAttributes(change: changeInstance, group: configGroupWithChanges) {
    let changes:difference = {
      currentValue: change.currentVersion.value,
      previousValue: change.baselineVersion.value,
      transactionToken:change.currentVersion.transactionToken
    }
    if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
      if (group.name === change.currentVersion.value as string && !group.deleted) {
        group.changes.name = changes;
      }
      if (group.deleted) {
        group.name = change.currentVersion.value as string;
        group.changes.name = {
          currentValue: change.destinationVersion.value,
          previousValue: change.baselineVersion.value,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else {
    }
  }
  updateViewAttributes(change: changeInstance, view: viewWithChanges) {
    let changes:difference = {
      currentValue: change.currentVersion.value,
      previousValue: change.baselineVersion.value,
      transactionToken:change.currentVersion.transactionToken
    }
    if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
      if (view.name === change.currentVersion.value as string && !view.deleted) {
        view.changes.name = changes; 
      }
      if (view.deleted) {
        view.name = change.currentVersion.value as string;
        view.changes.name = {
          currentValue: change.destinationVersion.value,
          previousValue: change.baselineVersion.value,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.itemTypeId === ATTRIBUTETYPEID.PRODUCT_TYPE) {
      if (view.changes.productApplicabilities === undefined) {
        view.changes.productApplicabilities = [];
      }
      if (view.productApplicabilities?.some((val)=>val===change.currentVersion.value as string) && !view.deleted && !view.changes.productApplicabilities?.some((val)=>val.currentValue===change.currentVersion.value) && change.currentVersion.transactionToken.id!=='-1') {
        view.changes.productApplicabilities?.push(changes)
      }
      if (view.deleted && !view.productApplicabilities?.some((val) => val === change.currentVersion.value as string) && change.currentVersion.transactionToken.id !== '-1') {
        view.productApplicabilities?.push(change.currentVersion.value as string);
        if (!view.changes.productApplicabilities?.some((val) => val.currentValue === change.currentVersion.value as string)) {
          view.changes.productApplicabilities?.push(changes)
        }
      }
    } else {
    }
    return view;
  }
  processTupleChanges(changes: changeInstance[], applic: PlConfigApplicUIBranchMapping, branchId: string) {
    return zip(from(changes.sort((a, b) => a.deleted && b.deleted ? 0 : a.deleted && !b.deleted ? 1 : !a.deleted && b.deleted ? -1 : 0)), interval(500).pipe(take(changes.length))).pipe(
      switchMap(([change, number]) => of(typeof change.currentVersion.value === 'string' && change.currentVersion.value !== null && (change.currentVersion.value).split("|", 2).length === 2 ? (change.currentVersion.value).split("|", 2)[1].split(", ") : []).pipe(
        filter(([id,appId])=>appId!=='1' && appId!=='-1'),
        switchMap(([id, appId]) => this.branchService.getApplicabilityToken(branchId, appId).pipe(
          filter((appToken)=>appToken.id!=='1'),
          tap((appToken) => {
            const [name, value] = appToken.name.split(" = ");
            if (change.currentVersion.modType === ModificationType.NEW) {
              if (applic.features.some((feature)=>feature.name===name)) {
                if (applic.views.some((view) => view.id === id)) {
                  let viewToChange = applic.views.find((view => view.id === id)) as view | viewWithChanges;
                  let feature = (applic.features.find((feature) => feature.name === name) as extendedFeatureWithChanges | extendedFeature);
                  let view =feature?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges
                  if (view.changes === undefined) {
                    if (feature?.multiValued) {
                      (applic.features.find((feature)=>feature.name===name)?.configurations.find((view)=>view.name===viewToChange.name) as ExtendedNameValuePairWithChanges).changes={value:{currentValue:view.value,previousValue:view.value.replace(value,'').replace(/,\s*$/, ""),transactionToken:change.currentVersion.transactionToken}}
                    } else {
                      (applic.features.find((feature)=>feature.name===name)?.configurations.find((view)=>view.name===viewToChange.name) as ExtendedNameValuePairWithChanges).changes={value:{currentValue:value,previousValue:'',transactionToken:change.currentVersion.transactionToken}}
                    }
                  } else {
                    if (feature.multiValued &&view.changes!==undefined) {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.currentValue = view.value;
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.previousValue = (view.changes.value.previousValue as string).replace(value,'').replace(/,\s*$/, "")
                    } else {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.currentValue = value;  
                    }
                  }
                }
                if (applic.groups.some((view) => view.id === id)) {
                  let viewToChange = applic.groups.find((view => view.id === id)) as configGroup | configGroupWithChanges;
                  let feature = (applic.features.find((feature) => feature.name === name) as extendedFeatureWithChanges | extendedFeature);
                  let view =feature?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges
                  if (view.changes === undefined) {
                    if (feature?.multiValued) {
                      (applic.features.find((feature)=>feature.name===name)?.configurations.find((view)=>view.name===viewToChange.name) as ExtendedNameValuePairWithChanges).changes={value:{currentValue:view.value,previousValue:view.value.replace(value,'').replace(/,\s*$/, ""),transactionToken:change.currentVersion.transactionToken}}
                    } else {
                      (applic.features.find((feature)=>feature.name===name)?.configurations.find((view)=>view.name===viewToChange.name) as ExtendedNameValuePairWithChanges).changes={value:{currentValue:value,previousValue:'',transactionToken:change.currentVersion.transactionToken}}
                    }
                  } else {
                    if (feature.multiValued &&view.changes!==undefined) {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.currentValue = view.value;
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.previousValue = (view.changes.value.previousValue as string).replace(value,'').replace(/,\s*$/, "")
                    } else {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.currentValue = value;  
                    }
                  }
                }
              } 
            } else if (change.currentVersion.modType === ModificationType.DELETED) {
              if (applic.features.some((feature) => feature.name === name)) {
                if (applic.views.some((view) => view.id === id)) {
                  let viewToChange = applic.views.find((view => view.id === id)) as view | viewWithChanges;
                  let feature = (applic.features.find((feature) => feature.name === name) as extendedFeatureWithChanges | extendedFeature);
                  let view = feature?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges;
                  if (view.changes === undefined) {
                    if (view.id) {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes = { value: { currentValue: '', previousValue: view.value.replace(value, '').replace(/,\s*$/, ""), transactionToken: change.currentVersion.transactionToken } };
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).value = view.value.replace(value, '').replace(/,\s*$/, "");
                    } else {
                      (applic.features.find((feature)=>feature.name===name)?.configurations.find((view)=>view.name===viewToChange.name) as ExtendedNameValuePairWithChanges).changes={value:{currentValue:view.value.replace(value,'').replace(/,\s*$/, ""),previousValue:view.value.replace(value,'').replace(/,\s*$/, ""),transactionToken:change.currentVersion.transactionToken}}
                    }
                  } else {
                    if (feature.multiValued &&view.changes!==undefined) {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.previousValue = (view.changes.value.previousValue as string).replace(value, '').replace(/,\s*$/, "");
                      if (view.id) {
                        (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).value = view.value.replace(value, '').replace(/,\s*$/, "");
                      }
                    } else {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.previousValue = value;
                      if (view.id) {
                        (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).value = value;
                      }
                    }
                  }
                }
                if (applic.groups.some((view) => view.id === id)) {
                  let viewToChange = applic.groups.find((view => view.id === id)) as configGroup | configGroupWithChanges;
                  let feature = (applic.features.find((feature) => feature.name === name) as extendedFeatureWithChanges | extendedFeature);
                  let view = feature?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges;
                  if (view.changes === undefined) {
                    if (view.id) {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes = { value: { currentValue: '', previousValue: view.value.replace(value, '').replace(/,\s*$/, ""), transactionToken: change.currentVersion.transactionToken } };
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).value = view.value.replace(value, '').replace(/,\s*$/, "");
                    } else {
                      (applic.features.find((feature)=>feature.name===name)?.configurations.find((view)=>view.name===viewToChange.name) as ExtendedNameValuePairWithChanges).changes={value:{currentValue:view.value.replace(value,'').replace(/,\s*$/, ""),previousValue:view.value.replace(value,'').replace(/,\s*$/, ""),transactionToken:change.currentVersion.transactionToken}}
                    }
                  } else {
                    if (feature.multiValued &&view.changes!==undefined) {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.previousValue = (view.changes.value.previousValue as string).replace(value, '').replace(/,\s*$/, "");
                      if (view.id) {
                        (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).value = view.value.replace(value, '').replace(/,\s*$/, "");
                      }
                    } else {
                      (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).changes.value.previousValue = value;
                      if (view.id) {
                        (applic.features.find((feature) => feature.name === name)?.configurations.find((view) => view.name === viewToChange.name) as ExtendedNameValuePairWithChanges).value = value;
                      }
                    }
                  }
                }
              }
            }
          })
        )),
        filter((value)=>value.id!=='1' && value.id!=='-1'),
        distinct(),
      )),
      scan((acc,curr)=>[...acc,curr],[] as NameValuePair[])
    )
  }
  isACfgGroup(name: string) {
    return this.groupList.pipe(
      switchMap((response) => of(response).pipe(
        mergeMap((responseGrouping) => from(responseGrouping).pipe(
          switchMap((grouping)=>iif(()=>grouping.name===name,of(true),of(false)))
        ))
      )),
      scan((acc, curr) => { if (curr !== false) { acc = curr; } return acc; }, false as boolean),
    )
  }
}
