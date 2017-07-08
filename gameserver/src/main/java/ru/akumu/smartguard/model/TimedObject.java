//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.model;

public class TimedObject<T> {
    public final T value;
    public final long time;

    public TimedObject(T value, long time) {
        this.value = value;
        this.time = time;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            TimedObject that = (TimedObject)o;
            if(this.time != that.time) {
                return false;
            } else {
                if(this.value != null) {
                    if(!this.value.equals(that.value)) {
                        return false;
                    }
                } else if(that.value != null) {
                    return false;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.value != null?this.value.hashCode():0;
        result = 31 * result + (int)(this.time ^ this.time >>> 32);
        return result;
    }
}
