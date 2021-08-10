import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { from, of } from 'rxjs';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';
import { Edge, Node } from '@swimlane/ngx-graph';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatDialog } from '@angular/material/dialog';
import { EditConnectionDialogComponent } from '../../dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { connection, newConnection } from '../../../../shared/types/connection';
import { filter, mergeMap, reduce, scan, switchMap, take, tap } from 'rxjs/operators';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { node, nodeData } from '../../../../shared/types/node';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';
import { RemovalDialog } from '../../../types/ConfirmRemovalDialog';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';

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
  constructor (private graphService: CurrentGraphService, private router: ConnectionViewRouterService, public dialog:MatDialog) {}

  ngOnInit(): void {
    this.graphService.update = true;
  }

  navigateToMessages(value: string) {
    this.router.connection = value;
  }

  navigateToMessagesInNewTab(location: string) {
    this.router.connectionInNewTab = location;
  }
  openLinkDialog(event:MouseEvent,value: Edge, nodes:Node[]) {
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

  /**
   * Opens Edit Dialog to Edit a connection, and sends response to Api
   * @param value connection to edit
   */
  openConnectionEditDialog(value: connection) {
    let dialogRef=this.dialog.open(EditConnectionDialogComponent, {
      data:Object.assign({},value)
    })
    dialogRef.afterClosed().pipe(
      //only take first response
      take(1),
      //filter out non-valid responses
      filter((dialogResponse) => dialogResponse !== undefined && dialogResponse !== null),
      //convert object to key-value pair emissions emitted sequentially instead of all at once
      mergeMap((arrayDialogResponse:connection) => from(Object.entries(arrayDialogResponse)).pipe(
        //filter out key-value pairs that are unchanged on value, and maintain id property
        filter((filteredProperties) => value[filteredProperties[0] as keyof connection] !== filteredProperties[1] || filteredProperties[0]==='id'),
        //accumulate into an array of properties that are changed
        reduce((acc, curr) => [...acc, curr], [] as [string, any][])
      )),
      //transform array of properties into Partial<connection> using Object.fromEntries()(ES2019)
      switchMap((arrayOfProperties) => of(Object.fromEntries(arrayOfProperties) as Partial<connection>).pipe(
        //HTTP PATCH call to update value
        switchMap((changes)=>this.graphService.updateConnection(changes))
      ))
    ).subscribe();
  }

  openRemoveConnectionDialog(value: connection, source:Node, target:Node) {
    let dialogRef = this.dialog.open(ConfirmRemovalDialogComponent, {
      data: {
        id: value.id,
        name: value.name,
        extraNames: [source.label,target.label],
        type:'connection'
      }
    })
    dialogRef.afterClosed().pipe(
      //only take first response
      take(1),
      //filter out non-valid responses
      filter((dialogResponse) => dialogResponse !== undefined && dialogResponse !== null),
      //make sure there is a name and id, and extra names
      filter((result) => result.name.length > 0 && result.id.length>0 && result.extraNames.length>0),
      mergeMap((dialogResults) => from([{ node:source.id,connection:dialogResults.id }, { node:target.id,connection:dialogResults.id }]).pipe(
        mergeMap((request)=>this.graphService.unrelateConnection(request.node,request.connection))
      ))
    ).subscribe();
  }

  openNodeDialog(event: MouseEvent, value: Node, edges:Edge[]) {
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

  openEditNodeDialog(value: nodeData) {
    let dialogRef = this.dialog.open(EditNodeDialogComponent, {
      data:Object.assign({},value)
    })
    dialogRef.afterClosed().pipe(
      //only take first response
      take(1),
      //filter out non-valid responses
      filter((dialogResponse) => dialogResponse !== undefined && dialogResponse !== null),
      //convert object to key-value pair emissions emitted sequentially instead of all at once
      mergeMap((arrayDialogResponse:node) => from(Object.entries(arrayDialogResponse)).pipe(
        //filter out key-value pairs that are unchanged on value, and maintain id property
        filter((filteredProperties) => value[filteredProperties[0] as keyof node] !== filteredProperties[1] || filteredProperties[0]==='id'),
        //accumulate into an array of properties that are changed
        reduce((acc, curr) => [...acc, curr], [] as [string, any][])
      )),
      //transform array of properties into Partial<node> using Object.fromEntries()(ES2019)
      switchMap((arrayOfProperties) => of(Object.fromEntries(arrayOfProperties) as Partial<node>).pipe(
        //HTTP PATCH call to update value
        switchMap((results)=>this.graphService.updateNode(results))
      ))
    ).subscribe();
  }

  removeNodeAndConnection(value: nodeData,sources:Edge[],targets:Edge[]) {
    let dialogRef = this.dialog.open(ConfirmRemovalDialogComponent, {
      data: {
        id: value.id,
        name: value.name,
        extraNames: [...sources.map(x=>x.label),...targets.map(x=>x.label)],
        type:'node'
      }
    })
    dialogRef.afterClosed().pipe(
      //only take first response
      take(1),
      //filter out non-valid responses
      filter((dialogResponse:RemovalDialog) => dialogResponse !== undefined && dialogResponse !== null),
      //make sure there is a name and id
      filter((result) => result.name.length > 0 && result.id.length > 0),
      //delete Node api call, then unrelate connection from other node(s) using unrelate api call
      switchMap((results)=>this.graphService.deleteNodeAndUnrelate(results.id,[...sources,...targets])) //needs testing
    ).subscribe();
  }

  createConnectionToNode(value: nodeData) {
    //todo open dialog to select node to connect to this node
    let dialogRef = this.dialog.open(CreateConnectionDialogComponent, {
      data:value
    })
    dialogRef.afterClosed().pipe(
      //only take first response
      take(1),
      //filter out non-valid responses
      filter((dialogResponse: newConnection) => dialogResponse !== undefined && dialogResponse !== null),
      //HTTP Rest call to create connection(branch/nodes/nodeId/connections/type), then rest call to relate it to the associated nodes(branch/nodes/nodeId/connections/id/type)
      switchMap((results)=>this.graphService.createNewConnection(results.connection,results.nodeId,value.id))
    ).subscribe();
  }

  openGraphDialog(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    //hacky way of keeping the event to white space only instead of activating on right mouse click of other elements
    let target = event.target as HTMLElement
    if (target.attributes.getNamedItem('class')?.value == 'panning-rect') {
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
