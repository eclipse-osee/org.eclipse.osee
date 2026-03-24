/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, computed, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { MarkdownComponent } from 'ngx-markdown';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { filter, of, switchMap } from 'rxjs';
import { HelpService } from '@osee/shared/services/help';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-messaging-help-content',
	imports: [AsyncPipe, MarkdownComponent],
	template: `<markdown
		[data]="markdown | async"
		[disableSanitizer]="true"
		class="[&_a]:tw-text-primary-700 [&_code]:tw-bg-background-status-bar [&_code]:tw-px-2 dark:[&_code]:tw-bg-background-card [&_ol]:tw-list-decimal [&_ol]:tw-pl-8 [&_ul]:tw-list-disc [&_ul]:tw-pl-8"></markdown> `,
})
export class MessagingHelpContentComponent {
	private route = inject(ActivatedRoute);
	private helpService = inject(HelpService);

	private paramMap = toSignal(this.route.paramMap, {
		initialValue: convertToParamMap({}),
	});
	protected id = computed(() => {
		const params = this.paramMap();
		return params.get('id') || '';
	});
	private _id$ = toObservable(this.id);

	markdown = this._id$.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) =>
			this.helpService
				.getHelpPage(id)
				.pipe(switchMap((page) => of(page.markdownContent)))
		)
	);
}

export default MessagingHelpContentComponent;
