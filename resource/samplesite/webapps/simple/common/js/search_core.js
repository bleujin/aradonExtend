
function ICSS(path, session){
	this.path = path ;
	this.session = session ;

	this.getSearcher = function(repoId){
		return new Searcher(this, repoId);
	} ;
	
	this.getRepositorySummary = function(repoId){
		return new RepositorySummary(this, repoId);
	} ;
	
	this.getSearchWordRank = function(rankId){
		return new SearchWordRank(this, rankId) ;
	} ;
	
	this.getSuggestWord = function(suggId) {
		return new SuggestWord(this, suggId) ;
	} ;
	
	this.getAutoComplete = function(autoId) {
		return new AutoComplete(this, autoId) ;
	} ;

	this.toString = function(){
		return this.path ;
	} ;
}


function Session(userId){
	this.userId = userId ;
	
	this.jsonString = function(){
		return "session:{userId:'" + this.userId + "'}";
	} ;
}


function RepositorySummary(icss, repoId){
	this.setICSS(icss) ;
	this.repoId = repoId ;
	this.list = function(){
		this.setDataPath('info/' + this.repoId + '.' + this.format) ;
		return this.execute(this, this.paramString(), 'POST') ;
	} ;
	
	this.paramString = function(ctype){
		return '{' + this.page.jsonString() + ', ' + this.params.jsonString() + '}';
	} ;
}


function SearchWordRank(icss, rankId){
	this.setICSS(icss) ;
	this.rankId = rankId ;
	this.list = function(){
		this.setDataPath('ranking/' + this.rankId + '.' + this.format) ;
		return this.execute(this, this.paramString(), 'POST') ;
	} ;
	
	this.paramString = function(ctype){
		return '{' + this.page.jsonString() + ', ' + this.params.jsonString() + '}';
	} ;
}

function SuggestWord(icss, suggId){
	this.setICSS(icss) ;
	this.suggId = suggId ;
	this.list = function(){
		this.setDataPath('suggest/' + this.suggId + '.' + this.format) ;
		return this.execute(this, this.paramString(), 'POST') ;
	} ;
	
	this.paramString = function(ctype){
		return '{' + this.page.jsonString() + ', ' + this.params.jsonString() + '}';
	} ;
}

function AutoComplete(icss, autoId){
	this.setICSS(icss) ;
	this.autoId = autoId ;
	this.list = function(){
		this.setDataPath('auto/' + this.autoId + '.' + this.format) ;
		return this.execute(this, this.paramString(), 'POST') ;
	} ;
	
	this.paramString = function(ctype){
		return '{' + this.page.jsonString() + ', ' + this.params.jsonString() + '}';
	} ;
}



function Searcher(icss, repoId){

	this.setICSS(icss) ;
	this.repoId = repoId ;
	this.sort = '' ;
	this.query = '' ;
	this.requestFilter = '' ;
	this.searchFilter = new Array() ;

	this.search = function(query){
		this.query = query ;
		this.setDataPath('search/' + this.repoId + '.' + this.format) ;
		return this.execute(this, this.paramString(), 'POST') ;
		// return eval('(' + this.execute(this, 'POST') + ')') ;
	} ; 

	this.paramString = function(ctype){
		var queryJson = 'query:"' + this.query + '", sort:"' + this.sort + '", filter:"' + this.requestFilter + '", ' + this.searchFilterToJsonString() + '' ; 
		return '{' + queryJson + ', ' +  this.page.jsonString() + ', ' + this.params.jsonString() + '}';
	} ;
	
	this.getQuery = function(){
		return this.query ;
	} ;
	
	this.toString = function(){
		return this.getPath() ;
	} ;

	this.setSort = function(sort){
		this.sort = sort ;
	} ;
	
	this.setFilter = function(filter) {
		this.requestFilter = filter ;
	} ;
	
	this.searchFilterToJsonString = function(){
		return 'searchfilter:[' + this.searchFilter.join(',') + ']' ;
	} ;
	
	this.addQueryFilter = function(query) {
		this.addServerFilter('{type:"query", query:"' + query + '"}') ;
	} ;
	
	this.addNumberRangeFilter = function(fieldname, from, to) {
		this.addServerFilter('{type:"nrange", name:"' + fieldname + '", from:' + from + ',to:' + to + '}') ;
	} ;
	
	this.addTermRangeFilter = function(fieldname, from, to) {
		this.addServerFilter('{type:"trange", name:"' + fieldname + '", from:"' + from + '", to:"' + to + '"}') ;
	} ;
	
	this.addTermFilter = function(fieldname, terms) {
		this.addServerFilter('{type:"term", name:"' + fieldname + '", terms:"' + terms + '"}') ;
	} ;
	
	this.addServerFilter = function(jsonfilter) {
		if (this.searchFilter.length > 10) alert('limit length of filter is 10') ;
		this.searchFilter[this.searchFilter.length] = jsonfilter  ;
	}
}

function ICSSLet() {
	this.icss = {};
	this.page = new Page(10, 1, 10) ;
	this.format = 'json' ;
	this.dataPath = '' ;
	this.params = new Params() ;

	this.setICSS = function(icss){
		this.icss = icss ;
	} ;
	
	this.setPage = function(listNum, pageNo, screenCount){
		this.page = new Page(listNum, pageNo, screenCount) ;
	} ;
		
	this.setFormat = function(format){
		this.format = format ;
	} ;
	
	this.getPath = function(){
		return this.getServerPath() + this.getDataPath() ;
	} ;
	
	this.getDataPath = function() {
		return this.dataPath ;
	} ;
	
	this.getServerPath = function(){
		return this.icss.path ;
	} ;
	
	this.setDataPath = function(dataPath) {
		this.dataPath = dataPath ;
	} ;
	
	this.setParam = function(paramName, paramValue){
		this.params.addParam(paramName, paramValue) ;
	} ;


	this.execute = function(queryObj, paramString, ctype){ // private function
		
		// return queryObj.getPath() + queryObj.paramString() ;
		
		var aj = new ajax(queryObj.getPath(), ctype) ;
		aj.addParam('parameter', paramString) ;
		aj.execMethod = function(parentThis, xmlRequest){
			parentThis.result = xmlRequest.responseText ;
			return parentThis ;
		} ;
		
		aj.connect(false) ;
		return aj.result ;
	} ;
}


Searcher.prototype = new ICSSLet() ;
RepositorySummary.prototype = new ICSSLet() ;
SearchWordRank.prototype = new ICSSLet() ;
SuggestWord.prototype = new ICSSLet() ;
AutoComplete.prototype = new ICSSLet() ;


function PageOutPut(linkName, rowCount, page){
	this.linkName = linkName ;
	this.rowCount = rowCount ;
	this.page = page ;
	
    this.toHtml = function(forwardName, strDepth) {
        var goForwardName = (forwardName) ? forwardName : "list";
        if(this.isValidPage()){
            var buffer = "";
            buffer += "<table border='0' cellspacing='0' cellpadding='0' class='pageout'><tr>";
            buffer += this.printPagePrev("<td width='25'>", "<img src='" + strDepth + "common/img/list_prev.gif' border='0'>", "<img src='" + strDepth + "common/img/list_prev.gif' border='0'>", "</td>", forwardName);
            buffer += "<td align=center>";
            buffer += this.printPageNoList(goForwardName);
            buffer += "</td>";
            buffer += this.printPageNext("<td width='25' align='right'>", "<img src='" + strDepth + "common/img/list_next.gif' border='0'>", "<img src='" + strDepth + "common/img/list_next.gif' border='0'>", "</td>", forwardName);
            buffer += "</tr></table>";
            return buffer;
        } else {
            return "<script type=\"text/javascript\">link.goListPage(link.getSelfForm().pageNo.value - 1, '" + goForwardName + "' )</script>";
        }
    }
	
	
	this.printPagePrev = function(head, body, repbody, tail, forwardName) {
		var page = this.page ;
		var rowCount = this.rowCount ;
		var linkName = this.linkName ;
		var result = "";

		result += head;
		if (page.getCurrentScreen() > 1) {
			result += "<a href=\"javascript:" + linkName + "." + "goPreScreen(" + page.getPreScreenEndPageNo() + (StringUtils.isBlank(forwardName) ? "" : ", '" + forwardName + "'") + ")\">" + body + "</a>";
		} else {
			result += repbody;
		}
		result += tail;

		return result;
	} ;

	this.printPageNext = function(head, body, repbody, tail, forwardName) {
		var page = this.page ;
		var rowCount = this.rowCount ;
		var linkName = this.linkName ;
		var result = "";

		result += head;
		if (page.getMaxScreen(rowCount) > page.getCurrentScreen()) {
			result += " <a href=\"javascript:" + linkName + "." + "goNextScreen(" + page.getNextScreenStartPageNo() + (StringUtils.isBlank(forwardName) ? "" : ", '" + forwardName + "'") + ")\">" + body + "</a>";
		} else {
			result += repbody;
		}
		result += tail;

		return result;
	} ;
	
	this.printPageNoList = function(forwardName) {
		var page = this.page ;
		var rowCount = this.rowCount ;
		var linkName = this.linkName ;
		var rtnValue = "";

		if (page.getPageNo() > 1) rtnValue += "<a id=\"n_prev_page\" href=\"javascript:link.goListPage(" + (page.getPageNo() - 1) + ", '" + forwardName + "')\"></a>";
		if (page.getPageNo() < page.getMaxPageNo(rowCount)) rtnValue += "<a id=\"n_next_page\" href=\"javascript:link.goListPage(" + (page.getPageNo() + 1) + ", '" + forwardName + "')\"></a>";

		for (startPage = page.getMinPageNoOnScreen(), endPage = Math.min(page.getMaxPageNo(rowCount), page.getMaxPageNoOnScreen()); startPage <= endPage; startPage++) {
			if (startPage == page.getPageNo())
				rtnValue += " <font color='#0066FF'><b>[" + startPage + "]</b></font>";
			else
				rtnValue += " <a href=\"javascript:" + linkName + "." + "goListPage(" + startPage + ", '" + forwardName + "')\">[" + startPage + "]</a>";
		}

		return rtnValue;
	} ;
	
	this.isValidPage = function() {
		var page = this.page ;
		var rowCount = this.rowCount ;
		if (page.getPageNo() > 1 && rowCount == 0) {
			return false;
		}
		return true;
	}


}


function Page(listNum, pageNo, screenCount){
	// readonly and private
	var listNum = (listNum < 1) ? 1 : listNum ;
	var _pageNo = (pageNo < 1) ? 0 : pageNo-1 ;
	var screenCount = (screenCount && screenCount > 0 ) ? screenCount : 10 ;
	
	this.getListNum = function(){
		return listNum ;
	} ;
	
	this.getPageNo = function(){
		return _pageNo+1 ;
	} ;
	
	this.getScreenCount = function(){
		return (screenCount > 1) ? screenCount : 1 ;
	} ;
	
	this.getStartLoc = function(){
		return listNum * _pageNo ;
	};
	
	this.getEndLoc = function(){
		return this.getListNum() * (_pageNo + 1) ;
	} ;

	this.getMaxScreenCount = function(){
		return this.getListNum() * this.getScreenCount() ;
	} ;
	
	this.getMaxCount = function(){
		return this.getListNum() * screenCount * (Math.floor(_pageNo/10) + 1) + 1;
	} ;
	
	this.getNextPage = function() {
		return new Page(this.getListNum(), this.getPageNo() + 1, this.getScreenCount());
	} ;

	this.getPrePage = function() {
		return new Page(this.getListNum(), this.getPageNo() - 1, this.getScreenCount());
	} ;

	this.getMaxPageNo = function(rowCount){
		return Math.floor((rowCount + this.getListNum() - 1) / this.getListNum()) ;		
	} ;
	
	this.getCurrentScreen = function(){
		return Math.floor(_pageNo / this.getScreenCount()) + 1 ; 
	} ;
	
	this.getMaxScreen = function(rowCount){
		return Math.floor((rowCount-1) / (this.getListNum()) * this.getScreenCount())  + 1;
	} ;

	this.getMinPageNo = function(rowCount){
		return (this.getMaxScreen(rowCount) -1) * this.getScreenCount() + 1 ;
	} ;
	
	this.getMinPageNoOnScreen = function() {
		return Math.floor(_pageNo / this.getScreenCount()) * this.getScreenCount() + 1;
	} ;
	
	this.getMaxPageNoOnScreen = function() {
		return Math.floor(_pageNo / this.getScreenCount()) * this.getScreenCount() + this.getScreenCount();
	} ;
	
	this.getNextScreenStartPageNo = function() {
		return this.getCurrentScreen() * this.getListNum() + 1;
	} ;
	this.getPreScreenEndPageNo = function(){
		return (this.getCurrentScreen() - 1) * this.getListNum() ;
	} ;

	
	this.jsonString = function(){
		if (this.listNum <= 0 || this.pageNo <= 0) return "page:{listnum:10, pageno:1, screencount:10}" ;
		
		return "page:{listnum:" + this.getListNum() + ", pageno:" + this.getPageNo() + ", screencount:" + this.getScreenCount() + "}" ;
	} ;
}

function Params(){
	this.params = new Map() ;
	
	this.addParam = function(key, value){
		this.params.put(key, value) ;
	} ;
	
	this.jsonString = function(){
		var result = 'param:{' ;
		
		for(var i=0, last=this.params.size() ; i < last ; i++){
			if (i > 0) result += ', ' ;
			
			result += this.params.getEntry(i).key + ":" ;
			var value = this.params.getEntry(i).value ;
			if (typeof value != 'number') value = "\"" + value + "\"" ;
			
			result += value ;
		}
		
		result += '}';
		return result ;
	} ;
}

function Map(){
	
	var keys = new Array();

	this.contains = function(key){
		AssertUtil.assertParamNotNull(key, "key");
		var entry = findEntry(key);
		return !(entry == null || entry instanceof NullKey);
	};

	this.get = function(key) {
		var entry = findEntry(key);
	 	if ( !(entry == null || entry instanceof NullKey) )
			return entry.value;
		else
			return null;
	};

	this.put = function(key, value) {
		AssertUtil.assertParamNotNull(key, "Map.put.key");
		AssertUtil.assertParamNotNull(value, "Map.put.value");
		var entry = findEntry(key);
		if (entry){
			entry.value = value;
		} else {
			addNewEntry(key, value);
		}
	};

	this.remove = function (key){
		AssertUtil.assertParamNotNull(key, "key");
		for (var i=0;i<keys.length;i++){
			var entry = keys[i];
			if (entry instanceof NullKey) continue;
			if (entry.key == key){
					keys[i] = NullKey;
			}
		}				
	};
	function findEntry(key){
		for (var i=0;i<keys.length;i++){
			var entry = keys[i];
			if (entry instanceof NullKey) continue;
			if (entry.key == key){
				return entry ;
			}
		}
		return null;
	};
	function addNewEntry(key, value){
		var entry = new Object();
		entry.key = key;
		entry.value = value;
		keys[keys.length] = entry; 
	};
	this.size = function(){
		return keys.length ;
	};
	
	this.getEntry = function(idx){
		return keys[idx] ;
	} ;
}




/**
 * TrimPath Template. Release 1.1.2.
 * Copyright (C) 2004 - 2007 TrimPath.
 */

if (typeof(TrimPath) == 'undefined')
    TrimPath = {};

// TODO: Debugging mode vs stop-on-error mode - runtime flag.
// TODO: Handle || (or) characters and backslashes.
// TODO: Add more modifiers.

(function() {               // Using a closure to keep global namespace clean.
    if (TrimPath.evalEx == null)
        TrimPath.evalEx = function(src) { return eval(src); };

    var UNDEFINED;
    if (Array.prototype.pop == null)  // IE 5.x fix from Igor Poteryaev.
        Array.prototype.pop = function() {
            if (this.length === 0) {return UNDEFINED;}
            return this[--this.length];
        };
    if (Array.prototype.push == null) // IE 5.x fix from Igor Poteryaev.
        Array.prototype.push = function() {
            for (var i = 0; i < arguments.length; ++i) {this[this.length] = arguments[i];}
            return this.length;
        };

    TrimPath.parseTemplate = function(tmplContent, optTmplName, optEtc) {
        if (optEtc == null)
            optEtc = TrimPath.parseTemplate_etc;
        var funcSrc = parse(tmplContent, optTmplName, optEtc);
        var func = TrimPath.evalEx(funcSrc, optTmplName, 1);
        if (func != null)
            return new optEtc.Template(optTmplName, tmplContent, funcSrc, func, optEtc);
        return null;
    } ;
    
    var exceptionDetails = function(e) {
        return (e.toString()) + ";\n " +
               (e.message) + ";\n " + 
               (e.name) + ";\n " + 
               (e.stack       || 'no stack trace') + ";\n " +
               (e.description || 'no further description') + ";\n " +
               (e.fileName    || 'no file name') + ";\n " +
               (e.lineNumber  || 'no line number');
    } ;

    try {
        String.prototype.process = function(context, optFlags) {
            var template = TrimPath.parseTemplate(this, null);
            if (template != null)
                return template.process(context, optFlags);
            return this;
        } ;
    } catch (e) { // Swallow exception, such as when String.prototype is sealed.
    }
    
    TrimPath.parseTemplate_etc = {};            // Exposed for extensibility.
    TrimPath.parseTemplate_etc.statementTag = "forelse|for|if|elseif|else|var|macro";
    TrimPath.parseTemplate_etc.statementDef = { // Lookup table for statement tags.
        "if"     : { delta:  1, prefix: "if (", suffix: ") {", paramMin: 1 },
        "else"   : { delta:  0, prefix: "} else {" },
        "elseif" : { delta:  0, prefix: "} else if (", suffix: ") {", paramDefault: "true" },
        "/if"    : { delta: -1, prefix: "}" },
        "for"    : { delta:  1, paramMin: 3, 
                     prefixFunc : function(stmtParts, state, tmplName, etc) {
                        if (stmtParts[2] != "in")
                            throw new etc.ParseError(tmplName, state.line, "bad for loop statement: " + stmtParts.join(' '));
                        var iterVar = stmtParts[1];
                        var listVar = "__LIST__" + iterVar;
                        return [ "var ", listVar, " = ", stmtParts[3], ";",
                             // Fix from Ross Shaull for hash looping, make sure that we have an array of loop lengths to treat like a stack.
                             "var __LENGTH_STACK__;",
                             "if (typeof(__LENGTH_STACK__) == 'undefined' || !__LENGTH_STACK__.length) __LENGTH_STACK__ = new Array();", 
                             "__LENGTH_STACK__[__LENGTH_STACK__.length] = 0;", // Push a new for-loop onto the stack of loop lengths.
                             "if ((", listVar, ") != null) { ",
                             "var ", iterVar, "_ct = 0;",       // iterVar_ct variable, added by B. Bittman     
                             "for (var ", iterVar, "_index in ", listVar, ") { ",
                             iterVar, "_ct++;",
                             "if (typeof(", listVar, "[", iterVar, "_index]) == 'function') {continue;}", // IE 5.x fix from Igor Poteryaev.
                             "__LENGTH_STACK__[__LENGTH_STACK__.length - 1]++;",
                             "var ", iterVar, " = ", listVar, "[", iterVar, "_index];" ].join("");
                     } },
        "forelse" : { delta:  0, prefix: "} } if (__LENGTH_STACK__[__LENGTH_STACK__.length - 1] == 0) { if (", suffix: ") {", paramDefault: "true" },
        "/for"    : { delta: -1, prefix: "} }; delete __LENGTH_STACK__[__LENGTH_STACK__.length - 1];" }, // Remove the just-finished for-loop from the stack of loop lengths.
        "var"     : { delta:  0, prefix: "var ", suffix: ";" },
        "macro"   : { delta:  1, 
                      prefixFunc : function(stmtParts, state, tmplName, etc) {
                          var macroName = stmtParts[1].split('(')[0];
                          return [ "var ", macroName, " = function", 
                                   stmtParts.slice(1).join(' ').substring(macroName.length),
                                   "{ var _OUT_arr = []; var _OUT = { write: function(m) { if (m) _OUT_arr.push(m); } }; " ].join('');
                     } }, 
        "/macro"  : { delta: -1, prefix: " return _OUT_arr.join(''); };" }
    } ;
    TrimPath.parseTemplate_etc.modifierDef = {
        "eat"        : function(v)    { return ""; },
        "escape"     : function(s)    { return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); },
        "capitalize" : function(s)    { return String(s).toUpperCase(); },
        "substring"  : function(s, start, limit)    { return String(s).substring(start,limit) ; }, 
        "default"    : function(s, d) { return s != null ? s : d; }
    } ;
    TrimPath.parseTemplate_etc.modifierDef.h = TrimPath.parseTemplate_etc.modifierDef.escape;

    TrimPath.parseTemplate_etc.Template = function(tmplName, tmplContent, funcSrc, func, etc) {
        this.process = function(context, flags) {
            if (context == null)
                context = {};
            if (context._MODIFIERS == null)
                context._MODIFIERS = {};
            if (context.defined == null)
                context.defined = function(str) { return (context[str] != undefined); };
            for (var k in etc.modifierDef) {
                if (context._MODIFIERS[k] == null)
                    context._MODIFIERS[k] = etc.modifierDef[k];
            }
            if (flags == null)
                flags = {};
            var resultArr = [];
            var resultOut = { write: function(m) { resultArr.push(m); } };
            try {
                func(resultOut, context, flags);
            } catch (e) {
                if (flags.throwExceptions == true)
                    throw e;
                var result = new String(resultArr.join("") + 
                    "[ERROR: template: <pre>" + exceptionDetails(e) + "</pre>]");
                result["exception"] = e;
                return result;
            }
            return resultArr.join("");
        } ;
        this.name       = tmplName;
        this.source     = tmplContent; 
        this.sourceFunc = funcSrc;
        this.toString   = function() { return "TrimPath.Template [" + tmplName + "]"; } ;
    } ;
    TrimPath.parseTemplate_etc.ParseError = function(name, line, message) {
        this.name    = name;
        this.line    = line;
        this.message = message;
    } ;
    TrimPath.parseTemplate_etc.ParseError.prototype.toString = function() { 
        return ("TrimPath template ParseError in " + this.name + ": line " + this.line + ", " + this.message);
    } ;
    
    var parse = function(body, tmplName, etc) {
        body = cleanWhiteSpace(body);
        var funcText = [ "var TrimPath_Template_TEMP = function(_OUT, _CONTEXT, _FLAGS) { with (_CONTEXT) {" ];
        var state    = { stack: [], line: 1 };                              // TODO: Fix line number counting.
        var endStmtPrev = -1;
        while (endStmtPrev + 1 < body.length) {
            var begStmt = endStmtPrev;
            // Scan until we find some statement markup.
            begStmt = body.indexOf("{", begStmt + 1);
            while (begStmt >= 0) {
                var endStmt = body.indexOf('}', begStmt + 1);
                var stmt = body.substring(begStmt, endStmt);
                var blockrx = stmt.match(/^\{(cdata|minify|eval)/); // From B. Bittman, minify/eval/cdata implementation.
                if (blockrx) {
                    var blockType = blockrx[1]; 
                    var blockMarkerBeg = begStmt + blockType.length + 1;
                    var blockMarkerEnd = body.indexOf('}', blockMarkerBeg);
                    if (blockMarkerEnd >= 0) {
                        var blockMarker;
                        if( blockMarkerEnd - blockMarkerBeg <= 0 ) {
                            blockMarker = "{/" + blockType + "}";
                        } else {
                            blockMarker = body.substring(blockMarkerBeg + 1, blockMarkerEnd);
                        }                        
                        
                        var blockEnd = body.indexOf(blockMarker, blockMarkerEnd + 1);
                        if (blockEnd >= 0) {                            
                            emitSectionText(body.substring(endStmtPrev + 1, begStmt), funcText);
                            
                            var blockText = body.substring(blockMarkerEnd + 1, blockEnd);
                            if (blockType == 'cdata') {
                                emitText(blockText, funcText);
                            } else if (blockType == 'minify') {
                                emitText(scrubWhiteSpace(blockText), funcText);
                            } else if (blockType == 'eval') {
                                if (blockText != null && blockText.length > 0) // From B. Bittman, eval should not execute until process().
                                    funcText.push('_OUT.write( (function() { ' + blockText + ' })() );');
                            }
                            begStmt = endStmtPrev = blockEnd + blockMarker.length - 1;
                        }
                    }                        
                } else if (body.charAt(begStmt - 1) != '@' &&               // Not an expression or backslashed,
                           body.charAt(begStmt - 1) != '\\') {              // so check if it is a statement tag.
                    var offset = (body.charAt(begStmt + 1) == '/' ? 2 : 1); // Close tags offset of 2 skips '/'.
                                                                            // 10 is larger than maximum statement tag length.
                    if (body.substring(begStmt + offset, begStmt + 10 + offset).search(TrimPath.parseTemplate_etc.statementTag) == 0) 
                        break;                                              // Found a match.
                }
                begStmt = body.indexOf("{", begStmt + 1);
            }
            if (begStmt < 0)                              // In "a{for}c", begStmt will be 1.
                break;
            var endStmt = body.indexOf("}", begStmt + 1); // In "a{for}c", endStmt will be 5.
            if (endStmt < 0)
                break;
            emitSectionText(body.substring(endStmtPrev + 1, begStmt), funcText);
            emitStatement(body.substring(begStmt, endStmt + 1), state, funcText, tmplName, etc);
            endStmtPrev = endStmt;
        }
        emitSectionText(body.substring(endStmtPrev + 1), funcText);
        if (state.stack.length != 0)
            throw new etc.ParseError(tmplName, state.line, "unclosed, unmatched statement(s): " + state.stack.join(","));
        funcText.push("}}; TrimPath_Template_TEMP");
        return funcText.join("");
    } ;
    
    var emitStatement = function(stmtStr, state, funcText, tmplName, etc) {
        var parts = stmtStr.slice(1, -1).split(' ');
        var stmt = etc.statementDef[parts[0]]; // Here, parts[0] == for/if/else/...
        if (stmt == null) {                    // Not a real statement.
            emitSectionText(stmtStr, funcText);
            return;
        }
        if (stmt.delta < 0) {
            if (state.stack.length <= 0)
                throw new etc.ParseError(tmplName, state.line, "close tag does not match any previous statement: " + stmtStr);
            state.stack.pop();
        } 
        if (stmt.delta > 0)
            state.stack.push(stmtStr);

        if (stmt.paramMin != null &&
            stmt.paramMin >= parts.length)
            throw new etc.ParseError(tmplName, state.line, "statement needs more parameters: " + stmtStr);
        if (stmt.prefixFunc != null)
            funcText.push(stmt.prefixFunc(parts, state, tmplName, etc));
        else 
            funcText.push(stmt.prefix);
        if (stmt.suffix != null) {
            if (parts.length <= 1) {
                if (stmt.paramDefault != null)
                    funcText.push(stmt.paramDefault);
            } else {
                for (var i = 1; i < parts.length; i++) {
                    if (i > 1)
                        funcText.push(' ');
                    funcText.push(parts[i]);
                }
            }
            funcText.push(stmt.suffix);
        }
    } ;

    var emitSectionText = function(text, funcText) {
        if (text.length <= 0)
            return;
        var nlPrefix = 0;               // Index to first non-newline in prefix.
        var nlSuffix = text.length - 1; // Index to first non-space/tab in suffix.
        while (nlPrefix < text.length && (text.charAt(nlPrefix) == '\n'))
            nlPrefix++;
        while (nlSuffix >= 0 && (text.charAt(nlSuffix) == ' ' || text.charAt(nlSuffix) == '\t'))
            nlSuffix--;
        if (nlSuffix < nlPrefix)
            nlSuffix = nlPrefix;
        if (nlPrefix > 0) {
            funcText.push('if (_FLAGS.keepWhitespace == true) _OUT.write("');
            var s = text.substring(0, nlPrefix).replace('\n', '\\n'); // A macro IE fix from BJessen.
            if (s.charAt(s.length - 1) == '\n')
            	s = s.substring(0, s.length - 1);
            funcText.push(s);
            funcText.push('");');
        }
        var lines = text.substring(nlPrefix, nlSuffix + 1).split('\n');
        for (var i = 0; i < lines.length; i++) {
            emitSectionTextLine(lines[i], funcText);
            if (i < lines.length - 1)
                funcText.push('_OUT.write("\\n");\n');
        }
        if (nlSuffix + 1 < text.length) {
            funcText.push('if (_FLAGS.keepWhitespace == true) _OUT.write("');
            var s = text.substring(nlSuffix + 1).replace('\n', '\\n');
            if (s.charAt(s.length - 1) == '\n')
            	s = s.substring(0, s.length - 1);
            funcText.push(s);
            funcText.push('");');
        }
    } ;
    
    var emitSectionTextLine = function(line, funcText) {
        var endMarkPrev = '}';
        var endExprPrev = -1;
        while (endExprPrev + endMarkPrev.length < line.length) {
            var begMark = "@{", endMark = "}";
            var begExpr = line.indexOf(begMark, endExprPrev + endMarkPrev.length); // In "a${b}c", begExpr == 1
            if (begExpr < 0)
                break;
            if (line.charAt(begExpr + 2) == '%') {
                begMark = "@{%";
                endMark = "%}";
            }
            var endExpr = line.indexOf(endMark, begExpr + begMark.length);         // In "a${b}c", endExpr == 4;
            if (endExpr < 0)
                break;
            emitText(line.substring(endExprPrev + endMarkPrev.length, begExpr), funcText);                
            // Example: exprs == 'firstName|default:"John Doe"|capitalize'.split('|')
            var exprArr = line.substring(begExpr + begMark.length, endExpr).replace(/\|\|/g, "#@@#").split('|');
            for (var k in exprArr) {
                if (exprArr[k].replace) // IE 5.x fix from Igor Poteryaev.
                    exprArr[k] = exprArr[k].replace(/#@@#/g, '||');
            }
            funcText.push('_OUT.write(');
            emitExpression(exprArr, exprArr.length - 1, funcText); 
            funcText.push(');');
            endExprPrev = endExpr;
            endMarkPrev = endMark;
        }
        emitText(line.substring(endExprPrev + endMarkPrev.length), funcText); 
    } ;
    
    var emitText = function(text, funcText) {
        if (text == null ||
            text.length <= 0)
            return;
        text = text.replace(/\\/g, '\\\\');
        text = text.replace(/\n/g, '\\n');
        text = text.replace(/"/g,  '\\"');
        funcText.push('_OUT.write("');
        funcText.push(text);
        funcText.push('");');
    } ;
    
    var emitExpression = function(exprArr, index, funcText) {
        // Ex: foo|a:x|b:y1,y2|c:z1,z2 is emitted as c(b(a(foo,x),y1,y2),z1,z2)
        var expr = exprArr[index]; // Ex: exprArr == [firstName,capitalize,default:"John Doe"]
        if (index <= 0) {          // Ex: expr    == 'default:"John Doe"'
            funcText.push(expr);
            return;
        }
        var parts = expr.split(':');
        funcText.push('_MODIFIERS["');
        funcText.push(parts[0]); // The parts[0] is a modifier function name, like capitalize.
        funcText.push('"](');
        emitExpression(exprArr, index - 1, funcText);
        if (parts.length > 1) {
            funcText.push(',');
            funcText.push(parts[1]);
        }
        funcText.push(')');
    } ;

    var cleanWhiteSpace = function(result) {
        result = result.replace(/\t/g,   "    ");
        result = result.replace(/\r\n/g, "\n");
        result = result.replace(/\r/g,   "\n");
        // result = result.replace(/^(\s*\S*(\s+\S+)*)\s*$/, '$1'); // Right trim by Igor Poteryaev.
        return result;
    } ;

    var scrubWhiteSpace = function(result) {
        result = result.replace(/^\s+/g,   "");
        result = result.replace(/\s+$/g,   "");
        result = result.replace(/\s+/g,   " ");
        // result = result.replace(/^(\s*\S*(\s+\S+)*)\s*$/, '$1'); // Right trim by Igor Poteryaev.
        return result;
    } ;

    // The DOM helper functions depend on DOM/DHTML, so they only work in a browser.
    // However, these are not considered core to the engine.
    //
    TrimPath.parseDOMTemplate = function(elementId, optDocument, optEtc) {
        if (optDocument == null)
            optDocument = document;
        var element = optDocument.getElementById(elementId);
        var content = element.value;     // Like textarea.value.
        if (content == null)
            content = element.innerHTML; // Like textarea.innerHTML.
        content = content.replace(/&lt;/g, "<").replace(/&gt;/g, ">");
        return TrimPath.parseTemplate(content, elementId, optEtc);
    } ;

    TrimPath.processDOMTemplate = function(elementId, context, optFlags, optDocument, optEtc) {
        return TrimPath.parseDOMTemplate(elementId, optDocument, optEtc).process(context, optFlags);
    } ;
}) ();
