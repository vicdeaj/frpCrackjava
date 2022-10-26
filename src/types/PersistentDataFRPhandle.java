package types;

import java.io.*;

public class PersistentData {
    static final byte VERSION_1 = 1;
    static final int VERSION_1_HEADER_SIZE = 1 + 1 + 4 + 4;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_SP = 1;
    public static final int TYPE_SP_WEAVER = 2;

    //public static final PersistentData NONE = new PersistentData(TYPE_NONE, UserHandle.USER_NULL, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED, null);
    public static final PersistentData NONE = new PersistentData(TYPE_NONE, 0, 0, null);

    final int type;
    final int userId;
    final int qualityForUi;
    final byte[] payload;

    private PersistentData(int type, int userId, int qualityForUi, byte[] payload) {
        this.type = type;
        this.userId = userId;
        this.qualityForUi = qualityForUi;
        this.payload = payload;
    }

    public static PersistentData fromBytes(byte[] frpData) {
        if (frpData == null || frpData.length == 0) {
            return NONE;
        }

        DataInputStream is = new DataInputStream(new ByteArrayInputStream(frpData));
        try {
            byte version = is.readByte();
            if (version == PersistentData.VERSION_1) {
                int type = is.readByte() & 0xFF;
                int userId = is.readInt();
                int qualityForUi = is.readInt();
                byte[] payload = new byte[frpData.length - VERSION_1_HEADER_SIZE];
                System.arraycopy(frpData, VERSION_1_HEADER_SIZE, payload, 0, payload.length);
                return new PersistentData(type, userId, qualityForUi, payload);
            } else {
    //            Slog.wtf(TAG, "Unknown PersistentData version code: " + version);
                System.out.println("Unknown PersistentData version code: " + version);
                return NONE;
            }
        } catch (IOException e) {
    //        Slog.wtf(TAG, "Could not parse PersistentData", e);
            System.out.println("Could not parse PersistentData");
            e.printStackTrace();
            return NONE;
        }
    }

    public static byte[] toBytes(int persistentType, int userId, int qualityForUi,
                                 byte[] payload) {
        if (persistentType == PersistentData.TYPE_NONE) {
            //Preconditions.checkArgument(payload == null, "TYPE_NONE must have empty payload");
            return null;
        }
        //Preconditions.checkArgument(payload != null && payload.length > 0, "empty payload must only be used with TYPE_NONE");

        ByteArrayOutputStream os = new ByteArrayOutputStream(
                VERSION_1_HEADER_SIZE + payload.length);
        DataOutputStream dos = new DataOutputStream(os);
        try {
            dos.writeByte(PersistentData.VERSION_1);
            dos.writeByte(persistentType);
            dos.writeInt(userId);
            dos.writeInt(qualityForUi);
            dos.write(payload);
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayOutputStream cannot throw IOException");
        }
        return os.toByteArray();
    }
}