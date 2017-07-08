//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.bans.model;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import l2s.gameserver.network.l2.GameClient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.session.ClientSessionManager;
import ru.akumu.smartguard.manager.session.model.ClientSession;
import ru.akumu.smartguard.manager.session.model.HWID;
import ru.akumu.smartguard.model.TimedObject;
import ru.akumu.smartguard.utils.log.GuardLog;

public class Ban implements Serializable {
    private static final long serialVersionUID = -1322322139926390329L;
    public final HWID hwid;
    public final Ban.Type type;
    public final long bannedUntil;
    public final long time;
    public final String comment;
    public int gmObjId;
    public final Set<TimedObject<String>> sessionAccounts;
    public final Set<TimedObject<String>> sessionIPs;
    public long _lastUpdate;



    public Ban(HWID hwid, String comment) {
        this(hwid, Ban.Type.NORMAL, comment, 0L, 0L);
    }

    public Ban(HWID hwid, long bannedUntil, String comment) {
        this(hwid, Ban.Type.TEMP, comment, bannedUntil, 0L);
    }

    private Ban(HWID hwid, Ban.Type type, String comment, long bannedUntil, long time) {
        this.sessionAccounts = new HashSet(GuardConfig.MaxInstances > 0?GuardConfig.MaxInstances:10);
        this.sessionIPs = new HashSet(GuardConfig.MaxInstances > 0?GuardConfig.MaxInstances:10);
        this._lastUpdate = 0L;
        if(hwid == null) {
            throw new InvalidParameterException("HWID can not be null");
        } else {
            this.hwid = hwid;
            this.bannedUntil = bannedUntil;
            this.comment = comment;
            this.type = type;
            this.time = time <= 0L?System.currentTimeMillis():time;
            ClientSession sess = ClientSessionManager.getSession(hwid);
            if(sess != null) {
                long timeNow = System.currentTimeMillis();

                GameClient client;
                for(Iterator i$ = sess.getClients().iterator(); i$.hasNext(); this.sessionIPs.add(new TimedObject(client.getIpAddr(), timeNow))) {
                    client = (GameClient)i$.next();
                    if(client.getLogin() != null) {
                        this.sessionAccounts.add(new TimedObject(client.getLogin(), timeNow));
                    }
                }
            }

            this._lastUpdate = System.currentTimeMillis();
        }
    }

    public void addAccount(String acc) {
        if(acc != null) {
            this.sessionAccounts.add(new TimedObject(acc.toLowerCase(), System.currentTimeMillis()));
            this._lastUpdate = System.currentTimeMillis();
        }
    }

    public boolean findAccount(String acc) {
        Iterator i$ = this.sessionAccounts.iterator();

        TimedObject ts;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            ts = (TimedObject)i$.next();
        } while(!acc.equals(ts.value));

        return true;
    }

    public String[] getAccountNames() {
        String[] res = new String[this.sessionAccounts.size()];
        int i = 0;

        TimedObject sessionAccount;
        for(Iterator i$ = this.sessionAccounts.iterator(); i$.hasNext(); res[i++] = (String)sessionAccount.value) {
            sessionAccount = (TimedObject)i$.next();
        }

        return res;
    }

    public boolean isExpired() {
        return this.type == Ban.Type.TEMP && this.bannedUntil <= System.currentTimeMillis();
    }

    public static Ban readFromXML(Element xmle) {
        if(xmle == null) {
            return null;
        } else {
            try {
                HWID e = HWID.fromString(xmle.getAttribute("hwid"));
                Ban.Type type = Ban.Type.valueOf(xmle.getAttribute("type").toUpperCase());
                long time = Long.parseLong(xmle.getAttribute("time"));
                long bannedUntil = 0L;
                String comment = null;
                if(type == Ban.Type.TEMP) {
                    bannedUntil = Long.parseLong(xmle.getElementsByTagName("end_time").item(0).getTextContent());
                }

                NodeList commentNodes = xmle.getElementsByTagName("comment");
                if(commentNodes.getLength() > 0) {
                    comment = commentNodes.item(0).getTextContent().trim();
                }

                Ban b = new Ban(e, type, comment, bannedUntil, time);
                NodeList gmNodes = xmle.getElementsByTagName("gmObjId");
                if(gmNodes.getLength() > 0) {
                    b.gmObjId = Integer.parseInt(gmNodes.item(0).getTextContent().trim());
                }

                NodeList accountListNode = xmle.getElementsByTagName("accounts");
                if(accountListNode.getLength() > 0) {
                    for(int ipv4ListNode = 0; ipv4ListNode < accountListNode.getLength(); ++ipv4ListNode) {
                        Node i = accountListNode.item(ipv4ListNode);
                        if(i.getNodeType() == 1) {
                            Element ipNode = (Element)i;
                            NodeList accountsElement = ipNode.getElementsByTagName("account");

                            for(int iNode = 0; iNode < accountsElement.getLength(); ++iNode) {
                                Node j = accountsElement.item(iNode);
                                if(j.getNodeType() == 1) {
                                    Element n = (Element)j;
                                    String ipElement = n.getTextContent().trim();
                                    long ip = Long.parseLong(n.getAttribute("time").trim());
                                    b.sessionAccounts.add(new TimedObject(ipElement, ip));
                                }
                            }
                        }
                    }
                }

                NodeList var24 = xmle.getElementsByTagName("ipv4");
                if(var24.getLength() > 0) {
                    for(int var25 = 0; var25 < var24.getLength(); ++var25) {
                        Node var26 = var24.item(var25);
                        if(var26.getNodeType() == 1) {
                            Element var27 = (Element)var26;
                            NodeList var28 = var27.getElementsByTagName("ip");

                            for(int var29 = 0; var29 < var28.getLength(); ++var29) {
                                Node var30 = var28.item(var29);
                                if(var30.getNodeType() == 1) {
                                    Element var31 = (Element)var30;
                                    String var32 = var31.getTextContent().trim();
                                    long ipTime = Long.parseLong(var31.getAttribute("time").trim());
                                    b.sessionIPs.add(new TimedObject(var32, ipTime));
                                }
                            }
                        }
                    }
                }

                return b;
            } catch (Exception var23) {
                GuardLog.logException(var23);
                return null;
            }
        }
    }

    public String toString() {
        return "Ban{hwid=" + this.hwid + ", type=" + this.type + ", bannedUntil=" + this.bannedUntil + ", comment=\'" + this.comment + '\'' + '}';
    }

    public static enum Type {
        TEMP,
        NORMAL;

        private Type() {
        }
    }
}
