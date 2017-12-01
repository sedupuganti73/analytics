package com.bnsf.analytics.convertors;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.util.Base64Utils;



@Converter
public class CryptoConvertor  implements AttributeConverter<String, String>{
	private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = "MySuperSecretKey".getBytes();
	@Override
	public String convertToDatabaseColumn(String attribute) {
		Key key = new SecretKeySpec(KEY, "AES");
	      try {
	         Cipher c = Cipher.getInstance(ALGORITHM);
	         c.init(Cipher.ENCRYPT_MODE, key);
	         return Base64Utils.encodeToString(c.doFinal(attribute.getBytes()));
	      } catch (Exception e) {
	         throw new RuntimeException(e);
	      }
	}
	@Override
	public String convertToEntityAttribute(String dbData) {
		Key key = new SecretKeySpec(KEY, "AES");
	      try {
	        Cipher c = Cipher.getInstance(ALGORITHM);
	        c.init(Cipher.DECRYPT_MODE, key);
	        return new String(c.doFinal(Base64Utils.decodeFromString(dbData)));
	      } catch (Exception e) {
	        throw new RuntimeException(e);
	      }
	}

}
