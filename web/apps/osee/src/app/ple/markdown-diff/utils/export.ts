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
import { MarkdownDiffEntry } from '../types/markdown-diff';
import { computeSideBySideDiff } from './compute-diff';

/** A newly added artifact: no prior value, so render every line as added. */
function isNewEntry(entry: MarkdownDiffEntry): boolean {
	return entry.changeType === 'New' || !entry.wasValue.trim();
}

/** A deleted artifact: no current value, so render every line as removed. */
function isDeletedEntry(entry: MarkdownDiffEntry): boolean {
	return (
		entry.changeType.toLowerCase().includes('deleted') ||
		!entry.isValue.trim()
	);
}

/**
 * Number of entries processed between yields to the event loop. Large reports
 * (thousands of entries, each running an LCS diff) would otherwise block the
 * main thread for seconds; yielding keeps the UI responsive.
 */
const EXPORT_BATCH_SIZE = 25;

/** Lets the browser paint/handle events before the next batch of entries. */
function yieldToEventLoop(): Promise<void> {
	return new Promise((resolve) => setTimeout(resolve, 0));
}

/**
 * Returns a fence long enough that none of the content lines can accidentally
 * close it. A CommonMark fence is only closed by a line of the same fence
 * character at least as long as the opener, so we use one backtick more than
 * the longest backtick run found in the content.
 */
function fenceFor(contentLines: string[]): string {
	let longestRun = 0;
	for (const line of contentLines) {
		const matches = line.match(/`+/g);
		if (matches) {
			for (const run of matches) {
				if (run.length > longestRun) {
					longestRun = run.length;
				}
			}
		}
	}
	return '`'.repeat(Math.max(3, longestRun + 1));
}

/**
 * Generates a Markdown export of the diff report.
 * Uses fenced code blocks with +/- prefixes to show changes.
 *
 * Async + batched so exporting thousands of entries does not lock up the UI.
 */
export async function generateMarkdownExport(
	entries: MarkdownDiffEntry[],
	branchName: string,
	parentBranchName: string
): Promise<string> {
	const lines: string[] = [];
	lines.push(`# Markdown Change Report`);
	lines.push('');
	lines.push(
		`Branch: **${branchName}** compared to parent: **${parentBranchName}**`
	);
	lines.push('');
	lines.push(`Total artifacts with markdown changes: ${entries.length}`);
	lines.push('');
	lines.push('---');
	lines.push('');

	for (let i = 0; i < entries.length; i++) {
		const entry = entries[i];
		lines.push(`## ${entry.artifactName} (ID: ${entry.artifactId})`);
		lines.push('');
		lines.push(`**Change Type:** ${entry.changeDescription}`);
		lines.push('');

		if (isNewEntry(entry)) {
			const contentLines = entry.isValue.split('\n');
			const fence = fenceFor(contentLines);
			lines.push(`${fence}diff`);
			for (const line of contentLines) {
				lines.push(`+ ${line}`);
			}
			lines.push(fence);
		} else if (isDeletedEntry(entry)) {
			const contentLines = entry.wasValue.split('\n');
			const fence = fenceFor(contentLines);
			lines.push(`${fence}diff`);
			for (const line of contentLines) {
				lines.push(`- ${line}`);
			}
			lines.push(fence);
		} else {
			const rows = computeSideBySideDiff(entry.wasValue, entry.isValue);
			const diffLines: string[] = [];
			for (const row of rows) {
				if (row.old.type === 'removed') {
					diffLines.push(`- ${row.old.content}`);
				}
				if (row.new.type === 'added') {
					diffLines.push(`+ ${row.new.content}`);
				}
				if (
					row.old.type === 'unchanged' &&
					row.new.type === 'unchanged'
				) {
					diffLines.push(`  ${row.old.content}`);
				}
			}
			const fence = fenceFor(diffLines);
			lines.push(`${fence}diff`);
			lines.push(...diffLines);
			lines.push(fence);
		}
		lines.push('');
		lines.push('---');
		lines.push('');

		if ((i + 1) % EXPORT_BATCH_SIZE === 0) {
			await yieldToEventLoop();
		}
	}

	return lines.join('\n');
}

/**
 * Generates an HTML export of the diff report with embedded styles.
 * Produces a self-contained HTML document suitable for printing or saving.
 */
export async function generateHtmlExport(
	entries: MarkdownDiffEntry[],
	branchName: string,
	parentBranchName: string
): Promise<string> {
	const entryHtml: string[] = [];
	for (let i = 0; i < entries.length; i++) {
		const entry = entries[i];
		const diffHtml = generateDiffTableHtml(entry);
		entryHtml.push(`
		<div class="entry">
			<div class="entry-header">
				<span class="entry-name">${escapeHtml(entry.artifactName)}</span>
				<span class="badge badge-${entry.changeType.toLowerCase()}">${escapeHtml(entry.changeDescription)}</span>
				<span class="entry-id">Artifact ID: ${escapeHtml(entry.artifactId)}</span>
			</div>
			${diffHtml}
		</div>`);

		if ((i + 1) % EXPORT_BATCH_SIZE === 0) {
			await yieldToEventLoop();
		}
	}
	const entryRows = entryHtml.join('\n');

	return `<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Markdown Change Report - ${escapeHtml(branchName)}</title>
	<style>
		body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; margin: 0; padding: 24px; background: #fafafa; color: #333; }
		h1 { font-size: 1.5rem; margin-bottom: 4px; }
		.subtitle { color: #666; font-size: 0.875rem; margin-bottom: 24px; }
		.entry { border: 1px solid #e0e0e0; border-radius: 8px; margin-bottom: 24px; overflow: hidden; background: #fff; }
		.entry-header { display: flex; align-items: center; gap: 8px; padding: 12px 16px; background: #f5f5f5; border-bottom: 1px solid #e0e0e0; }
		.entry-name { font-weight: bold; font-size: 0.95rem; }
		.entry-id { margin-left: auto; font-size: 0.75rem; color: #999; }
		.badge { padding: 2px 8px; border-radius: 4px; font-size: 0.7rem; font-weight: bold; }
		.badge-new { background: #c8e6c9; color: #2e7d32; }
		.badge-deleted { background: #ffcdd2; color: #c62828; }
		.badge-modified { background: #bbdefb; color: #1565c0; }
		.diff-table { width: 100%; border-collapse: collapse; font-family: 'SFMono-Regular', Consolas, monospace; font-size: 0.8rem; }
		.diff-table td { padding: 2px 8px; vertical-align: top; white-space: pre-wrap; word-break: break-word; }
		.diff-table .line-num { width: 36px; text-align: right; color: #999; user-select: none; border-right: 1px solid #e0e0e0; }
		.diff-table .divider { width: 1px; background: #e0e0e0; }
		.row-removed td { background: #ffebee; }
		.row-added td { background: #e8f5e9; }
		.col-header { padding: 4px 8px; font-size: 0.7rem; font-weight: bold; border-bottom: 1px solid #e0e0e0; background: #f5f5f5; }
		.col-header-old { }
		.col-header-new { }
		@media print { body { background: #fff; } .entry { break-inside: avoid; } }
	</style>
</head>
<body>
	<h1>Markdown Change Report</h1>
	<p class="subtitle">Branch: <strong>${escapeHtml(branchName)}</strong> compared to parent: <strong>${escapeHtml(parentBranchName)}</strong> &mdash; ${entries.length} artifact(s) with markdown changes</p>
	${entryRows}
</body>
</html>`;
}

function generateDiffTableHtml(entry: MarkdownDiffEntry): string {
	let rows: string;

	if (isNewEntry(entry)) {
		rows = entry.isValue
			.split('\n')
			.map(
				(line, i) => `
			<tr class="row-added">
				<td class="line-num"></td>
				<td></td>
				<td class="divider"></td>
				<td class="line-num">${i + 1}</td>
				<td>${escapeHtml(line)}</td>
			</tr>`
			)
			.join('');
	} else if (isDeletedEntry(entry)) {
		rows = entry.wasValue
			.split('\n')
			.map(
				(line, i) => `
			<tr class="row-removed">
				<td class="line-num">${i + 1}</td>
				<td>${escapeHtml(line)}</td>
				<td class="divider"></td>
				<td class="line-num"></td>
				<td></td>
			</tr>`
			)
			.join('');
	} else {
		const diffRows = computeSideBySideDiff(entry.wasValue, entry.isValue);
		rows = diffRows
			.map((row) => {
				const oldClass =
					row.old.type === 'removed' ? 'row-removed' : '';
				const newClass = row.new.type === 'added' ? 'row-added' : '';
				// If both sides have a class, apply to each cell individually
				const rowClass =
					oldClass && newClass ? '' : oldClass || newClass;
				return `
			<tr class="${rowClass}">
				<td class="line-num${oldClass ? ' row-removed' : ''}">${row.old.lineNumber ?? ''}</td>
				<td${oldClass ? ' class="row-removed"' : ''}>${escapeHtml(row.old.content)}</td>
				<td class="divider"></td>
				<td class="line-num${newClass ? ' row-added' : ''}">${row.new.lineNumber ?? ''}</td>
				<td${newClass ? ' class="row-added"' : ''}>${escapeHtml(row.new.content)}</td>
			</tr>`;
			})
			.join('');
	}

	return `
		<table class="diff-table">
			<tr>
				<td class="col-header col-header-old" colspan="2">Previous</td>
				<td class="divider"></td>
				<td class="col-header col-header-new" colspan="2">Current</td>
			</tr>
			${rows}
		</table>`;
}

function escapeHtml(text: string): string {
	return text
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/"/g, '&quot;');
}
