import { Injectable } from '@angular/core';
import { Node,Edge } from '@swimlane/ngx-graph';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, repeatWhen, share, switchMap, take, tap } from 'rxjs/operators';
import { ConnectionService } from './connection.service';
import { GraphService } from './graph.service';
import { NodeService } from './node.service';
import { RouteStateService } from './route-state-service.service';
import { connection } from '../types/connection'
import { node } from '../types/node'
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';

@Injectable({
  providedIn: 'root'
})
export class CurrentGraphService {

  private _nodes = this.routeStateService.id.pipe(
    share(),
    filter((val)=>val!=="" && val!=='-1'),
    switchMap((val) => this.graphService.getNodes(val).pipe(
      map((split) => this.transform(split)),
      repeatWhen(_=>this.updated),
      share()
    )),
  )
  private _nodeOptions = this.routeStateService.id.pipe(
    share(),
    filter((val) => val !== "" && val !== '-1'),
    switchMap((val) => this.nodeService.getNodes(val).pipe(
      repeatWhen(_ => this.updated),
      share()
    ))
  )
  private _update = new Subject<boolean>();
  constructor (private graphService: GraphService,private nodeService:NodeService, private connectionService: ConnectionService, private routeStateService: RouteStateService) { }
  
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

  updateConnection(connection: Partial<connection>) {
    return this.connectionService.updateConnection(this.routeStateService.id.getValue(), connection).pipe(
      tap((value) => {
        if (!value.errors) {
          this.update = true;
        }
      })
    )
  }

  unrelateConnection(nodeId:string,id:string) {
    return this.connectionService.unrelateConnection(this.routeStateService.id.getValue(),nodeId,id).pipe(
      tap((value) => {
        if (!value.errors) {
          this.update = true;
        }
      })
    )
  }

  updateNode(node: Partial<node>) {
    return this.nodeService.patchNode(this.routeStateService.id.getValue(),node).pipe(
      tap((value) => {
        if (!value.errors) {
          this.update = true;
        }
      })
    )
  }
  deleteNode(nodeId: string) {
    return this.nodeService.deleteNode(this.routeStateService.id.getValue(), nodeId).pipe(
      tap((value) => {
        if (!value.errors) {
          this.update = true;
        }
      })
    )
  }

  deleteNodeAndUnrelate(nodeId: string, edges: Edge[]) {
    //find all ids in edge array where nodeId !== id
    let relatedResponses: Observable<OSEEWriteApiResponse>[] = [];
    edges.forEach((edge) => {
      if (edge.source !== nodeId && edge.id !== undefined && edge.target === nodeId) {
        //make unrelate observables for each id(source)
        relatedResponses.push(this.unrelateConnection(edge.source, edge.id.replace('a','')));
      }
      if (edge.target !== nodeId && edge.id !== undefined && edge.source === nodeId) {
        //make unrelate observables for each id(target)
        relatedResponses.push(this.unrelateConnection(edge.target, edge.id.replace('a','')));
      }
    })
    //return combineLatest with delete node
    return combineLatest([...relatedResponses, this.deleteNode(nodeId)]);
  }

  createNewConnection(connection:connection,sourceId:string,targetId:string) {
    return this.connectionService.createConnection(this.routeStateService.id.getValue(), sourceId, 'primary', connection).pipe(
      take(1),
      switchMap((val) => this.connectionService.relateConnection(this.routeStateService.id.getValue(), targetId, 'secondary', val.ids[0], connection).pipe(
        tap((value) => {
          if (!value.errors) {
            this.update = true;
          }
        })
      ))
    )
  }

  createNewNode(node: node) {
    return this.nodeService.createNode(this.routeStateService.id.getValue(), node).pipe(
      tap((value) => {
        if (!value.errors) {
          this.update = true;
        }
      })
    );
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
}
