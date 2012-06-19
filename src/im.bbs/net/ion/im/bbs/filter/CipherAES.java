package net.ion.im.bbs.filter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*
 * simple AES Chipher
 */
public class CipherAES {
	private Cipher cipher;
	String keyString = "IM_Mobile_Aradon";

	public CipherAES() throws Exception {
		cipher = Cipher.getInstance("AES");
	}

	// 암호화
	public String encrypt(String plainText) throws Exception {

		byte[] raw = keyString.getBytes();

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(plainText.getBytes());
		return asHex(encrypted);

	}

	// 복호화
	public String decrypt(String cipherText) throws Exception {

		byte[] raw = keyString.getBytes();

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original = cipher.doFinal(fromString(cipherText));
		String originalString = new String(original);
		return originalString;

	}

	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	private static byte[] fromString(String hex) {
		int len = hex.length();
		byte[] buf = new byte[((len + 1) / 2)];

		int i = 0, j = 0;
		if ((len % 2) == 1)
			buf[j++] = (byte) fromDigit(hex.charAt(i++));

		while (i < len) {
			buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) | fromDigit(hex.charAt(i++)));
		}
		return buf;
	}

	private static int fromDigit(char ch) {
		if (ch >= '0' && ch <= '9')
			return ch - '0';
		if (ch >= 'A' && ch <= 'F')
			return ch - 'A' + 10;
		if (ch >= 'a' && ch <= 'f')
			return ch - 'a' + 10;

		throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
	}
	
	public static void main(String[] args) throws Exception {
		CipherAES encoder = new CipherAES();
		System.out.println(encoder.encrypt("IMMobile"));
	}
}
