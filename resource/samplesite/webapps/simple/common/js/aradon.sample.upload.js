

$net.ion.aradon.sample.Gallery = function(aradon, sessionId, boardId){
	var aradon = aradon ;
	var boardLet = aradon.getSection('sample').getLet('/bulletin/' + boardId) ;
	var refLet = aradon.getSystemLet('reference') ;
	var myself = this ;
	this.session =  aradon.createSession(sessionId) ;
	this.boardId = boardId ;
	this.images = [] ;
	

	this.vf = new $net.ion.common.valid.FormValid() ;
	this.vf.setForward('add') ;
	this.vf.required('memo', 'memo required', false); 

	
	this.listNum = 10 ;
	this.myTemplates = {} ;
	
	this.currentPageNo = 1 ;
	
	this.initTemplate = function(tplId, template) {
		this.myTemplates[tplId] = TrimPath.parseDOMTemplate(template) ;
		return this ;
	} ;

	
	this.goAddPage = function() {
		location.href = './upload.htm'
	} ;
	
	this.goViewPage = function(oid) {
		location.href = './view.htm?oid=' + oid
	} ;
	
	this.goListPage = function(pageNo){
		
		var template = this.myTemplates['list'] ;
		this.currentPageNo = pageNo ;
		
		boardLet.addParam('aradon.page.pageNo', pageNo) ;
		boardLet.addParam('aradon.page.listNum', this.listNum) ;
		
		boardLet.get(
			function(res) { 
				// alert('Success', res) ;
				
				var result = template.process(res) ;
				$("#result").html(result) ;
				
				var page = new $net.ion.common.Page(res.result.request.page.listNum, res.result.request.page.pageNo, this.listNum) ;
				var pout = new $net.ion.common.PageOutPut('page_table', res.result.response.totalCount, page) ;
				$("#result").append(pout.toHtml()) ;

				pout.bindLink("table#page_table", function(e){myself.goListPage($(this).text().substringBetween('[',']'));}, function(e){myself.goListPage(page.getNextScreenStartPageNo());}, function(e){myself.goListPage(page.getPreScreenEndPageNo());}) ;
			
			}) ;
		
	} ;

	
	this.viewPage = function(oid){

		var template = this.myTemplates['view'] ;

		boardLet.subLet(oid).get (
			function(res) { 
				var result = template.process(res) ;
				var divObj = document.createElement('div');
				$(divObj).html(result) ;
				$("#result").prepend(divObj)  ;
			}
		) ;
		refLet.subLet(oid).get (
			function(res) { 
				var images = res.result.nodes ;
				
				for(var i=0; i<images.length ; i++){
		            var imgObj = document.createElement('img');
		            imgObj.src = aradon.getSystemLet('file').subLet(images[i]._id).fullPath() ; //'http://localhost:9002/system/file/' + images[i]._id;
		            imgObj.width = 300 ;
		            imgObj.height = 200 ;
		            $('#result').append(imgObj);
		            $('#result').append(document.createTextNode(images[i].filename));
				
				}
			}
		)

	} ;
	
	
	this.delContent = function(oid) {
		var gallery = this ;
		
		boardLet.subLet(oid).del(
			function(res) { 
				// alert(res) ;
				gallery.goPreListPage() ;
			}
		) ;

		// TODO file && reference delete ??
		
		
	} ;
	
	this.addContent = function(){
		var gallery = this ;
		
		boardLet.addParam('memo', $F("memo")).addParam('reguserid', gallery.session.getUserId()).addParam('boardid' ,gallery.boardId) ;
		boardLet.post(
			function(res) { 
				gallery.setReference(res) ;
			}
		)
		

	} ;
	
	this.setReference = function(res){
		if (res) {
			
			var targets = [] ;
			for(var i=0, ilast = this.images.length; i < ilast ; i++) {
				targets[i] = this.images[i].nodes[0]._id ;
			}
			var src = res.result.nodes[0]._id ;
			
			
			if (this.images.length <= 0) {
				gallery.goPreListPage(res) ;
			} else {
				refLet.subLet(src + '/part/' + targets.join(',')).post(
					function(res) { 
						gallery.goPreListPage(res) ;
					}
				) ;
			}
		}	
	} ;
	
	this.goPreListPage = function(res){
		location.href = './list.htm'
	} ;


	this.appendImage = function(res) {
		this.images[this.images.length] = res.result ;
	} ;

}


