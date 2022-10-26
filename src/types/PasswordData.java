import java.nio.ByteBuffer;

public class PasswordData {

    // 256-bit synthetic password
    private static final byte SYNTHETIC_PASSWORD_LENGTH = 256 / 8;

    private static final int PASSWORD_SCRYPT_LOG_N = 11;
    private static final int PASSWORD_SCRYPT_LOG_R = 3;
    private static final int PASSWORD_SCRYPT_LOG_P = 1;
    private static final int PASSWORD_SALT_LENGTH = 16;
    private static final int PASSWORD_TOKEN_LENGTH = 32;
    private static final String TAG = "SyntheticPasswordManager";



    byte scryptLogN;
    byte scryptLogR;
    byte scryptLogP;
    public int credentialType;
    byte[] salt;
    // For GateKeeper-based credential, this is the password handle returned by GK,
    // for weaver-based credential, this is empty.
    public byte[] passwordHandle;

    public static PasswordData create(int passwordType) {
        PasswordData result = new PasswordData();
        result.scryptLogN = PASSWORD_SCRYPT_LOG_N;
        result.scryptLogR = PASSWORD_SCRYPT_LOG_R;
        result.scryptLogP = PASSWORD_SCRYPT_LOG_P;
        result.credentialType = passwordType;
        //result.salt = secureRandom(PASSWORD_SALT_LENGTH);
        result.salt = new byte[PASSWORD_SALT_LENGTH];
        return result;
    }

    public static PasswordData fromBytes(byte[] data) {
        PasswordData result = new PasswordData();
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data, 0, data.length);
        buffer.flip();
        result.credentialType = buffer.getInt();
        result.scryptLogN = buffer.get();
        result.scryptLogR = buffer.get();
        result.scryptLogP = buffer.get();
        int saltLen = buffer.getInt();
        result.salt = new byte[saltLen];
        buffer.get(result.salt);
        int handleLen = buffer.getInt();
        if (handleLen > 0) {
            result.passwordHandle = new byte[handleLen];
            buffer.get(result.passwordHandle);
        } else {
            result.passwordHandle = null;
        }
        return result;
    }

    public byte[] toBytes() {

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 3 * Byte.BYTES
                + Integer.BYTES + salt.length + Integer.BYTES +
                (passwordHandle != null ? passwordHandle.length : 0));
        buffer.putInt(credentialType);
        buffer.put(scryptLogN);
        buffer.put(scryptLogR);
        buffer.put(scryptLogP);
        buffer.putInt(salt.length);
        buffer.put(salt);
        if (passwordHandle != null && passwordHandle.length > 0) {
            buffer.putInt(passwordHandle.length);
            buffer.put(passwordHandle);
        } else {
            buffer.putInt(0);
        }
        return buffer.array();
    }
}