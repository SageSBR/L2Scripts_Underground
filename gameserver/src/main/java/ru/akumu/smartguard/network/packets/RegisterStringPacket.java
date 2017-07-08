//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.network.packets;

import java.security.InvalidParameterException;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import ru.akumu.smartguard.manager.screen.model.Color;
import ru.akumu.smartguard.manager.screen.model.Font;
import ru.akumu.smartguard.manager.screen.model.FontStyle;
import ru.akumu.smartguard.manager.screen.model.PredefinedMsg;
import ru.akumu.smartguard.manager.screen.model.ScreenPos;
import ru.akumu.smartguard.network.packets.ISmartPacket;

public class RegisterStringPacket extends L2GameServerPacket implements ISmartPacket {
    private final char Subcode;
    private final int _Id;
    private final String _text;
    private final Font _font;
    private final PredefinedMsg _pdMsg;
    private final Color _color;
    private final ScreenPos _screenPos;
    private final FontStyle _style;
    private short offsetX;
    private short offsetY;
    private final int _fadeInMs;
    private final int _showMs;
    private final int _fadeOutMs;

    public RegisterStringPacket(int Id, String _text, Font _font, PredefinedMsg _pdMsg, Color _color, ScreenPos _screenPos, FontStyle _style, int _fadeInMs, int _showMs, int _fadeOutMs) {
        this.Subcode = 2;
        this.offsetX = 0;
        this.offsetY = 0;
        this._Id = Id;
        if(_text != null && _text.length() != 0) {
            if(_text.length() > 2048) {
                throw new InvalidParameterException("Text too long, please specify up to 2048 characters.");
            } else if(_fadeInMs >= 0 && _fadeOutMs >= 0 && _fadeOutMs >= 0) {
                this._text = _text;
                this._font = _font;
                this._pdMsg = _pdMsg;
                this._color = _color;
                this._screenPos = _screenPos;
                this._style = _style;
                this._fadeInMs = _fadeInMs;
                this._showMs = _showMs;
                this._fadeOutMs = _fadeOutMs;
            } else {
                throw new InvalidParameterException();
            }
        } else {
            throw new InvalidParameterException("Text can not be null or empty.");
        }
    }

    public RegisterStringPacket(int Id, String _text, Font _font, PredefinedMsg _pdMsg, Color _color, ScreenPos _screenPos, FontStyle _style) {
        this(Id, _text, _font, _pdMsg, _color, _screenPos, _style, 0, 0, 0);
    }

    public short getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(short offsetY) {
        this.offsetY = offsetY;
    }

    public short getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(short offsetX) {
        this.offsetX = offsetX;
    }

    protected void writeImpl() {
        this.writeC(255);
        this.writeC(2);
        this.writeD(this._Id);
        this.writeS(this._text);
        this.writeH(this._font.ordinal());
        this.writeH(this._pdMsg.ordinal());
        this.writeC(this._color.a);
        this.writeC(this._color.r);
        this.writeC(this._color.g);
        this.writeC(this._color.b);
        this.writeD(this._screenPos.mask);
        this.writeD(this._style.ordinal());
        this.writeD(this._fadeInMs);
        this.writeD(this._showMs);
        this.writeD(this._fadeOutMs);
        if(this._screenPos == ScreenPos.TopRightRelative) {
            this.writeH(this.offsetX);
            this.writeH(this.offsetY);
        }

    }

    protected boolean writeOpcodes() {
        return true;
    }
}
