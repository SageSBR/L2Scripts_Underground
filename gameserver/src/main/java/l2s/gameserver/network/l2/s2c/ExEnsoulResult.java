package l2s.gameserver.network.l2.s2c;


public class ExEnsoulResult extends L2GameServerPacket {
    public enum Result {
        SUCCESS((byte)1);

        private final byte resultCode;

        private Result(byte resultCode) {
            this.resultCode = resultCode;
        }

        public byte getResultCode() {
            return resultCode;
        }
    }

    // 1 - success
    private byte result;
    // for now only 1
    private byte effectCount;
    // should be in loop of effectCount
    private int firstEffect;
    // not used?
    private byte secondEffect;

    public ExEnsoulResult(byte result, int firstEffect) {
        this.result = result;
        this.firstEffect = firstEffect;
        this.effectCount = 1; // for now should be 1
        this.secondEffect = 0; // not used?
    }

    public byte getEffectCount() {
        return effectCount;
    }

    public void setEffectCount(byte effectCount) {
        this.effectCount = effectCount;
    }

    public byte getSecondEffect() {
        return secondEffect;
    }

    public void setSecondEffect(byte secondEffect) {
        this.secondEffect = secondEffect;
    }

    @Override
    protected void writeImpl() {
        writeB(result);
        writeB(effectCount);
        for (byte i = 0; i < effectCount; ++i) {
            writeC(firstEffect);
        }
        writeB(secondEffect);
    }
}
