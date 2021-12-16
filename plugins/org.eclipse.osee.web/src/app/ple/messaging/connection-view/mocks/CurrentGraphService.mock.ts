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
import { of, BehaviorSubject, Subject, ReplaySubject } from "rxjs";
import { MimPreferencesMock } from "../../shared/mocks/MimPreferences.mock";
import { OSEEWriteApiResponse } from "../../shared/types/ApiWriteResponse";
import { CurrentGraphService } from "../services/current-graph.service";
import { connection, transportType } from '../../shared/types/connection'
import { node } from '../../shared/types/node'
import { response } from "./Response.mock";
import { settingsDialogData } from "../../shared/types/settingsdialog";
import { applic } from "../../../../types/applicability/applic";
import { changeReportMock } from "src/app/ple-services/http/change-report.mock";
import { changeInstance } from "src/app/types/change-report/change-report";

let sideNavContentPlaceholder = new ReplaySubject<{ opened: boolean, field: string, currentValue: string | number | applic|boolean|transportType, previousValue?: string | number | applic|boolean|transportType, user?: string, date?: string }>();
sideNavContentPlaceholder.next({opened:true,field:'',currentValue:''})
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
  preferences: of(MimPreferencesMock),
  diff: of(changeReportMock),
  InDiff: new BehaviorSubject<boolean>(true),
  differences:new BehaviorSubject<changeInstance[]|undefined>(changeReportMock),
  sideNavContent: sideNavContentPlaceholder,
  set sideNav(value: { opened: boolean, field: string, currentValue: string | number | applic, previousValue?: string | number | applic, user?: string, date?: string }) { },
  get messageRoute(){return of({ beginning:'/ple/messaging/'+'working' + '/' + '8' + '/',end:'/diff' })}
  }