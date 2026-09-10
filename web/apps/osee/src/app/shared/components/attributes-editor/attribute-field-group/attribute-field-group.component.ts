/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Presentational wrapper that groups multiple instances of one attribute type
 * under a "Name (count)" header with a bordered box. Uses content projection
 * so the parent controls what fields and controls render inside.
 */
@Component({
	selector: 'osee-attribute-field-group',
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<div>
			<span
				class="tw-mb-1 tw-block tw-text-xs tw-font-medium"
				data-testid="attribute-group-header">
				{{ name() }}
				<span class="tw-opacity-50">({{ count() }})</span>
			</span>
			<div
				class="tw-flex tw-flex-col tw-gap-2 tw-rounded tw-border tw-border-solid tw-border-osee-neutral-50 tw-p-2 dark:tw-border-osee-neutral-60">
				<ng-content />
			</div>
		</div>
	`,
})
export class AttributeFieldGroupComponent {
	name = input.required<string>();
	count = input.required<number>();
}
