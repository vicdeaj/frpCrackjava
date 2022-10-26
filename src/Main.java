import types.PasswordData;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String path = "inputFiles/mio.bin";

       FrpReader reader = new FrpReader(path);
       PasswordData pwData = reader.readPass();

        // get passwordHandle from password
        // 1 scrypt (password token)
        // 2 sha512 (personalise)
        // 3 Verify (TEE goes brr)

       System.out.println(Arrays.toString(pwData.passwordHandle));
       System.out.println(pwData.passwordHandle.length);
    }
}