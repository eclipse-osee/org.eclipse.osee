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

/**
 * Client-side mirror of the custom markdown link tags defined (and documented as
 * the source of truth) in
 * `org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil`.
 *
 * Keep these patterns in sync with the Java constants `ARTIFACT_LINK_PATTERN`
 * and `IMAGE_LINK_PATTERN`.
 */

/** Valid image size labels for the `size` attribute on image-link tags. */
export const IMAGE_SIZE_VALUES = 'xs|s|m|l';

/**
 * Returns a FRESH global `<artifact-link>` regex (group 1 = artifact ID), mirroring
 * `MarkdownHtmlUtil.ARTIFACT_LINK_PATTERN`.
 *
 * These are factories rather than shared constants on purpose: a shared global
 * (`/g`) `RegExp` carries `lastIndex` state across `.test()`/`.exec()` calls, so a
 * shared singleton would hand different callers an inconsistent cursor and cause
 * intermittent misses. A fresh instance per use is stateless by construction.
 */
export function artifactLinkRegex(): RegExp {
	return /<artifact-link>(\d+)<\/artifact-link>/g;
}

/**
 * Returns a FRESH global `<image-link>` regex (group 1 = size, may be undefined;
 * group 2 = artifact ID), mirroring `MarkdownHtmlUtil.IMAGE_LINK_PATTERN`. Fresh
 * per call for the same stateless-by-construction reason as {@link artifactLinkRegex}.
 */
export function imageLinkRegex(): RegExp {
	return new RegExp(
		`<image-link(?:\\s+size="(${IMAGE_SIZE_VALUES})")?>(\\d+)<\\/image-link>`,
		'g'
	);
}

export function buildArtifactLinkTag(artifactId: string): string {
	return `<artifact-link>${artifactId}</artifact-link>`;
}

export function buildImageLinkTag(
	artifactId: string,
	size?: string | null
): string {
	const sizeAttr = size ? ` size="${size}"` : '';
	return `<image-link${sizeAttr}>${artifactId}</image-link>`;
}

/**
 * Replaces every `<artifact-link>ID</artifact-link>` in markdown with an anchor
 * the preview can render and the editor can intercept on click. The id is carried
 * in a `data-artifact-id` attribute (not a navigable `href`) so clicking opens the
 * artifact in a tab rather than following a dead link, and so the preview sandbox
 * does not treat it as external navigation.
 *
 * @param markdown    the raw markdown content
 * @param nameLookup  resolves an artifact id to a display name; falls back to the id
 */
export function resolveArtifactLinksToAnchors(
	markdown: string,
	nameLookup: (artifactId: string) => string | undefined
): string {
	return markdown.replace(artifactLinkRegex(), (_match, id: string) => {
		const name = nameLookup(id) ?? id;
		return `<a href="#" data-artifact-id="${id}">${escapeHtml(name)}</a>`;
	});
}

/** Collects the unique artifact ids referenced by `<artifact-link>` tags. */
export function extractArtifactLinkIds(markdown: string): string[] {
	const ids = new Set<string>();
	for (const match of markdown.matchAll(artifactLinkRegex())) {
		ids.add(match[1]);
	}
	return [...ids];
}

function escapeHtml(value: string): string {
	return value
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;');
}
