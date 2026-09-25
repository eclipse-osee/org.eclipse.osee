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
import { SseEventService } from '@osee/shared/services/network';
import {
	EditorDirtyService,
	PendingAttributeValuesService,
} from '@osee/shared/conflict-resolution';

@Component({
	selector: 'osee-persisted-artifact-attribute-editor',
	// Amber "caution" ring shown while a save is blocked by an unresolved conflict. This is a
	// caution state, NOT a destructive/error one, so the semantic `warning` token (which is red)
	// would be wrong here. The app has no amber/caution semantic token, so raw amber slots are
	// used deliberately: osee-yellow-10 in light mode, osee-amber-9 in dark for adequate contrast.
	host: {
		'[class.tw-block]': 'true',
		'[class.tw-rounded]': 'blockedUnsaved()',
		'[class.tw-ring-2]': 'blockedUnsaved()',
		'[class.tw-ring-osee-yellow-10]': 'blockedUnsaved()',
		'[class.dark:tw-ring-osee-amber-9]': 'blockedUnsaved()',
		'[class.tw-ring-offset-1]': 'blockedUnsaved()',
	},
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
					@if (showLabel()) {
						<mat-label>{{ attr().name ?? '' }}</mat-label>
					}
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
							(liveInput)="onLiveInput($event)"
							[label]="showLabel() ? (attr().name ?? '') : ''"
							[placeholder]="showLabel() ? '' : 'Enter value...'"
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
	private dirtyService = inject(EditorDirtyService);
	private pendingValuesService = inject(PendingAttributeValuesService);
	private sseEventService = inject(SseEventService);
	private destroyRef = inject(DestroyRef);

	/** The attribute to edit. */
	attr = input.required<attribute<string, ATTRIBUTETYPEID>>();
	/** The artifact that owns this attribute. */
	artifactId = input.required<`${number}`>();
	/** Applicability of the owning artifact. */
	artifactApplicability = input.required<applic>();
	/** Whether the field is disabled. */
	disabled = input(false);
	/** Whether a remote conflict exists (blocks auto-save). */
	conflicted = input(false);
	/** Whether to show the field label. Set false when inside a grouped multi-instance section. */
	showLabel = input(true);

	/** Inverse of disabled for components that use editable. */
	protected editable = computed(() => !this.disabled());

	/** Whether the markdown editor is currently focused. */
	protected readonly markdownFocused = signal(false);

	/**
	 * Unique key for dirty tracking. Uses the artifact ID and attribute instance
	 * ID (both immutable across edits) — intentionally excludes the gammaId so
	 * dirty state survives a resource reload that bumps the gamma. This keeps the
	 * conflict "red ring" visible after a remote change arrives.
	 */
	private editorKey = computed(
		() => `${this.artifactId()}-${this.attr().id}`
	);

	/**
	 * True when this field has an unsaved edit that is currently blocked: a remote
	 * change landed on the entity while this editor is dirty, so auto-save is held
	 * until the user resolves. Shown as an amber ring (caution), NOT red -- whether
	 * this specific field truly conflicts with the server is only known once the
	 * resolution dialog fetches server state; the dialog is where genuine conflicts
	 * (and the read-only auto-saved changes) are shown.
	 */
	protected blockedUnsaved = computed(
		() => this.conflicted() && this.dirtyService.isDirty(this.editorKey())
	);

	/**
	 * Safely coerce the attribute value to a displayable string.
	 * Handles cases where the backend sends an object instead of a string.
	 */
	protected displayValue = computed(() => {
		// Re-hydrate an in-progress conflict edit. This editor can be destroyed and
		// rebuilt for structural reasons while a conflict is pending (e.g. a sibling
		// instance of the same type is added/removed, flipping the panel's
		// single-vs-grouped branch). On rebuild the attribute still carries the server
		// value, so without this the field would show the server value even though the
		// user's edit is preserved in the pending map. Prefer the tracked pending value
		// while conflicted and dirty so the field shows what the user actually typed.
		if (this.conflicted()) {
			const pending = this.pendingValuesService.get(this.attr().id);
			if (
				pending !== undefined &&
				this.dirtyService.isDirty(this.editorKey())
			) {
				return pending;
			}
		}

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

	/** Tracks server-ready transitions so a save deferred while disconnected flushes on reconnect. */
	private wasServerReady = this.sseEventService.serverReady();

	constructor() {
		effect(() => {
			if (!this.saving()) {
				this.previousValue.set(this.displayValue());
			}
		});

		// A blur-save attempted while disconnected is rejected by the mutation chokepoint but the
		// edit stays dirty/pending. When the server becomes ready again, flush it so the user's
		// change is persisted. If the server state diverged, the normal conflict path handles it.
		effect(() => {
			const ready = this.sseEventService.serverReady();
			const becameReady = ready && !this.wasServerReady;
			this.wasServerReady = ready;
			if (!becameReady || this.conflicted()) {
				return;
			}
			const pending = this.pendingValuesService.get(this.attr().id);
			if (
				pending !== undefined &&
				this.dirtyService.isDirty(this.editorKey())
			) {
				this.saveAttribute(pending);
			}
		});
	}

	/**
	 * Records the latest in-progress value and keeps the dirty flag in sync.
	 *
	 * The pending value is a plain `Map` write with no reactivity, so it is
	 * updated on every call (the conflict dialog needs the newest value). The
	 * dirty signal, however, rebuilds a `Set` and notifies subscribers, so it is
	 * only touched on an actual clean↔dirty transition — not on every keystroke.
	 * If the value is edited back to the persisted value, the field is cleaned.
	 */
	private trackPendingEdit(newValue: string) {
		const key = this.editorKey();
		if (newValue !== this.previousValue()) {
			this.pendingValuesService.set(this.attr().id, newValue);
			if (!this.dirtyService.isDirty(key)) {
				this.dirtyService.markDirty(key);
			}
		} else if (this.dirtyService.isDirty(key) && !this.conflicted()) {
			// Value edited back to the persisted value -- clean the field. While a
			// conflict is pending we do NOT clean/prune: every edited attribute must
			// stay tracked so it appears in the resolution dialog and keeps its ring.
			this.dirtyService.markClean(key);
			this.pendingValuesService.remove(this.attr().id);
		}
	}

	onBooleanChange(checked: boolean) {
		const newValue = checked ? 'true' : 'false';
		if (newValue !== this.previousValue()) {
			this.trackPendingEdit(newValue);
			this.saveAttribute(newValue);
		}
	}

	onValueChange(newValue: string) {
		if (newValue !== this.previousValue()) {
			this.trackPendingEdit(newValue);
			this.saveAttribute(newValue);
		}
	}

	/**
	 * Fires on every keystroke (before blur) for single-line/text inputs so a
	 * concurrent remote change is detected as a conflict while the user is still
	 * typing — matching the markdown editor. The actual save still happens on
	 * blur via `onValueChange`. Dirty-signal churn is avoided by `trackPendingEdit`.
	 */
	onLiveInput(newValue: string) {
		this.trackPendingEdit(newValue);
	}

	/** Stores pending markdown content without saving. */
	private pendingMarkdown: string | null = null;

	/** Called when markdown editor content changes (typing). */
	onMarkdownChange(newValue: string) {
		this.pendingMarkdown = newValue;
		this.trackPendingEdit(newValue);
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

		// While a conflict is pending, focusout must not touch local edit state: the
		// save is blocked until resolution, and the pending value + dirty ring must
		// survive so this field still appears in the resolution dialog. Losing focus
		// (e.g. clicking the Add Attribute button) is otherwise indistinguishable
		// from a no-op blur, which would clear the pending value and silently drop
		// the conflict. Keep pendingMarkdown intact too, so a later blur does not
		// then fall into the clean-up branch.
		if (this.conflicted()) {
			return;
		}

		if (
			this.pendingMarkdown !== null &&
			this.pendingMarkdown !== this.previousValue()
		) {
			this.saveAttribute(this.pendingMarkdown);
			this.pendingMarkdown = null;
		} else if (this.dirtyService.isDirty(this.editorKey())) {
			// No net change — clear dirty state (guarded to avoid signal churn).
			this.dirtyService.markClean(this.editorKey());
			this.pendingValuesService.remove(this.attr().id);
		}
	}

	private saveAttribute(newValue: string) {
		// Block save if a remote conflict exists — user must resolve first
		if (this.conflicted()) {
			return;
		}

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
					this.pendingValuesService.remove(this.attr().id);
					this.saving.set(false);
				},
				error: () => {
					this.saving.set(false);
				},
			});
	}

	ngOnDestroy() {
		// While a conflict is pending, this editor's dirty flag and pending value are
		// owned by the panel-level conflict flow, not by this component instance. The
		// editor can be torn down and rebuilt for purely structural reasons (e.g. a
		// sibling instance of the same type is added/removed, flipping the panel
		// between its single-instance and grouped rendering branches). Clearing state
		// on such a teardown would silently drop an in-progress conflict edit -- the
		// field would lose its ring and revert to the server value. So while
		// conflicted we preserve the state; it is cleared wholesale on resolve/dismiss.
		if (this.conflicted()) {
			return;
		}
		this.dirtyService.markClean(this.editorKey());
		this.pendingValuesService.remove(this.attr().id);
	}
}
