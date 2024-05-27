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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { CurrentViewSelectorComponent } from '@osee/shared/components';
import { iif, of, switchMap } from 'rxjs';
import { MessageToolbarComponent } from '../message-toolbar/message-toolbar.component';
import { MessageTableComponent } from '../tables/message-table/message-table.component';

@Component({
	selector: 'osee-message-interface',
	standalone: true,
	imports: [
		MessagingControlsComponent,
		CurrentViewSelectorComponent,
		MessageTableComponent,
		AsyncPipe,
		MessageToolbarComponent,
	],
	template: `<osee-messaging-controls
			[branchControls]="false"
			[actionControls]="true"
			[diff]="true"
			[diffRouteLink]="
				(inDiffMode | async) === 'false'
					? [
							{
								outlets: {
									primary: 'diff',
									rightSideNav: null,
								},
							},
						]
					: '../'
			">
			<osee-current-view-selector />
		</osee-messaging-controls>
		<div
			class="tw-inline-block tw-max-h-[82vh] tw-w-full tw-max-w-[100vw] tw-overflow-auto tw-bg-background-background max-sm:tw-block">
			<osee-messaging-message-table />
		</div>
		<osee-message-toolbar [messagesCount]="messagesCount()" />`,
})
export class MessageInterfaceComponent {
	private messageService = inject(CurrentMessagesService);
	protected inDiffMode = this.messageService.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);
	private _messagesCount = this.messageService.messagesCount;
	messagesCount = toSignal(this._messagesCount, {
		initialValue: 0,
	});
}
export default MessageInterfaceComponent;
