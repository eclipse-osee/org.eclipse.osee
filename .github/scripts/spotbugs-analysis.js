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

// ---------------------------------------------------------------------------
// HTML report generator for changed-file bugs
// ---------------------------------------------------------------------------

function generateChangedHtml(bugs, srcToRepo, version) {
  const priorityText = { '1': 'High', '2': 'Medium', '3': 'Low' };
  const priorityClass = { '1': 'priority-high', '2': 'priority-medium', '3': 'priority-low' };

  // Summary counts
  const highCount = bugs.filter((b) => b.priority === '1').length;
  const medCount = bugs.filter((b) => b.priority === '2').length;
  const lowCount = bugs.filter((b) => b.priority === '3').length;

  // Group by file
  const byFile = {};
  for (const bug of bugs) {
    const file = srcToRepo[bug.sourcepath] || bug.sourcepath || '(unknown)';
    if (!byFile[file]) byFile[file] = [];
    byFile[file].push(bug);
  }

  // Helper to build a bug table
  const bugTable = (bugList) => {
    const rows = bugList
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
    return `<table>
<thead><tr><th>Priority</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead>
<tbody>${rows}</tbody>
</table>`;
  };

  // Build file sections
  const fileSections = Object.keys(byFile)
    .sort()
    .map((file) => {
      const fileBugs = byFile[file];
      const fileName = file.split('/').pop();
      return `<div class="file-group">
<h3>${escapeHtml(fileName)} (${fileBugs.length})</h3>
${bugTable(fileBugs)}
</div>`;
    })
    .join('\n');

  // File nav links
  const fileNav = Object.keys(byFile)
    .sort()
    .map((file) => `<span class="nav-item">${escapeHtml(file.split('/').pop())} (${byFile[file].length})</span>`)
    .join('\n');

  return `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>SpotBugs — Changed Files Report</title>
<style>
  * { box-sizing: border-box; }
  body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; margin: 0; padding: 2rem; background: #fafbfc; color: #24292f; }
  h1 { color: #1a1a1a; margin-bottom: 0.25rem; }
  h3 { color: #555; font-size: 1rem; margin: 1.5rem 0 0.5rem; }
  .version { color: #666; font-size: 0.85rem; margin-bottom: 1.5rem; }

  /* Tabs */
  .tabs { display: flex; gap: 0; border-bottom: 2px solid #e1e4e8; margin-bottom: 1.5rem; }
  .tab { padding: 0.75rem 1.5rem; cursor: pointer; font-weight: 500; color: #666; border-bottom: 2px solid transparent; margin-bottom: -2px; user-select: none; }
  .tab:hover { color: #1976d2; }
  .tab.active { color: #1976d2; border-bottom-color: #1976d2; }
  .page { display: none; }
  .page.active { display: block; }

  /* Summary cards */
  .summary-cards { display: flex; gap: 1rem; flex-wrap: wrap; margin: 1rem 0; }
  .card { background: #fff; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem 1.5rem; min-width: 140px; }
  .card .number { font-size: 2rem; font-weight: 700; }
  .card .label { color: #666; font-size: 0.85rem; margin-top: 0.25rem; }
  .card.high .number { color: #d32f2f; }
  .card.medium .number { color: #f57c00; }
  .card.low .number { color: #fbc02d; }
  .card.total .number { color: #1976d2; }

  /* Tables */
  table { border-collapse: collapse; width: 100%; margin-top: 0.5rem; background: #fff; border: 1px solid #e1e4e8; border-radius: 6px; overflow: hidden; }
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

  /* Browse nav */
  .browse-nav { background: #fff; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem 1.5rem; margin: 1rem 0; column-count: 3; column-gap: 2rem; }
  .browse-nav .nav-item { display: block; padding: 3px 0; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-size: 0.9rem; }

  /* Search */
  #bug-search { width: 100%; padding: 0.6rem 1rem; border: 1px solid #e1e4e8; border-radius: 6px; font-size: 0.9rem; margin-bottom: 1rem; outline: none; }
  #bug-search:focus { border-color: #1976d2; box-shadow: 0 0 0 2px rgba(25,118,210,0.15); }
</style>
</head>
<body>
<h1>&#x1f41e; SpotBugs — Changed Files Report</h1>
<p class="version">SpotBugs ${escapeHtml(version)}</p>

${bugs.length > 0 ? `
<div class="tabs">
  <div class="tab active" data-page="summary">Summary</div>
  <div class="tab" data-page="by-file">By File</div>
  <div class="tab" data-page="all-issues">All Issues</div>
</div>

<div class="page active" id="summary">
  <h2>Summary</h2>
  <p>${bugs.length} issue${bugs.length !== 1 ? 's' : ''} found in files changed by this PR.</p>
  <div class="summary-cards">
    <div class="card total"><div class="number">${bugs.length}</div><div class="label">Total Issues</div></div>
    <div class="card high"><div class="number">${highCount}</div><div class="label">High Priority</div></div>
    <div class="card medium"><div class="number">${medCount}</div><div class="label">Medium Priority</div></div>
    <div class="card low"><div class="number">${lowCount}</div><div class="label">Low Priority</div></div>
    <div class="card"><div class="number">${Object.keys(byFile).length}</div><div class="label">Files</div></div>
  </div>
</div>

<div class="page" id="by-file">
  <h2>Browse by File</h2>
  <div class="browse-nav">${fileNav}</div>
  ${fileSections}
</div>

<div class="page" id="all-issues">
  <h2>All Issues (${bugs.length})</h2>
  <input type="text" id="bug-search" placeholder="Search by file, method, type, or description..." />
  <div id="all-bugs-table"></div>
</div>

<script>
// Tab switching
document.querySelectorAll('.tab').forEach(function(tab) {
  tab.addEventListener('click', function() {
    document.querySelectorAll('.tab').forEach(function(t) { t.classList.remove('active'); });
    document.querySelectorAll('.page').forEach(function(p) { p.classList.remove('active'); });
    tab.classList.add('active');
    document.getElementById(tab.dataset.page).classList.add('active');
  });
});

// Searchable "All Issues" table
var ALL_BUGS = ${JSON.stringify(bugs.map((bug) => ({
    priority: bug.priority,
    category: bug.category,
    file: srcToRepo[bug.sourcepath] || bug.sourcepath || '',
    method: bug.methodName || '',
    type: bug.type,
    desc: (bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message) || bug.type,
  })))};

var priorityLabels = { '1': 'High', '2': 'Medium', '3': 'Low' };
var priorityClasses = { '1': 'priority-high', '2': 'priority-medium', '3': 'priority-low' };

function esc(s) {
  var d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}

function renderTable(bugsToRender) {
  if (bugsToRender.length === 0) {
    document.getElementById('all-bugs-table').innerHTML = '<p style="color:#666;margin-top:1rem;">No results match your search.</p>';
    return;
  }
  var rows = '';
  for (var i = 0; i < bugsToRender.length; i++) {
    var b = bugsToRender[i];
    var pClass = priorityClasses[b.priority] || '';
    var pLabel = priorityLabels[b.priority] || b.priority;
    var docsUrl = 'https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#' + b.type.toLowerCase();
    rows += '<tr>'
      + '<td class="' + pClass + '">' + pLabel + '</td>'
      + '<td><code>' + esc(b.file.split('/').pop()) + '</code></td>'
      + '<td><code>' + esc(b.method) + '()</code></td>'
      + '<td><a href="' + docsUrl + '">' + esc(b.type) + '</a></td>'
      + '<td>' + esc(b.desc) + '</td>'
      + '</tr>';
  }
  document.getElementById('all-bugs-table').innerHTML =
    '<table><thead><tr><th>Priority</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead><tbody>' + rows + '</tbody></table>';
}

function applySearch() {
  var query = document.getElementById('bug-search').value.toLowerCase().trim();
  if (!query) {
    renderTable(ALL_BUGS);
  } else {
    var filtered = ALL_BUGS.filter(function(b) {
      return b.file.toLowerCase().indexOf(query) !== -1
        || b.method.toLowerCase().indexOf(query) !== -1
        || b.type.toLowerCase().indexOf(query) !== -1
        || b.desc.toLowerCase().indexOf(query) !== -1;
    });
    renderTable(filtered);
  }
}

var searchTimeout;
document.getElementById('bug-search').addEventListener('input', function() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(applySearch, 200);
});

renderTable(ALL_BUGS);
</script>
` : `<p><strong>&#x2705; No issues found in changed files.</strong></p>`}
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

  // Helper to build a bug table
  const bugTable = (bugList, includeCategory) => {
    const catHeader = includeCategory ? '<th>Category</th>' : '';
    const rows = bugList
      .map((bug) => {
        const pClass = priorityClass[bug.priority] || '';
        const pLabel = priorityText[bug.priority] || bug.priority;
        const desc = bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message;
        const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
        const file = bug.sourcepath ? bug.sourcepath.split('/').pop() : '';
        const catCol = includeCategory ? `<td>${escapeHtml(bug.category)}</td>` : '';
        return `<tr>
          <td class="${pClass}">${pLabel}</td>
          ${catCol}
          <td><code>${escapeHtml(file)}</code></td>
          <td><code>${escapeHtml(bug.methodName || '')}()</code></td>
          <td><a href="${docsUrl}">${escapeHtml(bug.type)}</a></td>
          <td>${escapeHtml(desc || bug.type)}</td>
        </tr>`;
      })
      .join('\n');
    return `<table>
<thead><tr><th>Priority</th>${catHeader}<th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead>
<tbody>${rows}</tbody>
</table>`;
  };

  // Build category sections
  const categorySections = Object.keys(byCategory)
    .sort()
    .map((cat) => {
      const catBugs = byCategory[cat];
      return `<div class="category-group">
<h3>${escapeHtml(cat)} (${catBugs.length})</h3>
${bugTable(catBugs, false)}
</div>`;
    })
    .join('\n');

  // Build package sections
  const packageSections = Object.keys(byPackage)
    .sort()
    .map((pkg) => {
      const pkgBugs = byPackage[pkg];
      return `<div class="package-group">
<h3>${escapeHtml(pkg)} (${pkgBugs.length})</h3>
${bugTable(pkgBugs, false)}
</div>`;
    })
    .join('\n');

  // Category nav links
  const categoryNav = Object.keys(byCategory)
    .sort()
    .map((cat) => `<span class="nav-item">${escapeHtml(cat)} (${byCategory[cat].length})</span>`)
    .join('\n');

  // Package nav links
  const packageNav = Object.keys(byPackage)
    .sort()
    .map((pkg) => `<span class="nav-item">${escapeHtml(pkg)} (${byPackage[pkg].length})</span>`)
    .join('\n');

  return `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>SpotBugs — Full Analysis Report</title>
<style>
  * { box-sizing: border-box; }
  body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; margin: 0; padding: 2rem; background: #fafbfc; color: #24292f; }
  h1 { color: #1a1a1a; margin-bottom: 0.25rem; }
  h3 { color: #555; font-size: 1rem; margin: 1.5rem 0 0.5rem; }
  .version { color: #666; font-size: 0.85rem; margin-bottom: 1.5rem; }

  /* Tabs */
  .tabs { display: flex; gap: 0; border-bottom: 2px solid #e1e4e8; margin-bottom: 1.5rem; }
  .tab { padding: 0.75rem 1.5rem; cursor: pointer; font-weight: 500; color: #666; border-bottom: 2px solid transparent; margin-bottom: -2px; user-select: none; }
  .tab:hover { color: #1976d2; }
  .tab.active { color: #1976d2; border-bottom-color: #1976d2; }
  .page { display: none; }
  .page.active { display: block; }

  /* Summary cards */
  .summary-cards { display: flex; gap: 1rem; flex-wrap: wrap; margin: 1rem 0; }
  .card { background: #fff; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem 1.5rem; min-width: 140px; }
  .card .number { font-size: 2rem; font-weight: 700; }
  .card .label { color: #666; font-size: 0.85rem; margin-top: 0.25rem; }
  .card.high .number { color: #d32f2f; }
  .card.medium .number { color: #f57c00; }
  .card.low .number { color: #fbc02d; }
  .card.total .number { color: #1976d2; }

  /* Tables */
  table { border-collapse: collapse; width: 100%; margin-top: 0.5rem; background: #fff; border: 1px solid #e1e4e8; border-radius: 6px; overflow: hidden; }
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

  /* Browse nav */
  .browse-nav { background: #fff; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem 1.5rem; margin: 1rem 0; column-count: 3; column-gap: 2rem; }
  .browse-nav .nav-item { display: block; padding: 3px 0; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-size: 0.9rem; }

  /* All Issues pagination */
  .page-controls { display: flex; align-items: center; gap: 1rem; margin: 1rem 0; }
  .page-controls button { padding: 0.4rem 1rem; border: 1px solid #e1e4e8; border-radius: 4px; background: #fff; cursor: pointer; font-size: 0.85rem; }
  .page-controls button:hover { background: #f6f8fa; }
  .page-controls button:disabled { opacity: 0.4; cursor: default; }
  .page-controls span { font-size: 0.85rem; color: #666; }
  #bug-search { width: 100%; padding: 0.6rem 1rem; border: 1px solid #e1e4e8; border-radius: 6px; font-size: 0.9rem; margin-bottom: 1rem; outline: none; }
  #bug-search:focus { border-color: #1976d2; box-shadow: 0 0 0 2px rgba(25,118,210,0.15); }
</style>
</head>
<body>
<h1>&#x1f41e; SpotBugs — Full Analysis Report</h1>
<p class="version">SpotBugs ${escapeHtml(version)}</p>

<div class="tabs">
  <div class="tab active" data-page="summary">Summary</div>
  <div class="tab" data-page="by-category">By Category</div>
  <div class="tab" data-page="by-package">By Package</div>
  <div class="tab" data-page="all-bugs">All Issues</div>
  <div class="tab" data-page="info">Info</div>
</div>

<div class="page active" id="summary">
  <h2>Summary</h2>
  <div class="summary-cards">
    <div class="card total"><div class="number">${bugs.length}</div><div class="label">Total Issues</div></div>
    <div class="card high"><div class="number">${highCount}</div><div class="label">High Priority</div></div>
    <div class="card medium"><div class="number">${medCount}</div><div class="label">Medium Priority</div></div>
    <div class="card low"><div class="number">${lowCount}</div><div class="label">Low Priority</div></div>
    <div class="card"><div class="number">${Object.keys(byCategory).length}</div><div class="label">Categories</div></div>
    <div class="card"><div class="number">${Object.keys(byPackage).length}</div><div class="label">Packages</div></div>
  </div>
</div>

<div class="page" id="by-category">
  <h2>Browse by Category</h2>
  <div class="browse-nav">${categoryNav}</div>
  ${categorySections}
</div>

<div class="page" id="by-package">
  <h2>Browse by Package</h2>
  <div class="browse-nav">${packageNav}</div>
  ${packageSections}
</div>

<div class="page" id="all-bugs">
  <h2>All Issues (${bugs.length})</h2>
  <input type="text" id="bug-search" placeholder="Search by file, method, type, or description..." />
  <div class="page-controls">
    <button id="prev-btn" disabled>&larr; Previous</button>
    <span id="page-info"></span>
    <button id="next-btn">Next &rarr;</button>
  </div>
  <div id="all-bugs-table"></div>
  <div class="page-controls">
    <button id="prev-btn2" disabled>&larr; Previous</button>
    <span id="page-info2"></span>
    <button id="next-btn2">Next &rarr;</button>
  </div>
</div>

<div class="page" id="info">
  <h2>Info</h2>
  <table>
  <tbody>
    <tr><td><strong>SpotBugs Version</strong></td><td>${escapeHtml(version)}</td></tr>
    <tr><td><strong>Analysis Scope</strong></td><td><code>org.eclipse.osee.-</code></td></tr>
    <tr><td><strong>Effort</strong></td><td>Max</td></tr>
    <tr><td><strong>Threshold</strong></td><td>Low (all priorities reported)</td></tr>
    <tr><td><strong>Total Issues</strong></td><td>${bugs.length}</td></tr>
  </tbody>
  </table>
</div>

<script>
// Tab switching
document.querySelectorAll('.tab').forEach(function(tab) {
  tab.addEventListener('click', function() {
    document.querySelectorAll('.tab').forEach(function(t) { t.classList.remove('active'); });
    document.querySelectorAll('.page').forEach(function(p) { p.classList.remove('active'); });
    tab.classList.add('active');
    document.getElementById(tab.dataset.page).classList.add('active');
  });
});

// Paginated "All Issues" table with search
var ALL_BUGS = ${JSON.stringify(bugs.map((bug) => ({
    priority: bug.priority,
    category: bug.category,
    file: bug.sourcepath ? bug.sourcepath.split('/').pop() : '',
    method: bug.methodName || '',
    type: bug.type,
    desc: (bug.longMsg && bug.longMsg !== bug.message ? bug.longMsg : bug.message) || bug.type,
  })))};

var PAGE_SIZE = 100;
var currentPage = 0;
var filteredBugs = ALL_BUGS;

var priorityLabels = { '1': 'High', '2': 'Medium', '3': 'Low' };
var priorityClasses = { '1': 'priority-high', '2': 'priority-medium', '3': 'priority-low' };

function esc(s) {
  var d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}

function renderPage(page) {
  var totalPages = Math.ceil(filteredBugs.length / PAGE_SIZE) || 1;
  currentPage = Math.max(0, Math.min(page, totalPages - 1));
  var start = currentPage * PAGE_SIZE;
  var end = Math.min(start + PAGE_SIZE, filteredBugs.length);
  var rows = '';
  for (var i = start; i < end; i++) {
    var b = filteredBugs[i];
    var pClass = priorityClasses[b.priority] || '';
    var pLabel = priorityLabels[b.priority] || b.priority;
    var docsUrl = 'https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#' + b.type.toLowerCase();
    rows += '<tr>'
      + '<td class="' + pClass + '">' + pLabel + '</td>'
      + '<td>' + esc(b.category) + '</td>'
      + '<td><code>' + esc(b.file) + '</code></td>'
      + '<td><code>' + esc(b.method) + '()</code></td>'
      + '<td><a href="' + docsUrl + '">' + esc(b.type) + '</a></td>'
      + '<td>' + esc(b.desc) + '</td>'
      + '</tr>';
  }
  var tableHtml = filteredBugs.length > 0
    ? '<table><thead><tr><th>Priority</th><th>Category</th><th>File</th><th>Method</th><th>Type</th><th>Description</th></tr></thead><tbody>' + rows + '</tbody></table>'
    : '<p style="color:#666;margin-top:1rem;">No results match your search.</p>';
  document.getElementById('all-bugs-table').innerHTML = tableHtml;

  var info = filteredBugs.length > 0
    ? 'Showing ' + (start + 1) + '–' + end + ' of ' + filteredBugs.length
    : '0 results';
  document.getElementById('page-info').textContent = info;
  document.getElementById('page-info2').textContent = info;
  document.getElementById('prev-btn').disabled = currentPage === 0;
  document.getElementById('prev-btn2').disabled = currentPage === 0;
  document.getElementById('next-btn').disabled = currentPage >= totalPages - 1;
  document.getElementById('next-btn2').disabled = currentPage >= totalPages - 1;
}

function applySearch() {
  var query = document.getElementById('bug-search').value.toLowerCase().trim();
  if (!query) {
    filteredBugs = ALL_BUGS;
  } else {
    filteredBugs = ALL_BUGS.filter(function(b) {
      return b.file.toLowerCase().indexOf(query) !== -1
        || b.method.toLowerCase().indexOf(query) !== -1
        || b.type.toLowerCase().indexOf(query) !== -1
        || b.desc.toLowerCase().indexOf(query) !== -1
        || b.category.toLowerCase().indexOf(query) !== -1;
    });
  }
  renderPage(0);
}

var searchTimeout;
document.getElementById('bug-search').addEventListener('input', function() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(applySearch, 200);
});

document.getElementById('prev-btn').addEventListener('click', function() { renderPage(currentPage - 1); });
document.getElementById('next-btn').addEventListener('click', function() { renderPage(currentPage + 1); });
document.getElementById('prev-btn2').addEventListener('click', function() { renderPage(currentPage - 1); });
document.getElementById('next-btn2').addEventListener('click', function() { renderPage(currentPage + 1); });

renderPage(0);
</script>
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

  // Always clean up old SpotBugs reviews and review comments first, even if
  // there are no new bugs to report. This prevents stale comments from accumulating.
  const SPOTBUGS_MARKER = '<!-- spotbugs-auto-review -->';
  console.log('Cleaning up old SpotBugs reviews and comments...');

  // Delete old SpotBugs reviews (the top-level "SpotBugs found issues" entries)
  try {
    const reviews = ghGet(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews?per_page=100`
    );
    for (const r of reviews) {
      if (r.body && r.body.includes(SPOTBUGS_MARKER)) {
        try {
          // Dismiss the review first if it's not already dismissed, then delete
          execSync(
            `curl -fsSL -X DELETE ` +
              `-H "Authorization: token ${token}" ` +
              `-H "Accept: application/vnd.github+json" ` +
              `-H "X-GitHub-Api-Version: 2022-11-28" ` +
              `"https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews/${r.id}"`,
            { encoding: 'utf8' }
          );
          console.log(`  Deleted old review ${r.id}`);
        } catch (err) {
          console.warn(`  Could not delete old review ${r.id}: ${err.message}`);
        }
      }
    }
  } catch (e) {
    console.warn(`Could not fetch existing reviews: ${e.message}`);
  }

  // Delete old SpotBugs review comments (file-level comments)
  try {
    let commentPage = 1;
    while (true) {
      const existingComments = ghGet(
        `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments?per_page=100&page=${commentPage}`
      );
      if (existingComments.length === 0) break;
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
            console.log(`  Deleted old comment ${c.id}`);
          } catch (err) {
            console.warn(`  Could not delete old comment ${c.id}: ${err.message}`);
          }
        }
      }
      if (existingComments.length < 100) break;
      commentPage++;
    }
  } catch (e) {
    console.warn(`Could not fetch existing review comments: ${e.message}`);
  }

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
    body.push(`<!-- spotbugs-auto-review -->`);
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
      position: 1,
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

  try {
    ghPost(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews`,
      {
        commit_id: commitId,
        event: 'COMMENT',
        body: '<!-- spotbugs-auto-review -->\n:beetle: **SpotBugs** found issues in changed files.',
        comments: reviewComments,
      }
    );
    console.log('Batch review posted successfully');
  } catch (e) {
    console.warn(`Batch review failed: ${e.message}`);
    // Fallback: post each comment individually
    console.log('Falling back to individual comments...');
    for (const c of reviewComments) {
      try {
        ghPost(
          `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments`,
          {
            commit_id: commitId,
            path: c.path,
            body: c.body,
            position: 1,
          }
        );
        console.log(`  Posted comment on ${c.path}`);
      } catch (err) {
        console.warn(`  Could not comment on ${c.path}: ${err.message}`);
      }
    }
  }
}

main();