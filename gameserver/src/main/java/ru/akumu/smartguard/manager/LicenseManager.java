//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.utils.Base64;
import ru.akumu.smartguard.utils.crypt.KeyObject;
import ru.akumu.smartguard.utils.crypt.SCrypt;
import ru.akumu.smartguard.utils.log.GuardLog;

public class LicenseManager {
    private static final File LicenseConfig;
    private static LicenseManager _instance;
	private final Map<LicenseManager.KeyType, byte[]> _keys = new HashMap<KeyType, byte[]>(LicenseManager.KeyType.values().length);
    public int LicenseID = -1;
    public String LicenseToken = null;

    public static LicenseManager getInstance() {
        return _instance != null?_instance:(_instance = new LicenseManager());
    }

    private LicenseManager() {
        try {
            if(!LicenseConfig.exists()) {
                throw new IOException("License data file not found.");
            }

            DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = e.newDocumentBuilder();
            Document doc = docBuilder.parse(LicenseConfig);
            NodeList licenseNodes = doc.getElementsByTagName("license");
            if(licenseNodes.getLength() > 0) {
                Element license = (Element)licenseNodes.item(0);
                this.LicenseID = Integer.parseInt(license.getAttribute("id").trim());
                this.LicenseToken = license.getAttribute("token").trim();
                NodeList keysNodes = license.getElementsByTagName("key");
                if(keysNodes.getLength() > 0) {
                    for(int i = 0; i < keysNodes.getLength(); ++i) {
                        Node keyNode = keysNodes.item(i);
                        if(keyNode.getNodeType() == 1) {
                            Element keyElement = (Element)keyNode;
                            LicenseManager.KeyType type = LicenseManager.KeyType.valueOf(keyElement.getAttribute("type").trim());
                            byte[] key = Base64.decode(keyElement.getTextContent().trim());
                            this._keys.put(type, key);
                        }
                    }
                }

                GuardLog.getLogger().log(Level.INFO, String.format("Smart guard info. ID: %d, TOKEN: %s", new Object[]{Integer.valueOf(this.LicenseID), this.LicenseToken}));
            }
        } catch (Exception var12) {
            GuardLog.logException(var12);
        }

    }

    public KeyObject makeNetworkKey(LicenseManager.KeyType type, byte[] xorKey) {
        if(!type.network) {
            throw new InvalidParameterException("KeyType must be network compatable.");
        } else if(xorKey == null) {
            throw new InvalidParameterException("Xor key can not be null.");
        } else {
            byte[] baseKey = (byte[])this._keys.get(type);
            if(baseKey == null) {
                return null;
            } else {
                byte[] temp = new byte[baseKey.length];
                System.arraycopy(baseKey, 0, temp, 0, baseKey.length);

                for(int ko = 0; ko < temp.length; ++ko) {
                    temp[ko] ^= xorKey[ko % xorKey.length];
                }

                KeyObject var6 = new KeyObject();
                SCrypt.init(temp, var6);
                return var6;
            }
        }
    }

    public boolean cryptInternalData(byte[] data) {
		if (data == null)
			return false;
        byte[] internalKey = (byte[])this._keys.get(LicenseManager.KeyType.INTERNAL);
        if(internalKey == null) {
            return false;
        } else {
            KeyObject ko = new KeyObject();
            SCrypt.init(internalKey, ko);
            SCrypt.crypt(data, ko);
            return true;
        }
    }

    static {
        LicenseConfig = new File(GuardConfig.SMART_GUARD_DIR, "license.xml");
    }

    public static enum KeyType {
        IN(true),
        OUT(true),
        INTERNAL(false);

        public boolean network;

        private KeyType(boolean network) {
            this.network = network;
        }
    }
}
