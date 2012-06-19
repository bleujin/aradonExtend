package net.ion.radon.impl.let.board;

import junit.framework.TestCase;

import org.restlet.data.Form;
import org.restlet.data.Parameter;

public class TestForm extends TestCase {
	
	public void testFormAdd() throws Exception {
		Form form = new Form();
		form.add("color", "red");
		form.add("color", "white");
		form.add("color", "bleu");
		
		String[] values = form.getValuesArray("color") ;
		assertEquals(3, values.length) ;
		assertEquals("red,white,bleu", form.getValues("color")) ;
		assertEquals("red", form.getFirstValue("color")) ;
	}
	
	public void testParameter() throws Exception {
		Form form = new Form() ;
		for (int i = 0; i < 3; i++) {
			form.add(new Parameter("color", "color" + i)) ;
		}
		String[] values = form.getValuesArray("color") ;
		assertEquals(3, values.length) ;
	}
	
	
}