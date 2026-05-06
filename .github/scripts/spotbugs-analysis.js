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
    // Find the best SourceLine with a sourcepath attribute.
    // BugInstance XML contains multiple SourceLine elements (on Class, Method, etc.)
    // We want one with sourcepath defined — prefer the last one (most specific).
    let sourcepath = '';
    const slMatches = b.matchAll(/<SourceLine[^>]*>/g);
    for (const slMatch of slMatches) {
      const sp = slMatch[0].match(/sourcepath="([^"]*)"/);
      if (sp) sourcepath = sp[1];
    }
    bugs.push({
      type: opening.match(/type="([^"]*)"/)?.[1] || '',
      priority: opening.match(/priority="([^"]*)"/)?.[1] || '',
      category: opening.match(/category="([^"]*)"/)?.[1] || '',
      sourcepath,
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
    `curl -sS -X POST -w "\\n%{http_code}" ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `-H "Content-Type: application/json" ` +
      `-d @${tmp} ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  const lines = out.trim().split('\n');
  const httpCode = lines.pop();
  const responseBody = lines.join('\n');
  if (parseInt(httpCode, 10) >= 400) {
    const err = new Error(`HTTP ${httpCode}: ${url}`);
    err.responseBody = responseBody;
    console.warn(`GitHub API error (${httpCode}): ${responseBody.substring(0, 500)}`);
    throw err;
  }
  return JSON.parse(responseBody);
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
// Shared HTML styles
// ---------------------------------------------------------------------------

const HTML_STYLES = `
  * { box-sizing: border-box; }
  body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; margin: 0; padding: 2rem; background: #fafbfc; color: #24292f; }
  h1 { color: #1a1a1a; margin-bottom: 0.25rem; }
  h2 { color: #333; margin-top: 2rem; border-bottom: 1px solid #e1e4e8; padding-bottom: 0.4rem; }
  .version { color: #666; font-size: 0.85rem; margin-bottom: 1.5rem; }
  .summary-cards { display: flex; gap: 1rem; flex-wrap: wrap; margin: 1rem 0; }
  .card { background: #fff; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem 1.5rem; min-width: 140px; }
  .card .number { font-size: 2rem; font-weight: 700; }
  .card .label { color: #666; font-size: 0.85rem; margin-top: 0.25rem; }
  .card.high .number { color: #d32f2f; }
  .card.medium .number { color: #f57c00; }
  .card.low .number { color: #fbc02d; }
  .card.total .number { color: #1976d2; }
  table { border-collapse: collapse; width: 100%; margin-top: 1rem; background: #fff; border: 1px solid #e1e4e8; border-radius: 6px; overflow: hidden; }
  th, td { border-bottom: 1px solid #eee; padding: 10px 14px; text-align: left; font-size: 0.9rem; }
  th { background: #f6f8fa; font-weight: 600; color: #444; }
  tr:last-child td { border-bottom: none; }
  tr:hover td { background: #f9f9f9; }
  code { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; font-size: 0.85em; }
  a { color: #1976d2; text-decoration: none; }
  a:hover { text-decoration: underline; }
  .priority-high { color: #d32f2f; font-weight: 600; }
  .priority-medium { color: #f57c00; font-weight: 600; }
  .priority-low { color: #fbc02d; font-weight: 600; }
  .section-nav { background: #fff; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem 1.5rem; margin: 1.5rem 0; }
  .section-nav a { margin-right: 1.5rem; font-weight: 500; }
  .category-group, .package-group { margin-top: 1rem; }
  .category-group h3, .package-group h3 { color: #555; font-size: 1rem; margin-bottom: 0.5rem; }
`;

// ---------------------------------------------------------------------------
// HTML report generator for changed-file bugs
// ---------------------------------------------------------------------------

function generateChangedHtml(bugs, srcToRepo, version) {
  const priorityText = { '1': 'High', '2': 'Medium', '3': 'Low' };
  const priorityClass = { '1': 'priority-high', '2': 'priority-medium', '3': 'priority-low' };

  const rows = bugs
    .map((bug) => {
      const file = srcToRepo[bug.sourcepath] || bug.sourcepath || '';
      const pClass = priorityClass[bug.priority] || '';
      const pLabel = priorityText[bug.priority] || bug.priority;
      const desc = bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message;
      const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
      return `<tr>
        <td class="${pClass}">${pLabel}</td>
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
<style>${HTML_STYLES}</style>
</head>
<body>
<h1>&#x1f41e; SpotBugs — Changed Files Report</h1>
<p class="version">SpotBugs ${escapeHtml(version)}</p>
<p>${bugs.length} issue${bugs.length !== 1 ? 's' : ''} found in files changed by this PR.</p>
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

// ---------------------------------------------------------------------------
// HTML report generator for full analysis
// ---------------------------------------------------------------------------

function generateFullHtml(bugs, version) {
  const priorityText = { '1': 'High', '2': 'Medium', '3': 'Low' };
  const priorityClass = { '1': 'priority-high', '2': 'priority-medium', '3': 'priority-low' };

  // Summary counts
  const highCount = bugs.filter((b) => b.priority === '1').length;
  const medCount = bugs.filter((b) => b.priority === '2').length;
  const lowCount = bugs.filter((b) => b.priority === '3').length;

  // Group by category
  const byCategory = {};
  for (const bug of bugs) {
    const cat = bug.category || 'UNKNOWN';
    if (!byCategory[cat]) byCategory[cat] = [];
    byCategory[cat].push(bug);
  }

  // Group by package
  const byPackage = {};
  for (const bug of bugs) {
    const sp = bug.sourcepath || '';
    const pkg = sp.includes('/') ? sp.substring(0, sp.lastIndexOf('/')).replace(/\//g, '.') : '(default)';
    if (!byPackage[pkg]) byPackage[pkg] = [];
    byPackage[pkg].push(bug);
  }

  // Build category sections
  const categorySections = Object.keys(byCategory)
    .sort()
    .map((cat) => {
      const catBugs = byCategory[cat];
      const catRows = catBugs
        .map((bug) => {
          const pClass = priorityClass[bug.priority] || '';
          const pLabel = priorityText[bug.priority] || bug.priority;
          const desc = bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message;
          const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
          const file = bug.sourcepath ? bug.sourcepath.split('/').pop() : '';
          return `<tr>
            <td class="${pClass}">${pLabel}</td>
            <td><code>${escapeHtml(file)}</code></td>
            <td><code>${escapeHtml(bug.methodName || '')}()</code></td>
            <td><a href="${docsUrl}">${escapeHtml(bug.type)}</a></td>
            <td>${escapeHtml(desc || bug.type)}</td>
          </tr>`;
        })
        .join('\n');
      return `<div class="category-group">
<h3 id="cat-${escapeHtml(cat)}">${escapeHtml(cat)} (${catBugs.length})</h3>
<table>
<thead><tr><th>Priority</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead>
<tbody>${catRows}</tbody>
</table>
</div>`;
    })
    .join('\n');

  // Build package sections
  const packageSections = Object.keys(byPackage)
    .sort()
    .map((pkg) => {
      const pkgBugs = byPackage[pkg];
      const pkgRows = pkgBugs
        .map((bug) => {
          const pClass = priorityClass[bug.priority] || '';
          const pLabel = priorityText[bug.priority] || bug.priority;
          const desc = bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message;
          const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
          const file = bug.sourcepath ? bug.sourcepath.split('/').pop() : '';
          return `<tr>
            <td class="${pClass}">${pLabel}</td>
            <td><code>${escapeHtml(file)}</code></td>
            <td><code>${escapeHtml(bug.methodName || '')}()</code></td>
            <td><a href="${docsUrl}">${escapeHtml(bug.type)}</a></td>
            <td>${escapeHtml(desc || bug.type)}</td>
          </tr>`;
        })
        .join('\n');
      return `<div class="package-group">
<h3 id="pkg-${escapeHtml(pkg)}">${escapeHtml(pkg)} (${pkgBugs.length})</h3>
<table>
<thead><tr><th>Priority</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead>
<tbody>${pkgRows}</tbody>
</table>
</div>`;
    })
    .join('\n');

  // Category nav links
  const categoryNav = Object.keys(byCategory)
    .sort()
    .map((cat) => `<a href="#cat-${escapeHtml(cat)}">${escapeHtml(cat)} (${byCategory[cat].length})</a>`)
    .join(' ');

  // Package nav links
  const packageNav = Object.keys(byPackage)
    .sort()
    .map((pkg) => `<a href="#pkg-${escapeHtml(pkg)}">${escapeHtml(pkg)} (${byPackage[pkg].length})</a>`)
    .join(' ');

  return `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>SpotBugs — Full Analysis Report</title>
<style>${HTML_STYLES}</style>
</head>
<body>
<h1>&#x1f41e; SpotBugs — Full Analysis Report</h1>
<p class="version">SpotBugs ${escapeHtml(version)}</p>

<div class="section-nav">
  <a href="#summary">Summary</a>
  <a href="#by-category">Browse by Category</a>
  <a href="#by-package">Browse by Package</a>
  <a href="#all-bugs">All Issues</a>
  <a href="#info">Info</a>
</div>

<h2 id="summary">Summary</h2>
<div class="summary-cards">
  <div class="card total"><div class="number">${bugs.length}</div><div class="label">Total Issues</div></div>
  <div class="card high"><div class="number">${highCount}</div><div class="label">High Priority</div></div>
  <div class="card medium"><div class="number">${medCount}</div><div class="label">Medium Priority</div></div>
  <div class="card low"><div class="number">${lowCount}</div><div class="label">Low Priority</div></div>
  <div class="card"><div class="number">${Object.keys(byCategory).length}</div><div class="label">Categories</div></div>
  <div class="card"><div class="number">${Object.keys(byPackage).length}</div><div class="label">Packages</div></div>
</div>

<h2 id="by-category">Browse by Category</h2>
<div class="section-nav">${categoryNav}</div>
${categorySections}

<h2 id="by-package">Browse by Package</h2>
<div class="section-nav">${packageNav}</div>
${packageSections}

<h2 id="all-bugs">All Issues (${bugs.length})</h2>
<table>
<thead><tr><th>Priority</th><th>Category</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead>
<tbody>
${bugs
  .map((bug) => {
    const pClass = priorityClass[bug.priority] || '';
    const pLabel = priorityText[bug.priority] || bug.priority;
    const desc = bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message;
    const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
    const file = bug.sourcepath ? bug.sourcepath.split('/').pop() : '';
    return `<tr>
      <td class="${pClass}">${pLabel}</td>
      <td>${escapeHtml(bug.category)}</td>
      <td><code>${escapeHtml(file)}</code></td>
      <td><code>${escapeHtml(bug.methodName || '')}()</code></td>
      <td><a href="${docsUrl}">${escapeHtml(bug.type)}</a></td>
      <td>${escapeHtml(desc || bug.type)}</td>
    </tr>`;
  })
  .join('\n')}
</tbody>
</table>

<h2 id="info">Info</h2>
<table>
<tbody>
  <tr><td><strong>SpotBugs Version</strong></td><td>${escapeHtml(version)}</td></tr>
  <tr><td><strong>Analysis Scope</strong></td><td><code>org.eclipse.osee.-</code></td></tr>
  <tr><td><strong>Effort</strong></td><td>Max</td></tr>
  <tr><td><strong>Threshold</strong></td><td>Low (all priorities reported)</td></tr>
  <tr><td><strong>Total Issues</strong></td><td>${bugs.length}</td></tr>
</tbody>
</table>

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
  // --- Get SpotBugs version ---
  let spotbugsVersion = 'unknown';
  try {
    const vOut = execSync(`${spotbugsBin} -version`, { encoding: 'utf8' }).trim();
    spotbugsVersion = vOut.split('\n')[0];
  } catch (e) {
    console.warn(`Could not determine SpotBugs version: ${e.message}`);
  }
  console.log(`SpotBugs version: ${spotbugsVersion}`);

  // --- Run SpotBugs analysis (single run, XML only) ---
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

  const fullCount = countBugs('spotbugs-report-full.xml');
  const allBugs = parseBugs('spotbugs-report-full.xml');
  console.log(`Full analysis: ${fullCount} total bug(s)`);

  // --- Generate full HTML report ---
  const fullHtml = generateFullHtml(allBugs, spotbugsVersion);
  fs.writeFileSync('spotbugs-report-full.html', fullHtml);
  console.log(`Generated spotbugs-report-full.html with ${allBugs.length} issue(s)`);

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
  const changedHtml = generateChangedHtml(filteredBugs, srcToRepo, spotbugsVersion);
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
  for (const c of reviewComments) {
    console.log(`  Review comment for: ${c.path}`);
  }

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
  console.log(`Using commit_id: ${commitId}`);

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
    const reviewPayload = {
      event: 'COMMENT',
      body: ':beetle: **SpotBugs** found issues in changed files.',
      comments: reviewComments,
    };
    // Only include commit_id if we have a fresh one from the API.
    // Omitting it lets GitHub default to the PR's current head.
    if (commitId) {
      reviewPayload.commit_id = commitId;
    }
    console.log(`Review payload: ${reviewComments.length} comment(s), commit_id=${commitId || '(omitted)'}`);
    ghPost(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews`,
      reviewPayload
    );
    console.log('Batch review posted successfully');
  } catch (e) {
    console.warn(`Batch review failed: ${e.message}`);
    // Retry without commit_id in case it was the problem
    console.log('Retrying batch review without commit_id...');
    try {
      ghPost(
        `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews`,
        {
          event: 'COMMENT',
          body: ':beetle: **SpotBugs** found issues in changed files.',
          comments: reviewComments,
        }
      );
      console.log('Batch review posted successfully (without commit_id)');
    } catch (e2) {
      console.warn(`Batch review without commit_id also failed: ${e2.message}`);
      // Final fallback: post each comment individually as line comments
      console.log('Falling back to individual line comments...');
      for (const c of reviewComments) {
        try {
          ghPost(
            `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments`,
            {
              commit_id: commitId,
              path: c.path,
              body: c.body,
              line: 1,
              side: 'RIGHT',
            }
          );
          console.log(`  Posted comment on ${c.path}`);
        } catch (err) {
          console.warn(`  Could not comment on ${c.path}: ${err.message}`);
        }
      }
    }
  }
}

main();