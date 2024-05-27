/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { connection } from '@osee/messaging/shared/types';
import { CurrentGraphService } from '../../services/current-graph.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-graph-link-link-menu',
	standalone: true,
	imports: [MatMenuItem, RouterLink, MatIcon],
	template: ` <a
			mat-menu-item
			[disabled]="routingDisabled()"
			[routerLink]="route()"
			queryParamsHandling="merge"
			[attr.data-cy]="'goto-' + connectionName()"
			><mat-icon>subdirectory_arrow_right</mat-icon>Go to
			{{ connectionName() }}</a
		>
		<a
			mat-menu-item
			[disabled]="routingDisabled()"
			[routerLink]="route()"
			queryParamsHandling="merge"
			target="_blank"
			><mat-icon class="tw-text-osee-blue-9">open_in_new</mat-icon>Go to
			{{ connectionName() }} in new tab</a
		>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraphLinkLinkMenuComponent {
	data = input.required<connection>();
	protected connectionName = computed(() => this.data().name.value);

	private graphService = inject(CurrentGraphService);
	private _messageRoute = this.graphService.messageRoute;
	private messageRoute = toSignal(this._messageRoute, {
		initialValue: { beginning: '', end: '' },
	});
	private messageRouteBegin = computed(() => this.messageRoute().beginning);
	private messageRouteEnd = computed(() => this.messageRoute().end);
	protected routingDisabled = computed(() => this.data().id === undefined);

	protected route = computed(() => {
		if (this.routingDisabled()) {
			return '';
		}
		return (
			this.messageRouteBegin() +
			this.data().id +
			'/messages' +
			this.messageRouteEnd()
		);
	});
}
