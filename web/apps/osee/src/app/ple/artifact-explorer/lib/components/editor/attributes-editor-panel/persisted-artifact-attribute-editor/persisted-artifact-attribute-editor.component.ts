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
	DestroyRef,
	effect,
	inject,
	input,
	OnDestroy,
	signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { applic } from '@osee/applicability/types';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	FocusLostInputComponent,
	MarkdownEditorComponent,
} from '@osee/shared/components';
import { CurrentTransactionService } from '@osee/transactions/services';
import { take } from 'rxjs';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { ArtifactEditorDirtyService } from '../../../../services/artifact-editor-dirty.service';

@Component({
	selector: 'osee-persisted-artifact-attribute-editor',
	imports: [
		FormsModule,
		FocusLostInputComponent,
		MatSlideToggle,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MarkdownEditorComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	viewProviders: [provideOptionalControlContainerNgForm()],
	template: `
		@switch (attr().storeType) {
			@case ('Boolean') {
				<mat-slide-toggle
					[ngModel]="displayValue() === 'true'"
					name="persisted-attr-boolean"
					[disabled]="disabled()"
					(ngModelChange)="onBooleanChange($event)"
					class="primary-slide-toggle">
					{{ displayValue() === 'true' ? 'True' : 'False' }}
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
						[ngModel]="displayValue()"
						[name]="'persisted-attr-enum-' + attr().typeId"
						(ngModelChange)="onValueChange($event)">
						@for (opt of attr().enumOptions ?? []; track opt) {
							<mat-option [value]="opt">{{ opt }}</mat-option>
						}
						@if (
							displayValue() &&
							!(attr().enumOptions ?? []).includes(displayValue())
						) {
							<mat-option [value]="displayValue()">{{
								displayValue()
							}}</mat-option>
						}
					</mat-select>
				</mat-form-field>
			}
			@default {
				@if (attr().name === 'Markdown Content') {
					<osee-markdown-editor
						class="tw-block"
						[mdContent]="displayValue()"
						(mdContentChange)="onMarkdownChange($event)"
						[disabled]="!editable()"
						[artifactId]="artifactId()"
						(focusin)="markdownFocused.set(true)"
						(focusout)="onMarkdownFocusOut($event)" />
				} @else {
					<span class="tw-block">
						<osee-focus-lost-input
							[disabled]="disabled()"
							[value]="displayValue()"
							(valueChange)="onValueChange($event)"
							[label]="attr().name ?? ''"
							[tooltip]="attr().name ?? ''">
						</osee-focus-lost-input>
					</span>
				}
			}
		}
	`,
})
export class PersistedArtifactAttributeEditorComponent implements OnDestroy {
	private currentTxService = inject(CurrentTransactionService);
	private dirtyService = inject(ArtifactEditorDirtyService);
	private destroyRef = inject(DestroyRef);

	/** The attribute to edit. */
	attr = input.required<attribute<string, ATTRIBUTETYPEID>>();
	/** The artifact that owns this attribute. */
	artifactId = input.required<`${number}`>();
	/** Applicability of the owning artifact. */
	artifactApplicability = input.required<applic>();
	/** Whether the field is disabled. */
	disabled = input(false);

	/** Inverse of disabled for components that use editable. */
	protected editable = computed(() => !this.disabled());

	/** Whether the markdown editor is currently focused. */
	protected readonly markdownFocused = signal(false);

	/** Unique key for dirty tracking. */
	private editorKey = computed(
		() => `${this.artifactId()}-${this.attr().typeId}`
	);

	/**
	 * Safely coerce the attribute value to a displayable string.
	 * Handles cases where the backend sends an object instead of a string.
	 */
	protected displayValue = computed(() => {
		const val = this.attr().value;
		if (val === null || val === undefined) {
			return '';
		}
		if (typeof val === 'string') {
			return val;
		}
		if (typeof val === 'object') {
			return (val as { name?: string }).name ?? JSON.stringify(val);
		}
		return String(val);
	});

	/**
	 * Tracks the last known persisted value to avoid redundant saves
	 * (e.g., when FocusLostInputComponent emits on init or the resource refetches).
	 */
	private previousValue = signal('');

	/** Guards against the effect resetting previousValue while a save is in-flight. */
	private saving = signal(false);

	constructor() {
		effect(() => {
			if (!this.saving()) {
				this.previousValue.set(this.displayValue());
			}
		});
	}

	onBooleanChange(checked: boolean) {
		const newValue = checked ? 'true' : 'false';
		if (newValue !== this.previousValue()) {
			this.saveAttribute(newValue);
		}
	}

	onValueChange(newValue: string) {
		if (newValue !== this.previousValue()) {
			this.dirtyService.markDirty(this.editorKey());
			this.saveAttribute(newValue);
		}
	}

	/** Stores pending markdown content without saving. */
	private pendingMarkdown: string | null = null;

	/** Called when markdown editor content changes (typing). */
	onMarkdownChange(newValue: string) {
		this.pendingMarkdown = newValue;
		if (newValue !== this.previousValue()) {
			this.dirtyService.markDirty(this.editorKey());
		}
	}

	/**
	 * Called on focusout from the markdown editor.
	 * Only triggers a save when focus genuinely leaves the editor
	 * (i.e., not when moving between the textarea and toolbar buttons).
	 */
	onMarkdownFocusOut(event: FocusEvent) {
		const editor = event.currentTarget as HTMLElement;
		const newTarget = event.relatedTarget as HTMLElement | null;

		// If focus moved to another element within the editor (e.g., a
		// toolbar button), skip the save — the user is still interacting.
		if (newTarget && editor.contains(newTarget)) {
			return;
		}

		this.markdownFocused.set(false);

		if (
			this.pendingMarkdown !== null &&
			this.pendingMarkdown !== this.previousValue()
		) {
			this.saveAttribute(this.pendingMarkdown);
			this.pendingMarkdown = null;
		} else {
			// No changes were made — clear dirty state
			this.dirtyService.markClean(this.editorKey());
		}
	}

	private saveAttribute(newValue: string) {
		const current = this.attr();
		const updated = { ...current, value: newValue };
		const attrs =
			current.id === '-1' && current.gammaId === '-1'
				? { add: [updated] }
				: { set: [updated] };

		this.saving.set(true);
		this.currentTxService
			.modifyArtifactAndMutate(
				`Modifying ${current.name ?? 'attribute'}`,
				this.artifactId(),
				this.artifactApplicability(),
				attrs
			)
			.pipe(takeUntilDestroyed(this.destroyRef), take(1))
			.subscribe({
				next: () => {
					this.previousValue.set(newValue);
					this.dirtyService.markClean(this.editorKey());
					this.saving.set(false);
				},
				error: () => {
					this.saving.set(false);
				},
			});
	}

	ngOnDestroy() {
		this.dirtyService.markClean(this.editorKey());
	}
}
