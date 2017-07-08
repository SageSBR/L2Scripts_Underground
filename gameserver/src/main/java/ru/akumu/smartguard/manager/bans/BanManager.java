//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.bans;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.bans.model.Ban;
import ru.akumu.smartguard.manager.bans.model.Ban.Type;
import ru.akumu.smartguard.manager.session.ClientSessionManager;
import ru.akumu.smartguard.manager.session.model.ClientSession;
import ru.akumu.smartguard.manager.session.model.HWID;
import ru.akumu.smartguard.model.TimedObject;
import ru.akumu.smartguard.utils.log.GuardLog;

public class BanManager {
    private static final HashMap<HWID, Ban> _bans = new HashMap(100);
    private static final List<TimedObject<Ban>> _delayedBans = new ArrayList(100);
    private static final File banlistFile;
    private static long _lastStore;

    public BanManager() {
    }

    public static Collection<Ban> getBans() {
        HashMap var0 = _bans;
        synchronized(_bans) {
            HashSet res = new HashSet(_bans.size());
            res.addAll(_bans.values());
            return res;
        }
    }

    public static Ban getBan(String hwid) {
        HWID hw = HWID.fromString(hwid);
        return hw == null?null:getBan(hw);
    }

    public static Ban getBan(HWID hwid) {
        Ban ban = null;
        HashMap var2 = _bans;
        synchronized(_bans) {
            Iterator i$ = _bans.entrySet().iterator();

            while(i$.hasNext()) {
                Entry en = (Entry)i$.next();
                HWID h = (HWID)en.getKey();
                if(h.equalsForBan(hwid)) {
                    ban = (Ban)en.getValue();
                    break;
                }
            }
        }

        if(ban != null && ban.isExpired()) {
            GuardLog.getLogger().info(String.format("[BanManager]: Ban has expired and was removed: %s", new Object[]{ban}));
            removeBan(ban.hwid);
            return null;
        } else {
            return ban;
        }
    }

    public static void addBan(Ban ban) {
        addBan(ban, false);
    }

    public static void addBan(Ban ban, boolean kickSession) {
        if(ban != null) {
            HashMap session = _bans;
            synchronized(_bans) {
                _bans.put(ban.hwid, ban);
            }

            if(kickSession) {
                ClientSession session1 = ClientSessionManager.getSession(ban.hwid);
                if(session1 != null) {
                    session1.disconnect();
                }
            }

            store();
        }
    }

    public static void addDelayedBan(Ban ban, long delay) {
        if(ban != null) {
            if(delay <= 0L) {
                throw new InvalidParameterException("Delay must be positive");
            } else {
                List var3 = _delayedBans;
                synchronized(_delayedBans) {
                    _delayedBans.add(new TimedObject(ban, delay));
                }
            }
        }
    }

    public static void removeBan(String hwid) {
        HWID hw = HWID.fromString(hwid);
        if(hw != null) {
            removeBan(hw);
        }
    }

    public static void removeBan(HWID hwid) {
        if(hwid != null) {
            HashMap var2 = _bans;
            Ban ban;
            synchronized(_bans) {
                ban = (Ban)_bans.remove(hwid);
            }

            if(ban != null) {
                store();
            }

        }
    }

    public static Ban checkAccount(String account) {
        if(account == null) {
            return null;
        } else {
            String acc = account.toLowerCase();
            HashMap var2 = _bans;
            synchronized(_bans) {
                Iterator i$ = _bans.values().iterator();

                Ban ban;
                do {
                    if(!i$.hasNext()) {
                        return null;
                    }

                    ban = (Ban)i$.next();
                } while(!ban.findAccount(acc));

                return ban;
            }
        }
    }

    public static boolean checkBan(String hwid) {
        HWID hw = HWID.fromString(hwid);
        return hw != null && checkBan(hw);
    }

    public static boolean checkBan(HWID hwid) {
        Ban ban = getBan(hwid);
        return ban != null;
    }

    static void reload() {
        try {
            if(!banlistFile.exists()) {
                return;
            }

            DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = e.newDocumentBuilder();
            Document doc = docBuilder.parse(banlistFile);
            NodeList nList = doc.getElementsByTagName("ban");

            for(int temp = 0; temp < nList.getLength(); ++temp) {
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == 1) {
                    Ban ban = Ban.readFromXML((Element)nNode);
                    if(ban != null) {
                        HashMap var7 = _bans;
                        synchronized(_bans) {
                            _bans.put(ban.hwid, ban);
                        }
                    }
                }
            }
        } catch (Exception var10) {
            GuardLog.getLogger().severe("[SmartGuard] Error reloading ban list!");
            GuardLog.logException(var10);
        }

    }

    public static void store() {
        HashMap var0 = _bans;
        synchronized(_bans) {
            try {
                DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = e.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("banlist");
                doc.appendChild(rootElement);

                Element transformer;
                for(Iterator com = _bans.values().iterator(); com.hasNext(); rootElement.appendChild(transformer)) {
                    Ban transformerFactory = (Ban)com.next();
                    transformer = doc.createElement("ban");
                    Attr source = doc.createAttribute("hwid");
                    source.setValue(transformerFactory.hwid.plain);
                    transformer.setAttributeNode(source);
                    Attr result = doc.createAttribute("type");
                    result.setValue(transformerFactory.type.toString());
                    transformer.setAttributeNode(result);
                    Attr time = doc.createAttribute("time");
                    time.setValue(String.valueOf(transformerFactory.time));
                    transformer.setAttributeNode(time);
                    Element firstname;
                    if(transformerFactory.type == Type.TEMP) {
                        firstname = doc.createElement("end_time");
                        firstname.appendChild(doc.createTextNode(String.valueOf(transformerFactory.bannedUntil)));
                        transformer.appendChild(firstname);
                    }

                    if(transformerFactory.gmObjId > 0) {
                        firstname = doc.createElement("gmObjId");
                        firstname.appendChild(doc.createTextNode(String.valueOf(transformerFactory.gmObjId)));
                        transformer.appendChild(firstname);
                    }

                    Iterator i$;
                    TimedObject ipp;
                    Element ip;
                    Attr addtime;
                    if(transformerFactory.sessionAccounts.size() > 0) {
                        firstname = doc.createElement("accounts");
                        i$ = transformerFactory.sessionAccounts.iterator();

                        while(i$.hasNext()) {
                            ipp = (TimedObject)i$.next();
                            if(ipp != null) {
                                ip = doc.createElement("account");
                                ip.appendChild(doc.createTextNode(((String)ipp.value).trim()));
                                addtime = doc.createAttribute("time");
                                addtime.setValue(String.valueOf(ipp.time));
                                ip.setAttributeNode(addtime);
                                firstname.appendChild(ip);
                            }
                        }

                        transformer.appendChild(firstname);
                    }

                    if(transformerFactory.sessionIPs.size() > 0) {
                        firstname = doc.createElement("ipv4");
                        i$ = transformerFactory.sessionIPs.iterator();

                        while(i$.hasNext()) {
                            ipp = (TimedObject)i$.next();
                            if(ipp != null) {
                                ip = doc.createElement("ip");
                                ip.appendChild(doc.createTextNode(((String)ipp.value).trim()));
                                addtime = doc.createAttribute("time");
                                addtime.setValue(String.valueOf(ipp.time));
                                ip.setAttributeNode(addtime);
                                firstname.appendChild(ip);
                            }
                        }

                        transformer.appendChild(firstname);
                    }

                    if(transformerFactory.comment != null) {
                        firstname = doc.createElement("comment");
                        firstname.appendChild(doc.createTextNode(transformerFactory.comment));
                        transformer.appendChild(firstname);
                    }
                }

                Comment com1 = doc.createComment(" SmartGuard banlist file ");
                doc.insertBefore(com1, rootElement);
                TransformerFactory transformerFactory1 = TransformerFactory.newInstance();
                Transformer transformer1 = transformerFactory1.newTransformer();
                transformer1.setOutputProperty("indent", "yes");
                transformer1.setOutputProperty("method", "xml");
                transformer1.setOutputProperty("encoding", "UTF-8");
                transformer1.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                DOMSource source1 = new DOMSource(doc);
                StreamResult result1 = new StreamResult(banlistFile);
                transformer1.transform(source1, result1);
            } catch (Exception var17) {
                GuardLog.getLogger().severe("[SmartGuard] Error saving ban list!");
                GuardLog.logException(var17);
            }

            _lastStore = System.currentTimeMillis();
        }
    }

    static {
        banlistFile = new File(GuardConfig.SMART_GUARD_DIR, "bans.xml");
        _lastStore = 0L;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new BanManager.BanWorker(), 30000L, 30000L);
        reload();
        store();
    }

    private static class BanWorker extends TimerTask {
        private BanWorker() {
        }

        public void run() {
            try {
                synchronized(BanManager._delayedBans) {
                    Iterator needsStore = BanManager._delayedBans.iterator();

                    while(needsStore.hasNext()) {
                        TimedObject it = (TimedObject)needsStore.next();
                        if(it.time <= System.currentTimeMillis()) {
                            BanManager.addBan((Ban)it.value, true);
                            needsStore.remove();
                        }
                    }
                }
            } catch (Exception var10) {
                GuardLog.logException(var10);
            }

            try {
                synchronized(BanManager._bans) {
                    boolean needsStore1 = false;
                    Iterator it1 = BanManager._bans.entrySet().iterator();

                    while(it1.hasNext()) {
                        Entry en = (Entry)it1.next();
                        Ban b = (Ban)en.getValue();
                        if(b._lastUpdate > BanManager._lastStore) {
                            needsStore1 = true;
                        }

                        if(b.isExpired()) {
                            GuardLog.getLogger().info(String.format("Ban \'%s\' has expired and will be removed.", new Object[]{b}));
                            it1.remove();
                            needsStore1 = true;
                        }
                    }

                    if(needsStore1) {
                        BanManager.store();
                    }
                }
            } catch (Exception var8) {
                GuardLog.logException(var8);
            }

        }
    }
}
