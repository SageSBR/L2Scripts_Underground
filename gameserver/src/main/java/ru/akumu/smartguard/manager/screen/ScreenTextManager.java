//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.screen;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import l2s.gameserver.network.l2.GameClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.screen.model.Color;
import ru.akumu.smartguard.manager.screen.model.Font;
import ru.akumu.smartguard.manager.screen.model.FontStyle;
import ru.akumu.smartguard.manager.screen.model.PredefinedMsg;
import ru.akumu.smartguard.manager.screen.model.ScreenPos;
import ru.akumu.smartguard.network.packets.RegisterStringPacket;
import ru.akumu.smartguard.utils.log.GuardLog;

public class ScreenTextManager {
    private static final File banlistFile;
    public static final int DT_TOP = 0;
    public static final int DT_LEFT = 0;
    public static final int DT_CENTER = 1;
    public static final int DT_RIGHT = 2;
    public static final int DT_VCENTER = 4;
    public static final int DT_BOTTOM = 8;
    private static ScreenTextManager _instance;
    private final HashMap<Integer, RegisterStringPacket> _generalStrings = new HashMap(1000);
    private final int _counterDefaultValue = 1000;
    private int _counter = 1000;
    private final Object _counterLock = new Object();

    public static ScreenTextManager getInstance() {
        return _instance != null?_instance:(_instance = new ScreenTextManager());
    }

    private ScreenTextManager() {
        this.load();
    }

    private void load() {
        HashMap var1 = this._generalStrings;
        synchronized(this._generalStrings) {
            this._generalStrings.clear();
            this._counter = 1000;

            try {
                DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = e.newDocumentBuilder();
                Document doc = docBuilder.parse(banlistFile);
                NodeList nList = doc.getElementsByTagName("string");

                for(int temp = 0; temp < nList.getLength(); ++temp) {
                    Node nNode = nList.item(temp);
                    if(nNode.getNodeType() == 1) {
                        Element xmle = (Element)nNode;

                        try {
                            String e1 = xmle.getAttribute("value").trim();
                            Font font = Font.valueOf(xmle.getAttribute("font").trim());
                            ScreenPos pos = ScreenPos.valueOf(xmle.getAttribute("pos").trim());
                            FontStyle style = FontStyle.valueOf(xmle.getAttribute("style").trim());
                            String clrstr = xmle.getAttribute("color").trim().toLowerCase();
                            if(clrstr.length() != 8) {
                                throw new Exception("Color length must be 8");
                            }

                            int idx = 0;
                            int[] argb = new int[4];

                            for(int color = 0; color < 8; color += 2) {
                                argb[idx++] = Integer.parseInt(clrstr.substring(color, color + 2), 16);
                            }

                            Color var31 = new Color(argb[0], argb[1], argb[2], argb[3]);
                            int showTime = 0;
                            int fadeIn = 0;
                            int fadeOut = 0;
                            short offsetX = 0;
                            short offsetY = 0;
                            PredefinedMsg msgId = PredefinedMsg.None;
                            NodeList aNode = xmle.getElementsByTagName("showtime");

                            int j;
                            Node n;
                            Element st;
                            for(j = 0; j < aNode.getLength(); ++j) {
                                n = aNode.item(j);
                                if(n.getNodeType() == 1) {
                                    st = (Element)n;
                                    showTime = Integer.parseInt(st.getAttribute("value").trim());
                                }
                            }

                            aNode = xmle.getElementsByTagName("fadein");

                            for(j = 0; j < aNode.getLength(); ++j) {
                                n = aNode.item(j);
                                if(n.getNodeType() == 1) {
                                    st = (Element)n;
                                    fadeIn = Integer.parseInt(st.getAttribute("value").trim());
                                }
                            }

                            aNode = xmle.getElementsByTagName("fadeout");

                            for(j = 0; j < aNode.getLength(); ++j) {
                                n = aNode.item(j);
                                if(n.getNodeType() == 1) {
                                    st = (Element)n;
                                    fadeOut = Integer.parseInt(st.getAttribute("value").trim());
                                }
                            }

                            aNode = xmle.getElementsByTagName("msgId");

                            for(j = 0; j < aNode.getLength(); ++j) {
                                n = aNode.item(j);
                                if(n.getNodeType() == 1) {
                                    st = (Element)n;
                                    msgId = PredefinedMsg.valueOf(st.getAttribute("value").trim());
                                }
                            }

                            aNode = xmle.getElementsByTagName("pos");

                            for(j = 0; j < aNode.getLength(); ++j) {
                                n = aNode.item(j);
                                if(n.getNodeType() == 1) {
                                    st = (Element)n;
                                    offsetX = Short.parseShort(st.getAttribute("x").trim());
                                    offsetY = Short.parseShort(st.getAttribute("y").trim());
                                }
                            }

                            this.registerGeneralString(this.GetNextStringId(), e1, font, msgId, var31, pos, style, fadeIn, showTime, fadeOut, offsetX, offsetY);
                        } catch (Exception var28) {
                            GuardLog.getLogger().warning("Can not load D3DX string!");
                            GuardLog.logException(var28);
                        }
                    }
                }
            } catch (Exception var29) {
                GuardLog.logException(var29);
            }

        }
    }

    public int GetNextStringId() {
        Object var1 = this._counterLock;
        synchronized(this._counterLock) {
            if(this._counter == 2147483647) {
                this._counter = 1000;
            }

            return this._counter++;
        }
    }

    public void registerGeneralString(int Id, String _text, Font _font, PredefinedMsg _pdMsg, Color _color, ScreenPos _screenPos, FontStyle _style, int _fadeInMs, int _showMs, int _fadeOutMs, short x, short y) {
        RegisterStringPacket rsp = new RegisterStringPacket(Id, _text, _font, _pdMsg, _color, _screenPos, _style, _fadeInMs, _showMs, _fadeOutMs);
        rsp.setOffsetX(x);
        rsp.setOffsetY(y);
        this._generalStrings.put(Integer.valueOf(Id), rsp);
    }

    public void onPlayerLogin(GameClient client) {
        if(client != null) {
            Iterator i$ = this._generalStrings.values().iterator();

            while(i$.hasNext()) {
                RegisterStringPacket rsp = (RegisterStringPacket)i$.next();
                client.sendPacket(rsp);
            }

        }
    }

    static {
        banlistFile = new File(GuardConfig.SMART_GUARD_DIR, "d3dx.xml");
    }
}
