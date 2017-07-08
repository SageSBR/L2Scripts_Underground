//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.network.packets;

import java.security.InvalidParameterException;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import ru.akumu.smartguard.network.packets.ISmartPacket;

public class MsgPacket extends L2GameServerPacket implements ISmartPacket {
    private final char Subcode;
    private MsgPacket.MsgMode _mode;
    private MsgPacket.MsgType _type;
    private boolean _exit;
    private String _text;

    public MsgPacket(MsgPacket.MsgType type, boolean exit) {
        this(MsgPacket.MsgMode.PREDEFINED, type, exit, (String)null);
    }

    public MsgPacket(String text, boolean exit) {
        this(MsgPacket.MsgMode.CUSTOM, MsgPacket.MsgType.NO_ERROR, exit, text);
    }

    private MsgPacket(MsgPacket.MsgMode _mode, MsgPacket.MsgType _type, boolean _exit, String _text) {
        this.Subcode = 0;
        if(_mode == MsgPacket.MsgMode.CUSTOM && _text == null) {
            throw new InvalidParameterException("Text can not be null for custom messages.");
        } else {
            this._mode = _mode;
            this._type = _type;
            this._exit = _exit;
            this._text = _text;
        }
    }

    protected void writeImpl() {
        this.writeC(255);
        this.writeC(0);
        this.writeC(_mode.ordinal());
        switch(_mode.ordinal()) {
            case 1:
                this.writeC(this._type.ordinal());
                break;
            case 2:
                this.writeS(this._text);
        }

        this.writeC(this._exit?1:0);
    }

    protected boolean writeOpcodes() {
        return true;
    }

    public static enum MsgType {
        NO_ERROR,
        GENERAL_ERROR,
        BANNED_ALREADY,
        VERSION_MISSMATCH,
        VM_LOGIN,
        NO_UPDATER,
        DETECTED_KICK,
        DETECTED_BAN,
        INSTANCE_LIMIT,
        TEMP_BAN,
        OLD_PATCH;

        public final MsgPacket paket = this.ordinal() != 0?new MsgPacket(this, true):null;

        private MsgType() {
        }
    }

    private static enum MsgMode {
        PREDEFINED,
        CUSTOM;

        private MsgMode() {
        }
    }
}
