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
import { ArtifactLinkService } from '../../../../services/artifact-link.service';
import { MarkdownDropListRegistryService } from '../../../../services/markdown-drop-list-registry.service';

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
				<div class="tw-flex tw-flex-col tw-gap-1">
					@if (showLabel()) {
						<span
							class="tw-text-xs tw-text-[color:var(--mat-sys-on-surface-variant)]"
							[id]="'persisted-attr-boolean-label-' + attr().id">
							{{ attr().name ?? '' }}
						</span>
					}
					<mat-slide-toggle
						[ngModel]="displayValue() === 'true'"
						name="persisted-attr-boolean"
						[disabled]="disabled()"
						(ngModelChange)="onBooleanChange($event)"
						[attr.aria-label]="attr().name ?? 'Boolean attribute'"
						[attr.aria-labelledby]="
							showLabel()
								? 'persisted-attr-boolean-label-' + attr().id
								: null
						"
						class="primary-slide-toggle">
						{{ displayValue() === 'true' ? 'True' : 'False' }}
					</mat-slide-toggle>
				</div>
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
						(ngModelChange)="onEnumChange($event)">
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
						(mdContentCommitted)="onMarkdownCommitted($event)"
						[disabled]="!editable()"
						[artifactId]="artifactId()"
						[acceptArtifactDrops]="true"
						[dropListId]="markdownDropListId"
						[editorBranchId]="branchId()"
						[artifactLinkNames]="artifactLinkNames()"
						(artifactLinkIdsChange)="
							resolveArtifactLinkNames($event)
						"
						(artifactLinkClicked)="openArtifactLink($event)"
						(focusin)="markdownFocused.set(true)"
						(focusout)="onMarkdownFocusOut($event)" />
				} @else {
					<span class="tw-block">
						<osee-focus-lost-input
							[disabled]="disabled()"
							[value]="displayValue()"
							(valueChange)="onTextCommitted($event)"
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
	private artifactLinkService = inject(ArtifactLinkService);
	private dropListRegistry = inject(MarkdownDropListRegistryService);

	/** Resolved display names for `<artifact-link>` ids, used to label preview anchors. */
	protected readonly artifactLinkNames = signal<Map<string, string>>(
		new Map()
	);

	/**
	 * Unique CDK drop-list id for this editor's markdown drop target, registered
	 * with {@link MarkdownDropListRegistryService} so the hierarchy drag source can
	 * connect to it. Registered on construction and removed on destroy.
	 */
	protected readonly markdownDropListId = this.dropListRegistry.nextId();

	/** The attribute to edit. */
	attr = input.required<attribute<string, ATTRIBUTETYPEID>>();
	/** The artifact that owns this attribute. */
	artifactId = input.required<`${number}`>();
	/** Applicability of the owning artifact. */
	artifactApplicability = input.required<applic>();
	/**
	 * Branch of the owning artifact. Used to resolve `<artifact-link>` targets on
	 * the same branch as the link (markdown editor), matching desktop behavior.
	 * Attribute SAVES do not read this: the branch for writes comes from the
	 * subtree's branch context (see provideBranchContext on ArtifactEditorComponent),
	 * so a tab keeps writing to its own branch regardless of the global selector.
	 * Defaults to empty, in which case artifact-link resolution falls back to the
	 * current UI branch.
	 */
	branchId = input<string>('');
	/** View of the owning artifact, passed through to artifact-link resolution. */
	viewId = input<string>('');
	/** Whether the field is disabled. */
	disabled = input(false);
	/** Whether a remote conflict exists (blocks auto-save). */
	conflicted = input(false);
	/** Whether to show the field label. Set false when inside a grouped multi-instance section. */
	showLabel = input(true);

	/** Inverse of disabled for components that use editable. */
	protected editable = computed(() => !this.disabled());

	/** Whether this editor is for the Markdown Content attribute (renders the markdown editor). */
	protected readonly isMarkdown = computed(
		() => this.attr().name === 'Markdown Content'
	);

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
		// Register this editor's drop-list id with the hierarchy drag source only
		// while it is actually rendering the markdown editor; remove it otherwise so
		// the hierarchy never connects to a non-existent list.
		effect(() => {
			if (this.isMarkdown()) {
				this.dropListRegistry.register(this.markdownDropListId);
			} else {
				this.dropListRegistry.unregister(this.markdownDropListId);
			}
		});

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
				// Capture the generation this live edit belongs to, so a later deferred
				// commit can tell whether a resolve/discard invalidated it in between.
				this.editGeneration = this.dirtyService.generation();
			}
		} else if (this.dirtyService.isDirty(key) && !this.conflicted()) {
			// Value edited back to the persisted value -- clean the field. While a
			// conflict is pending we do NOT clean/prune: every edited attribute must
			// stay tracked so it appears in the resolution dialog and keeps its ring.
			this.dirtyService.markClean(key);
			this.pendingValuesService.remove(this.attr().id);
			this.editGeneration = null;
		}
	}

	/**
	 * Generation (from {@link EditorDirtyService}) that this field's current live
	 * edit was started in, or `null` when there is no live edit. A bulk clear
	 * (conflict resolve/discard/forced refresh) bumps the service generation; a
	 * deferred commit whose captured generation no longer matches is a stale echo
	 * of a discarded edit and must be dropped. See {@link commitDeferred}.
	 */
	private editGeneration: number | null = null;

	/**
	 * The single authoritative commit path. An edit is persisted only when it is a
	 * real change AND not blocked by a pending conflict (resolution owns the write
	 * while conflicted). Used by every widget via {@link commitImmediate} /
	 * {@link commitDeferred}; those differ only in the staleness check appropriate
	 * to their timing, not in how they save.
	 */
	private commit(newValue: string) {
		if (newValue !== this.previousValue()) {
			this.trackPendingEdit(newValue);
			this.saveAttribute(newValue);
		}
	}

	/**
	 * Commits an edit that happens synchronously at the user's action — boolean
	 * toggle, enum select, dropped markdown link/image. There is no staleness window
	 * (the action and the save are in the same tick), so it simply records and saves.
	 */
	private commitImmediate(newValue: string) {
		this.commit(newValue);
	}

	/**
	 * Commits an edit reported asynchronously after the user's action — the text
	 * input's debounced blur (and the markdown blur). Because time passes between
	 * the edit and this commit, a conflict resolve/discard can land in between.
	 *
	 * A deferred commit is only valid if it still represents this field's live edit.
	 * The authoritative test is value identity against the pending store: live
	 * keystrokes keep the pending value equal to the latest input, so a genuine
	 * commit's value matches it. A STALE commit fails this because either:
	 *   - resolve/discard cleared the pending slot (no pending => dropped), or
	 *   - the user discarded and then started a NEW edit, so the pending value is
	 *     now the new text while this echo still carries the OLD text (mismatch =>
	 *     dropped), which a generation-only check would miss.
	 * The generation is also checked as a cheap first gate (a bulk clear bumps it),
	 * but value identity is what makes the retype-after-discard race safe.
	 */
	private commitDeferred(newValue: string) {
		const pending = this.pendingValuesService.get(this.attr().id);
		const stale =
			this.editGeneration === null ||
			this.editGeneration !== this.dirtyService.generation() ||
			!this.dirtyService.isDirty(this.editorKey()) ||
			pending !== newValue;
		if (stale) {
			return;
		}
		this.commit(newValue);
	}

	onBooleanChange(checked: boolean) {
		this.commitImmediate(checked ? 'true' : 'false');
	}

	/** Enum selection is atomic — the select event is both the dirty signal and the commit. */
	onEnumChange(newValue: string) {
		this.commitImmediate(newValue);
	}

	/** Debounced blur commit of the text input. */
	onTextCommitted(newValue: string) {
		this.commitDeferred(newValue);
	}

	/**
	 * Fires on every keystroke (before blur) for single-line/text inputs so a
	 * concurrent remote change is detected as a conflict while the user is still
	 * typing — matching the markdown editor. Records the live edit (dirty + pending
	 * + generation); the actual save happens on blur via {@link onTextCommitted}.
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
	 * Called when the markdown editor commits an edit via a non-focus action (a
	 * dropped artifact link or inserted image). These bypass the textarea focus/blur
	 * cycle, so `onMarkdownFocusOut` would never fire to persist them — the field
	 * would stay dirty and block tab close. Save immediately instead (unless a
	 * conflict is pending, in which case the normal resolution flow applies).
	 */
	onMarkdownCommitted(newValue: string) {
		this.pendingMarkdown = newValue;
		// While conflicted, keep the edit tracked (dirty + pending) so it surfaces in
		// the resolution dialog, but do not save -- mirror onMarkdownFocusOut.
		if (this.conflicted()) {
			this.trackPendingEdit(newValue);
			return;
		}
		// A drop/image insert is an immediate commit: persist now rather than waiting
		// for a focusout that never comes.
		this.commitImmediate(newValue);
		this.pendingMarkdown = null;
	}

	/** Resolves display names for the artifact-link ids currently in the content. */
	protected resolveArtifactLinkNames(ids: string[]) {
		if (ids.length === 0) {
			this.artifactLinkNames.set(new Map());
			return;
		}
		this.artifactLinkService
			.getArtifactNames(ids, this.branchId(), this.viewId())
			.pipe(take(1))
			.subscribe((names) => this.artifactLinkNames.set(names));
	}

	/**
	 * Opens an artifact-link clicked in the preview in a new tab on the link's branch.
	 * The link opens on the same branch as this editor, so the new tab inherits this
	 * editor's editability ({@link editable}) rather than re-deriving it from the
	 * global branch type (which can differ from the link's branch).
	 */
	protected openArtifactLink(artifactId: string) {
		this.artifactLinkService.openArtifact(
			artifactId,
			this.branchId(),
			this.viewId(),
			this.editable()
		);
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
			// Blur is a deferred commit (like the text input): route through the
			// stale-commit guard so a focusout settling after a resolve/discard does
			// not re-save a discarded value.
			this.commitDeferred(this.pendingMarkdown);
			this.pendingMarkdown = null;
		} else if (this.dirtyService.isDirty(this.editorKey())) {
			// No net change — clear dirty state (guarded to avoid signal churn).
			this.dirtyService.markClean(this.editorKey());
			this.pendingValuesService.remove(this.attr().id);
			this.editGeneration = null;
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
					this.editGeneration = null;
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
		this.dropListRegistry.unregister(this.markdownDropListId);
		if (this.conflicted()) {
			return;
		}
		this.dirtyService.markClean(this.editorKey());
		this.pendingValuesService.remove(this.attr().id);
	}
}
