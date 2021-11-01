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
import { BehaviorSubject, combineLatest, from, iif, Observable, of, Subject } from 'rxjs';
import { catchError, filter, map, reduce, repeatWhen, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ConnectionService } from './connection.service';
import { GraphService } from './graph.service';
import { NodeService } from './node.service';
import { RouteStateService } from './route-state-service.service';
import { connection, connectionWithChanges, OseeEdge, transportType } from '../../shared/types/connection'
import { node, nodeChanges, nodeData, nodeDataWithChanges, OseeNode } from '../../shared/types/node'
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { transaction, transactionToken } from 'src/app/transactions/transaction';
import { settingsDialogData } from '../../shared/types/settingsdialog';
import { applic } from '../../../../types/applicability/applic';
import { DiffUIService } from 'src/app/ple-services/httpui/diff-uiservice.service';
import { ATTRIBUTETYPEID } from '../../shared/constants/AttributeTypeId.enum';
import { ARTIFACTTYPEID } from '../../shared/constants/ArtifactTypeId.enum';
import { changeInstance, changeTypeEnum, itemTypeIdRelation } from '../../../../types/change-report/change-report.d';

@Injectable({
  providedIn: 'root'
})
export class CurrentGraphService {

  private _diff = this.diffService.diff;

  private _graph = this.routeStateService.id.pipe(
    switchMap((val) => iif(() => val !== "" && val !== '-1' && val !== undefined,
    this.graphService.getNodes(val).pipe(
    map((split) => this.transform(split)),
    repeatWhen(_=>this.updated),
    share(),
    shareReplay(1),
  ),
    of({ nodes: [], edges: [] }))
    )
  )
  private _nodes = combineLatest([this.routeStateService.isInDiff, this._graph]).pipe(
    switchMap(([diffState, graph]) => iif(() => diffState,
      this.differences.pipe(
        filter((val)=>val!==undefined),
        map((differences) => this.parseIntoNodesAndEdges(differences as changeInstance[], graph)),
      ),
      of(graph)
    )),
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

  private _sideNavContent = new Subject<{opened:boolean, field:string, currentValue:string|number|applic|transportType, previousValue?:string|number|applic|transportType,transaction?:transactionToken,user?:string,date?:string}>();
  private _update = new Subject<boolean>();
  private _differences = new BehaviorSubject<changeInstance[]|undefined>(undefined);
  constructor (private graphService: GraphService, private nodeService: NodeService, private connectionService: ConnectionService, private routeStateService: RouteStateService, private applicabilityService: ApplicabilityListService, private preferenceService: MimPreferencesService, private userService: UserDataAccountService, private diffService: DiffUIService) {}
  get differences() {
    return this._differences;
  }
  set difference(value: changeInstance[]) {
    this._differences.next(value);
  }
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
  get sideNavContent() {
    return this._sideNavContent;
  }

  get diff() {
    return this._diff;
  }

  set sideNav(value: {opened:boolean, field:string, currentValue:string|number|applic|transportType, previousValue?:string|number|applic|transportType,transaction?:transactionToken,user?:string,date?:string}) {
    this._sideNavContent.next(value);
  }

  get InDiff() {
    return this.routeStateService.isInDiff
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

  updateNode(node: Partial<node|nodeData>) {
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

  deleteNodeAndUnrelate(nodeId: string, edges: OseeEdge<connection|connectionWithChanges>[]) {
    return this.deleteNode(nodeId);
  }

  createNewConnection(connection: connection, sourceId: string, targetId: string) {
    return this.nodeOptions.pipe(
      take(1),
      switchMap((nodes) => from(nodes.sort((a, b) =>((a?.id || '-1') < (b?.id || '-1') ? -1 : (a?.id || '-1') === (b?.id || '-1')?0:1))).pipe( //sorts nodes array
        filter((val) => val.id === sourceId || val.id === targetId),
        reduce((acc, curr) => [...acc, curr], [] as node[]),
        switchMap((nodeArray) => iif(() => nodeArray[0]?.id === sourceId && nodeArray[1]?.id === targetId,
          combineLatest([this.connectionService.createNodeRelation(nodeArray[0].id||'', false), this.connectionService.createNodeRelation(nodeArray[1].id||'', true)]).pipe(
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
          combineLatest([this.connectionService.createNodeRelation(nodeArray[0].id||'', true), this.connectionService.createNodeRelation(nodeArray[1].id||'', false)]).pipe(
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

  createNewNode(node: node|nodeData) {
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
  private transform(apiResponse: { nodes: OseeNode<nodeData>[], edges: OseeEdge<connection>[] }) {
    let returnObj: { nodes: OseeNode<nodeData>[], edges: OseeEdge<connection>[] }={nodes:[],edges:[]};
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

  private parseIntoNodesAndEdges(changes: changeInstance[], graph: { nodes: OseeNode<nodeData|nodeDataWithChanges>[], edges: OseeEdge<connection|connectionWithChanges>[] }) {
    let newNodes: changeInstance[] = [];
    let newNodesId: string[] = [];
    let newConnections: changeInstance[] = [];
    let newConnectionsId: string[] = [];
    changes.forEach((change) => {
      //this loop is solely just for building a list of deleted nodes/connections
      if (change.itemTypeId === ARTIFACTTYPEID.CONNECTION && !newConnectionsId.includes(change.artId) && !newNodesId.includes(change.artId)) {
        //deleted connection
        newConnectionsId.push(change.artId);
      } else if (change.itemTypeId === ARTIFACTTYPEID.NODE && !newConnectionsId.includes(change.artId) && !newNodesId.includes(change.artId)) {
        //deleted node
        newNodesId.push(change.artId);
      }
    })
    changes.forEach((change) => {
      if (graph.nodes.find((val) => val.data.id === change.artId)) {
        let index = graph.nodes.indexOf(graph.nodes.find((val) => val.data.id === change.artId) as OseeNode<nodeData|nodeDataWithChanges>);
        graph.nodes[index]=this.nodeChange(change,graph.nodes[index])
      } else if (graph.edges.find((val) => val.data.id === change.artId)) {
        let index = graph.edges.indexOf(graph.edges.find((val) => val.data.id === change.artId) as OseeEdge<connection|connectionWithChanges>);
        graph.edges[index]=this.edgeChange(change,graph.edges[index])
      } else if (newConnectionsId.includes(change.artId)) {
        //deleted connection
        newConnections.push(change);
      } else if (newNodesId.includes(change.artId)) {
        //deleted node
        newNodes.push(change);
      }
    })
    newNodes.sort((a, b) => Number(a.artId) - Number(b.artId));
    newConnections.sort((a, b) => Number(a.artId) - Number(b.artId));
    let nodes = this.splitByArtId(newNodes);
    nodes.forEach((value) => {
      if (value.length > 0) {
        graph.nodes.push(this.fixNode(this.nodeDeletionChanges(value))); 
      }
    })
    let connections = this.splitByArtId(newConnections);
    connections.forEach((value) => {
      if (value.length > 0) {
        let edge = this.connectionDeletionChanges(value)
        if (edge.id !== '') {
          graph.edges.push(edge) 
        } 
      }
    })
    //search change array for relation changes,append as source,target
    changes.forEach((change) => {
      //not doing this currently, need to update UI to remove relation on both ends before this would work.
      if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
        let relType = change.itemTypeId as itemTypeIdRelation;
        if (relType.id === "6039606571486514296") {
          //sending node
        } else if (relType.id === "6039606571486514297") {
          //receiving node
        }
      }
    })
    return graph;
  }

  private splitByArtId(changes: changeInstance[]): changeInstance[][]{
    let returnValue: changeInstance[][] = [];
    let prev: Partial<changeInstance> | undefined = undefined;
    let tempArray: changeInstance[] = [];
    changes.forEach((value, index) => {
      if (prev !== undefined) {
        if (prev.artId === value.artId) {
          //condition where equal, add to array
          tempArray.push(value);
        } else {
          prev = Object.assign(prev, value);
          returnValue.push(tempArray);
          tempArray = [];
          tempArray.push(value);
          //condition where not equal, set prev to value, push old array onto returnValue, create new array
        }
      } else {
        tempArray = [];
        tempArray.push(value);
        prev = {}
        prev = Object.assign(prev, value);
        //create new array, push prev onto array, set prev 
      }
    })
    if (tempArray !== []) {
      returnValue.push(tempArray)
    }
    return returnValue;
  }

  private isNodeDataWithChanges(node: OseeNode<nodeData | nodeDataWithChanges>): node is OseeNode<nodeDataWithChanges> { return (node as OseeNode<nodeDataWithChanges>).data.changes !== undefined; }

  private initializeNode(node: OseeNode<nodeData | nodeDataWithChanges>): OseeNode<nodeDataWithChanges>{
    let tempNode: OseeNode<nodeDataWithChanges>;
    if (!this.isNodeDataWithChanges(node)) {
      //node.data = Object.assign < nodeData, changes: { nodeChanges >}>(node.data, { changes: {}})
      tempNode = node as OseeNode<nodeDataWithChanges>;
      tempNode.data.changes = {}
      return tempNode;
    } else {
      tempNode = node;
      return tempNode;
    }
  }
  private nodeChange(change: changeInstance, node: OseeNode<nodeData | nodeDataWithChanges>): OseeNode<nodeDataWithChanges> {
    return this.parseNodeChange(change, this.initializeNode(node));
  }

  private parseNodeChange(change: changeInstance, node: OseeNode<nodeDataWithChanges>) {
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.currentVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        node.data.changes.name = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        node.data.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACENODEADDRESS) {
        node.data.changes.interfaceNodeAddress = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACENODEBGCOLOR) {
        node.data.changes.interfaceNodeBgColor = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      node.data.changes.applicability = {
        previousValue: change.baselineVersion.applicabilityToken,
        currentValue: change.currentVersion.applicabilityToken,
        transactionToken:change.currentVersion.transactionToken
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      //do nothing currently
    }
    return node;
  }

  private initializeEdge(edge: OseeEdge<connection|connectionWithChanges>): OseeEdge<connectionWithChanges>{
    let tempEdge: OseeEdge<connectionWithChanges>;
    if (!this.isEdgeDataWithChanges(edge)) {
      //node.data = Object.assign < nodeData, changes: { nodeChanges >}>(node.data, { changes: {}})
      tempEdge = edge as OseeEdge<connectionWithChanges>;
      tempEdge.data.changes = {}
      return tempEdge;
    } else {
      tempEdge = edge;
      return tempEdge;
    }
  }
  private isEdgeDataWithChanges(edge: OseeEdge<connection|connectionWithChanges>): edge is OseeEdge<connectionWithChanges> { return (edge as OseeEdge<connectionWithChanges>).data.changes !== undefined; }
  private edgeChange(change: changeInstance, edge: OseeEdge<connection | connectionWithChanges>) {
    return this.parseEdgeChange(change, this.initializeEdge(edge));
  }
  private parseEdgeChange(change: changeInstance, edge: OseeEdge<connectionWithChanges>) {
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.currentVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        edge.data.changes.name = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        edge.data.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACETRANSPORTTYPE) {
        edge.data.changes.transportType = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      edge.data.changes.applicability = {
        previousValue: change.baselineVersion.applicabilityToken,
        currentValue: change.currentVersion.applicabilityToken,
        transactionToken:change.currentVersion.transactionToken
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      //do nothing currently
    }
    return edge;
  }
  private nodeDeletionChanges(changes: changeInstance[]): OseeNode<nodeDataWithChanges> {
    let tempNode: OseeNode<nodeDataWithChanges>={
      data: {
        deleted:true,
        id: '-1',
        name: '',
        changes: {},
        interfaceNodeAddress: '',
        interfaceNodeBgColor:''
      },
      id: '-1'
    };
    changes.forEach((value) => {
      tempNode = this.nodeDeletionChange(value, tempNode);
    })
    return tempNode;
  }

  private parseNodeDeletionChange(change: changeInstance, node: OseeNode<nodeDataWithChanges>) {
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.destinationVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        node.data.changes.name = changes;
        node.label = change.currentVersion.value as string;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        node.data.description = change.currentVersion.value as string;
        node.data.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACENODEADDRESS) {
        node.data.interfaceNodeAddress = change.currentVersion.value as string;
        node.data.changes.interfaceNodeAddress = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACENODEBGCOLOR) {
        node.data.interfaceNodeBgColor = change.currentVersion.value as string;
        node.data.changes.interfaceNodeBgColor = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      node.data.applicability = change.currentVersion.applicabilityToken as applic;
      node.data.changes.applicability = {
        previousValue: change.baselineVersion.applicabilityToken,
        currentValue: change.currentVersion.applicabilityToken,
        transactionToken:change.currentVersion.transactionToken
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      //do nothing currently
    }
    return node;
  }
  private nodeDeletionChange(change: changeInstance, node: OseeNode<nodeDataWithChanges>): OseeNode<nodeDataWithChanges>{
    node.id = change.artId;
    if (node.data === undefined) {
      node.data = {
        id: '-1',
        name: '',
        deleted:true,
        interfaceNodeAddress: '',
        interfaceNodeBgColor:'',
        changes: {}
      }
    }
    if (node.data.changes === undefined) {
      node.data.changes={}
    }
    return this.parseNodeDeletionChange(change,node)
  }

  private connectionDeletionChanges(changes: changeInstance[]): OseeEdge<connectionWithChanges>{
    let tempEdge: OseeEdge<connectionWithChanges> = {
      id: '', source: '', target: '',
      data:
      {
        deleted:true,
        dashed:false,
        changes: {},
        name: '',
        transportType:transportType.Ethernet
      }
    }
    changes.forEach((value) => {
      tempEdge = this.connectionDeletionChange(value, tempEdge);
    })
    return tempEdge;
  }
  private connectionDeletionChange(change: changeInstance, edge: OseeEdge<connectionWithChanges>): OseeEdge<connectionWithChanges>{
    edge.id = 'a' + change.artId;
    if (edge.data === undefined) {
      edge.data = {
        deleted:true,
        dashed:false,
        changes: {},
        name: '',
        transportType:transportType.Ethernet
      }
    }
    if (edge.data.changes === undefined) {
      edge.data.changes={}
    }
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.currentVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        edge.data.changes.name = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        edge.data.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACETRANSPORTTYPE) {
        edge.data.changes.transportType = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      edge.data.changes.applicability = {
        previousValue: change.baselineVersion.applicabilityToken,
        currentValue: change.currentVersion.applicabilityToken,
        transactionToken:change.currentVersion.transactionToken
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      //do nothing currently
    }
    return edge;
  }
  private fixNode(node: OseeNode<nodeData | nodeDataWithChanges>) {
    if (node.data.interfaceNodeBgColor === undefined) {
      node.data.interfaceNodeBgColor = "";
    }
    if (node.label === undefined) {
      node.label = "";
    }
    return node;
  }

  private parseConnectionOrderXML(xml: string) {
    let list = xml.split("<OrderList>")[1].split("</OrderList>")[0];
    let orders = list.split("/><");
    let orderArray: { list: string, orderType: string, relType: string, side: string }[] = [];
    orders[0] = orders[0].replace("<", "");
    orders[0] = '"' + orders[0];
    orders[orders.length - 1] = orders[orders.length - 1].replace("/>", "");
    orders.forEach((value, index) => {
      if (value.match("\"Order ")) {
        value = value.replace("Order ", '');
      } else {
        value = value.replace("Order ", '\"'); 
      }
      value = value.replace(new RegExp('(\"( +))', 'g'), '\", \"');
      value = value.replace(new RegExp('=', 'g'), "\":");
      value = "{" + value + "}"
      orderArray.push(JSON.parse(value));
    })
    return orderArray;
  }
}
