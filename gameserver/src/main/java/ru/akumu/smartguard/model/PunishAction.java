//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.model;

import java.security.InvalidParameterException;
import ru.akumu.smartguard.model.PunishMode;

public class PunishAction {
    public static final PunishAction STATIC_REALTIME_BAN;
    public static final PunishAction STATIC_REALTIME_DISCONNECT;
    public static final PunishAction STATIC_REALTIME_LOG;
    public final PunishMode _mode;
    public final PunishAction.Type _type;
    public final int _time;
    public final int _delayMin;
    public final int _delayMax;

    public PunishAction(PunishMode _mode, int _delayMin, int _delayMax) {
        if(_mode != PunishMode.BAN) {
            throw new InvalidParameterException("Can not make temporary disconnect or log action.");
        } else if(_delayMin > _delayMax) {
            throw new InvalidParameterException("Min > Max");
        } else if(_delayMin > 0 && _delayMax > 0) {
            this._mode = _mode;
            this._type = PunishAction.Type.DELAYED;
            this._time = 0;
            this._delayMin = _delayMin;
            this._delayMax = _delayMax;
        } else {
            throw new InvalidParameterException("Time must be positive.");
        }
    }

    public PunishAction(PunishMode _mode, int _time) {
        if(_mode != PunishMode.BAN) {
            throw new InvalidParameterException("Can not make temporary disconnect or log action.");
        } else if(_time < 0) {
            throw new InvalidParameterException("Time can not be negative.");
        } else {
            this._mode = _mode;
            this._type = PunishAction.Type.TEMPORARY;
            this._time = _time;
            this._delayMin = 0;
            this._delayMax = 0;
        }
    }

    public PunishAction(PunishMode _mode) {
        this._mode = _mode;
        this._type = PunishAction.Type.REALTIME;
        this._time = 0;
        this._delayMin = 0;
        this._delayMax = 0;
    }

    static {
        STATIC_REALTIME_BAN = new PunishAction(PunishMode.BAN);
        STATIC_REALTIME_DISCONNECT = new PunishAction(PunishMode.DISCONNECT);
        STATIC_REALTIME_LOG = new PunishAction(PunishMode.LOG);
    }

    public static enum Type {
        REALTIME(10),
        TEMPORARY(5),
        DELAYED(0);

        public final int priority;

        private Type(int p) {
            this.priority = p;
        }
    }
}
