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
import { Node,Edge } from '@swimlane/ngx-graph';
import { combineLatest, from, iif, Observable, of, Subject } from 'rxjs';
import { catchError, filter, map, reduce, repeatWhen, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ConnectionService } from './connection.service';
import { GraphService } from './graph.service';
import { NodeService } from './node.service';
import { RouteStateService } from './route-state-service.service';
import { connection } from '../../shared/types/connection'
import { node } from '../../shared/types/node'
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { transaction } from 'src/app/transactions/transaction';
import { settingsDialogData } from '../../shared/types/settingsdialog';

@Injectable({
  providedIn: 'root'
})
export class CurrentGraphService {

  private _nodes = this.routeStateService.id.pipe(
    share(),
    switchMap((val) => iif(() => val !== "" && val !== '-1' && val !== undefined,
      this.graphService.getNodes(val).pipe(
      map((split) => this.transform(split)),
      repeatWhen(_=>this.updated),
      share(),
      shareReplay(1),
    ),
      of({ nodes: [], edges: [] })
    )),
    shareReplay(1),
  )
  private _nodeOptions = this.routeStateService.id.pipe(
    share(),
    filter((val) => val !== "" && val !== '-1'),
    switchMap((val) => this.nodeService.getNodes(val).pipe(
      repeatWhen(_ => this.updated),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
  )
  private _applics = this.routeStateService.id.pipe(
    share(),
    switchMap(id => this.applicabilityService.getApplicabilities(id).pipe(
      repeatWhen(_ => this.updated),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
  )
  
  private _preferences = combineLatest([this.routeStateService.id, this.userService.getUser()]).pipe(
    share(),
    filter(([id, user]) => id !== "" && id !== '-1'),
    switchMap(([id, user]) => this.preferenceService.getUserPrefs(id, user).pipe(
      repeatWhen(_ => this.updated),
      share(),
      shareReplay(1)
    )),
    shareReplay(1)
  )

  private _branchPrefs = combineLatest([this.routeStateService.id, this.userService.getUser()]).pipe(
    share(),
    switchMap(([branch,user]) => this.preferenceService.getBranchPrefs(user).pipe(
      repeatWhen(_ => this.updated),
      share(),
      switchMap((branchPrefs) => from(branchPrefs).pipe(
        filter((pref) => !pref.includes(branch + ":")),
        reduce((acc, curr) => [...acc, curr], [] as string[]),
      )),
      shareReplay(1) 
    )),
    shareReplay(1),
  )
  private _update = new Subject<boolean>();
  constructor (private graphService: GraphService,private nodeService:NodeService, private connectionService: ConnectionService, private routeStateService: RouteStateService, private applicabilityService: ApplicabilityListService,private preferenceService: MimPreferencesService, private userService: UserDataAccountService) { }
  
  get nodes() {
    return this._nodes;
  }

  get updated() {
    return this._update;
  }

  set update(value: boolean) {
    this._update.next(true);
  }

  get nodeOptions() {
    return this._nodeOptions;
  }

  get applic() {
    return this._applics;
  }

  get preferences() {
    return this._preferences;
  }

  get BranchPrefs() {
    return this._branchPrefs;
  }

  updateConnection(connection: Partial<connection>) {
    return this.connectionService.changeConnection(this.routeStateService.id.getValue(), connection).pipe(
      take(1),
      switchMap(transaction => this.connectionService.performMutation(this.routeStateService.id.getValue(), transaction).pipe(
        tap(() => {
          this.update = true;
        })
      ))
    )
  }

  unrelateConnection(nodeId: string, id: string) {
    return this.nodes.pipe(
      take(1),
      switchMap((nodesArray) => from(nodesArray.edges).pipe( //turn into multi-emission
        filter((edge) => edge.source === nodeId || edge.target === nodeId), //only emit source/target edges
        switchMap((edge) => this.nodeService.getNode(this.routeStateService.id.getValue(), nodeId).pipe( //get node information
          take(1),
          switchMap((node) => iif(() => edge.source === nodeId, this.connectionService.createNodeRelation((node?.id)||'', false,id), this.connectionService.createNodeRelation((node?.id)||'', true,id)).pipe( //create primary relation if nodeId==source else nodeId==target create secondary relation
            switchMap((relation) => this.connectionService.deleteRelation(this.routeStateService.id.getValue(), relation).pipe( //turn into transaction
              switchMap((transaction) => this.connectionService.performMutation(this.routeStateService.id.getValue(), transaction).pipe(
                tap(() => {
                  this.update = true;
                })
              )) //send to /orcs/tx
            ))
          ))
        ))
      ))
    )
  }

  updateNode(node: Partial<node>) {
    return this.nodeService.changeNode(this.routeStateService.id.getValue(), node).pipe(
      take(1),
      switchMap((transaction) => this.nodeService.performMutation(this.routeStateService.id.getValue(), transaction).pipe(
        tap(() => {
            this.update = true;
        })
      ))
    )
  }
  deleteNode(nodeId: string) {
    return this.nodeService.deleteArtifact(this.routeStateService.id.getValue(), nodeId).pipe(
      take(1),
      switchMap((transaction) => this.nodeService.performMutation(this.routeStateService.id.getValue(), transaction).pipe(
        tap(() => {
          this.update = true;
        })
      ))
    )
  }

  deleteNodeAndUnrelate(nodeId: string, edges: Edge[]) {
    return this.deleteNode(nodeId);
  }

  createNewConnection(connection: connection, sourceId: string, targetId: string) {
    return this.nodeOptions.pipe(
      take(1),
      switchMap((nodes) => from(nodes.sort((a, b) =>((a?.id || '-1') < (b?.id || '-1') ? -1 : (a?.id || '-1') === (b?.id || '-1')?0:1))).pipe( //sorts nodes array
        filter((val) => val.id === sourceId || val.id === targetId),
        reduce((acc, curr) => [...acc, curr], [] as node[]),
        switchMap((nodeArray) => iif(() => nodeArray[0]?.id === sourceId && nodeArray[1]?.id === targetId,
          combineLatest([this.connectionService.createNodeRelation(nodeArray[0].name, false), this.connectionService.createNodeRelation(nodeArray[1].name, true)]).pipe(
            take(1),
            map(latest => [latest[0], latest[1]]),
            switchMap((relations) => this.connectionService.createConnection(this.routeStateService.id.getValue(), connection, relations).pipe(
              take(1),
              switchMap((newConnection) => this.connectionService.performMutation(this.routeStateService.id.getValue(), newConnection).pipe(
                tap(() => {
                  this.update = true;
                })
              ))
            ))
          ),//else flip order of target/source
          combineLatest([this.connectionService.createNodeRelation(nodeArray[0].name, true), this.connectionService.createNodeRelation(nodeArray[1].name, false)]).pipe(
            take(1),
            map(latest => [latest[0], latest[1]]),
            switchMap((relations) => this.connectionService.createConnection(this.routeStateService.id.getValue(), connection, relations).pipe(
              take(1),
              switchMap((newConnection) => this.connectionService.performMutation(this.routeStateService.id.getValue(), newConnection).pipe(
                tap(() => {
                  this.update = true;
                })
              ))
            ))
          )
        ))
      ))
    )
  }

  createNewNode(node: node) {
    return this.nodeService.createNode(this.routeStateService.id.getValue(), node).pipe(
      take(1),
      switchMap((transaction) => this.nodeService.performMutation(this.routeStateService.id.getValue(), transaction).pipe(
        tap(() => {
            this.update = true;
        })
      ))
    )
  }
  /**
   * Changes edges to have an id containing a+id
   * @param apiResponse api response containing nodes and edges
   * @returns transformation of api response
   */
  private transform(apiResponse: { nodes: Node[], edges: Edge[] }) {
    let returnObj: { nodes: Node[], edges: Edge[] }={nodes:[],edges:[]};
    apiResponse.nodes.forEach((node) => {
      returnObj.nodes.push({ ...node, id: node.id.toString() })
    });
    apiResponse.edges.forEach((edge) => {
      returnObj.edges.push({ ...edge, id: 'a'+edge?.id?.toString() })
    })
    return returnObj;
  }

  updatePreferences(preferences: settingsDialogData) {
    return this.createUserPreferenceBranchTransaction(preferences.editable).pipe(
      take(1),
      switchMap((transaction) => this.nodeService.performMutation(this.routeStateService.id.getValue(), transaction).pipe(
        take(1),
        tap(() => {
          this.update = true
        })
      )
      )
    )
  }

  private createUserPreferenceBranchTransaction(editMode:boolean) {
    return combineLatest(this.preferences, this.routeStateService.id, this.BranchPrefs).pipe(
      take(1),
      switchMap(([prefs, branch, branchPrefs]) =>
        iif(
        () => prefs.hasBranchPref,
          of<transaction>(
            {
              branch: "570",
              txComment: 'Updating MIM User Preferences',
              modifyArtifacts:
                [
                  {
                    id: prefs.id,
                    setAttributes:
                      [
                        { typeName: "MIM Branch Preferences", value: [...branchPrefs, `${branch}:${editMode}`] }
                      ],
                  }
                ]
            }
          ),
          of<transaction>(
            {
              branch: "570",
              txComment: "Updating MIM User Preferences",
              modifyArtifacts:
                [
                  {
                    id: prefs.id,
                    addAttributes:
                      [
                        { typeName: "MIM Branch Preferences", value: `${branch}:${editMode}` }
                      ]
                  }
                ]
              }
          ),
        )
      ))
  }
}
