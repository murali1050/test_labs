package services;

import java.io.IOException;

import sun.misc.BASE64Encoder;

public class Base64EncodeGenerator {

	public static void main(String[] args) throws IOException {

		BASE64Encoder enc = new BASE64Encoder();
		String encode =  enc.encode((new StringBuffer("node_ftsswcfx").append(":").append("ftsswcfx").toString()).getBytes());
		System.out.println("Encode:" +encode);
	}
}
