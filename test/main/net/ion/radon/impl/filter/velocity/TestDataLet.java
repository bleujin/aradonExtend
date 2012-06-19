package net.ion.radon.impl.filter.velocity;

import java.util.List;
import java.util.Map;

import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.let.DefaultLet;

import org.restlet.representation.Representation;

public class TestDataLet extends DefaultLet{

	

	protected Representation myGet() throws Exception {
		
		IRequest req = IRequest.create(getInnerRequest().getFormParameter()) ;
		IResponse res = IResponse.EMPTY_RESPONSE ;
		List<Map<String, ?>> datas = ListUtil.newList() ;
		
		datas.add(MapUtil.create("name", "bleujin")) ;
		datas.add(MapUtil.create("name", 1)) ;
		
		return toRepresentation(req, datas, res);
	}
	
}
