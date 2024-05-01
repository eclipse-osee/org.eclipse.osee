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
import { AsyncPipe, NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, viewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import type {
	OseeEdge,
	OseeNode,
	connection,
	connectionWithChanges,
	newConnection,
	node,
	nodeData,
	nodeDataWithChanges,
} from '@osee/messaging/shared/types';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { Subject } from 'rxjs';
import { filter, switchMap, take, takeUntil } from 'rxjs/operators';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { GraphLinkMenuComponent } from '../../menu/graph-link-menu/graph-link-menu.component';
import { GraphNodeMenuComponent } from '../../menu/graph-node-menu/graph-node-menu.component';
import { CurrentGraphService } from '../../services/current-graph.service';

@Component({
	selector: 'osee-connection-view-graph',
	templateUrl: './graph.component.html',
	styles: [':host{ width: 100%; height: 100%;}'],
	standalone: true,
	imports: [
		AsyncPipe,
		NgClass,
		NgTemplateOutlet,
		RouterLink,
		NgxGraphModule,
		MatMenu,
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatIcon,
		GraphLinkMenuComponent,
		GraphNodeMenuComponent,
		MatTooltip,
		MatLabel,
	],
})
export class GraphComponent implements OnInit, OnDestroy {
	private _done = new Subject<void>();
	@Input() editMode: boolean = false;
	data = this.graphService.nodes.pipe(takeUntil(this._done));
	update = this.graphService.updated;
	linkPosition = {
		x: '0',
		y: '0',
	};
	nodePosition = {
		x: '0',
		y: '0',
	};
	graphMenuPosition = {
		x: '0',
		y: '0',
	};
	linkMenuTrigger = viewChild.required<MatMenuTrigger>('linkMenuTrigger');
	nodeMenuTrigger = viewChild.required<MatMenuTrigger>('nodeMenuTrigger');
	graphMenuTrigger = viewChild.required<MatMenuTrigger>('graphMenuTrigger');

	_messageRoute = this.graphService.messageRoute;
	constructor(
		private graphService: CurrentGraphService,
		public dialog: MatDialog
	) {}
	ngOnDestroy(): void {
		this._done.next();
		this._done.complete();
	}

	ngOnInit(): void {
		this.graphService.update = true;
	}

	openLinkDialog(
		event: MouseEvent,
		value: OseeEdge<connection | connectionWithChanges>,
		nodes: OseeNode<node | nodeData | nodeDataWithChanges>[]
	) {
		event.preventDefault();
		this.linkPosition.x = event.clientX + 'px';
		this.linkPosition.y = event.clientY + 'px';
		//find node names based on value.data.source and value.data.target
		let source = nodes.find((node) => node.id === value.source);
		let target = nodes.find((node) => node.id === value.target);
		this.linkMenuTrigger().menuData = {
			data: value.data,
			source: source,
			target: target,
		};
		this.nodeMenuTrigger().closeMenu();
		this.graphMenuTrigger().closeMenu();
		this.linkMenuTrigger().openMenu();
	}

	openNodeDialog(
		event: MouseEvent,
		value: OseeNode<node | nodeData | nodeDataWithChanges>,
		edges: OseeEdge<connection | connectionWithChanges>[]
	) {
		event.preventDefault();
		this.nodePosition.x = event.clientX + 'px';
		this.nodePosition.y = event.clientY + 'px';
		let source = edges.filter((edge) => edge.source === value.id);
		let target = edges.filter((edge) => edge.target === value.id);
		this.nodeMenuTrigger().menuData = {
			data: value.data,
			sources: source,
			targets: target,
		};
		this.linkMenuTrigger().closeMenu();
		this.graphMenuTrigger().closeMenu();
		this.nodeMenuTrigger().openMenu();
	}

	openGraphDialog(event: MouseEvent) {
		event.stopPropagation();
		event.preventDefault();
		//hacky way of keeping the event to white space only instead of activating on right mouse click of other elements
		let target = event.target as HTMLElement;
		if (
			target.attributes
				.getNamedItem('class')
				?.value.includes('panning-rect')
		) {
			this.graphMenuPosition.x = event.clientX + 'px';
			this.graphMenuPosition.y = event.clientY + 'px';
			this.linkMenuTrigger().closeMenu();
			this.nodeMenuTrigger().closeMenu();
			this.graphMenuTrigger().openMenu();
		}
	}

	createNewNode() {
		let dialogRef = this.dialog.open(CreateNewNodeDialogComponent);
		dialogRef
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(dialogResponse: node) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				switchMap((results) => this.graphService.createNewNode(results))
			)
			.subscribe();
	}

	createNewConnection() {
		let dialogRef = this.dialog.open(CreateConnectionDialogComponent, {
			minWidth: '40%',
		});
		dialogRef
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(dialogResponse: newConnection) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				switchMap((res) =>
					this.graphService.createNewConnection(
						res.connection as connection,
						res.nodeIds
					)
				)
			)
			.subscribe();
	}
}
