
$net.ion.aradon.sample.TemplateManager = function(aradon, sessionId, groupid){
	var aradon = aradon ;
	this.templateLet = aradon.getSection('template').getLet('/velocity/string/' + groupid) ;
	this.session = aradon.createSession(sessionId) ;
	this.groupid = groupid ;
	var myself = this ;

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
	}
}

//if you use same named input box.. use this pattern..
var $F = function(name){
	return $("#frm").find("[name='" + name + "']").val() ;
} ;


