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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	input,
	model,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	FocusLostInputComponent,
	MarkdownEditorComponent,
} from '@osee/shared/components';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';

/**
 * Presentational, non-persisting editor for a single attribute value. Renders
 * the same widget the artifact editor uses for the attribute's store type
 * (boolean toggle, enum select, markdown editor, or text input), but is a pure
 * value in/out component -- it never issues a transaction.
 *
 * Used by the conflict resolution dialog so "Manual Resolution" edits the value
 * with the correct widget.
 */
@Component({
	selector: 'osee-attribute-value-editor',
	imports: [
		FormsModule,
		MatSlideToggle,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		FocusLostInputComponent,
		MarkdownEditorComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	viewProviders: [provideOptionalControlContainerNgForm()],
	template: `
		@switch (attr().storeType) {
			@case ('Boolean') {
				<mat-slide-toggle
					[ngModel]="value() === 'true'"
					name="value-editor-boolean"
					[disabled]="disabled()"
					(ngModelChange)="value.set($event ? 'true' : 'false')"
					class="primary-slide-toggle">
					{{ value() === 'true' ? 'True' : 'False' }}
				</mat-slide-toggle>
			}
			@case ('Enumeration') {
				<mat-form-field
					class="tw-w-full"
					appearance="outline"
					subscriptSizing="dynamic">
					<mat-label>{{ attr().name ?? '' }}</mat-label>
					<mat-select
						[disabled]="disabled()"
						[ngModel]="value()"
						[name]="'value-editor-enum-' + attr().typeId"
						(ngModelChange)="value.set($event)">
						@for (opt of attr().enumOptions ?? []; track opt) {
							<mat-option [value]="opt">{{ opt }}</mat-option>
						}
						@if (
							value() &&
							!(attr().enumOptions ?? []).includes(value())
						) {
							<mat-option [value]="value()">{{
								value()
							}}</mat-option>
						}
					</mat-select>
				</mat-form-field>
			}
			@default {
				@if (attr().name === 'Markdown Content') {
					<osee-markdown-editor
						class="tw-block"
						[mdContent]="value()"
						(mdContentChange)="value.set($event)"
						[disabled]="disabled()"
						[artifactId]="artifactId()" />
				} @else {
					<osee-focus-lost-input
						[disabled]="disabled()"
						[value]="value()"
						(valueChange)="value.set($event)"
						(liveInput)="value.set($event)"
						[label]="attr().name ?? ''"
						[tooltip]="attr().name ?? ''">
					</osee-focus-lost-input>
				}
			}
		}
	`,
})
export class AttributeValueEditorComponent {
	/** The attribute providing metadata (store type, enum options, name). */
	attr = input.required<attribute<string, ATTRIBUTETYPEID>>();
	/** Owning artifact ID -- required by the markdown editor for image uploads. */
	artifactId = input<string>('');
	/** Whether editing is disabled. */
	disabled = input(false);
	/** The edited value (two-way bindable). */
	value = model<string>('');

	/** Inverse of disabled, for readability in templates that need it. */
	protected editable = computed(() => !this.disabled());
}
