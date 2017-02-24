package com.company.rebatedor.test;

import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.company.disparador.DisparadorHTTP;

public class DispararComParametrosTest {

	DisparadorHTTP disparadorHTTP = new DisparadorHTTP();

	@Test
	public void test() {
		System.out.println("Inside DispararComParametrosTest");
		
		Map<String, String> argumentos = new TreeMap<String, String>();
		argumentos.put(DisparadorHTTP.PREFIX_ARG + DisparadorHTTP.HTTP_HOST_ARG, "http://localhost:8080/rebatedor-json/rebatedor/post");
		argumentos.put(DisparadorHTTP.PREFIX_ARG + DisparadorHTTP.HTTP_VERB_ARG, "POST");
		argumentos.put(DisparadorHTTP.PREFIX_ARG + DisparadorHTTP.HTTP_HEADER_ARG + "_1", "Content-Type:application/json");
		argumentos.put(DisparadorHTTP.PREFIX_ARG + DisparadorHTTP.HTTP_BODY_ARG, "{\"id\":\"0305\",\"nome\":\"MERCHANT123\"}");
		
		assertNotNull(disparadorHTTP.disparar(argumentos));
	}

}
