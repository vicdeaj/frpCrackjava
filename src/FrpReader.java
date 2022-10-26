import types.PasswordData;
import types.PersistentDataFRPhandle;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FrpReader {
    private final int FRP_CREDENTIAL_RESERVED_SIZE = 1000;
    private final int MAX_FRP_CREDENTIAL_HANDLE_SIZE = FRP_CREDENTIAL_RESERVED_SIZE - 4;
    private final int TEST_MODE_RESERVED_SIZE = 10000;


    private byte[] inputFileBytes;

    FrpReader(String path){
        try {
            DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            inputFileBytes = is.readAllBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public PasswordData readPass(){
        // get credentialHandle start byte

        int BlockDeviceSize = inputFileBytes.length;
        int FrpCredentialDataOffset = BlockDeviceSize - 1 - FRP_CREDENTIAL_RESERVED_SIZE;
        byte[] Credentialbytes = Arrays.copyOfRange(inputFileBytes, FrpCredentialDataOffset, BlockDeviceSize - 1);

        int dataLength = ByteBuffer.wrap(Credentialbytes).getInt();
        Credentialbytes = Arrays.copyOfRange(Credentialbytes, 4, Credentialbytes.length);

        PersistentDataFRPhandle pa = PersistentDataFRPhandle.fromBytes(Credentialbytes);
        return PasswordData.fromBytes(pa.payload);
    }

}
