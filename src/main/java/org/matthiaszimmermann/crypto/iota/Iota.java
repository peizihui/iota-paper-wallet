package org.matthiaszimmermann.crypto.iota;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.matthiaszimmermann.crypto.core.Account;
import org.matthiaszimmermann.crypto.core.Entropy;
import org.matthiaszimmermann.crypto.core.Mnemonic;
import org.matthiaszimmermann.crypto.core.Network;
import org.matthiaszimmermann.crypto.core.Protocol;
import org.matthiaszimmermann.crypto.core.Technology;

import jota.error.ArgumentException;
import jota.pow.ICurl;
import jota.pow.JCurl;
import jota.pow.SpongeFactory;
import jota.utils.IotaAPIUtils;

// TODO transfer creation /w offline signing
// http://ogrelab.ikratko.com/sending-new-transfer-to-iota-node-using-java-aka-sendtransfer/
public class Iota extends Protocol {
	
	public static final String TRYTE_ALPHABET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public Iota(Network network) {
		super(Technology.Iota, network);
	}

	@Override
	public Account createAccount(List<String> mnemonic, String passPhrase) {
		return new IotaAccount(mnemonic, passPhrase, getNetwork());
	}	

	@Override
	public Account restoreAccount(JSONObject accountJson, String passPhrase) {
		try {
			return new IotaAccount(accountJson, passPhrase, getNetwork());
		} 
		catch (JSONException e) {
			throw new RuntimeException("Failed to create Bitcoin account from json object", e);
		}
	}
	
	@Override
	public List<String> generateMnemonicWords() {
		byte [] entropy = Entropy.generateEntropy();
		List<String> wordList = null;

		try {
			wordList = Mnemonic.loadWordList();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load mnemonic default word list");
		}

		return Mnemonic.toWords(entropy, wordList);
	}

	@Override
	public void validateMnemonicWords(List<String> mnemonicWords) {
		// TODO add some validation here. if something looks bad throw an illegal arg exception	
	}
	
	// https://github.com/modum-io/tokenapp-keys-iota/blob/master/src/main/java/io/modum/IotaAddressGenerator.java
	public static String deriveAddressFromSeed(String seed) {
		ICurl curl = new JCurl(SpongeFactory.Mode.CURLP81);
		int index = 0;

		try {			
			return IotaAPIUtils.newAddress(
					seed, 
					IotaAccount.SECURITY_LEVEL_DEFAULT,
					index, 
					IotaAccount.CHECKSUM_DEFAULT,
					curl);
		} 
		catch (ArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	} 

}