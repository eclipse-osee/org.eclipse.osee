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
import { navigationStructure } from './top-level-navigation-structure';
import { navigationElement } from '@osee/shared/types';
import { routes as appRoutes } from '../../../../app.routes';
import { Routes } from '@angular/router';

/**
 * Collects all route paths from the Angular route config (non-lazy, top-level).
 * Since lazy-loaded children aren't available at test time, we validate
 * that each nav routerLink's first segment matches a registered top-level path.
 */
function collectTopLevelPaths(routes: Routes): Set<string> {
	const paths = new Set<string>();
	for (const route of routes) {
		if (
			route.path !== undefined &&
			route.path !== '' &&
			route.path !== '**'
		) {
			paths.add('/' + route.path);
		}
	}
	return paths;
}

/**
 * Recursively collects all navigation elements into a flat list
 * with their depth for reporting.
 */
function flattenNavElements(
	elements: navigationElement[],
	depth = 0
): { element: navigationElement; depth: number; path: string[] }[] {
	const result: {
		element: navigationElement;
		depth: number;
		path: string[];
	}[] = [];
	for (const element of elements) {
		const labelPath = [element.label];
		result.push({ element, depth, path: labelPath });
		if (element.children.length > 0) {
			const childResults = flattenNavElements(
				element.children,
				depth + 1
			);
			for (const child of childResults) {
				child.path = [element.label, ...child.path];
				result.push(child);
			}
		}
	}
	return result;
}

describe('Navigation Structure Validation', () => {
	const allElements = flattenNavElements(navigationStructure);
	const topLevelPaths = collectTopLevelPaths(appRoutes);

	describe('Dropdown items', () => {
		const dropdowns = allElements.filter((e) => e.element.isDropdown);

		it('every dropdown should have at least one child', () => {
			const violations = dropdowns.filter(
				(e) => e.element.children.length === 0
			);
			expect(violations.map((v) => v.path.join(' > '))).toEqual([]);
		});

		it('no dropdown should have an empty label', () => {
			const violations = dropdowns.filter(
				(e) => e.element.label.trim() === ''
			);
			expect(violations.map((v) => v.path.join(' > '))).toEqual([]);
		});

		it('every dropdown should have routerLink as empty string', () => {
			const violations = dropdowns.filter(
				(e) => e.element.routerLink !== ''
			);
			expect(violations.map((v) => v.path.join(' > '))).toEqual([]);
		});
	});

	describe('Leaf (non-dropdown) items', () => {
		const leaves = allElements.filter((e) => !e.element.isDropdown);

		it('every leaf item should have a non-empty routerLink', () => {
			const violations = leaves.filter(
				(e) => e.element.routerLink.trim() === '' && !e.element.external
			);
			expect(violations.map((v) => v.path.join(' > '))).toEqual([]);
		});

		it('every leaf routerLink should start with /', () => {
			const violations = leaves.filter(
				(e) =>
					e.element.routerLink !== '' &&
					!e.element.external &&
					!e.element.routerLink.startsWith('/')
			);
			expect(violations.map((v) => v.path.join(' > '))).toEqual([]);
		});

		it('every leaf routerLink should match a registered top-level route', () => {
			const violations = leaves.filter((e) => {
				if (e.element.routerLink === '' || e.element.external) {
					return false;
				}
				// Check if the routerLink starts with any registered top-level path
				// This handles multi-segment paths like '/server/health'
				const link = e.element.routerLink;
				return ![...topLevelPaths].some(
					(registeredPath) =>
						link === registeredPath ||
						link.startsWith(registeredPath + '/')
				);
			});
			expect(
				violations.map(
					(v) => `${v.path.join(' > ')} (${v.element.routerLink})`
				)
			).toEqual([]);
		});

		it('no duplicate routerLinks among siblings', () => {
			const duplicates: string[] = [];

			function checkSiblings(
				elements: navigationElement[],
				parentPath: string
			) {
				const seen = new Map<string, string>();
				for (const el of elements) {
					if (!el.isDropdown && el.routerLink !== '') {
						if (seen.has(el.routerLink)) {
							duplicates.push(
								`${parentPath}: "${el.label}" and "${seen.get(el.routerLink)}" share routerLink "${el.routerLink}"`
							);
						} else {
							seen.set(el.routerLink, el.label);
						}
					}
					if (el.children.length > 0) {
						checkSiblings(
							el.children,
							`${parentPath} > ${el.label}`
						);
					}
				}
			}

			checkSiblings(navigationStructure, 'root');
			expect(duplicates).toEqual([]);
		});

		it('no leaf item should have non-empty children', () => {
			const violations = leaves.filter(
				(e) => e.element.children.length > 0
			);
			expect(
				violations.map(
					(v) =>
						`${v.path.join(' > ')} has ${v.element.children.length} children but isDropdown=false`
				)
			).toEqual([]);
		});

		it('all leaf children under a dropdown should share a common route prefix with siblings', () => {
			const violations: string[] = [];

			function checkPrefix(
				elements: navigationElement[],
				parentPath: string
			) {
				// Collect leaf routerLinks at this level
				const leafLinks = elements
					.filter((el) => !el.isDropdown && el.routerLink !== '')
					.map((el) => el.routerLink);

				if (leafLinks.length > 1) {
					// Find the longest common prefix among siblings
					const commonPrefix = leafLinks.reduce((prefix, link) => {
						let i = 0;
						while (
							i < prefix.length &&
							i < link.length &&
							prefix[i] === link[i]
						) {
							i++;
						}
						return prefix.substring(0, i);
					});

					// The common prefix should be at least the first path segment (e.g., "/ple" or "/ci")
					const firstSegment =
						'/' + leafLinks[0].split('/').filter(Boolean)[0];
					if (!commonPrefix.startsWith(firstSegment)) {
						violations.push(
							`${parentPath}: siblings do not share a common route prefix (links: ${leafLinks.join(', ')})`
						);
					}

					// Each leaf should start with the common prefix
					for (const link of leafLinks) {
						if (!link.startsWith(commonPrefix)) {
							violations.push(
								`${parentPath}: "${link}" does not share prefix "${commonPrefix}" with siblings`
							);
						}
					}
				}

				// Recurse into dropdown children
				for (const el of elements) {
					if (el.isDropdown && el.children.length > 0) {
						checkPrefix(el.children, `${parentPath} > ${el.label}`);
					}
				}
			}

			checkPrefix(navigationStructure, 'root');
			expect(violations).toEqual([]);
		});
	});

	describe('General structure', () => {
		it('every element should have a non-empty label', () => {
			const violations = allElements.filter(
				(e) => e.element.label.trim() === ''
			);
			expect(violations.length).toBe(0);
		});

		it('every element should have a valid icon or empty string', () => {
			const violations = allElements.filter(
				(e) => e.element.icon === undefined || e.element.icon === null
			);
			expect(violations.length).toBe(0);
		});

		it('navigation structure should not be empty', () => {
			expect(navigationStructure.length).toBeGreaterThan(0);
		});
	});
});
