// SpotBugs PR Analysis Script
// Runs SpotBugs on all org.eclipse.osee packages, then filters results to
// only report issues in files changed by the PR.
// Usage: node .github/scripts/spotbugs-analysis.js
// Requires environment: GITHUB_TOKEN, GITHUB_REPOSITORY, GITHUB_RUN_ID,
//   GITHUB_SERVER_URL, GITHUB_EVENT_PATH (all provided by GitHub Actions)

const fs = require('fs');
const { execSync } = require('child_process');

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function countBugs(file) {
  if (!fs.existsSync(file)) return -1;
  const m = fs.readFileSync(file, 'utf8').match(/total_bugs="(\d+)"/);
  return m ? parseInt(m[1], 10) : 0;
}

function parseBugs(file) {
  if (!fs.existsSync(file)) return [];
  const xml = fs.readFileSync(file, 'utf8');
  const bugs = [];

  // Use split-based parsing instead of a single regex over the entire file.
  // The [\s\S]*? regex fails on multi-MB XML due to backtracking limits.
  const chunks = xml.split('<BugInstance ');
  for (let i = 1; i < chunks.length; i++) {
    const endIdx = chunks[i].indexOf('</BugInstance>');
    if (endIdx === -1) continue;
    const b = '<BugInstance ' + chunks[i].substring(0, endIdx + '</BugInstance>'.length);

    const tag = (t) => {
      const x = b.match(new RegExp(`<${t}>([^<]*)</${t}>`));
      return x ? x[1] : '';
    };
    const opening = b.substring(0, b.indexOf('>') + 1);
    const sl = b.match(/<SourceLine[^>]*>/)?.[0] || '';
    bugs.push({
      type: opening.match(/type="([^"]*)"/)?.[1] || '',
      priority: opening.match(/priority="([^"]*)"/)?.[1] || '',
      category: opening.match(/category="([^"]*)"/)?.[1] || '',
      sourcepath: sl.match(/sourcepath="([^"]*)"/)?.[1] || '',
      methodName: (b.match(/<Method[^>]*name="([^"]*)"/) || [])[1] || '',
      message: tag('ShortMessage'),
      longMsg: tag('LongMessage'),
    });
  }
  return bugs;
}

const PRIORITY_LABELS = {
  '1': ':red_circle: High',
  '2': ':orange_circle: Medium',
  '3': ':yellow_circle: Low',
};

// ---------------------------------------------------------------------------
// GitHub API helpers (using curl for reliable networking in Alpine containers)
// ---------------------------------------------------------------------------

const token = process.env.GITHUB_TOKEN;
const [owner, repo] = process.env.GITHUB_REPOSITORY.split('/');
const runId = process.env.GITHUB_RUN_ID;
const eventPath = process.env.GITHUB_EVENT_PATH;
const spotbugsBin =
  process.env.SPOTBUGS_BIN || '/root/spotbugs/current/bin/spotbugs';

const event = JSON.parse(fs.readFileSync(eventPath, 'utf8'));
const pullNumber = event.pull_request?.number;
const headSha = event.pull_request?.head?.sha;

if (!pullNumber) {
  console.log('Not a pull request event — skipping.');
  process.exit(0);
}

function ghGet(url) {
  const out = execSync(
    `curl -fsSL ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  return JSON.parse(out);
}

function ghPost(url, body) {
  const tmp = '/tmp/_gh_post_body.json';
  fs.writeFileSync(tmp, JSON.stringify(body));
  const out = execSync(
    `curl -fsSL -X POST ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `-H "Content-Type: application/json" ` +
      `-d @${tmp} ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  return JSON.parse(out);
}

function ghPatch(url, body) {
  const tmp = '/tmp/_gh_patch_body.json';
  fs.writeFileSync(tmp, JSON.stringify(body));
  const out = execSync(
    `curl -fsSL -X PATCH ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `-H "Content-Type: application/json" ` +
      `-d @${tmp} ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  return JSON.parse(out);
}

/** Paginate through all changed files in the PR. */
function getChangedFiles() {
  const files = [];
  let page = 1;
  while (true) {
    const batch = ghGet(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/files?per_page=100&page=${page}`
    );
    files.push(...batch);
    if (batch.length < 100) break;
    page++;
  }
  return files;
}

// ---------------------------------------------------------------------------
// HTML report generator for changed-file bugs
// ---------------------------------------------------------------------------

function generateChangedHtml(bugs, srcToRepo) {
  const priorityText = { '1': 'High', '2': 'Medium', '3': 'Low' };
  const priorityColor = { '1': '#d32f2f', '2': '#f57c00', '3': '#fbc02d' };

  const rows = bugs
    .map((bug) => {
      const file = srcToRepo[bug.sourcepath] || bug.sourcepath || '';
      const color = priorityColor[bug.priority] || '#999';
      const pLabel = priorityText[bug.priority] || bug.priority;
      const desc = bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message;
      const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
      return `<tr>
        <td style="color:${color};font-weight:bold">${pLabel}</td>
        <td><code>${escapeHtml(file)}</code></td>
        <td><code>${escapeHtml(bug.methodName || '')}()</code></td>
        <td><a href="${docsUrl}">${escapeHtml(bug.type)}</a></td>
        <td>${escapeHtml(desc || bug.type)}</td>
      </tr>`;
    })
    .join('\n');

  return `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>SpotBugs — Changed Files Report</title>
<style>
  body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; margin: 2rem; }
  h1 { color: #333; }
  table { border-collapse: collapse; width: 100%; margin-top: 1rem; }
  th, td { border: 1px solid #ddd; padding: 8px 12px; text-align: left; }
  th { background: #f5f5f5; }
  tr:nth-child(even) { background: #fafafa; }
  code { background: #f0f0f0; padding: 2px 4px; border-radius: 3px; font-size: 0.9em; }
  a { color: #1976d2; text-decoration: none; }
  a:hover { text-decoration: underline; }
  .summary { color: #555; margin-top: 0.5rem; }
</style>
</head>
<body>
<h1>&#x1f41e; SpotBugs — Changed Files Report</h1>
<p class="summary">${bugs.length} issue${bugs.length !== 1 ? 's' : ''} found in files changed by this PR.</p>
${
  bugs.length > 0
    ? `<table>
<thead><tr><th>Priority</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead>
<tbody>
${rows}
</tbody>
</table>`
    : `<p><strong>&#x2705; No issues found in changed files.</strong></p>`
}
</body>
</html>`;
}

function escapeHtml(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// ---------------------------------------------------------------------------
// Main
// ---------------------------------------------------------------------------

function main() {
  // --- Run SpotBugs analysis (single run, full codebase) ---
  console.log('Running SpotBugs full analysis on org.eclipse.osee.-');
  try {
    execSync(
      `${spotbugsBin} -textui -low -effort:max ` +
        `-xml:withMessages -output spotbugs-report-full.xml ` +
        `-auxclasspathFromInput -onlyAnalyze "org.eclipse.osee.-" ` +
        `-nested:false compiled-classes/`,
      { stdio: 'inherit' }
    );
  } catch (e) {
    console.warn(`SpotBugs (xml) exited with code ${e.status}`);
  }
  try {
    execSync(
      `${spotbugsBin} -textui -low -effort:max ` +
        `-html:fancy-hist.xsl -output spotbugs-report-full.html ` +
        `-auxclasspathFromInput -onlyAnalyze "org.eclipse.osee.-" ` +
        `-nested:false compiled-classes/`,
      { stdio: 'inherit' }
    );
  } catch (e) {
    console.warn(`SpotBugs (html) exited with code ${e.status}`);
  }

  const fullCount = countBugs('spotbugs-report-full.xml');
  const allBugs = parseBugs('spotbugs-report-full.xml');
  console.log(`Full analysis: ${fullCount} total bug(s)`);

  // --- Get changed files and build source path mapping ---
  const changedFiles = getChangedFiles();
  const srcToRepo = {};
  for (const f of changedFiles) {
    if (!f.filename.endsWith('.java')) continue;
    for (const marker of ['/src/', '/src-gen/']) {
      const idx = f.filename.indexOf(marker);
      if (idx !== -1) {
        const rel = f.filename.substring(idx + marker.length);
        srcToRepo[rel] = f.filename;
        break;
      }
    }
  }

  // --- Filter bugs to only those in files changed by the PR ---
  const changedPaths = new Set(changedFiles.map((f) => f.filename));
  const filteredBugs = allBugs.filter((bug) => {
    const repoPath = srcToRepo[bug.sourcepath];
    return repoPath && changedPaths.has(repoPath);
  });
  console.log(`Filtered bugs: ${filteredBugs.length} of ${allBugs.length} are in changed files`);

  // --- Generate HTML report for changed files (no extra SpotBugs run) ---
  const changedHtml = generateChangedHtml(filteredBugs, srcToRepo);
  fs.writeFileSync('spotbugs-report-changed.html', changedHtml);
  console.log(`Generated spotbugs-report-changed.html with ${filteredBugs.length} issue(s)`);

  // --- Artifact download URL (uses nightly.link for direct download without auth) ---
  const artifactUrl = `https://nightly.link/${owner}/${repo}/actions/runs/${runId}/spotbugs-report.zip`;

  // =============================================
  // 1. PR COMMENT (in the conversation thread)
  // =============================================
  const marker = '<!-- spotbugs-analysis-comment -->';
  const lines = [marker, `## :beetle: SpotBugs Analysis`, ``];

  if (filteredBugs.length > 0) {
    lines.push(
      `### :pushpin: Issues in Changed Files — ${filteredBugs.length} issue(s)`
    );
    lines.push(``);
    lines.push(`| File | Method | Priority | Message |`);
    lines.push(`|------|--------|----------|---------|`);
    for (const bug of filteredBugs) {
      const file = bug.sourcepath
        ? `\`${bug.sourcepath.split('/').pop()}\``
        : '';
      const method = bug.methodName ? `\`${bug.methodName}()\`` : '';
      const msg = bug.message.replace(/\|/g, '\\|');
      lines.push(
        `| ${file} | ${method} | ${PRIORITY_LABELS[bug.priority] || bug.priority} | ${msg} |`
      );
    }
    lines.push(``);
  } else {
    lines.push(`### :pushpin: Changed Files`);
    lines.push(
      `> :white_check_mark: No issues in files changed by this PR.`
    );
    lines.push(``);
  }

  if (fullCount > 0) {
    lines.push(`### :file_folder: Full Analysis`);
    lines.push(
      `> :warning: **${fullCount}** total issue(s) across all \`org.eclipse.osee\` packages.`
    );
  } else if (fullCount === 0) {
    lines.push(`### :file_folder: Full Analysis`);
    lines.push(`> :white_check_mark: No issues found.`);
  } else {
    lines.push(`> :x: SpotBugs report was not generated.`);
  }

  lines.push(``);
  lines.push(
    `[:arrow_down: Download the SpotBugs HTML reports](${artifactUrl})`
  );

  const prBody = lines.join('\n');

  // Upsert the PR comment
  const commentsData = ghGet(
    `https://api.github.com/repos/${owner}/${repo}/issues/${pullNumber}/comments`
  );
  const existing = commentsData.find((c) => c.body.includes(marker));
  if (existing) {
    ghPatch(
      `https://api.github.com/repos/${owner}/${repo}/issues/comments/${existing.id}`,
      { body: prBody }
    );
  } else {
    ghPost(
      `https://api.github.com/repos/${owner}/${repo}/issues/${pullNumber}/comments`,
      { body: prBody }
    );
  }

  // =============================================
  // 2. FILE-LEVEL REVIEW COMMENTS (on each file)
  // =============================================
  if (filteredBugs.length === 0) return;

  const bugsByFile = {};
  for (const bug of filteredBugs) {
    const repoPath = srcToRepo[bug.sourcepath];
    if (!repoPath) continue;
    if (!bugsByFile[repoPath]) bugsByFile[repoPath] = [];
    bugsByFile[repoPath].push(bug);
  }

  const reviewComments = [];
  for (const [filePath, bugs] of Object.entries(bugsByFile)) {
    const fileName = filePath.split('/').pop();
    const body = [];
    body.push(
      `## :beetle: SpotBugs — ${bugs.length} issue${bugs.length !== 1 ? 's' : ''} in \`${fileName}\``
    );
    body.push(``);

    for (let i = 0; i < bugs.length; i++) {
      const bug = bugs[i];
      const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
      const method = bug.methodName
        ? `\`${bug.methodName}()\``
        : '_unknown_';
      const desc =
        bug.longMsg && bug.longMsg !== bug.message
          ? bug.longMsg
          : bug.message;

      body.push(`**${desc || bug.type}**`);
      body.push(``);
      body.push(
        `${PRIORITY_LABELS[bug.priority] || bug.priority} &nbsp;&nbsp; **Method:** ${method} &nbsp;&nbsp; **Type:** [\`${bug.type}\`](${docsUrl})`
      );
      body.push(``);
      // Add a separator between bugs, but not after the last one
      if (i < bugs.length - 1) {
        body.push(`---`);
        body.push(``);
      }
    }

    reviewComments.push({
      path: filePath,
      body: body.join('\n'),
      subject_type: 'file',
    });
  }

  if (reviewComments.length === 0) return;
  console.log(
    `Posting file-level comments on ${reviewComments.length} file(s)`
  );

  // Fetch the current PR head SHA to avoid 422 if the PR was updated after
  // the workflow was triggered (stale event payload).
  let commitId = headSha;
  try {
    const prData = ghGet(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}`
    );
    commitId = prData.head.sha;
  } catch (e) {
    console.warn(`Could not fetch current PR head SHA, using event payload: ${e.message}`);
  }

  // Delete previous SpotBugs review comments to avoid duplicates on re-runs.
  const SPOTBUGS_MARKER = ':beetle: SpotBugs';
  try {
    const existingComments = ghGet(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments?per_page=100`
    );
    for (const c of existingComments) {
      if (c.body && c.body.includes(SPOTBUGS_MARKER)) {
        try {
          execSync(
            `curl -fsSL -X DELETE ` +
              `-H "Authorization: token ${token}" ` +
              `-H "Accept: application/vnd.github+json" ` +
              `-H "X-GitHub-Api-Version: 2022-11-28" ` +
              `"https://api.github.com/repos/${owner}/${repo}/pulls/comments/${c.id}"`,
            { encoding: 'utf8' }
          );
        } catch (err) {
          console.warn(`Could not delete old comment ${c.id}: ${err.message}`);
        }
      }
    }
  } catch (e) {
    console.warn(`Could not fetch existing review comments: ${e.message}`);
  }

  try {
    ghPost(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews`,
      {
        commit_id: commitId,
        event: 'COMMENT',
        body: ':beetle: **SpotBugs** found issues in changed files.',
        comments: reviewComments,
      }
    );
  } catch (e) {
    console.warn(`Batch review failed: ${e.message}`);
    for (const c of reviewComments) {
      try {
        ghPost(
          `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments`,
          {
            commit_id: commitId,
            path: c.path,
            body: c.body,
            subject_type: 'file',
          }
        );
      } catch (err) {
        console.warn(`Could not comment on ${c.path}: ${err.message}`);
      }
    }
  }
}

main();