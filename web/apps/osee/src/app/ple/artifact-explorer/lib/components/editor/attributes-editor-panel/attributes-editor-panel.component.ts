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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	effect,
	inject,
	input,
	signal,
	untracked,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { HttpResourceRef } from '@angular/common/http';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { attribute } from '@osee/attributes/types';
import {
	ATTRIBUTETYPEID,
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
	isAttributeInstanceDeletable,
} from '@osee/attributes/constants';
import { PersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { FormDirective } from '@osee/shared/directives';
import { artifactWithRelations } from '@osee/artifact-with-relations/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { PersistedArtifactAttributeEditorComponent } from './persisted-artifact-attribute-editor/persisted-artifact-attribute-editor.component';
import { AttributeGroupComponent } from './attribute-group/attribute-group.component';
import { AttributeDeleteButtonComponent } from '@osee/shared/components';
import {
	NativeContentEditorComponent,
	NativeEditorAttributes,
	NameAttribute,
	ExtensionAttribute,
	NativeContentAttribute,
} from '../../../../../../shared/components/attributes-editor/native-content-editor/native-content-editor.component';
import { CurrentTransactionService } from '@osee/transactions/services';
import { map, skip, take } from 'rxjs';
import {
	AddAttributeDialogComponent,
	addAttributeDialogData,
	addAttributeDialogResult,
} from './add-attribute-dialog/add-attribute-dialog.component';
import {
	ConflictResolutionService,
	EditorDirtyService,
	PendingAttributeValuesService,
	StagedAttributeService,
	conflictChangeNotification,
	resolutionOperations,
} from '@osee/shared/conflict-resolution';

@Component({
	selector: 'osee-attributes-editor-panel',
	imports: [
		FormsModule,
		MatIcon,
		MatIconButton,
		MatTooltip,
		PersistedApplicabilityDropdownComponent,
		FormDirective,
		PersistedArtifactAttributeEditorComponent,
		NativeContentEditorComponent,
		AttributeGroupComponent,
		AttributeDeleteButtonComponent,
	],
	viewProviders: [provideOptionalControlContainerNgForm()],
	providers: [PendingAttributeValuesService, StagedAttributeService],
	templateUrl: './attributes-editor-panel.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
	styles: [
		`
			:host {
				--mat-select-enabled-trigger-text-color: inherit;
			}
		`,
	],
})
export class AttributesEditorPanelComponent {
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private tabService = inject(ArtifactExplorerTabService);
	private currentTxService = inject(CurrentTransactionService);
	private uiService = inject(UiService);
	private dialog = inject(MatDialog);
	private dirtyService = inject(EditorDirtyService);
	private pendingValuesService = inject(PendingAttributeValuesService);
	private stagedAttributeService = inject(StagedAttributeService);
	private conflictResolution = inject(ConflictResolutionService);

	tab = input.required<artifactTab>();
	deleteMode = input(false);
	/** Incremented by parent when a remote change is detected. */
	remoteChangeCount = input(0);

	/**
	 * Remote-change stream fed to the conflict dialog so it re-derives against fresh
	 * server state while open. Each parent increment of {@link remoteChangeCount} maps to
	 * an `attribute_modified` notification; the initial value is skipped so only genuine
	 * new remote changes trigger a live re-fetch. The dialog re-fetches on each emission.
	 */
	private readonly remoteChanges$ = toObservable(this.remoteChangeCount).pipe(
		skip(1),
		map(
			(): conflictChangeNotification => ({
				changeTypes: ['attribute_modified'],
				isLocal: false,
			})
		)
	);
	/** Shared artifact resource from parent (includes relations). */
	artifactResource =
		input.required<HttpResourceRef<artifactWithRelations | undefined>>();

	branchHasPleCategory = this.currBranchInfoService.branchHasPleCategory;

	/**
	 * Whether this artifact has been changed remotely while the user has
	 * unsaved local edits. When true, the template should show a warning banner.
	 */
	readonly remoteChangeWhileDirty = signal(false);

	/** True while the conflict dialog is fetching the latest server state. */
	readonly resolvingConflict = signal(false);

	// Derived signals for the resource
	private branchId = computed(() => this.tab().branchId);
	private _artifactId = computed(() => this.tab().artifact.id);
	private viewId = computed(() => this.tab().viewId);

	/** Track last seen remote change count to detect only NEW remote events. */
	private lastSeenRemoteCount = 0;

	/**
	 * Detects when a new remote change arrives while editors are dirty.
	 * Only fires on actual remoteChangeCount increments — not on re-evaluations
	 * caused by unrelated signal changes (like dirty state clearing during save).
	 *
	 * When dirty, the parent skips reloading the shared resource so the user's
	 * in-progress edits are preserved. We simply flag the conflict here; the
	 * latest server state is fetched on demand when the resolution dialog opens.
	 */
	private _flagRemoteConflict = effect(() => {
		const count = this.remoteChangeCount();
		if (count > this.lastSeenRemoteCount) {
			this.lastSeenRemoteCount = count;
			// Check dirty state without tracking it — we only want this effect
			// to re-run when remoteChangeCount changes, not when dirty state changes.
			const isDirty = untracked(() =>
				this.dirtyService.hasDirtyEditors()
			);
			if (isDirty) {
				this.remoteChangeWhileDirty.set(true);
			}
		}
	});

	/**
	 * Dismisses the remote-change warning and forces a refresh,
	 * discarding any local unsaved edits.
	 */
	dismissAndRefresh() {
		this.dirtyService.clearAll();
		this.pendingValuesService.clear();
		this.stagedAttributeService.clear();
		this.remoteChangeWhileDirty.set(false);
		this.artifactResource().reload();
	}

	/**
	 * Records a dirty attribute value so it can be used in conflict resolution.
	 * Called by child editors when they have pending unsaved changes.
	 */
	trackDirtyValue(attrId: string, value: string) {
		this.pendingValuesService.set(attrId, value);
	}

	/**
	 * Removes a tracked dirty value (e.g., after a successful save).
	 */
	clearDirtyValue(attrId: string) {
		this.pendingValuesService.remove(attrId);
	}

	/**
	 * Opens the conflict resolution dialog. Fetches the latest server state
	 * on demand (the shared resource was intentionally NOT reloaded so local
	 * edits are preserved), then compares each dirty attribute against the
	 * server value to build the set of true conflicts.
	 */
	openConflictResolutionDialog() {
		this.conflictResolution.resolve({
			entityName:
				this.artifactResource().value()?.name ??
				this.tab().artifact.name,
			entityId: this.artifactId(),
			// Base set for conflict detection is the real server-backed attributes
			// only. Staged adds are reconciled through their own channel below;
			// including them here would corrupt base-vs-server instance counts.
			baseAttrs: this.serverBackedAttributes(),
			fetchServerAttrs: () =>
				this.artExpHttpService
					.getartifactWithRelations(
						this.branchId(),
						this._artifactId(),
						this.viewId(),
						false
					)
					.pipe(map((a) => a.attributes)),
			pendingValues: this.pendingValuesService.getAll(),
			stagedAdds: this.stagedAddsForResolution(),
			// Keep an open dialog live: each new remote change re-fetches + re-categorizes
			// so the user never resolves against a stale server value.
			changes: this.remoteChanges$,
			commit: (ops) =>
				this.currentTxService
					.modifyArtifactAndMutate(
						'Resolving attribute conflicts',
						this.artifactId(),
						this.applicability(),
						this.toAttrConfig(ops)
					)
					.pipe(
						// Surface optimistic-concurrency rejections to the shared flow so a
						// stale write (someone changed the attr while the dialog was open) is
						// re-resolved against the latest value rather than silently reported
						// as applied.
						map((result) => ({
							staleGammas: result.failedGammas ?? [],
						}))
					),
			refresh: () => this.artifactResource().reload(),
			clearLocalState: () => {
				this.dirtyService.clearAll();
				this.pendingValuesService.clear();
				this.stagedAttributeService.clear();
				this.remoteChangeWhileDirty.set(false);
			},
			onError: (message) => (this.uiService.ErrorText = message),
			setResolving: (resolving) => this.resolvingConflict.set(resolving),
		});
	}

	/**
	 * Builds the staged-add inputs for conflict resolution, overlaying each staged
	 * instance with its latest pending value (a staged add edited in place before
	 * resolving records the newest value under its stagedKey, not on the stored
	 * instance). Keyed by stagedKey so the shared logic can echo it back on any
	 * resulting collision conflict.
	 */
	private stagedAddsForResolution() {
		return this.stagedAttributeService
			.getAll()
			.map(({ stagedKey, attr }) => {
				const pending = this.pendingValuesService.get(stagedKey);
				return {
					key: stagedKey,
					attr:
						pending !== undefined
							? { ...attr, value: pending }
							: attr,
				};
			});
	}

	/** Maps resolution operations to the transaction attr-config shape. */
	private toAttrConfig(ops: resolutionOperations): {
		set?: attribute<string, ATTRIBUTETYPEID>[];
		add?: attribute<string, ATTRIBUTETYPEID>[];
	} {
		const attrConfig: {
			set?: attribute<string, ATTRIBUTETYPEID>[];
			add?: attribute<string, ATTRIBUTETYPEID>[];
		} = {};
		if (ops.set.length > 0) attrConfig.set = ops.set;
		if (ops.add.length > 0) attrConfig.add = ops.add;
		return attrConfig;
	}

	/** Sync artifact name back to tab title when resource refreshes with a new name. */
	private _nameSyncEffect = effect(() => {
		const name = this.artifactResource().value()?.name;
		if (name && name !== this.tab().artifact.name) {
			this.tabService.updateTabTitle(this.tab().artifact.id, name);
		}
	});

	/**
	 * Computed that returns the current attributes sorted by typeId.
	 * Intentionally caches the last known good value in `_lastAttributes` as a side effect
	 * so that the UI does not flash empty while the resource is refetching.
	 */
	/**
	 * The artifact's real, server-backed attributes (sorted), WITHOUT any locally
	 * staged additions. This is the base set for conflict detection: staged adds
	 * are reconciled separately via their own channel, so mixing them in here would
	 * corrupt base-vs-server comparisons (e.g. inflating per-type instance counts
	 * used to detect a server-side add of the same type).
	 *
	 * Caches the last known good value in `_lastAttributes` as a side effect so the
	 * UI does not flash empty while the resource is refetching.
	 */
	protected serverBackedAttributes = computed<
		attribute<string, ATTRIBUTETYPEID>[]
	>(() => {
		const resourceAttrs = this.artifactResource().value()?.attributes;
		if (resourceAttrs) {
			this._lastAttributes = [...resourceAttrs].sort(this.byTypeThenId);
		}
		return (
			this._lastAttributes ??
			[...this.tab().artifact.attributes].sort(this.byTypeThenId)
		);
	});

	/**
	 * The attributes rendered in the editor: the server-backed set plus any
	 * locally-staged additions (created while conflicted, not yet persisted) so
	 * they render and can be edited in place. Staged instances keep their
	 * server-add sentinel gamma but expose the stable stagedKey as `id` so each
	 * stages/rings/resolves independently; the child editor keys dirty/pending
	 * state by `id`, and its persist path is a no-op while conflicted, so these
	 * never hit the transaction layer until resolution.
	 */
	protected attributes = computed<attribute<string, ATTRIBUTETYPEID>[]>(
		() => {
			const serverAttrs = this.serverBackedAttributes();
			const staged = this.stagedAttributesForDisplay();
			if (staged.length === 0) {
				return serverAttrs;
			}
			return [...serverAttrs, ...staged].sort(this.byTypeThenId);
		}
	);

	/** Stable attribute ordering: by type, then by instance id. */
	private byTypeThenId = (
		a: attribute<string, ATTRIBUTETYPEID>,
		b: attribute<string, ATTRIBUTETYPEID>
	) => {
		const typeCompare = a.typeId.localeCompare(b.typeId);
		if (typeCompare !== 0) return typeCompare;
		return a.id.localeCompare(b.id);
	};

	private _lastAttributes: attribute<string, ATTRIBUTETYPEID>[] | null = null;

	/**
	 * Staged additions shaped for display: the stable {@link stagedKey} is exposed
	 * as the instance `id` so dirty flags, pending values, and the amber ring key
	 * uniquely per staged instance (all share the `-1` add sentinel otherwise). The
	 * cast is deliberate and local to this panel, which owns the temp-key
	 * convention; the child editor treats `id` opaquely for keying and never
	 * persists these while conflicted.
	 */
	private stagedAttributesForDisplay = computed<
		attribute<string, ATTRIBUTETYPEID>[]
	>(() =>
		this.stagedAttributeService.stagedAdds().map(
			({ stagedKey, attr }) =>
				({
					...attr,
					id: stagedKey,
				}) as unknown as attribute<string, ATTRIBUTETYPEID>
		)
	);

	/** The Name attribute (always shown first). */
	protected nameAttr = computed(() =>
		this.attributes().find((a) => a.name?.toLowerCase() === 'name')
	);

	/** All attributes except Name and native-content-related ones (shown after applicability). */
	protected otherAttrs = computed(() =>
		this.attributes().filter(
			(a) =>
				a.name?.toLowerCase() !== 'name' &&
				a.typeId !== ATTRIBUTETYPEIDENUM.NATIVE_CONTENT &&
				a.typeId !== ATTRIBUTETYPEIDENUM.EXTENSION
		)
	);

	/** Attributes grouped by typeId for rendering. Single-instance types are solo, multi-instance are grouped. */
	protected groupedAttrs = computed(() => {
		const attrs = this.otherAttrs();
		const groups = new Map<
			string,
			{ name: string; attrs: attribute<string, ATTRIBUTETYPEID>[] }
		>();

		for (const attr of attrs) {
			const key = attr.typeId;
			if (!groups.has(key)) {
				groups.set(key, { name: attr.name ?? key, attrs: [] });
			}
			groups.get(key)!.attrs.push(attr);
		}

		return [...groups.values()];
	});

	/** Detect if this artifact has native content (Name + Extension + Native Content). */
	protected nativeEditorAttrs = computed<NativeEditorAttributes | null>(
		() => {
			const attrs = this.attributes();
			const name = attrs.find(
				(a) => a.typeId === BASEATTRIBUTETYPEIDENUM.NAME
			) as NameAttribute | undefined;
			const ext = attrs.find(
				(a) => a.typeId === ATTRIBUTETYPEIDENUM.EXTENSION
			) as ExtensionAttribute | undefined;
			const native = attrs.find(
				(a) => a.typeId === ATTRIBUTETYPEIDENUM.NATIVE_CONTENT
			) as NativeContentAttribute | undefined;
			return name && native
				? { name, extension: ext, nativeContent: native }
				: null;
		}
	);

	protected readonly pendingNativeName = signal<string | null>(null);
	protected readonly pendingNativeExtension = signal<string | null>(null);
	protected readonly hasUnsavedNativeChanges = signal<boolean>(false);
	private nativeContentChanges: attribute<string, ATTRIBUTETYPEID>[] = [];

	protected handleNativeContentChanges(
		changes: attribute<string, ATTRIBUTETYPEID>[]
	) {
		this.nativeContentChanges = changes;

		const nameChange = changes.find(
			(a) => a.typeId === BASEATTRIBUTETYPEIDENUM.NAME
		);
		const extChange = changes.find(
			(a) => a.typeId === ATTRIBUTETYPEIDENUM.EXTENSION
		);

		this.pendingNativeName.set(nameChange?.value ?? null);
		this.pendingNativeExtension.set(extChange?.value ?? null);
		this.hasUnsavedNativeChanges.set(changes.length > 0);
	}

	protected saveNativeContent() {
		if (this.nativeContentChanges.length === 0) return;

		// Build a single modifyArtifact call with all changes
		const setAttrs = this.nativeContentChanges.filter(
			(a) => !(a.id === '-1' && a.gammaId === '-1')
		);
		const addAttrs = this.nativeContentChanges.filter(
			(a) => a.id === '-1' && a.gammaId === '-1'
		);

		this.currentTxService
			.modifyArtifactAndMutate(
				'Updating native content',
				this.artifactId(),
				this.applicability(),
				{
					...(setAttrs.length > 0 ? { set: setAttrs } : {}),
					...(addAttrs.length > 0 ? { add: addAttrs } : {}),
				}
			)
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.nativeContentChanges = [];
					this.pendingNativeName.set(null);
					this.pendingNativeExtension.set(null);
					this.hasUnsavedNativeChanges.set(false);
				},
				error: () => {
					// Leave hasUnsavedNativeChanges as true so the user knows the save failed
				},
			});
	}

	protected editable = computed<boolean>(
		() =>
			this.artifactResource().value()?.editable ??
			this.tab().artifact.editable
	);

	protected artifactId = computed<`${number}`>(
		() =>
			(this.artifactResource().value()?.id ??
				this.tab().artifact.id) as `${number}`
	);

	protected applicability = computed(
		() =>
			this.artifactResource().value()?.applicability ??
			this.tab().artifact.applicability
	);

	/** The artifact type ID (used to fetch valid attribute types). */
	private artifactTypeId = computed<`${number}`>(
		() =>
			(this.artifactResource().value()?.typeId ??
				this.tab().artifact.typeId) as `${number}`
	);

	/** Opens the Add Attribute dialog. */
	openAddAttributeDialog() {
		this.artExpHttpService
			.getArtifactTypeAttributes(this.artifactTypeId())
			.pipe(take(1))
			.subscribe((allTypes) => {
				const dialogData: addAttributeDialogData = {
					allAttributeTypes: allTypes,
					existingAttributes: this.attributes(),
				};
				const dialogRef = this.dialog.open(
					AddAttributeDialogComponent,
					{
						data: dialogData,
						width: '480px',
						restoreFocus: false,
					}
				);
				dialogRef
					.afterClosed()
					.pipe(take(1))
					.subscribe(
						(result: addAttributeDialogResult | undefined) => {
							if (result && result.selectedTypes.length > 0) {
								this.addAttributes(result.selectedTypes);
							}
						}
					);
			});
	}

	/**
	 * Adds new attribute instances via the transaction service.
	 * Creates new attributes with default empty values for the selected types.
	 */
	private addAttributes(types: attribute<string, ATTRIBUTETYPEID>[]) {
		const newAttrs: attribute<string, ATTRIBUTETYPEID>[] = types.map(
			(type) => ({
				id: '-1' as const,
				typeId: type.typeId,
				gammaId: '-1' as const,
				// Prefer the server-seeded default (carried in the token's value,
				// e.g. Extension -> "md"); fall back to a store-type default.
				value: this.getSeededDefaultValue(type),
				name: type.name,
				storeType: type.storeType,
				multiplicity: type.multiplicity,
			})
		);

		// While a conflict is pending, do NOT commit new attributes to the server:
		// that would bypass resolution and immediately apply changes made in a
		// conflict state. Stage them locally instead (amber ring, editable in place),
		// to be reconciled and applied through the resolution dialog.
		if (this.remoteChangeWhileDirty()) {
			this.stageAttributes(newAttrs);
			return;
		}

		this.currentTxService
			.modifyArtifactAndMutate(
				`Adding attribute${newAttrs.length > 1 ? 's' : ''} to artifact`,
				this.artifactId(),
				this.applicability(),
				{ add: newAttrs }
			)
			.pipe(take(1))
			.subscribe({
				error: (err) => {
					this.uiService.ErrorText = `Failed to add attribute: ${err?.message ?? 'Unknown error'}`;
				},
			});
	}

	/**
	 * Stages new attribute instances locally instead of persisting them, used while
	 * the artifact is conflicted. Each staged instance gets a stable client key so
	 * it is dirty-tracked, ringed, and resolved independently (all share the `-1`
	 * add sentinel otherwise). The pending value is recorded under the same key so
	 * an in-place edit before resolution is carried into the dialog.
	 */
	private stageAttributes(newAttrs: attribute<string, ATTRIBUTETYPEID>[]) {
		for (const attr of newAttrs) {
			const stagedKey = this.stagedAttributeService.nextKey();
			this.stagedAttributeService.add(stagedKey, attr);
			this.pendingValuesService.set(stagedKey, `${attr.value}`);
			this.dirtyService.markDirty(`${this.artifactId()}-${stagedKey}`);
		}
	}

	/**
	 * Checks whether a specific attribute instance can be deleted
	 * based on multiplicity minimums. Shared with the create dialog so the rule
	 * stays consistent.
	 */
	protected isDeletable(attr: attribute<string, ATTRIBUTETYPEID>): boolean {
		return isAttributeInstanceDeletable(attr, this.attributes());
	}

	/**
	 * Deletes a single attribute instance inline from the editor.
	 * Checks multiplicity minimum before allowing deletion.
	 */
	protected deleteInlineAttribute(attr: attribute<string, ATTRIBUTETYPEID>) {
		if (!isAttributeInstanceDeletable(attr, this.attributes())) {
			this.uiService.ErrorText =
				'Cannot delete: at least one instance of this attribute is required.';
			return;
		}

		// A staged (not-yet-persisted) instance was never sent to the server, so
		// "deleting" it is purely local: drop it from the staged store and clear its
		// local edit state. It must NOT issue a delete mutation (there is nothing to
		// delete server-side) -- doing so while conflicted would round-trip the
		// server, shift the conflict base, and silently drop the pending conflict.
		if (this.stagedAttributeService.has(attr.id)) {
			this.discardStagedAttribute(attr.id);
			return;
		}

		// Deleting a persisted instance while conflicted would immediately commit and
		// bypass resolution (the same class of bug staging fixes for adds). Hold it
		// until the user resolves, matching how value edits are blocked while conflicted.
		if (this.remoteChangeWhileDirty()) {
			this.uiService.ErrorText =
				'Resolve the pending conflict before deleting this attribute.';
			return;
		}

		this.deleteAttributes([attr]);
	}

	/**
	 * Discards a locally-staged addition: removes it from the staged store and
	 * clears the dirty flag and pending value tracked under its client key. When no
	 * staged adds or other edits remain, the conflict banner is dismissed so the
	 * editor returns to a clean state.
	 */
	private discardStagedAttribute(stagedKey: string) {
		this.stagedAttributeService.remove(stagedKey);
		this.pendingValuesService.remove(stagedKey);
		this.dirtyService.markClean(`${this.artifactId()}-${stagedKey}`);
	}

	/**
	 * Deletes attribute instances via the transaction service using gammas.
	 */
	private deleteAttributes(attrs: attribute<string, ATTRIBUTETYPEID>[]) {
		this.currentTxService
			.modifyArtifactAndMutate(
				`Deleting attribute${attrs.length > 1 ? 's' : ''} from artifact`,
				this.artifactId(),
				this.applicability(),
				{ delete: attrs }
			)
			.pipe(take(1))
			.subscribe({
				error: (err) => {
					this.uiService.ErrorText = `Failed to delete attribute: ${err?.message ?? 'Unknown error'}`;
				},
			});
	}

	/**
	 * Returns the initial value for a newly added attribute. Prefers the
	 * server-seeded default carried in the attribute type token's `value`
	 * (from GET /orcs/types/artifact/{id}/attributes, e.g. Extension -> "md"),
	 * falling back to a store-type default when the type has no default.
	 */
	private getSeededDefaultValue(
		type: attribute<string, ATTRIBUTETYPEID>
	): string {
		const seeded = `${type.value ?? ''}`;
		if (seeded !== '') {
			return seeded;
		}
		return this.getStoreTypeDefault(type);
	}

	/** Returns an appropriate default value for a new attribute based on store type. */
	private getStoreTypeDefault(
		type: attribute<string, ATTRIBUTETYPEID>
	): string {
		switch (type.storeType) {
			case 'Boolean':
				return 'false';
			case 'Integer':
			case 'Long':
				return '0';
			default:
				return '';
		}
	}
}
