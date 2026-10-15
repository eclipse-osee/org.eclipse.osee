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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarkdownEditorComponent } from './markdown-editor.component';
import { provideMarkdown } from 'ngx-markdown';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ArtifactExplorerHttpService } from '../../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../ple/artifact-explorer/lib/testing/artifact-explorer-http.service.mock';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';

describe('MarkdownEditorComponent', () => {
	let component: MarkdownEditorComponent;
	let fixture: ComponentFixture<MarkdownEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MarkdownEditorComponent, NoopAnimationsModule],
			providers: [
				provideMarkdown(),
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MarkdownEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('mdContent', 'this is a **bold**');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('artifact drag-and-drop linking', () => {
		/** Minimal CdkDragDrop stub carrying the dragged artifact as item.data. */
		function dropEvent(data: unknown) {
			return { item: { data } } as never;
		}

		/**
		 * An artifact-with-relations-shaped object. `typeId` mirrors the lightweight
		 * hierarchy drag payload (type is present, attributes are not); `extensions`
		 * lets a test exercise the secondary Extension-attribute signal for a fully
		 * attributed artifact.
		 */
		function artifact(
			id: string,
			opts: { typeId?: string; extensions?: string[] } = {}
		) {
			return {
				id,
				name: `Artifact ${id}`,
				typeId: opts.typeId ?? '789',
				attributes: (opts.extensions ?? []).map((ext) => ({
					typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
					value: ext,
				})),
			};
		}

		beforeEach(() => {
			fixture.componentRef.setInput('acceptArtifactDrops', true);
			fixture.componentRef.setInput('mdContent', '');
			fixture.detectChanges();
		});

		it('inserts an <artifact-link> for a non-image artifact and emits a committed edit', () => {
			const committed: string[] = [];
			component.mdContentCommitted.subscribe((v) => committed.push(v));

			component.onArtifactDropped(dropEvent(artifact('500')));

			expect(component.mdContent()).toContain(
				'<artifact-link>500</artifact-link>'
			);
			expect(committed.at(-1)).toContain(
				'<artifact-link>500</artifact-link>'
			);
		});

		it('inserts an <image-link> for an image-TYPE artifact by default (embed intent)', () => {
			// Mirrors the real hierarchy drag payload: image detected by typeId
			// (ARTIFACTTYPEIDENUM.IMAGE), with no Extension attribute present.
			const img = artifact('600', { typeId: ARTIFACTTYPEIDENUM.IMAGE });
			component.onArtifactDragEntered(dropEvent(img));
			component.onArtifactDropped(dropEvent(img));
			expect(component.mdContent()).toContain(
				'<image-link>600</image-link>'
			);
			expect(component.mdContent()).not.toContain('<artifact-link>');
		});

		it('detects an image via a supported Extension attribute when the type is not Image (fully attributed artifact)', () => {
			const img = artifact('650', { extensions: ['png'] });
			component.onArtifactDragEntered(dropEvent(img));
			expect(component['artifactDragIsImage']()).toBe(true);
			component.onArtifactDropped(dropEvent(img));
			expect(component.mdContent()).toContain(
				'<image-link>650</image-link>'
			);
		});

		it('inserts an <artifact-link> for an image artifact when dropped with the link intent', () => {
			const img = artifact('600', { typeId: ARTIFACTTYPEIDENUM.IMAGE });
			component.onArtifactDragEntered(dropEvent(img));
			// The user hovered the "Insert link" half of the split overlay.
			component['setDropIntent']('link');
			component.onArtifactDropped(dropEvent(img));
			expect(component.mdContent()).toContain(
				'<artifact-link>600</artifact-link>'
			);
			expect(component.mdContent()).not.toContain('<image-link>');
		});

		it('exposes that the drag is an image on enter (drives the split overlay)', () => {
			component.onArtifactDragEntered(
				dropEvent(artifact('600', { typeId: ARTIFACTTYPEIDENUM.IMAGE }))
			);
			expect(component['artifactDragIsImage']()).toBe(true);

			component.onArtifactDragEntered(dropEvent(artifact('601')));
			expect(component['artifactDragIsImage']()).toBe(false);
		});

		it('a non-image artifact ignores drop intent and always inserts an artifact-link', () => {
			const nonImage = artifact('602');
			component.onArtifactDragEntered(dropEvent(nonImage));
			// Even if intent were somehow 'image', a non-image stays a reference.
			component['setDropIntent']('image');
			component.onArtifactDropped(dropEvent(nonImage));
			expect(component.mdContent()).toContain(
				'<artifact-link>602</artifact-link>'
			);
			expect(component.mdContent()).not.toContain('<image-link>');
		});

		it('ignores a drop whose payload is not a real artifact (no id / sentinel id)', () => {
			const before = component.mdContent();
			component.onArtifactDropped(dropEvent(undefined));
			component.onArtifactDropped(dropEvent(artifact('-1')));
			expect(component.mdContent()).toBe(before);
		});

		it('does nothing on drop when disabled', () => {
			fixture.componentRef.setInput('disabled', true);
			fixture.detectChanges();
			const before = component.mdContent();
			component.onArtifactDropped(dropEvent(artifact('700')));
			expect(component.mdContent()).toBe(before);
		});

		it('enter-predicate accepts an artifact payload and rejects a non-artifact (tab reorder) payload', () => {
			const acceptsArtifact = component['acceptArtifactDrag']({
				data: artifact('800'),
			} as never);
			const rejectsNonArtifact = component['acceptArtifactDrag']({
				data: undefined,
			} as never);
			expect(acceptsArtifact).toBe(true);
			expect(rejectsNonArtifact).toBe(false);
		});

		it('emits artifactLinkClicked and prevents default when a preview link is clicked ON ITS TEXT', () => {
			const clicked: string[] = [];
			component.artifactLinkClicked.subscribe((id) => clicked.push(id));

			// Build <a data-artifact-id="900">name</a> and fire the click with the
			// inner TEXT NODE as the event target — this is what a real user click
			// produces. A handler that reads event.target directly gets the text
			// node (no closest()) and falls through to native href navigation; the
			// resolve-to-nearest-element guard is what makes this work.
			const anchor = document.createElement('a');
			anchor.setAttribute('data-artifact-id', '900');
			anchor.setAttribute('href', '#');
			anchor.textContent = 'Linked artifact';
			document.body.appendChild(anchor);
			const textNode = anchor.firstChild as Text;

			const event = new MouseEvent('click', { bubbles: true });
			Object.defineProperty(event, 'target', { value: textNode });
			const prevented = vi.spyOn(event, 'preventDefault');

			component.onPreviewClick(event);
			anchor.remove();

			expect(clicked).toContain('900');
			expect(prevented).toHaveBeenCalled();
		});

		it('ignores a preview click that is not on an artifact link', () => {
			const clicked: string[] = [];
			component.artifactLinkClicked.subscribe((id) => clicked.push(id));

			const div = document.createElement('div');
			div.textContent = 'plain text';
			document.body.appendChild(div);
			const event = new MouseEvent('click', { bubbles: true });
			Object.defineProperty(event, 'target', {
				value: div.firstChild,
			});
			const prevented = vi.spyOn(event, 'preventDefault');

			component.onPreviewClick(event);
			div.remove();

			expect(clicked).toEqual([]);
			// A non-link click must NOT be hijacked.
			expect(prevented).not.toHaveBeenCalled();
		});

		it('emits the referenced artifact-link ids when content changes', () => {
			const idSets: string[][] = [];
			component.artifactLinkIdsChange.subscribe((ids) =>
				idSets.push(ids)
			);
			component.mdContent.set(
				'see <artifact-link>11</artifact-link> and <artifact-link>22</artifact-link>'
			);
			fixture.detectChanges();
			expect(idSets.at(-1)).toEqual(['11', '22']);
		});
	});
});
