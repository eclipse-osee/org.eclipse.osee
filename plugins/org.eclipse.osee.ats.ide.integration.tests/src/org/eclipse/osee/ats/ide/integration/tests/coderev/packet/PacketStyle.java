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
package org.eclipse.osee.ats.ide.integration.tests.coderev.packet;

/**
 * THROWAWAY shared look-and-feel for the XWidget review packet pages (Overview, Consolidation, Diff, Testing, and the
 * BLAM / Work Definition siblings). Centralizes the colorized "band" theme (framework = blue, ats = purple, agile =
 * green, deleted = red), the note/callout boxes, tables, and the self-contained Java syntax highlighter so every page
 * in {@code ~/CodeXWidgetReview/} looks the same. All CSS/JS is inlined - the packet stays offline and zippable.
 * Delete with the rest of the packet tooling after the migration is reviewed and merged.
 *
 * @author Donald G. Dunne
 */
public final class PacketStyle {

   private PacketStyle() {
      // utility
   }

   /**
    * @return the shared {@code <style>} block for a packet page: page shell, headings, band theming, sub-category
    * chips, count-summary cards, note boxes, and tables. Pair with {@link #highlighterCss()} /
    * {@link #highlighterScript()} on pages that show source code.
    */
   public static String pageCss() {
      return "<style>" //
         + ":root{--fw:#1f6feb;--ats:#8250df;--agile:#1a7f37;--del:#cf222e;--ink:#1f2328;--muted:#656d76;"
         + "--line:#d0d7de;--bg:#ffffff;--soft:#f6f8fa;}"
         + "*{box-sizing:border-box;}"
         + "body{font-family:-apple-system,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:var(--ink);"
         + "background:var(--bg);margin:0;line-height:1.5;}"
         + ".wrap{max-width:1000px;margin:0 auto;padding:28px 24px 64px;}"
         + "h1{font-size:24px;margin:0 0 4px;color:var(--ink);}"
         + "h2{font-size:18px;margin:28px 0 2px;color:var(--ink);}"
         + ".sub{color:var(--muted);margin:0 0 24px;font-size:14px;}"
         + "code{font-family:'SFMono-Regular',Consolas,'Liberation Mono',Menlo,monospace;font-size:12.5px;"
         + "background:var(--soft);padding:1px 5px;border-radius:4px;}"
         + "a{color:var(--fw);}"
         // count-summary cards
         + ".summary{display:flex;flex-wrap:wrap;gap:14px;margin:0 0 28px;}"
         + ".stat{flex:1 1 150px;border:1px solid var(--line);border-radius:8px;padding:14px 16px;"
         + "background:var(--soft);}"
         + ".stat .num{font-size:30px;font-weight:700;line-height:1;}"
         + ".stat .lbl{color:var(--muted);font-size:13px;margin-top:6px;}"
         + ".stat.arrow .num{color:var(--fw);}.stat.new .num{color:var(--agile);}.stat.del .num{color:var(--del);}"
         // note / callout box (indented so it reads as part of its section)
         + ".note{margin:12px 24px;padding:10px 14px;border-left:3px solid var(--fw);background:var(--soft);"
         + "border-radius:4px;font-size:13px;color:var(--muted);}"
         // bands
         + ".band{margin:0 0 28px;}"
         + ".band>h2{padding:6px 0 6px 12px;border-left:5px solid var(--line);}"
         + ".band.framework>h2{border-left-color:var(--fw);}"
         + ".band.ats>h2{border-left-color:var(--ats);}"
         + ".band.agile>h2{border-left-color:var(--agile);}"
         + ".band.deleted>h2{border-left-color:var(--del);}"
         + ".band .count{color:var(--muted);font-weight:400;font-size:14px;}"
         // sub-category chip, tinted to its band
         + ".cat{margin:18px 0 0 12px;}"
         + ".cat h3{font-size:13px;margin:0 0 8px;text-transform:uppercase;letter-spacing:.05em;font-weight:700;"
         + "display:inline-block;padding:3px 10px;border-radius:4px;}"
         + ".band.framework .cat h3{color:var(--fw);background:#ddf0ff;}"
         + ".band.ats .cat h3{color:var(--ats);background:#efe4ff;}"
         + ".band.agile .cat h3{color:var(--agile);background:#dcf5e3;}"
         + ".cat h3 .plain{text-transform:none;font-weight:400;opacity:.75;}"
         // mapping table (new name + was)
         + "table{border-collapse:collapse;width:100%;}"
         + "td{padding:5px 10px;vertical-align:top;border-bottom:1px solid var(--soft);}"
         + "td.newname{white-space:nowrap;width:1%;font-weight:600;}"
         + "td.newname code{color:var(--ink);font-size:14.5px;}"
         + "td.was{color:var(--muted);font-size:13px;}"
         + "td.was code{background:transparent;padding:0;}"
         + "td.was .brandnew{font-style:italic;}"
         + ".deleted-list code{color:var(--del);}"
         + "</style>";
   }

   /** @return the token-color CSS used by {@link #highlighterScript()} (shared with the diff / source-copy pages). */
   public static String highlighterCss() {
      return "<style>.tok-kw{color:#0000ff;}.tok-str{color:#a31515;}.tok-com{color:#008000;}"
         + ".tok-ann{color:#808000;}.tok-num{color:#098658;}</style>";
   }

   /** @return a self-contained client-side Java syntax highlighter (no external libraries, no network). */
   public static String highlighterScript() {
      StringBuilder s = new StringBuilder();
      s.append("<script>document.addEventListener('DOMContentLoaded', function() {");
      s.append(
         "  var KW = new RegExp('\\\\b(?:abstract|assert|boolean|break|byte|case|catch|char|class|const|"
            + "continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|"
            + "instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|"
            + "strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while|true|false|"
            + "null|var|record|yield)\\\\b','g');");
      s.append("  function esc(s){return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');}");
      s.append("  function hl(code){");
      s.append(
         "    var re=/(\\/\\*[\\s\\S]*?\\*\\/)|(\\/\\/[^\\n]*)|(\"(?:\\\\.|[^\"\\\\])*\")|"
            + "('(?:\\\\.|[^'\\\\])*')|(@\\w+)|(\\b\\d[\\w.]*\\b)/g;");
      s.append("    var out='',last=0,m;");
      s.append("    while((m=re.exec(code))!==null){");
      s.append("      out+=esc(code.slice(last,m.index)).replace(KW,'<span class=\\'tok-kw\\'>$&</span>');");
      s.append("      var cls=m[1]?'tok-com':m[2]?'tok-com':m[3]?'tok-str':m[4]?'tok-str':m[5]?'tok-ann':'tok-num';");
      s.append("      out+='<span class=\\''+cls+'\\'>'+esc(m[0])+'</span>';");
      s.append("      last=re.lastIndex;");
      s.append("    }");
      s.append("    out+=esc(code.slice(last)).replace(KW,'<span class=\\'tok-kw\\'>$&</span>');");
      s.append("    return out;");
      s.append("  }");
      s.append(
         "  document.querySelectorAll('code.language-java').forEach(function(c){ c.innerHTML=hl(c.textContent); });");
      s.append("});</script>");
      return s.toString();
   }

   /** @return HTML-escaped text safe for element content. */
   public static String escapeHtml(String s) {
      if (s == null) {
         return "";
      }
      return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
   }
}
