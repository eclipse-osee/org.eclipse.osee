import { of, BehaviorSubject } from "rxjs";
import { MimPreferencesMock } from "../../shared/mocks/MimPreferences.mock";
import { OSEEWriteApiResponse } from "../../shared/types/ApiWriteResponse";
import { CurrentGraphService } from "../services/current-graph.service";
import { connection } from '../../shared/types/connection'
import { node } from '../../shared/types/node'
import { response } from "./Response.mock";
import { settingsDialogData } from "../../shared/types/settingsdialog";

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
    return of(response)
  },
  createNewConnection(connection: connection, sourceId: string, targetId: string) {
    return of(response)
  }, 
  createNewNode(node: node) {
    return of(response)
  },
  updatePreferences(preferences: settingsDialogData) {
    return of(response);
  },
  nodeOptions: of([{id:'1',name:'First'},{id:'2',name:'Second'}]),
  applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
  preferences: of(MimPreferencesMock)
  }