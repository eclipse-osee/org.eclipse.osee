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
import {
	Component,
	Input,
	OnDestroy,
	OnInit,
	viewChild,
	inject,
} from '@angular/core';
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
	nodeData,
} from '@osee/messaging/shared/types';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { Subject } from 'rxjs';
import { filter, switchMap, take, takeUntil } from 'rxjs/operators';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { GraphLinkMenuComponent } from '../../menu/graph-link/graph-link-menu.component';
import { GraphNodeMenuComponent } from '../../menu/graph-node/graph-node-menu.component';
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
	private graphService = inject(CurrentGraphService);
	dialog = inject(MatDialog);

	private _done = new Subject<void>();
	@Input() editMode = false;
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

	ngOnDestroy(): void {
		this._done.next();
		this._done.complete();
	}

	ngOnInit(): void {
		this.graphService.update = true;
	}

	openLinkDialog(
		event: MouseEvent,
		value: OseeEdge<connection>,
		nodes: OseeNode<nodeData>[]
	) {
		event.preventDefault();
		this.linkPosition.x = event.clientX + 'px';
		this.linkPosition.y = event.clientY + 'px';
		//find node names based on value.data.source and value.data.target
		const source = nodes.find((node) => node.id === value.source);
		const target = nodes.find((node) => node.id === value.target);
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
		value: OseeNode<nodeData>,
		edges: OseeEdge<connection>[]
	) {
		event.preventDefault();
		this.nodePosition.x = event.clientX + 'px';
		this.nodePosition.y = event.clientY + 'px';
		const source = edges.filter((edge) => edge.source === value.id);
		const target = edges.filter((edge) => edge.target === value.id);
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
		const target = event.target as HTMLElement;
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
		const dialogRef = this.dialog.open(CreateNewNodeDialogComponent);
		dialogRef
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(dialogResponse: nodeData) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				switchMap((results) => this.graphService.createNewNode(results))
			)
			.subscribe();
	}

	createNewConnection() {
		const dialogRef = this.dialog.open(CreateConnectionDialogComponent, {
			minWidth: '40%',
		});
		dialogRef
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(dialogResponse: connection) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				switchMap((res) => this.graphService.createNewConnection(res))
			)
			.subscribe();
	}
}
