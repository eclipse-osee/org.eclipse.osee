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
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { combineLatest, from, iif, of } from 'rxjs';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';
import { Edge, Node } from '@swimlane/ngx-graph';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatDialog } from '@angular/material/dialog';
import { EditConnectionDialogComponent } from '../../dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { connection, connectionWithChanges, newConnection, OseeEdge, transportType } from '../../../../shared/types/connection';
import { filter, map, mergeMap, reduce, scan, switchMap, take, tap } from 'rxjs/operators';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { node, nodeData, nodeDataWithChanges, OseeNode } from '../../../../shared/types/node';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';
import { RemovalDialog } from '../../../types/ConfirmRemovalDialog';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { applic } from 'src/app/types/applicability/applic';
import { difference } from 'src/app/types/change-report/change-report';

@Component({
  selector: 'osee-connectionview-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.sass']
})
export class GraphComponent implements OnInit {

  @Input() editMode: boolean = false;
  data = this.graphService.nodes;
  update = this.graphService.updated;
  linkPosition = {
    x: "0",
    y:"0"
  }
  nodePosition = {
    x: "0",
    y:"0"
  }
  graphMenuPosition = {
    x: "0",
    y:"0"
  }
  @ViewChild('linkMenuTrigger') linkMenuTrigger!: MatMenuTrigger;
  @ViewChild('nodeMenuTrigger') nodeMenuTrigger!: MatMenuTrigger;
  @ViewChild('graphMenuTrigger') graphMenuTrigger!: MatMenuTrigger;

  _messageRoute = this.graphService.messageRoute
  constructor (private graphService: CurrentGraphService, private router: ConnectionViewRouterService, public dialog:MatDialog) {}

  ngOnInit(): void {
    this.graphService.update = true;
  }

  openLinkDialog(event:MouseEvent,value: OseeEdge<connection|connectionWithChanges>, nodes:OseeNode<node|nodeData|nodeDataWithChanges>[]) {
    event.preventDefault();
    this.linkPosition.x = event.clientX + 'px';
    this.linkPosition.y = event.clientY + 'px';
    //find node names based on value.data.source and value.data.target
    let source = nodes.find((node) => node.id === value.source);
    let target = nodes.find((node) => node.id === value.target);
    this.linkMenuTrigger.menuData = {
      data: value.data,
      source: source,
      target:target,
    }
    this.nodeMenuTrigger.closeMenu();
    this.graphMenuTrigger.closeMenu();
    this.linkMenuTrigger.openMenu();
  }

  openNodeDialog(event: MouseEvent, value: OseeNode<node|nodeData|nodeDataWithChanges>, edges:OseeEdge<connection|connectionWithChanges>[]) {
    event.preventDefault();
    this.nodePosition.x = event.clientX + 'px';
    this.nodePosition.y = event.clientY + 'px';
    let source = edges.filter((edge) => edge.source === value.id);
    let target = edges.filter((edge) => edge.target === value.id);
    this.nodeMenuTrigger.menuData = {
      data: value.data,
      sources: source,
      targets:target
    }
    this.linkMenuTrigger.closeMenu();
    this.graphMenuTrigger.closeMenu();
    this.nodeMenuTrigger.openMenu();
  }

  openGraphDialog(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    //hacky way of keeping the event to white space only instead of activating on right mouse click of other elements
    let target = event.target as HTMLElement
    if (target.attributes.getNamedItem('class')?.value.includes('panning-rect')) {
      this.graphMenuPosition.x = event.clientX + 'px';
      this.graphMenuPosition.y = event.clientY + 'px';
      this.linkMenuTrigger.closeMenu();
      this.nodeMenuTrigger.closeMenu();
      this.graphMenuTrigger.openMenu();
    }
  }

  createNewNode() {
    let dialogRef = this.dialog.open(CreateNewNodeDialogComponent);
    dialogRef.afterClosed().pipe(
      take(1),
      filter((dialogResponse: node) => dialogResponse !== undefined && dialogResponse !== null),
      switchMap((results)=>this.graphService.createNewNode(results))
    ).subscribe()
  }
}
