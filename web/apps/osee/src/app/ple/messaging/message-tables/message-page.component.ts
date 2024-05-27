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
import {
	ChangeDetectionStrategy,
	Component,
	OnDestroy,
	OnInit,
	inject,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { combineLatest, iif, of } from 'rxjs';
import MessageInterfaceComponent from './lib/message-interface/message-interface.component';

@Component({
	selector: 'osee-messaging-message-page',
	templateUrl: './message-page.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [MessageInterfaceComponent],
})
export class MessagePageComponent implements OnInit, OnDestroy {
	private router = inject(Router);
	private route = inject(ActivatedRoute);
	private messageService = inject(CurrentMessagesService);

	ngOnDestroy(): void {
		this.messageService.clearRows();
		this.messageService.toggleDone = true;
	}

	ngOnInit(): void {
		combineLatest([
			this.route.paramMap,
			this.route.data,
			iif(() => this.router.url.includes('diff'), of(false), of(true)),
		]).subscribe(([values, data, mode]) => {
			if (mode) {
				this.messageService.messageFilter.set(
					values.get('type')?.trim().toLowerCase() || ''
				); //@todo FIX
				this.messageService.branchType =
					(values.get('branchType') as 'working' | 'baseline' | '') ||
					'';
				this.messageService.branch = values.get('branchId') || '';
				this.messageService.connection =
					(values.get('connection') as `${number}`) || '-1';
				this.messageService.messageId = '';
				this.messageService.subMessageId = '-1';
				this.messageService.submessageToStructureBreadCrumbs = '';
				this.messageService.singleStructureId = '';
				this.messageService.DiffMode = false;
			} else {
				this.messageService.connection =
					(values.get('connection') as `${number}`) || '-1';
				this.messageService.messageId = '';
				this.messageService.subMessageId = '-1';
				this.messageService.submessageToStructureBreadCrumbs = '';
				this.messageService.singleStructureId = '';
				this.messageService.difference = data.diff;
			}
		});
	}
}

export default MessagePageComponent;
