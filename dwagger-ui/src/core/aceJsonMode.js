import ace from "ace-builds";
import "ace-builds/src-noconflict/mode-json.js";

// 基于内置 JSON 模式扩展：空字符串单独成词，样式上才能给它留出视觉缝隙
ace.define("ace/mode/dwagger_json", [
  "require",
  "exports",
  "module",
  "ace/lib/oop",
  "ace/mode/json",
  "ace/mode/json_highlight_rules",
  "ace/tokenizer"
], function (require, exports) {
  var oop = require("ace/lib/oop");
  var JsonMode = require("ace/mode/json").Mode;
  var JsonHighlightRules = require("ace/mode/json_highlight_rules").JsonHighlightRules;
  var Tokenizer = require("ace/tokenizer").Tokenizer;

  var DwaggerJsonHighlightRules = function () {
    JsonHighlightRules.call(this);
    // 只匹配作为值的空字符串，避免影响空字符串作为 key 的场景
    this.$rules.start.unshift({ token: "string.empty", regex: '""(?!\\s*:)' });
  };
  oop.inherits(DwaggerJsonHighlightRules, JsonHighlightRules);

  var DwaggerJsonMode = function () {
    JsonMode.call(this);
    this.$highlightRules = new DwaggerJsonHighlightRules();
    this.$tokenizer = new Tokenizer(this.$highlightRules.getRules());
  };
  oop.inherits(DwaggerJsonMode, JsonMode);

  exports.Mode = DwaggerJsonMode;
});

export const ACE_JSON_MODE = "dwagger_json";

// 其余语言（xml/text/javascript）仍用 Ace 自带模式
export function toAceLang(lang) {
  return lang === "json" ? ACE_JSON_MODE : lang;
}
