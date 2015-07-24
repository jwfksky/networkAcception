package store.yifan.cn.networkacception.utils.encrypt;


import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TOTP {

	private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
		try {
			Mac hmac;
			hmac = Mac.getInstance(crypto);
			SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
			// SecretKeySpec macKey = new SecretKeySpec(keyBytes, "SHA1");
			hmac.init(macKey);
			return hmac.doFinal(text);
		} catch (GeneralSecurityException gse) {
			throw new UndeclaredThrowableException(gse);
		}
	}

	private static byte[] hexStr2Bytes(String hex) {
		try {
			System.out.println("hex = " + hex);
			byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
			byte[] ret = new byte[bArray.length - 1];
			for (int i = 0; i < ret.length; i++)
				ret[i] = bArray[i + 1];
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}

	private static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000,
			100000, 1000000, 10000000, 100000000 };// 0 1 2 3 4 5 6 7 8

	public static String generateTOTP(String key, String time,
			String returnDigits, String crypto) {
		int codeDigits = Integer.decode(returnDigits).intValue();
		System.out.println("codeDigits = " + codeDigits);
		String result = null;
		System.out.println("before time =" + time + " ,time.length() = "
				+ time.length());
		while (time.length() < 16) {
			time = "0" + time;
		}
		System.out.println("after time = " + time + " ,time.length() = "
				+ time.length());
		byte[] msg = hexStr2Bytes(time);
		// byte[] k = hexStr2Bytes(key);
		 byte[] k = Base32.decode(key);
		for (byte b : k) {
			System.out.print(b);
		}
		System.out.println("-----");
		byte[] hash = hmac_sha(crypto, k, msg);
		int offset = hash[hash.length - 1] & 0xf;

		int binary = ((hash[offset] & 0x7f) << 24)
				| ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
		int otp = binary % DIGITS_POWER[codeDigits];
		result = Integer.toString(otp);
		while (result.length() < codeDigits) {
			result = "0" + result;
		}
		return result;
	}

	public static void main(String[] args) {
		long interval = System.currentTimeMillis();
//		long interval = 59000L;
		System.out.println("interval = " + interval);
		long T0 = 0;
		long X = 30;
		long T = (interval - T0) / X;
		System.out.println("T = " + T);
		String steps = Long.toHexString(T).toUpperCase();
		System.out.println("steps = " + steps);
		// 3132333435363738393031323334353637383930
		// 4810655495111710311898112120120102524957117120115112
		// GBVDOMJTOVTXMYTQPB4GMNBRHF2XQ43Q
		String secret = "GBVDOMJTOVTXMYTQPB4GMNBRHF2XQ43Q";
		String encryptedPwd = generateTOTP(secret,
				steps, "6", "HmacSHA1");
		System.out.println("encryptedPwd = " + encryptedPwd);
	}

}
