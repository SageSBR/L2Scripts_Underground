//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.session.model;

import java.io.Serializable;
import ru.akumu.smartguard.manager.session.model.HWID;

public class ClientData implements Serializable {
    public String account;
    public HWID hwid;
    public short langId;

    public ClientData(HWID _hwid, String _account, short _langId) {
        hwid = _hwid;
        account = _account;
        langId = _langId;
    }

    public ClientData(String hwid, String account, short langId) {
        this(HWID.fromString(hwid), account, langId);
    }

    public ClientData(byte[] hwid, String account, short langId) {
        this(HWID.fromData(hwid), account, langId);
    }

    public String toString() {
        return "ClientData{account=\'" + this.account + '\'' + ", hwid=" + this.hwid + ", langId=" + this.langId + '}';
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            ClientData that = (ClientData)o;
            return this.langId != that.langId?false:(!this.account.equals(that.account)?false:this.hwid.equals(that.hwid));
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.account.hashCode();
        result = 31 * result + this.hwid.hashCode();
        result = 31 * result + this.langId;
        return result;
    }
}
