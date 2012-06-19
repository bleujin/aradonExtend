


$net.ion.aradon.sample.Board = function(aradon, sessionId, boardId){
	var aradon = aradon ;
	this.boardLet = aradon.getSection('board').getLet('/list/' + boardId) ;
	// .getPath('sample/bulletin/') ;
	this.session = aradon.createSession(sessionId) ;
	this.boardId = boardId ;
	this.listNum = 5 ;
	this.myTemplates = {} ;
	var myself = this ;
	
	this.currentPageNo = 1 ;
	
	this.initTemplate = function(templates) {
		this.myTemplates['list'] = TrimPath.parseDOMTemplate(templates.list) ;
		this.myTemplates['view'] = TrimPath.parseDOMTemplate(templates.view) ;
		this.myTemplates['edit'] = TrimPath.parseDOMTemplate(templates.edit) ;
		this.myTemplates['add']  = TrimPath.parseDOMTemplate(templates.add) ;
		return this ;
	} ;

	this.goNextScreen = function(pageNo){
		return this.goListPage(pageNo) ;
	} ;
	this.goPreScreen = function(pageNo){
		return this.goListPage(pageNo) ;
	} ;
	
	this.goPreListPage = function(){
		return this.goListPage(this.currentPageNo, 'list') ;
	} ;
	
	this.goAddPage = function() {
		
		var template = this.myTemplates['add'] ;
		var result = template.process({}) ;
		$("#result").html(result)  ;

	} ;
	
	this.goViewPage = function(no){
		var targetLet = aradon.getSection('board').getLet('/view/' + boardId) ;
		
		var template = this.myTemplates['view'] ;

		targetLet.subLet(no).get(
			function(res) { 
				var result = template.process(res) ;
				$("#result").html(result)  ;
			}
		) ;	
		

	} ;
	
	this.goEditPage = function(no) {
		var targetLet = aradon.getSection('board').getLet('/edit/' + boardId) ;
		var template = this.myTemplates['edit'] ;
		targetLet.subLet(no).get(
			function(res) { 
				var result = template.process(res) ;
				$("#result").html(result)  ;
			}
		) ;	

	} ;

	this.goListPage = function(pageNo){
		var targetLet = aradon.getSection('board').getLet('/list/' + boardId + '/' + this.listNum + '/' + pageNo) ;
		
		var template = this.myTemplates['list'] ;
		this.currentPageNo = pageNo ;
		
		
		// ..... 
		//this.boardLet.addParam('aradon.page.pageNo', pageNo) ;
		targetLet.addParam('aradon.result.format', 'json') ;
		//this.boardLet.addParam('aradon.page.listNum', this.listNum) ;
		var board = this ;
		targetLet.get(
			function(res) { 
				var result = template.process(res) ;
				$("#result").html(result) ;
				
				// var page = new $net.ion.common.Page(this.listNum, pageNo, this.listNum) ;
				var page = new $net.ion.common.Page(board.listNum, pageNo, 10) ;

				var pout = new $net.ion.common.PageOutPut('page_table', res.result.response.totalCount, page) ;

				// alert( pageNo, new $net.ion.common.PageOutPut('page_table', res.result.response.totalCount, page).toHtml())
				$("#result").append(pout.toHtml()) ;
				
				pout.bindLink("table#page_table", 
					function(e){
						board.goListPage($(this).text().substringBetween('[',']'));
					}, 
					function(e){
						board.goListPage(page.getNextScreenStartPageNo());
					}, 
					function(e){
						board.goListPage(page.getPreScreenEndPageNo());
					}) ;
			}) ;
	} ;


	this.delArticle = function(no) {
		var board = this ;
		var targetLet = aradon.getSection('board').getLet('/delete/' + boardId) ;

		targetLet.clearParam() ;
		targetLet.subLet(no).del(
			function(res) { 
				// alert(res) ;
				board.goPreListPage() ;
			}
		) ;
		
	} ;
	
	

	this.editArticle = function(no) {
		var board = this ;

		var targetLet = aradon.getSection('board').getLet('/edit/' + boardId) ;
		
		targetLet.clearParam() ;
		targetLet.addParam('subject', $F("subject")) ;
		targetLet.addParam('content', $F("content")) ;

		targetLet.subLet(no).post(
			function(res) { 
				board.goPreListPage() ;
			}
		) ;		

	}

	this.addArticle = function() {
		var board = this ;
		
		var targetLet = aradon.getSection('board').getLet('/add/' + boardId) ;
		
		targetLet.clearParam() ;
		targetLet.addParam('subject', $F("subject")) ;
		targetLet.addParam('content', $F("content")) ;
		targetLet.addParam('creuser', board.session.getUserId()) ;
		
		
		targetLet.post(
			function(res) { 
					board.goPreListPage() ;
			}) ;
	}


	this.toString = function(){
		return this.path ;
	} ;
}

// if you use same named input box.. use this pattern..
var $F = function(name){
	return $("#result").find("[name='" + name + "']").val() ;
} ;



