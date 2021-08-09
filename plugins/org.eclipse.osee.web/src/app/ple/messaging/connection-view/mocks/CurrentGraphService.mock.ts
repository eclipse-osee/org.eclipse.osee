import { of, BehaviorSubject } from "rxjs";
import { OSEEWriteApiResponse } from "../../shared/types/ApiWriteResponse";
import { CurrentGraphService } from "../services/current-graph.service";
import { connection } from '../types/connection'
import { node } from '../types/node'
import { response } from "./Response.mock";

export const graphServiceMock: Partial<CurrentGraphService> = {
    nodes: of({ nodes: [], edges: [] }),
    updated: new BehaviorSubject<boolean>(true),
    set update(value: boolean) {
      return;
    },
    updateConnection(connection: Partial<connection>) {
      return of(response);
    },
    unrelateConnection(nodeId: string, id: string) {
      return of(response);
    },
    updateNode(node: Partial<node>) {
        return of(response);
    },
    deleteNodeAndUnrelate(nodeId: string, edges: []) {
        return of([response])
    },
    createNewConnection(connection: connection, sourceId: string, targetId: string) {
        return of(response)
    },
    createNewNode(node: node) {
        return of(response)
    },
  nodeOptions: of([]),
  applic: of([{ id: '1', name:'Base'},{id:'2',name:'Second'}])
  }