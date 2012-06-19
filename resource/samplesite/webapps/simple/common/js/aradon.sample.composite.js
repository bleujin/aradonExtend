
$net.ion.aradon.sample.Composite = function(aradon, sessionId){
	var aradon = aradon ;
	this.procLet = aradon.getSection('db').getLet('/proc') ;
	this.templateLet = aradon.getSection('template').getLet('/velocity/string') ;
	this.session = aradon.createSession(sessionId) ;
	var myself = this ;

	this.getData = function(procId){
		this.procLet.clearParam() ;
		this.procLet.addParam('aradon.result.format', 'json') ;
		this.procLet.addParam('deptno', '20') ;
		
		this.procLet.subLet(procId).post(
			function(res) { 
				editor.setCode(JSON.stringify(res)) ;
			}
		) ;	
	} ;
	
	this.getTemplate = function(uid){
		this.templateLet.subLet(uid).get(
			function(res) { 
				editor.setCode(res.result.nodes[0].template) ;
			}
		) ;	
	} ;
	
	
	this.setTemplate = function(uid){
		// alert(editor.getCode(), uid);
		var targetLet = this.templateLet.subLet(uid) ;
		targetLet.clearParam() ;
		targetLet.addParam('template', editor.getCode()) ;
		
		
		targetLet.put(
			function(res) { 
				alert('write success');
			}
		) ;	
	} ;
	
	this.applyTemplate = function(){
		
		this.procLet.clearParam() ;
		this.procLet.addParam('aradon.result.format', 'json') ;
		this.procLet.addParam('deptno', '20') ;
		
		this.procLet.subLet($F('procId')).post(
			function(res) {
				var targetLet = myself.templateLet.subLet($F('tplId')) ;
				targetLet.clearParam() ;
				targetLet.addParam('source', "{'name':'bleujin'}") ;
				targetLet.addParam('data', JSON.stringify(res)) ;
				targetLet.addParam('aradon.result.format', 'html') ;
				// $.ajaxSetup({dataType:'text/html'}) ;
				targetLet.post(
					function(res) { 
						alert('write success');
						editor.setCode(JSON.stringify(res)) ;
					}
				) ;	
			}
		) ;	
	}
}

//if you use same named input box.. use this pattern..
var $F = function(name){
	return $("#frm").find("[name='" + name + "']").val() ;
} ;


