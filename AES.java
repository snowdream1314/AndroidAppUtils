import android.annotation.SuppressLint;
import android.util.Base64;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@SuppressLint("TrulyRandom")
public class AES {


	@SuppressLint("TrulyRandom")
	public static String encrypt(String src, String aes) {
		try {
			String key = getRawKey(aes);
			String iv = getIv(aes);

			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = src.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength
						+ (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);

			return Base64.encodeToString(encrypted, 0);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String decrypt(String data, String aes) {
		try {
			String key = getRawKey(aes);
			String iv = getIv(aes);
			byte[] encrypted1 = Base64.decode(data, 0);

			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressLint("TrulyRandom")
	private static String getRawKey(String key) throws Exception {
		String sha384ofkey = sha384(key);
		String skey = sha384ofkey.substring(0, 16);
		return skey;

	}

	private static String getIv(String key) {
		String sha384ofkey = sha384(key);
		String sIv = sha384ofkey.substring(32, 48);
		return sIv;
	}

	public static String sha384(String key) {
		String rt = "";
		String hashType = "SHA-384";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(hashType);
			messageDigest.update(key.getBytes());
			byte[] bs = messageDigest.digest();
			rt = toHex(bs);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return rt;

	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private final static String HEX = "0123456789ABCDEF";

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

	public static String base64Encode(byte[] s) {
		if (s == null)
			return null;
		return Base64.encodeToString(s, Base64.DEFAULT);

	}

	public static byte[] base64Decode(String s) throws IOException {

		if (s == null)

			return null;

		return Base64.decode(s.getBytes(), Base64.DEFAULT);
	}

}
