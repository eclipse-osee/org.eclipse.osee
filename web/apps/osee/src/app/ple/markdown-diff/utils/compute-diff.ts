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
import { DiffRow } from '../types/markdown-diff';

/**
 * Computes a side-by-side diff between two texts using LCS.
 * Returns an array of DiffRow objects for rendering in a two-panel view.
 */
export function computeSideBySideDiff(
	oldText: string,
	newText: string
): DiffRow[] {
	// If one side is empty, show all lines as added/removed without LCS
	if (!oldText.trim()) {
		return newText.split('\n').map(
			(line, i): DiffRow => ({
				old: { type: 'empty', lineNumber: null, content: '' },
				new: { type: 'added', lineNumber: i + 1, content: line },
			})
		);
	}
	if (!newText.trim()) {
		return oldText.split('\n').map(
			(line, i): DiffRow => ({
				old: { type: 'removed', lineNumber: i + 1, content: line },
				new: { type: 'empty', lineNumber: null, content: '' },
			})
		);
	}

	const oldLines = oldText.split('\n');
	const newLines = newText.split('\n');
	const lcs = computeLCS(oldLines, newLines);
	const rows: DiffRow[] = [];

	let oldIdx = 0;
	let newIdx = 0;
	let oldLineNum = 1;
	let newLineNum = 1;

	for (const match of lcs) {
		// Pair up removed and added lines before this match
		const removedLines: string[] = [];
		const addedLines: string[] = [];

		while (oldIdx < match.oldIndex) {
			removedLines.push(oldLines[oldIdx]);
			oldIdx++;
		}
		while (newIdx < match.newIndex) {
			addedLines.push(newLines[newIdx]);
			newIdx++;
		}

		// Pair removed/added into side-by-side rows
		const maxLen = Math.max(removedLines.length, addedLines.length);
		for (let i = 0; i < maxLen; i++) {
			rows.push({
				old:
					i < removedLines.length
						? {
								type: 'removed',
								lineNumber: oldLineNum + i,
								content: removedLines[i],
							}
						: { type: 'empty', lineNumber: null, content: '' },
				new:
					i < addedLines.length
						? {
								type: 'added',
								lineNumber: newLineNum + i,
								content: addedLines[i],
							}
						: { type: 'empty', lineNumber: null, content: '' },
			});
		}
		oldLineNum += removedLines.length;
		newLineNum += addedLines.length;

		// Emit the matched (unchanged) line
		rows.push({
			old: {
				type: 'unchanged',
				lineNumber: oldLineNum,
				content: oldLines[oldIdx],
			},
			new: {
				type: 'unchanged',
				lineNumber: newLineNum,
				content: newLines[newIdx],
			},
		});
		oldIdx++;
		newIdx++;
		oldLineNum++;
		newLineNum++;
	}

	// Remaining lines after last LCS match
	const remainingRemoved: string[] = [];
	const remainingAdded: string[] = [];

	while (oldIdx < oldLines.length) {
		remainingRemoved.push(oldLines[oldIdx]);
		oldIdx++;
	}
	while (newIdx < newLines.length) {
		remainingAdded.push(newLines[newIdx]);
		newIdx++;
	}

	const maxLen = Math.max(remainingRemoved.length, remainingAdded.length);
	for (let i = 0; i < maxLen; i++) {
		rows.push({
			old:
				i < remainingRemoved.length
					? {
							type: 'removed',
							lineNumber: oldLineNum + i,
							content: remainingRemoved[i],
						}
					: { type: 'empty', lineNumber: null, content: '' },
			new:
				i < remainingAdded.length
					? {
							type: 'added',
							lineNumber: newLineNum + i,
							content: remainingAdded[i],
						}
					: { type: 'empty', lineNumber: null, content: '' },
		});
	}

	return rows;
}

type LCSMatch = {
	oldIndex: number;
	newIndex: number;
};

function computeLCS(oldLines: string[], newLines: string[]): LCSMatch[] {
	const m = oldLines.length;
	const n = newLines.length;

	const dp: number[][] = Array.from({ length: m + 1 }, () =>
		Array(n + 1).fill(0)
	);

	for (let i = 1; i <= m; i++) {
		for (let j = 1; j <= n; j++) {
			if (oldLines[i - 1] === newLines[j - 1]) {
				dp[i][j] = dp[i - 1][j - 1] + 1;
			} else {
				dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
			}
		}
	}

	const matches: LCSMatch[] = [];
	let i = m;
	let j = n;

	while (i > 0 && j > 0) {
		if (oldLines[i - 1] === newLines[j - 1]) {
			matches.unshift({ oldIndex: i - 1, newIndex: j - 1 });
			i--;
			j--;
		} else if (dp[i - 1][j] > dp[i][j - 1]) {
			i--;
		} else {
			j--;
		}
	}

	return matches;
}
